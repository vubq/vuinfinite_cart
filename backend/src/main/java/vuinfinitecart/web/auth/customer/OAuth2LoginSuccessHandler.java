package vuinfinitecart.web.auth.customer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Called after successful OAuth2 login (Google, Facebook).
 * Extracts user info from OAuth2User, calls CustomerAuthService,
 * sets the refresh token as HttpOnly cookie, then redirects to frontend.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String REFRESH_TOKEN_COOKIE = "customer_rt";
    private static final String FRONTEND_REDIRECT    = "http://localhost:5173/auth/callback";

    private final CustomerAuthService customerAuthService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();
        String provider = token.getAuthorizedClientRegistrationId();  // google | facebook

        String providerId = oAuth2User.getAttribute("sub") != null
                ? oAuth2User.getAttribute("sub")    // Google
                : oAuth2User.getAttribute("id");    // Facebook

        String email     = oAuth2User.getAttribute("email");
        String name      = oAuth2User.getAttribute("name");
        String avatarUrl = extractAvatar(oAuth2User, provider);

        try {
            var result = customerAuthService.handleOAuthLogin(provider, providerId, email, name, avatarUrl);

            // Set refresh token as HttpOnly cookie
            Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, result.refreshToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production
            cookie.setPath("/api/auth/customer/refresh");
            cookie.setMaxAge(604800);
            cookie.setAttribute("SameSite", "Strict");
            response.addCookie(cookie);

            // Redirect to frontend with access token as query param (short-lived, frontend handles it)
            String redirectUrl = FRONTEND_REDIRECT + "?token=" + result.tokenResponse().getAccessToken();
            response.sendRedirect(redirectUrl);

        } catch (Exception ex) {
            log.error("OAuth2 login failed for provider {}: {}", provider, ex.getMessage());
            response.sendRedirect("http://localhost:5173/login?error=oauth_failed");
        }
    }

    private String extractAvatar(OAuth2User user, String provider) {
        if ("google".equals(provider)) {
            return user.getAttribute("picture");
        }
        // Facebook returns nested picture object — simplified here
        return null;
    }
}
