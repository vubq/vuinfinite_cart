package vuinfinitecart.web.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vuinfinitecart.web.admin.entity.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {
    @Query("SELECT DISTINCT a FROM Admin a " +
           "LEFT JOIN FETCH a.permissionGroups pg " +
           "LEFT JOIN FETCH pg.permissions " +
           "LEFT JOIN FETCH a.individualPermissions " +
           "ORDER BY a.id ASC")
    java.util.List<Admin> findAllWithDetails();

    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT a FROM Admin a " +
           "LEFT JOIN FETCH a.permissionGroups pg " +
           "LEFT JOIN FETCH pg.permissions " +
           "LEFT JOIN FETCH a.individualPermissions " +
           "WHERE a.id = :id")
    Optional<Admin> findByIdWithPermissions(Long id);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "DELETE FROM admin_permission_groups WHERE group_id = :groupId", nativeQuery = true)
    void removeGroupAssociations(Long groupId);
}
