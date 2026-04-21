package vuinfinitecart.web.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuinfinitecart.web.admin.dto.AdminResponse;
import vuinfinitecart.web.admin.entity.Admin;
import vuinfinitecart.web.admin.repository.AdminRepository;
import vuinfinitecart.web.common.exception.AppException;
import vuinfinitecart.web.common.security.SecurityUtils;
import vuinfinitecart.web.permission.entity.Permission;

import org.springframework.security.crypto.password.PasswordEncoder;
import vuinfinitecart.web.admin.dto.AdminCreateRequest;
import vuinfinitecart.web.admin.dto.AdminPermissionUpdateRequest;
import vuinfinitecart.web.permission.entity.PermissionGroup;
import vuinfinitecart.web.permission.repository.PermissionGroupRepository;
import vuinfinitecart.web.permission.repository.PermissionRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final AdminRepository adminRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminResponse createAdmin(AdminCreateRequest request) {
        Admin currentAdmin = getCurrentAdmin();
        if (!currentAdmin.isSuperadmin()) {
            throw AppException.forbidden("Only Superadmins can create new admin accounts");
        }

        if (adminRepository.existsByUsername(request.getUsername())) {
            throw AppException.badRequest("Username already exists");
        }
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw AppException.badRequest("Email already exists");
        }

        Admin newAdmin = new Admin();
        newAdmin.setUsername(request.getUsername());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setFullName(request.getFullName());
        newAdmin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newAdmin.setSuperadmin(false); // Cannot create superadmins via UI for safety
        newAdmin.setStatus(Admin.AdminStatus.ACTIVE);

        if (request.getGroupIds() != null) {
            List<PermissionGroup> groups = permissionGroupRepository.findAllById(request.getGroupIds());
            newAdmin.setPermissionGroups(new HashSet<>(groups));
        }

        if (request.getIndividualPermissionIds() != null) {
            List<Permission> perms = permissionRepository.findAllById(request.getIndividualPermissionIds());
            newAdmin.setIndividualPermissions(new HashSet<>(perms));
        }

        return mapToResponse(adminRepository.save(newAdmin));
    }

    @Transactional
    public AdminResponse updatePermissions(Long targetId, AdminPermissionUpdateRequest request) {
        Admin currentAdmin = getCurrentAdmin();
        Admin targetAdmin = adminRepository.findById(targetId)
                .orElseThrow(() -> AppException.notFound("Admin not found"));

        validateManagementAction(currentAdmin, targetAdmin);

        if (request.getGroupIds() != null) {
            List<PermissionGroup> groups = permissionGroupRepository.findAllById(request.getGroupIds());
            targetAdmin.setPermissionGroups(new HashSet<>(groups));
        }

        if (request.getIndividualPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getIndividualPermissionIds());
            targetAdmin.setIndividualPermissions(new HashSet<>(permissions));
        }

        return mapToResponse(adminRepository.save(targetAdmin));
    }

    @Transactional(readOnly = true)
    public List<AdminResponse> getAllAdmins() {
        return adminRepository.findAllWithDetails().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminResponse updateAdminStatus(Long targetId, Admin.AdminStatus status) {
        Admin currentAdmin = getCurrentAdmin();
        Admin targetAdmin = adminRepository.findById(targetId)
                .orElseThrow(() -> AppException.notFound("Admin not found"));

        validateManagementAction(currentAdmin, targetAdmin);

        // Prevent self-deactivation if superadmin
        if (currentAdmin.getId().equals(targetId) && currentAdmin.isSuperadmin() && status == Admin.AdminStatus.INACTIVE) {
            throw AppException.forbidden("Superadmin cannot deactivate themselves");
        }

        targetAdmin.setStatus(status);
        return mapToResponse(adminRepository.save(targetAdmin));
    }

    /**
     * Logic: 
     * 1. Only Superadmin can manage admins.
     * 2. Superadmins cannot manage other Superadmins.
     */
    private void validateManagementAction(Admin currentAdmin, Admin targetAdmin) {
        if (!currentAdmin.isSuperadmin()) {
            throw AppException.forbidden("Only Superadmins can manage other admin accounts");
        }

        // Check if target is a different superadmin
        if (targetAdmin.isSuperadmin() && !currentAdmin.getId().equals(targetAdmin.getId())) {
            throw AppException.forbidden("Superadmins cannot manage other Superadmin accounts");
        }
    }

    private Admin getCurrentAdmin() {
        Long currentAdminId = SecurityUtils.getCurrentAdminId();
        return adminRepository.findById(currentAdminId)
                .orElseThrow(() -> AppException.unauthorized("Authentication required"));
    }

    private AdminResponse mapToResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .superadmin(admin.isSuperadmin())
                .status(admin.getStatus())
                .permissions(aggregatePermissions(admin))
                .permissionGroupIds(admin.getPermissionGroups().stream().map(PermissionGroup::getId).collect(Collectors.toSet()))
                .individualPermissionIds(admin.getIndividualPermissions().stream().map(Permission::getId).collect(Collectors.toSet()))
                .build();
    }

    private Set<String> aggregatePermissions(Admin admin) {
        Set<String> perms = admin.getPermissionGroups().stream()
                .flatMap(group -> group.getPermissions().stream())
                .map(Permission::getKey)
                .collect(Collectors.toSet());
        
        perms.addAll(admin.getIndividualPermissions().stream()
                .map(Permission::getKey)
                .collect(Collectors.toSet()));
        
        return perms;
    }
}
