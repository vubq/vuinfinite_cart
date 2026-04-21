package vuinfinitecart.web.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Whitelist-based refresh token store in Redis.
 * Only tokens explicitly stored here are considered valid.
 * Enables instant revocation (logout, ban, password change).
 */
@Component
@RequiredArgsConstructor
public class RedisTokenStore {

    private static final String CUSTOMER_RT_PREFIX = "customer_rt:";
    private static final String ADMIN_RT_PREFIX    = "admin_rt:";

    private final StringRedisTemplate redis;

    // ─── Customer ─────────────────────────────────────────────────────────────

    public void saveCustomerRefreshToken(Long customerId, String token, Duration ttl) {
        redis.opsForValue().set(CUSTOMER_RT_PREFIX + customerId, token, ttl);
    }

    public Optional<String> getCustomerRefreshToken(Long customerId) {
        return Optional.ofNullable(redis.opsForValue().get(CUSTOMER_RT_PREFIX + customerId));
    }

    public boolean isCustomerRefreshTokenValid(Long customerId, String token) {
        return getCustomerRefreshToken(customerId)
                .map(stored -> stored.equals(token))
                .orElse(false);
    }

    public void deleteCustomerRefreshToken(Long customerId) {
        redis.delete(CUSTOMER_RT_PREFIX + customerId);
    }

    // ─── Admin ────────────────────────────────────────────────────────────────

    public void saveAdminRefreshToken(Long adminId, String token, Duration ttl) {
        redis.opsForValue().set(ADMIN_RT_PREFIX + adminId, token, ttl);
    }

    public Optional<String> getAdminRefreshToken(Long adminId) {
        return Optional.ofNullable(redis.opsForValue().get(ADMIN_RT_PREFIX + adminId));
    }

    public boolean isAdminRefreshTokenValid(Long adminId, String token) {
        return getAdminRefreshToken(adminId)
                .map(stored -> stored.equals(token))
                .orElse(false);
    }

    public void deleteAdminRefreshToken(Long adminId) {
        redis.delete(ADMIN_RT_PREFIX + adminId);
    }
}
