package vuinfinitecart.web.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
}
