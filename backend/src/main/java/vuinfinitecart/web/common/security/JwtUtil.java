package vuinfinitecart.web.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vuinfinitecart.web.config.JwtProperties;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private final JwtProperties props;

    public JwtUtil(JwtProperties props) {
        this.props = props;
    }

    // ─── Customer tokens ──────────────────────────────────────────────────────

    public String generateCustomerAccessToken(Long customerId, String email) {
        return buildToken(Map.of("sub", String.valueOf(customerId), "email", email, "type", "customer_access"),
                props.getCustomerAccessTokenExpiryMs(), customerKey());
    }

    public String generateCustomerRefreshToken(Long customerId) {
        return buildToken(Map.of("sub", String.valueOf(customerId), "type", "customer_refresh"),
                props.getCustomerRefreshTokenExpiryMs(), customerKey());
    }

    public Claims validateCustomerToken(String token) {
        return parseClaims(token, customerKey());
    }

    // ─── Admin tokens ─────────────────────────────────────────────────────────

    public String generateAdminAccessToken(Long adminId, String username, boolean superadmin, java.util.Collection<String> permissions) {
        return buildToken(Map.of(
                "sub", String.valueOf(adminId),
                "username", username,
                "superadmin", superadmin,
                "type", "admin_access",
                "permissions", permissions
            ),
            props.getAdminAccessTokenExpiryMs(), adminKey());
    }

    public String generateAdminRefreshToken(Long adminId) {
        return buildToken(Map.of("sub", String.valueOf(adminId), "type", "admin_refresh"),
                props.getAdminRefreshTokenExpiryMs(), adminKey());
    }

    public Claims validateAdminToken(String token) {
        return parseClaims(token, adminKey());
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String buildToken(Map<String, Object> claims, long expiryMs, SecretKey key) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryMs))
                .signWith(key)
                .compact();
    }

    private Claims parseClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey customerKey() {
        return Keys.hmacShaKeyFor(props.getCustomerSecret().getBytes());
    }

    private SecretKey adminKey() {
        return Keys.hmacShaKeyFor(props.getAdminSecret().getBytes());
    }
}
