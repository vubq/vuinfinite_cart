package vuinfinitecart.web.auth.admin;

import lombok.Builder;
import lombok.Getter;
import java.util.Set;

@Getter
@Builder
public class AuthTokenResponse {
    private String accessToken;
    private long expiresIn;       // seconds
    private String tokenType;     // Bearer

    // Admin info
    private Long adminId;
    private String username;
    private String fullName;
    private boolean superadmin;
    private Set<String> permissions;
}
