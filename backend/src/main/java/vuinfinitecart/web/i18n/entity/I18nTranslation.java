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
@Table(name = "i18n_translations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"locale", "namespace", "translation_key"}))
public class I18nTranslation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String locale; // e.g., 'en', 'vi'

    @Column(nullable = false, length = 100)
    private String namespace; // e.g., 'common', 'product'

    @Column(name = "translation_key", nullable = false, length = 500)
    private String key;

    @Column(name = "translation_value", nullable = false, columnDefinition = "TEXT")
    private String value;
}
