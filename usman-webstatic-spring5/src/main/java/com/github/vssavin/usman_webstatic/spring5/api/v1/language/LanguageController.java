package com.github.vssavin.usman_webstatic.spring5.api.v1.language;

import com.github.vssavin.usman_webstatic_core.UsmanLocaleConfig;
import com.github.vssavin.usman_webstatic_core.UsmanWebstaticConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private final UsmanWebstaticConfigurer webstaticConfigurer;

    LanguageController(UsmanWebstaticConfigurer webstaticConfigurer) {
        this.webstaticConfigurer = webstaticConfigurer;
    }

    @GetMapping(value = "/usman/v1/languages", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, String>> getLanguage() {
        return new ResponseEntity<>(UsmanLocaleConfig.getAvailableLanguages(), HttpStatus.OK);
    }

    @GetMapping(value = "/usman/v1/languages/default", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getDefaultLanguage() {
        String defaultLanguage = UsmanLocaleConfig.getAvailableLanguages()
            .get(webstaticConfigurer.getDefaultLanguage());
        if (defaultLanguage == null) {
            defaultLanguage = "";
        }
        else {
            defaultLanguage = webstaticConfigurer.getDefaultLanguage();
        }

        return new ResponseEntity<>("\"" + defaultLanguage + "\"", HttpStatus.OK);
    }

}
