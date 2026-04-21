package vuinfinitecart.web.permission.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {
    private Long id;
    private String resource;
    private String action;
    private String description;
    private String key;
}
