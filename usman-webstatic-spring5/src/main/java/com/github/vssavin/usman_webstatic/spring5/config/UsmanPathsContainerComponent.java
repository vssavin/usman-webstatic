package com.github.vssavin.usman_webstatic.spring5.config;

import com.github.vssavin.usman_webstatic_core.UsmanPathsContainer;
import com.github.vssavin.usmancore.config.UsmanUrlsConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A {@link com.github.vssavin.usmancore.config.PermissionPathsContainer} implementation
 * as spring component with webstatic permission paths.
 *
 * @author vssavin on 18.12.2023
 */
@Component
public class UsmanPathsContainerComponent extends UsmanPathsContainer {

    @Autowired
    public UsmanPathsContainerComponent(UsmanUrlsConfigurer usmanUrlsConfigurer) {
        super(usmanUrlsConfigurer);
    }

}
