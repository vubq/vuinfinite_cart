package vuinfinitecart.web.permission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vuinfinitecart.web.common.response.ApiResponse;
import vuinfinitecart.web.permission.dto.PermissionGroupRequest;
import vuinfinitecart.web.permission.dto.PermissionGroupResponse;
import vuinfinitecart.web.permission.dto.PermissionResponse;
import vuinfinitecart.web.permission.service.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/permission-groups")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ApiResponse<PermissionGroupResponse> createGroup(@RequestBody PermissionGroupRequest request) {
        return ApiResponse.ok("Permission group created successfully", permissionService.createGroup(request));
    }

    @PutMapping("/permission-groups/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ApiResponse<PermissionGroupResponse> updateGroup(@PathVariable Long id, @RequestBody PermissionGroupRequest request) {
        return ApiResponse.ok("Permission group updated successfully", permissionService.updateGroup(id, request));
    }

    @DeleteMapping("/permission-groups/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ApiResponse<Void> deleteGroup(@PathVariable Long id) {
        permissionService.deleteGroup(id);
        return ApiResponse.ok("Permission group deleted successfully");
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/permission-groups")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ApiResponse<List<PermissionGroupResponse>> getAllGroups() {
        return ApiResponse.ok(permissionService.getAllGroups());
    }
}
