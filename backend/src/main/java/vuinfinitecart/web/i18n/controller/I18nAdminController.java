package vuinfinitecart.web.i18n.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vuinfinitecart.web.common.response.ApiResponse;
import vuinfinitecart.web.common.response.PageResponse;
import vuinfinitecart.web.i18n.entity.I18nLanguage;
import vuinfinitecart.web.i18n.entity.I18nTranslation;
import vuinfinitecart.web.i18n.service.I18nLanguageService;
import vuinfinitecart.web.i18n.service.I18nService;
import vuinfinitecart.web.i18n.dto.I18nTranslationDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/i18n")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class I18nAdminController {

    private final I18nLanguageService languageService;
    private final I18nService translationService;

    // --- Language Management ---

    @GetMapping("/languages")
    public ApiResponse<List<I18nLanguage>> getAllLanguages() {
        return ApiResponse.ok(languageService.getAllLanguages());
    }

    @PostMapping("/languages")
    public ApiResponse<I18nLanguage> createLanguage(@RequestBody I18nLanguage language) {
        return ApiResponse.ok("Language created successfully", languageService.createLanguage(language));
    }

    @PutMapping("/languages/{id}")
    public ApiResponse<I18nLanguage> updateLanguage(@PathVariable Long id, @RequestBody I18nLanguage language) {
        return ApiResponse.ok("Language updated successfully", languageService.updateLanguage(id, language));
    }

    @PatchMapping("/languages/{id}/default")
    public ApiResponse<Void> setDefault(@PathVariable Long id) {
        languageService.setDefault(id);
        return ApiResponse.ok("Default language updated successfully");
    }

    @PatchMapping("/languages/{id}/toggle-active")
    public ApiResponse<Void> toggleActive(@PathVariable Long id) {
        languageService.toggleActive(id);
        return ApiResponse.ok("Language status updated successfully");
    }

    // --- Translation Management ---

    @GetMapping("/translations")
    public ApiResponse<PageResponse<I18nTranslationDTO>> getTranslations(
            @RequestParam(required = false) String namespace,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ApiResponse.ok(translationService.getTranslationsForAdmin(namespace, search, pageable));
    }

    @GetMapping("/namespaces")
    public ApiResponse<List<String>> getNamespaces() {
        return ApiResponse.ok(translationService.getAllNamespaces());
    }

    @PutMapping("/translations")
    public ApiResponse<I18nTranslation> upsertTranslation(@RequestBody I18nTranslation translation) {
        return ApiResponse.ok("Translation updated successfully", translationService.upsertTranslation(translation));
    }

    @DeleteMapping("/translations/{namespace}/{key}")
    public ApiResponse<Void> deleteTranslationKey(@PathVariable String namespace, @PathVariable String key) {
        translationService.deleteTranslationKey(namespace, key);
        return ApiResponse.ok("Translation key deleted successfully");
    }
}
