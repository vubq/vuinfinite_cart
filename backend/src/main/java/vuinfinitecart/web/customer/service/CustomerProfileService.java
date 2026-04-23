package vuinfinitecart.web.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vuinfinitecart.web.common.exception.AppException;
import vuinfinitecart.web.common.security.SecurityUtils;
import vuinfinitecart.web.customer.dto.CustomerProfileResponse;
import vuinfinitecart.web.customer.dto.CustomerProfileUpdateRequest;
import vuinfinitecart.web.customer.entity.Customer;
import vuinfinitecart.web.customer.repository.CustomerRepository;
import vuinfinitecart.web.media.service.MediaService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MediaService mediaService;

    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile() {
        Customer customer = getCurrentCustomer();
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerProfileResponse updateProfile(CustomerProfileUpdateRequest request) {
        Customer customer = getCurrentCustomer();

        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw AppException.badRequest("Email already exists");
            }
            customer.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            customer.setName(request.getName());
        }

        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), customer.getPasswordHash())) {
                throw AppException.badRequest("Current password is incorrect");
            }
            customer.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        return mapToResponse(customerRepository.save(customer));
    }

    @Transactional
    public String updateAvatar(MultipartFile file) {
        Customer customer = getCurrentCustomer();
        
        // 1. Delete old avatar if exists
        if (customer.getAvatarUrl() != null) {
            mediaService.deleteImageByUrl(customer.getAvatarUrl());
        }

        // 2. Upload new avatar with randomized name
        String folder = "avatars/customers/" + customer.getId();
        Map uploadResult = mediaService.uploadProfileAvatar(file, folder);
        String newUrl = (String) uploadResult.get("secure_url");

        // 3. Update entity
        customer.setAvatarUrl(newUrl);
        customerRepository.save(customer);

        return newUrl;
    }

    private Customer getCurrentCustomer() {
        Long id = SecurityUtils.getCurrentCustomerId();
        if (id == null) throw AppException.unauthorized("Not logged in");
        return customerRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Customer not found"));
    }

    private CustomerProfileResponse mapToResponse(Customer customer) {
        return CustomerProfileResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .avatarUrl(customer.getAvatarUrl())
                .build();
    }
}
