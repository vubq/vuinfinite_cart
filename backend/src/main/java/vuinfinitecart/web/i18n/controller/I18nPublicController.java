package vuinfinitecart.web.i18n.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vuinfinitecart.web.common.response.ApiResponse;
import vuinfinitecart.web.i18n.service.I18nService;

import java.util.Map;

@RestController
@RequestMapping("/api/i18n")
@RequiredArgsConstructor
public class I18nPublicController {

    private final I18nService i18nService;

    @GetMapping("/{locale}/{namespace}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getTranslations(
            @PathVariable String locale,
            @PathVariable String namespace
    ) {
        Map<String, String> translations = i18nService.getTranslations(locale, namespace);
        return ResponseEntity.ok(ApiResponse.ok(translations));
    }
}
