package vuinfinitecart.web.auth.customer;

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
@RequestMapping("/api/auth/customer")
@RequiredArgsConstructor
public class CustomerAuthController {

    private static final String REFRESH_TOKEN_COOKIE = "customer_rt";
    private final CustomerAuthService customerAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerAuthService.LoginResult>> register(
            @Valid @RequestBody CustomerRegisterRequest request,
            HttpServletResponse response
    ) {
        var result = customerAuthService.register(request);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Registration successful", result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<CustomerTokenResponse>> login(
            @Valid @RequestBody CustomerLoginRequest request,
            HttpServletResponse response
    ) {
        var result = customerAuthService.login(request);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Login successful", result.tokenResponse()));
    }

    /**
     * POST /api/auth/customer/refresh
     * Called by frontend when access token expires.
     * Reads refresh token from HttpOnly cookie.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<CustomerTokenResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractCookie(request, REFRESH_TOKEN_COOKIE);
        var result = customerAuthService.refresh(refreshToken);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(result.tokenResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractCookie(request, REFRESH_TOKEN_COOKIE);
        customerAuthService.logout(refreshToken);
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    // ─── Cookie helpers ───────────────────────────────────────────────────────

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // Set true in production
        cookie.setPath("/api/auth/customer/refresh");
        cookie.setMaxAge(604800);  // 7 days
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/customer/refresh");
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
