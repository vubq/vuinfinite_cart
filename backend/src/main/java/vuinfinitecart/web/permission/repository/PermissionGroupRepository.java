package vuinfinitecart.web.permission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuinfinitecart.web.permission.entity.PermissionGroup;

import java.util.List;
import java.util.Optional;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {
    Optional<PermissionGroup> findByName(String name);
    List<PermissionGroup> findByNameContainingIgnoreCase(String name);
}
