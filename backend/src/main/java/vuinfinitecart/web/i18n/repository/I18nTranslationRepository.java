package vuinfinitecart.web.i18n.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vuinfinitecart.web.i18n.entity.I18nTranslation;

import java.util.List;
import java.util.Optional;

public interface I18nTranslationRepository extends JpaRepository<I18nTranslation, Long> {
    List<I18nTranslation> findByLocaleAndNamespace(String locale, String namespace);
    
    Optional<I18nTranslation> findByLocaleAndNamespaceAndKey(String locale, String namespace, String key);
    
    Page<I18nTranslation> findByNamespace(String namespace, Pageable pageable);
    
    Page<I18nTranslation> findByNamespaceAndKeyContaining(String namespace, String key, Pageable pageable);

    @Query("SELECT DISTINCT t.key FROM I18nTranslation t WHERE t.namespace = :namespace AND (:search IS NULL OR LOWER(t.key) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<String> findUniqueKeysByNamespace(String namespace, String search, Pageable pageable);

    List<I18nTranslation> findByNamespaceAndKeyIn(String namespace, List<String> keys);

    @Query("SELECT DISTINCT t.namespace FROM I18nTranslation t")
    List<String> findDistinctNamespaces();

    void deleteByNamespaceAndKey(String namespace, String key);
}
