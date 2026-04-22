package vuinfinitecart.web.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vuinfinitecart.web.admin.dto.AdminCreateRequest;
import vuinfinitecart.web.admin.dto.AdminPermissionUpdateRequest;
import vuinfinitecart.web.admin.dto.AdminResponse;
import vuinfinitecart.web.admin.entity.Admin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import vuinfinitecart.web.common.response.PageResponse;
import vuinfinitecart.web.common.response.ApiResponse;
import vuinfinitecart.web.admin.service.AdminManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ApiResponse<AdminResponse> createAdmin(@RequestBody AdminCreateRequest request) {
        return ApiResponse.ok("Officer invited successfully", adminManagementService.createAdmin(request));
    }

    @PutMapping("/admins/{id}/permissions")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ApiResponse<AdminResponse> updatePermissions(@PathVariable Long id, @RequestBody AdminPermissionUpdateRequest request) {
        return ApiResponse.ok("Permissions updated successfully", adminManagementService.updatePermissions(id, request));
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('admins:read')")
    public ApiResponse<PageResponse<AdminResponse>> getAllAdmins(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Admin.AdminStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(adminManagementService.getAllAdmins(search, status, pageable));
    }

    @PatchMapping("/admins/{id}/status")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('admins:update')")
    public ApiResponse<AdminResponse> updateAdminStatus(@PathVariable Long id, @RequestParam Admin.AdminStatus status) {
        return ApiResponse.ok("Status updated successfully", adminManagementService.updateAdminStatus(id, status));
    }
}
