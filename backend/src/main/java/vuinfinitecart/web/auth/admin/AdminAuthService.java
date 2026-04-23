package vuinfinitecart.web.auth.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vuinfinitecart.web.admin.entity.Admin;
import vuinfinitecart.web.admin.repository.AdminRepository;
import vuinfinitecart.web.common.exception.AppException;
import vuinfinitecart.web.common.security.JwtUtil;
import vuinfinitecart.web.common.security.RedisTokenStore;
import vuinfinitecart.web.config.JwtProperties;
import vuinfinitecart.web.permission.entity.Permission;
import vuinfinitecart.web.permission.entity.PermissionGroup;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTokenStore tokenStore;
    private final JwtProperties jwtProperties;

    public LoginResult login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> AppException.unauthorized("Invalid username or password"));

        if (admin.getStatus() == Admin.AdminStatus.INACTIVE) {
            throw AppException.forbidden("Account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw AppException.unauthorized("Invalid username or password");
        }

        // Update last login
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);

        return buildLoginResult(admin);
    }

    @Transactional(readOnly = true)
    public LoginResult refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw AppException.unauthorized("Refresh token is missing");
        }

        var claims = jwtUtil.validateAdminToken(refreshToken);
        Long adminId = Long.valueOf(claims.getSubject());

        if (!tokenStore.isAdminRefreshTokenValid(adminId, refreshToken)) {
            throw AppException.unauthorized("Refresh token is invalid or expired");
        }

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> AppException.unauthorized("Admin not found"));

        if (admin.getStatus() == Admin.AdminStatus.INACTIVE) {
            tokenStore.deleteAdminRefreshToken(adminId);
            throw AppException.forbidden("Account is inactive");
        }

        return buildLoginResult(admin);
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        try {
            var claims = jwtUtil.validateAdminToken(refreshToken);
            tokenStore.deleteAdminRefreshToken(Long.valueOf(claims.getSubject()));
        } catch (Exception ignored) {
            // Token already invalid — logout is idempotent
        }
    }

    private LoginResult buildLoginResult(Admin admin) {
        Set<String> permissions = aggregatePermissions(admin);
        String accessToken  = jwtUtil.generateAdminAccessToken(admin.getId(), admin.getUsername(), admin.isSuperadmin(), permissions);
        String refreshToken = jwtUtil.generateAdminRefreshToken(admin.getId());

        tokenStore.saveAdminRefreshToken(
                admin.getId(),
                refreshToken,
                Duration.ofMillis(jwtProperties.getAdminRefreshTokenExpiryMs())
        );

        var tokenResponse = AuthTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(jwtProperties.getAdminAccessTokenExpiryMs() / 1000)
                .tokenType("Bearer")
                .adminId(admin.getId())
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .avatarUrl(admin.getAvatarUrl())
                .superadmin(admin.isSuperadmin())
                .permissions(permissions)
                .build();

        return new LoginResult(tokenResponse, refreshToken);
    }

    private Set<String> aggregatePermissions(Admin admin) {
        // Collect from groups
        Set<String> perms = admin.getPermissionGroups().stream()
                .flatMap(group -> group.getPermissions().stream())
                .map(Permission::getKey)
                .collect(Collectors.toSet());
        
        // Add individual overrides
        perms.addAll(
            admin.getIndividualPermissions().stream()
                .map(Permission::getKey)
                .collect(Collectors.toSet())
        );
        
        return perms;
    }

    public record LoginResult(AuthTokenResponse tokenResponse, String refreshToken) {}
}
