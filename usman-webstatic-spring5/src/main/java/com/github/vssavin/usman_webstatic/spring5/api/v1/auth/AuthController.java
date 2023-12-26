package com.github.vssavin.usman_webstatic.spring5.api.v1.auth;

import com.github.vssavin.usman_webstatic.spring5.config.Spring5LocaleConfig;
import com.github.vssavin.usman_webstatic_core.UsmanWebstaticBaseController;
import com.github.vssavin.usmancore.config.*;
import com.github.vssavin.usmancore.security.SecureService;
import com.github.vssavin.usmancore.spring5.user.UserSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication controller - allows user authentication.
 *
 * @author vssavin on 21.12.2023.
 */
@Controller
public class AuthController extends UsmanWebstaticBaseController implements ArgumentsProcessedNotifier {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final String pageLogin;

    private final String pageLogout;

    private final Set<String> pageLoginParams;

    private final Set<String> pageLogoutParams;

    private final UserSecurityService userSecurityService;

    private final UsmanConfigurer usmanConfigurer;

    private final UsmanUrlsConfigurer urlsConfigurer;

    private final RequestMappingHandlerMapping handlerMapping;

    private SecureService secureService;

    @Autowired
    AuthController(Spring5LocaleConfig localeConfig, UsmanConfigurer usmanConfigurer,
            UsmanUrlsConfigurer urlsConfigurer, UserSecurityService userSecurityService,
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.secureService = usmanConfigurer.getSecureService();
        this.usmanConfigurer = usmanConfigurer;
        this.urlsConfigurer = urlsConfigurer;
        this.handlerMapping = handlerMapping;
        this.pageLogin = urlsConfigurer.getLoginUrl().replace("/", "");
        this.pageLogout = urlsConfigurer.getLogoutUrl().replace("/", "");
        this.pageLoginParams = localeConfig.forPage(pageLogin).getKeys();
        this.pageLogoutParams = localeConfig.forPage(pageLogout).getKeys();
        this.userSecurityService = userSecurityService;
    }

    /**
     * Manually register mappings for login and logout methods.
     * @throws NoSuchMethodException if login/logout methods not found
     */
    @PostConstruct
    private void initRequestMappings() throws NoSuchMethodException {
        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());

        // Methods to be executed when HTTP method and URL are called
        Method loginMethod = AuthController.class.getDeclaredMethod("getLogin", HttpServletRequest.class,
                HttpServletResponse.class, String.class);

        Method logoutMethod = AuthController.class.getDeclaredMethod("getLogout", String.class);

        handlerMapping.registerMapping(RequestMappingInfo.paths(urlsConfigurer.getLoginUrl())
            .methods(RequestMethod.GET)
            .options(options)
            .build(), this, loginMethod);

        handlerMapping.registerMapping(RequestMappingInfo.paths(urlsConfigurer.getLogoutUrl())
            .methods(RequestMethod.GET)
            .options(options)
            .build(), this, logoutMethod);
    }

    ModelAndView getLogin(final HttpServletRequest request, final HttpServletResponse response,
            @RequestParam(required = false) final String lang) {
        try {
            if (userSecurityService.isAuthorizedAdmin(request)) {
                response.sendRedirect(urlsConfigurer.getAdminSuccessUrl());
            }
            else if (userSecurityService.isAuthorizedUser(request)) {
                response.sendRedirect(urlsConfigurer.getSuccessUrl());
            }
        }
        catch (IOException e) {
            log.error("Sending redirect i/o error: ", e);
        }

        ModelAndView modelAndView = new ModelAndView(pageLogin);
        addObjectsToModelAndView(modelAndView, pageLoginParams, secureService.getEncryptMethodName(), lang);
        modelAndView.addObject("registrationAllowed", usmanConfigurer.isRegistrationAllowed());
        modelAndView.addObject("googleAuthAllowed", usmanConfigurer.isGoogleAuthAllowed());
        modelAndView.addObject("loginTitle", usmanConfigurer.getLoginPageTitle());
        modelAndView.addObject("secureScripts", normalizeScriptNames(secureService.getScriptsList()));
        return modelAndView;
    }

    ModelAndView getLogout(@RequestParam(required = false) final String lang) {

        ModelAndView modelAndView = new ModelAndView(pageLogout);
        addObjectsToModelAndView(modelAndView, pageLogoutParams, secureService.getEncryptMethodName(), lang);
        return modelAndView;
    }

    private List<String> normalizeScriptNames(List<String> scripts) {
        String jsPrefix = "/js";
        return scripts.stream()
            .map(scriptName -> scriptName.substring(scriptName.indexOf(jsPrefix)))
            .collect(Collectors.toList());
    }

    @Override
    public void notifyArgumentsProcessed(Class<?> aClass) {
        if (aClass != null && UsmanSecureServiceArgumentsHandler.class.isAssignableFrom(aClass)) {
            this.secureService = usmanConfigurer.getSecureService();
        }
    }

}
