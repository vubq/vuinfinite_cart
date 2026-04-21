package vuinfinitecart.web.auth.admin;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vuinfinitecart.web.common.response.ApiResponse;

import jakarta.validation.Valid;
import java.util.Arrays;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private static final String REFRESH_TOKEN_COOKIE = "admin_rt";

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletResponse response
    ) {
        var result = adminAuthService.login(request);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Login successful", result.tokenResponse()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractCookie(request, REFRESH_TOKEN_COOKIE);
        var result = adminAuthService.refresh(refreshToken);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(result.tokenResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractCookie(request, REFRESH_TOKEN_COOKIE);
        adminAuthService.logout(refreshToken);
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    // ─── Cookie helpers ───────────────────────────────────────────────────────

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // TODO: set true in production (HTTPS)
        cookie.setPath("/api/auth/admin/refresh");
        cookie.setMaxAge(86400);   // 1 day
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/admin/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
