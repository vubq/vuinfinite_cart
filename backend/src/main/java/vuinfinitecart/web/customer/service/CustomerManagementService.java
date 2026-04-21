package vuinfinitecart.web.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuinfinitecart.web.common.exception.AppException;
import vuinfinitecart.web.common.security.RedisTokenStore;
import vuinfinitecart.web.customer.entity.Customer;
import vuinfinitecart.web.customer.repository.CustomerRepository;

import vuinfinitecart.web.customer.dto.CustomerAdminResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerManagementService {

    private final CustomerRepository customerRepository;
    private final RedisTokenStore tokenStore;

    @Transactional(readOnly = true)
    public List<CustomerAdminResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());
    }

    private CustomerAdminResponse mapToAdminResponse(Customer customer) {
        return CustomerAdminResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .name(customer.getName())
                .avatarUrl(customer.getAvatarUrl())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    @Transactional
    public void updateCustomerStatus(Long customerId, Customer.CustomerStatus status) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> AppException.notFound("Customer not found"));

        customer.setStatus(status);
        customerRepository.save(customer);

        // Instant Revocation: If banned, wipe their refresh token from Redis
        if (status == Customer.CustomerStatus.BANNED) {
            tokenStore.deleteCustomerRefreshToken(customerId);
        }
    }
}
