package vuinfinitecart.web.permission.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class PermissionGroupResponse {
    private Long id;
    private String name;
    private String description;
    private Set<PermissionResponse> permissions;
}
