package vuinfinitecart.web.permission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    public PermissionGroupResponse createGroup(@RequestBody PermissionGroupRequest request) {
        return permissionService.createGroup(request);
    }

    @PutMapping("/permission-groups/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public PermissionGroupResponse updateGroup(@PathVariable Long id, @RequestBody PermissionGroupRequest request) {
        return permissionService.updateGroup(id, request);
    }

    @DeleteMapping("/permission-groups/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deleteGroup(@PathVariable Long id) {
        permissionService.deleteGroup(id);
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public List<PermissionResponse> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @GetMapping("/permission-groups")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public List<PermissionGroupResponse> getAllGroups() {
        return permissionService.getAllGroups();
    }
}
