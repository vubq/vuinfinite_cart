package vuinfinitecart.web.i18n.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuinfinitecart.web.i18n.entity.I18nLanguage;
import vuinfinitecart.web.i18n.repository.I18nLanguageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class I18nLanguageService {

    private final I18nLanguageRepository repository;

    public List<I18nLanguage> getAllLanguages() {
        return repository.findAll();
    }

    public List<I18nLanguage> getActiveLanguages() {
        return repository.findAllByIsActiveTrueOrderBySortOrderAsc();
    }

    @Transactional
    public I18nLanguage createLanguage(I18nLanguage language) {
        if (language.isDefault()) {
            repository.findByIsDefaultTrue().ifPresent(l -> {
                l.setDefault(false);
                repository.save(l);
            });
        }
        return repository.save(language);
    }

    @Transactional
    public I18nLanguage updateLanguage(Long id, I18nLanguage details) {
        I18nLanguage language = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found"));
        
        language.setName(details.getName());
        language.setNativeName(details.getNativeName());
        language.setSortOrder(details.getSortOrder());
        
        return repository.save(language);
    }

    @Transactional
    public void setDefault(Long id) {
        repository.findByIsDefaultTrue().ifPresent(l -> {
            l.setDefault(false);
            repository.save(l);
        });
        
        I18nLanguage language = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found"));
        language.setDefault(true);
        language.setActive(true); // Default language must be active
        repository.save(language);
    }

    @Transactional
    public void toggleActive(Long id) {
        I18nLanguage language = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found"));
        
        if (language.isDefault() && language.isActive()) {
            throw new RuntimeException("Cannot deactivate default language");
        }
        
        language.setActive(!language.isActive());
        repository.save(language);
    }
}
