package vuinfinitecart.web.admin.dto;

import lombok.Builder;
import lombok.Data;
import vuinfinitecart.web.admin.entity.Admin;
import java.util.Set;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private boolean superadmin;
    private Admin.AdminStatus status;
    private Set<String> permissions;
    private Set<Long> permissionGroupIds;
    private Set<Long> individualPermissionIds;
}
