package com.github.vssavin.usman_webstatic.spring5.config;

import com.github.vssavin.usman_webstatic_core.UsmanLocaleConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * A {@link com.github.vssavin.usman_webstatic_core.UsmanLocaleConfig} implementation as a
 * spring component with language configuration.
 *
 * @author vssavin on 18.12.2023.
 */
@Configuration
public class Spring5LocaleConfig extends UsmanLocaleConfig {

    public Spring5LocaleConfig(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

}
