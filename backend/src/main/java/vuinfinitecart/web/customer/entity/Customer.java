package vuinfinitecart.web.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vuinfinitecart.web.common.audit.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    public enum CustomerStatus { ACTIVE, BANNED, UNVERIFIED }
}
