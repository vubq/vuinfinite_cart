package vuinfinitecart.web.i18n.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vuinfinitecart.web.i18n.entity.I18nTranslation;
import vuinfinitecart.web.i18n.repository.I18nTranslationRepository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            return cached.entrySet().stream()
                    .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue())
                    ));
        }

        // Fetch from DB
        List<I18nTranslation> translations = repository.findByLocaleAndNamespace(locale, namespace);
        Map<String, String> result = translations.stream()
                .collect(Collectors.toMap(I18nTranslation::getKey, I18nTranslation::getValue));

        // Save to Cache (Async or fire-and-forget in real life, done synchronously here for simplicity)
        if (!result.isEmpty()) {
            redis.opsForHash().putAll(cacheKey, result);
            redis.expire(cacheKey, Duration.ofDays(7));
        }

        return result;
    }

    @Transactional
    public void clearCache(String locale, String namespace) {
        redis.delete(REDIS_PREFIX + locale + ":" + namespace);
    }
}
