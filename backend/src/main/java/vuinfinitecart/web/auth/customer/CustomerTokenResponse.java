package vuinfinitecart.web.auth.customer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerTokenResponse {
    private String accessToken;
    private long expiresIn;
    private String tokenType;

    private Long customerId;
    private String email;
    private String name;
    private String avatarUrl;
}
