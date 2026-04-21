package vuinfinitecart.web.permission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuinfinitecart.web.permission.entity.Permission;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByResourceAndAction(String resource, String action);
}
