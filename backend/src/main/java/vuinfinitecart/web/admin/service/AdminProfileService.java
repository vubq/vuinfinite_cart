package vuinfinitecart.web.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vuinfinitecart.web.admin.dto.AdminProfileResponse;
import vuinfinitecart.web.admin.dto.AdminProfileUpdateRequest;
import vuinfinitecart.web.admin.entity.Admin;
import vuinfinitecart.web.admin.repository.AdminRepository;
import vuinfinitecart.web.common.exception.AppException;
import vuinfinitecart.web.common.security.SecurityUtils;
import vuinfinitecart.web.media.service.MediaService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminProfileService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final MediaService mediaService;

    @Transactional(readOnly = true)
    public AdminProfileResponse getProfile() {
        Admin admin = getCurrentAdmin();
        return mapToResponse(admin);
    }

    @Transactional
    public AdminProfileResponse updateProfile(AdminProfileUpdateRequest request) {
        Admin admin = getCurrentAdmin();

        if (request.getEmail() != null && !request.getEmail().equals(admin.getEmail())) {
            if (adminRepository.existsByEmail(request.getEmail())) {
                throw AppException.badRequest("Email already exists");
            }
            admin.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            admin.setFullName(request.getFullName());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), admin.getPasswordHash())) {
                throw AppException.badRequest("Current password is incorrect");
            }
            admin.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        return mapToResponse(adminRepository.save(admin));
    }

    @Transactional
    public String updateAvatar(MultipartFile file) {
        Admin admin = getCurrentAdmin();
        
        // 1. Delete old avatar if exists
        if (admin.getAvatarUrl() != null) {
            mediaService.deleteImageByUrl(admin.getAvatarUrl());
        }

        // 2. Upload new avatar with randomized name
        String folder = "avatars/admins/" + admin.getId();
        Map uploadResult = mediaService.uploadProfileAvatar(file, folder);
        String newUrl = (String) uploadResult.get("secure_url");

        // 3. Update entity
        admin.setAvatarUrl(newUrl);
        adminRepository.save(admin);

        return newUrl;
    }

    private Admin getCurrentAdmin() {
        Long id = SecurityUtils.getCurrentAdminId();
        if (id == null) throw AppException.unauthorized("Not logged in");
        return adminRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Admin not found"));
    }

    private AdminProfileResponse mapToResponse(Admin admin) {
        return AdminProfileResponse.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .avatarUrl(admin.getAvatarUrl())
                .build();
    }
}
