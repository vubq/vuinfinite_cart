package vuinfinitecart.web.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Intercepts customer API requests and validates the customer JWT access token.
 * Token is extracted from the Authorization header (Bearer).
 * Operates independently of AdminJwtFilter — both can be active in same browser session.
 */
@Slf4j
@RequiredArgsConstructor
public class CustomerJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final vuinfinitecart.web.customer.repository.CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        extractBearerToken(request).ifPresent(token -> {
            try {
                Claims claims = jwtUtil.validateCustomerToken(token);
                if ("customer_access".equals(claims.get("type"))) {
                    Long customerId = Long.valueOf(claims.getSubject());
                    String email = (String) claims.get("email");

                    // Immediate Banning Check
                    var customer = customerRepository.findById(customerId);
                    if (customer.isPresent() && customer.get().getStatus() == vuinfinitecart.web.customer.entity.Customer.CustomerStatus.BANNED) {
                        log.debug("Banned customer tried to access: {}", email);
                        return; // Fail silently, SecurityContext remains empty -> 401
                    }

                    var auth = new UsernamePasswordAuthenticationToken(
                            new AuthenticatedCustomer(customerId, email),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException ex) {
                log.debug("Invalid customer JWT: {}", ex.getMessage());
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
        String path = request.getServletPath();
        // Skip filter for admin and public auth endpoints
        return path.startsWith("/api/auth/admin") || path.startsWith("/api/auth/customer/oauth2");
    }
}
