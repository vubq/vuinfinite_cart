package vuinfinitecart.web.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class AdminCreateRequest {
    @NotBlank
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    @Size(min = 6)
    private String password;

    private Set<Long> groupIds;
    private Set<Long> individualPermissionIds;
}
