package com.github.vssavin.usman_webstatic_core;

import com.github.vssavin.usmancore.config.OAuth2Config;
import com.github.vssavin.usmancore.config.PermissionPathsContainer;
import com.github.vssavin.usmancore.config.UsmanConfigurer;
import com.github.vssavin.usmancore.config.UsmanUrlsConfigurer;

import java.util.List;

/**
 * Configures webstatic user management params.
 *
 * @author vssavin on 27.12.2023.
 */
public class UsmanWebstaticConfigurer extends UsmanConfigurer {

    private String defaultLanguage = "ru";

    private boolean configured = false;

    public UsmanWebstaticConfigurer() {
    }

    public UsmanWebstaticConfigurer(UsmanUrlsConfigurer urlsConfigurer, OAuth2Config oAuth2Config,
            List<PermissionPathsContainer> permissionPathsContainerList) {
        super(urlsConfigurer, oAuth2Config, permissionPathsContainerList);
    }

    public UsmanWebstaticConfigurer defaultLanguage(String defaultLanguage) {
        checkConfiguringAccess();
        if (defaultLanguage == null) {
            throw new IllegalArgumentException("default language cannot be null!");
        }

        this.defaultLanguage = defaultLanguage.toLowerCase();

        return this;
    }

    @Override
    public UsmanWebstaticConfigurer configure() {
        super.configure();
        this.configured = true;
        return this;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    private void checkConfiguringAccess() {
        if (this.configured) {
            throw new IllegalStateException("UsmanConfigurer is already configured!");
        }
    }

    @Override
    public String toString() {
        return "UsmanWebstaticConfigurer{" + "defaultLanguage='" + defaultLanguage + '\'' + ", configured=" + configured
                + "} " + super.toString();
    }

}
