package vuinfinitecart.web.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vuinfinitecart.web.admin.dto.AdminProfileResponse;
import vuinfinitecart.web.admin.dto.AdminProfileUpdateRequest;
import vuinfinitecart.web.admin.service.AdminProfileService;
import vuinfinitecart.web.common.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @GetMapping
    public ApiResponse<AdminProfileResponse> getProfile() {
        return ApiResponse.ok(adminProfileService.getProfile());
    }

    @PutMapping
    public ApiResponse<AdminProfileResponse> updateProfile(@RequestBody AdminProfileUpdateRequest request) {
        return ApiResponse.ok("Profile updated successfully", adminProfileService.updateProfile(request));
    }

    @PostMapping("/avatar")
    public ApiResponse<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok("Avatar updated successfully", adminProfileService.updateAvatar(file));
    }
}
