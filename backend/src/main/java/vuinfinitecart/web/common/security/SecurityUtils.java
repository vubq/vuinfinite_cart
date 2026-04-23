package vuinfinitecart.web.common.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {

    public static Optional<AuthenticatedAdmin> getCurrentAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedAdmin admin) {
            return Optional.of(admin);
        }
        return Optional.empty();
    }

    public static Long getCurrentAdminId() {
        return getCurrentAdmin().map(AuthenticatedAdmin::getAdminId).orElse(null);
    }

    public static Optional<AuthenticatedCustomer> getCurrentCustomer() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedCustomer customer) {
            return Optional.of(customer);
        }
        return Optional.empty();
    }

    public static Long getCurrentCustomerId() {
        return getCurrentCustomer().map(AuthenticatedCustomer::getCustomerId).orElse(null);
    }
}
