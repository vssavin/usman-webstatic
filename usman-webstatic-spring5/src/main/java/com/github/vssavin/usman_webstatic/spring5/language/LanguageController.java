package com.github.vssavin.usman_webstatic.spring5.language;

import com.github.vssavin.usman_webstatic_core.UsmanLocaleConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Provides information about supported languages.
 *
 * @author vssavin on 18.12.2023.
 */
@RestController
class LanguageController {

    @GetMapping("/usman/languages")
    ResponseEntity<Map<String, String>> getLanguage() {
        return new ResponseEntity<>(UsmanLocaleConfig.getAvailableLanguages(), HttpStatus.OK);
    }

}
