package com.github.vssavin.usman_webstatic_core;

/**
 * Provides data about supported languages for thymeleaf templates.
 *
 * @author vssavin on 19.12.2023.
 */
public class UsmanLanguageUtils {

    public String getLanguageSpan(String localeString) {
        String availableLocale = UsmanLocaleConfig.getAvailableLocale(localeString.toLowerCase());
        String languageName = UsmanLocaleConfig.getAvailableLanguageName(availableLocale, true);
        String flagIconName = UsmanLocaleConfig.getFlagName(availableLocale, true);
        return String.format("<span class=\"flag-icon %s\"></span> %s", flagIconName, languageName);
    }

    public String getLanguageText(String localeString) {
        return UsmanLocaleConfig.getAvailableLanguageName(localeString, true);
    }

}
