package vuinfinitecart.web.i18n.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vuinfinitecart.web.common.audit.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "i18n_languages")
public class I18nLanguage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code; // e.g., 'en', 'vi'

    @Column(nullable = false, length = 100)
    private String name; // e.g., 'Vietnamese', 'English'

    @Column(name = "native_name", length = 100)
    private String nativeName;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}
