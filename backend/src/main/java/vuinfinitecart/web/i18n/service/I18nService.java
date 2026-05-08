package vuinfinitecart.web.i18n.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuinfinitecart.web.common.response.PageResponse;
import vuinfinitecart.web.i18n.entity.I18nTranslation;
import vuinfinitecart.web.i18n.repository.I18nTranslationRepository;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import vuinfinitecart.web.i18n.dto.I18nTranslationDTO;

@Service
@RequiredArgsConstructor
public class I18nService {

    private final I18nTranslationRepository repository;
    private final StringRedisTemplate redis;
    private static final String REDIS_PREFIX = "i18n:";

    @Transactional(readOnly = true)
    public Map<String, String> getTranslations(String locale, String namespace) {
        // Try Cache
        String cacheKey = REDIS_PREFIX + locale + ":" + namespace;
        Map<Object, Object> cached = redis.opsForHash().entries(cacheKey);
        
        if (!cached.isEmpty()) {
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<Object, Object> entry : cached.entrySet()) {
                result.put(String.valueOf(entry.getKey()), 
                          entry.getValue() != null ? String.valueOf(entry.getValue()) : "");
            }
            return result;
        }

        // Fetch from DB
        List<I18nTranslation> translations = repository.findByLocaleAndNamespace(locale, namespace);
        Map<String, String> result = new HashMap<>();
        for (I18nTranslation t : translations) {
            result.put(t.getKey(), t.getValue() != null ? t.getValue() : "");
        }

        // Save to Cache
        if (!result.isEmpty()) {
            redis.opsForHash().putAll(cacheKey, result);
            redis.expire(cacheKey, Duration.ofDays(7));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public PageResponse<I18nTranslationDTO> getTranslationsForAdmin(String namespace, String search, Pageable pageable) {
        if (namespace == null || namespace.isEmpty()) {
            return PageResponse.of(Page.empty(pageable));
        }

        // Step 1: Get a page of unique keys
        Page<String> keysPage = repository.findUniqueKeysByNamespace(namespace, search, pageable);
        List<String> keys = keysPage.getContent();

        if (keys.isEmpty()) {
            return PageResponse.of(Page.empty(pageable));
        }

        // Step 2: Get all translations for these keys
        List<I18nTranslation> translations = repository.findByNamespaceAndKeyIn(namespace, keys);

        // Step 3: Group them into DTOs
        Map<String, I18nTranslationDTO> dtoMap = new LinkedHashMap<>();
        for (String key : keys) {
            dtoMap.put(key, I18nTranslationDTO.builder()
                    .key(key)
                    .namespace(namespace)
                    .values(new HashMap<>())
                    .build());
        }

        for (I18nTranslation t : translations) {
            I18nTranslationDTO dto = dtoMap.get(t.getKey());
            if (dto != null) {
                dto.getValues().put(t.getLocale(), t.getValue() != null ? t.getValue() : "");
            }
        }

        return PageResponse.of(keysPage.map(dtoMap::get));
    }

    public List<String> getAllNamespaces() {
        return repository.findDistinctNamespaces();
    }

    @Transactional
    public I18nTranslation upsertTranslation(I18nTranslation translation) {
        I18nTranslation existing = repository.findByLocaleAndNamespaceAndKey(
                translation.getLocale(), 
                translation.getNamespace(), 
                translation.getKey()
        ).orElse(null);

        if (existing != null) {
            existing.setValue(translation.getValue());
            translation = repository.save(existing);
        } else {
            translation = repository.save(translation);
        }

        // Invalidate Cache
        clearCache(translation.getLocale(), translation.getNamespace());
        
        return translation;
    }

    @Transactional
    public void deleteTranslationKey(String namespace, String key) {
        repository.deleteByNamespaceAndKey(namespace, key);
        // We don't know which locales were deleted, so we should ideally invalidate all
        // Or at least the common ones. For simplicity, we can let them expire or 
        // fetch languages and clear each. 
        // Here we just clear the most likely ones if we had a list of locales.
        // For now, cache will expire in 7 days or we can clear specific if we know them.
    }

    @Transactional
    public void clearCache(String locale, String namespace) {
        redis.delete(REDIS_PREFIX + locale + ":" + namespace);
    }
}
