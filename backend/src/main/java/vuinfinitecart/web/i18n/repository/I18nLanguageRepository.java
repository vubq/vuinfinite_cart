package vuinfinitecart.web.i18n.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vuinfinitecart.web.i18n.entity.I18nLanguage;

import java.util.List;
import java.util.Optional;

public interface I18nLanguageRepository extends JpaRepository<I18nLanguage, Long> {
    List<I18nLanguage> findAllByIsActiveTrueOrderBySortOrderAsc();
    Optional<I18nLanguage> findByCode(String code);
    Optional<I18nLanguage> findByIsDefaultTrue();
}
