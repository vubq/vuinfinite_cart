package vuinfinitecart.web.admin.dto;

import lombok.Data;

@Data
public class AdminProfileUpdateRequest {
    private String fullName;
    private String email;
    private String currentPassword;
    private String newPassword;
}
