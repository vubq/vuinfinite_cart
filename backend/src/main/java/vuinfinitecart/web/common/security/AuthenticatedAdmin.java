package vuinfinitecart.web.common.security;

import lombok.Getter;

/**
 * Represents an authenticated admin in the SecurityContext.
 */
@Getter
public class AuthenticatedAdmin {
    private final Long adminId;
    private final String username;
    private final boolean superadmin;
    private final java.util.Set<String> permissions;

    public AuthenticatedAdmin(Long adminId, String username, boolean superadmin, java.util.Set<String> permissions) {
        this.adminId = adminId;
        this.username = username;
        this.superadmin = superadmin;
        this.permissions = permissions;
    }
}
