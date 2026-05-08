package vuinfinitecart.web.i18n.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class I18nTranslationDTO {
    private String key;
    private String namespace;
    @Builder.Default
    private Map<String, String> values = new HashMap<>();
}
