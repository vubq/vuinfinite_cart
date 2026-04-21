package vuinfinitecart.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String customerSecret;
    private long customerAccessTokenExpiryMs;
    private long customerRefreshTokenExpiryMs;

    private String adminSecret;
    private long adminAccessTokenExpiryMs;
    private long adminRefreshTokenExpiryMs;
}
