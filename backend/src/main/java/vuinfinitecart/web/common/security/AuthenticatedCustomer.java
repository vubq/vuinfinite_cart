package vuinfinitecart.web.common.security;

import lombok.Getter;

/**
 * Represents an authenticated customer in the SecurityContext.
 */
@Getter
public class AuthenticatedCustomer {
    private final Long customerId;
    private final String email;

    public AuthenticatedCustomer(Long customerId, String email) {
        this.customerId = customerId;
        this.email = email;
    }
}
