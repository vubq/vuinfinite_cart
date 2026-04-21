package vuinfinitecart.web.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vuinfinitecart.web.admin.dto.AdminCreateRequest;
import vuinfinitecart.web.admin.dto.AdminPermissionUpdateRequest;
import vuinfinitecart.web.admin.dto.AdminResponse;
import vuinfinitecart.web.admin.entity.Admin;
import vuinfinitecart.web.admin.service.AdminManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    @PostMapping("/admins")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public AdminResponse createAdmin(@RequestBody AdminCreateRequest request) {
        return adminManagementService.createAdmin(request);
    }

    @PutMapping("/admins/{id}/permissions")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public AdminResponse updatePermissions(@PathVariable Long id, @RequestBody AdminPermissionUpdateRequest request) {
        return adminManagementService.updatePermissions(id, request);
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('admins:read')")
    public List<AdminResponse> getAllAdmins() {
        return adminManagementService.getAllAdmins();
    }

    @PatchMapping("/admins/{id}/status")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('admins:update')")
    public AdminResponse updateAdminStatus(@PathVariable Long id, @RequestParam Admin.AdminStatus status) {
        return adminManagementService.updateAdminStatus(id, status);
    }
}
