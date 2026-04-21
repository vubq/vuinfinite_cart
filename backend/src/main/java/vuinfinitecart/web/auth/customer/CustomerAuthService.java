package vuinfinitecart.web.auth.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuinfinitecart.web.common.exception.AppException;
import vuinfinitecart.web.common.security.JwtUtil;
import vuinfinitecart.web.common.security.RedisTokenStore;
import vuinfinitecart.web.config.JwtProperties;
import vuinfinitecart.web.customer.entity.Customer;
import vuinfinitecart.web.customer.entity.CustomerOAuth;
import vuinfinitecart.web.customer.repository.CustomerOAuthRepository;
import vuinfinitecart.web.customer.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final CustomerOAuthRepository customerOAuthRepository;
    private final JwtUtil jwtUtil;
    private final RedisTokenStore tokenStore;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResult register(CustomerRegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email already exists");
        }

        Customer customer = new Customer();
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setName(request.getName());
        customerRepository.save(customer);

        return buildLoginResult(customer);
    }

    public LoginResult login(CustomerLoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> AppException.unauthorized("Invalid email or password"));

        if (customer.getStatus() == Customer.CustomerStatus.BANNED) {
            throw AppException.forbidden("Account has been banned");
        }
        
        if (customer.getPasswordHash() == null) {
            throw AppException.unauthorized("Account was created via OAuth. Please log in with Google/Facebook.");
        }

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw AppException.unauthorized("Invalid email or password");
        }

        return buildLoginResult(customer);
    }

    /**
     * Called after successful OAuth2 callback.
     * Finds or creates customer, then issues tokens.
     */
    @Transactional
    public LoginResult handleOAuthLogin(String provider, String providerId,
                                        String email, String name, String avatarUrl) {
        // Try to find existing OAuth link
        var oauthLink = customerOAuthRepository.findByProviderAndProviderId(provider, providerId);

        Customer customer;
        if (oauthLink.isPresent()) {
            customer = oauthLink.get().getCustomer();
        } else {
            // Find by email or create new customer
            customer = customerRepository.findByEmail(email).orElseGet(() -> {
                var newCustomer = new Customer();
                newCustomer.setEmail(email);
                newCustomer.setName(name);
                newCustomer.setAvatarUrl(avatarUrl);
                return customerRepository.save(newCustomer);
            });

            // Create OAuth link
            var link = new CustomerOAuth();
            link.setCustomer(customer);
            link.setProvider(provider);
            link.setProviderId(providerId);
            customerOAuthRepository.save(link);
        }

        if (customer.getStatus() == Customer.CustomerStatus.BANNED) {
            throw AppException.forbidden("Account has been banned");
        }

        return buildLoginResult(customer);
    }

    public LoginResult refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw AppException.unauthorized("Refresh token is missing");
        }

        var claims = jwtUtil.validateCustomerToken(refreshToken);
        Long customerId = Long.valueOf(claims.getSubject());

        if (!tokenStore.isCustomerRefreshTokenValid(customerId, refreshToken)) {
            throw AppException.unauthorized("Refresh token is invalid or expired");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> AppException.unauthorized("Customer not found"));

        if (customer.getStatus() == Customer.CustomerStatus.BANNED) {
            tokenStore.deleteCustomerRefreshToken(customerId);
            throw AppException.forbidden("Account has been banned");
        }

        return buildLoginResult(customer);
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        try {
            var claims = jwtUtil.validateCustomerToken(refreshToken);
            tokenStore.deleteCustomerRefreshToken(Long.valueOf(claims.getSubject()));
        } catch (Exception ignored) {
            // Idempotent logout
        }
    }

    private LoginResult buildLoginResult(Customer customer) {
        String accessToken  = jwtUtil.generateCustomerAccessToken(customer.getId(), customer.getEmail());
        String refreshToken = jwtUtil.generateCustomerRefreshToken(customer.getId());

        tokenStore.saveCustomerRefreshToken(
                customer.getId(),
                refreshToken,
                Duration.ofMillis(jwtProperties.getCustomerRefreshTokenExpiryMs())
        );

        var tokenResponse = CustomerTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(jwtProperties.getCustomerAccessTokenExpiryMs() / 1000)
                .tokenType("Bearer")
                .customerId(customer.getId())
                .email(customer.getEmail())
                .name(customer.getName())
                .avatarUrl(customer.getAvatarUrl())
                .build();

        return new LoginResult(tokenResponse, refreshToken);
    }

    public record LoginResult(CustomerTokenResponse tokenResponse, String refreshToken) {}
}
