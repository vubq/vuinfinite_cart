package vuinfinitecart.web.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuinfinitecart.web.customer.entity.CustomerOAuth;

import java.util.Optional;

public interface CustomerOAuthRepository extends JpaRepository<CustomerOAuth, Long> {
    Optional<CustomerOAuth> findByProviderAndProviderId(String provider, String providerId);
}
