package vuinfinitecart.web.i18n.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuinfinitecart.web.i18n.entity.I18nTranslation;

import java.util.List;

public interface I18nTranslationRepository extends JpaRepository<I18nTranslation, Long> {
    List<I18nTranslation> findByLocaleAndNamespace(String locale, String namespace);
}
