package vuinfinitecart.web.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vuinfinitecart.web.common.response.ApiResponse;
import vuinfinitecart.web.customer.dto.CustomerProfileResponse;
import vuinfinitecart.web.customer.dto.CustomerProfileUpdateRequest;
import vuinfinitecart.web.customer.service.CustomerProfileService;

@RestController
@RequestMapping("/api/customer/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @GetMapping
    public ApiResponse<CustomerProfileResponse> getProfile() {
        return ApiResponse.ok(customerProfileService.getProfile());
    }

    @PutMapping
    public ApiResponse<CustomerProfileResponse> updateProfile(@RequestBody CustomerProfileUpdateRequest request) {
        return ApiResponse.ok("Profile updated successfully", customerProfileService.updateProfile(request));
    }

    @PostMapping("/avatar")
    public ApiResponse<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok("Avatar updated successfully", customerProfileService.updateAvatar(file));
    }
}
