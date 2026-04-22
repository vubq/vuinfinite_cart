package vuinfinitecart.web.permission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuinfinitecart.web.admin.repository.AdminRepository;
import vuinfinitecart.web.common.exception.AppException;
import vuinfinitecart.web.permission.dto.PermissionGroupRequest;
import vuinfinitecart.web.permission.dto.PermissionGroupResponse;
import vuinfinitecart.web.permission.dto.PermissionResponse;
import vuinfinitecart.web.permission.entity.Permission;
import vuinfinitecart.web.permission.entity.PermissionGroup;
import vuinfinitecart.web.permission.repository.PermissionGroupRepository;
import vuinfinitecart.web.permission.repository.PermissionRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public PermissionGroupResponse createGroup(PermissionGroupRequest request) {
        if (permissionGroupRepository.findByName(request.getName()).isPresent()) {
            throw AppException.badRequest("Group name already exists");
        }

        PermissionGroup group = new PermissionGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        
        if (request.getPermissionIds() != null) {
            List<Permission> perms = permissionRepository.findAllById(request.getPermissionIds());
            group.setPermissions(new HashSet<>(perms));
        }

        return mapToGroupResponse(permissionGroupRepository.save(group));
    }

    @Transactional
    public PermissionGroupResponse updateGroup(Long id, PermissionGroupRequest request) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Permission group not found"));

        if (!group.getName().equals(request.getName()) && 
            permissionGroupRepository.findByName(request.getName()).isPresent()) {
            throw AppException.badRequest("Group name already exists");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());

        if (request.getPermissionIds() != null) {
            List<Permission> perms = permissionRepository.findAllById(request.getPermissionIds());
            group.setPermissions(new HashSet<>(perms));
        } else {
            group.getPermissions().clear();
        }

        return mapToGroupResponse(permissionGroupRepository.save(group));
    }

    @Transactional
    public void deleteGroup(Long id) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Permission group not found"));

        // Side effect: Revoke from all admins
        adminRepository.removeGroupAssociations(id);
        
        permissionGroupRepository.delete(group);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermissionGroupResponse> getAllGroups(String keyword) {
        List<PermissionGroup> groups;
        if (keyword != null && !keyword.isBlank()) {
            groups = permissionGroupRepository.findByNameContainingIgnoreCase(keyword);
        } else {
            groups = permissionGroupRepository.findAll();
        }
        return groups.stream()
                .map(this::mapToGroupResponse)
                .collect(Collectors.toList());
    }

    private PermissionResponse mapToPermissionResponse(Permission p) {
        return PermissionResponse.builder()
                .id(p.getId())
                .resource(p.getResource())
                .action(p.getAction())
                .description(p.getDescription())
                .key(p.getKey())
                .build();
    }

    private PermissionGroupResponse mapToGroupResponse(PermissionGroup g) {
        return PermissionGroupResponse.builder()
                .id(g.getId())
                .name(g.getName())
                .description(g.getDescription())
                .permissions(g.getPermissions().stream()
                        .map(this::mapToPermissionResponse)
                        .collect(Collectors.toSet()))
                .build();
    }
}
