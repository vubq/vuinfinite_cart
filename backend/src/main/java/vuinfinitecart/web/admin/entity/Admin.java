package vuinfinitecart.web.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vuinfinitecart.web.common.audit.BaseEntity;
import vuinfinitecart.web.permission.entity.Permission;
import vuinfinitecart.web.permission.entity.PermissionGroup;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "admins")
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_superadmin", nullable = false)
    private boolean superadmin = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminStatus status = AdminStatus.ACTIVE;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "admin_permission_groups",
        joinColumns = @JoinColumn(name = "admin_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<PermissionGroup> permissionGroups = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "admin_permissions",
        joinColumns = @JoinColumn(name = "admin_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> individualPermissions = new HashSet<>();

    public enum AdminStatus { ACTIVE, INACTIVE }
}
