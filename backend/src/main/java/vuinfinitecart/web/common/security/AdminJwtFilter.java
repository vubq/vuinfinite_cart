package vuinfinitecart.web.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Intercepts admin API requests (/api/admin/**) and validates the admin JWT access token.
 * Completely independent from CustomerJwtFilter — separate keys, separate SecurityContext logic.
 */
@Slf4j
@RequiredArgsConstructor
public class AdminJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        extractBearerToken(request).ifPresent(token -> {
            try {
                Claims claims = jwtUtil.validateAdminToken(token);
                if ("admin_access".equals(claims.get("type"))) {
                    Long adminId = Long.valueOf(claims.getSubject());
                    String username = (String) claims.get("username");
                    boolean superadmin = claims.get("superadmin", Boolean.class) != null && claims.get("superadmin", Boolean.class);
                    List<String> perms = claims.get("permissions", List.class);

                    List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    if (superadmin) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
                    }
                    if (perms != null) {
                        perms.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
                    }

                    var auth = new UsernamePasswordAuthenticationToken(
                            new AuthenticatedAdmin(adminId, username, superadmin, perms != null ? new java.util.HashSet<>(perms) : new java.util.HashSet<>()),
                            null,
                            authorities
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException ex) {
                log.debug("Invalid admin JWT: {}", ex.getMessage());
            }
        });
        chain.doFilter(request, response);
    }

    private Optional<String> extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }
        return Optional.empty();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only apply this filter on admin paths
        return !request.getServletPath().startsWith("/api/admin");
    }
}
