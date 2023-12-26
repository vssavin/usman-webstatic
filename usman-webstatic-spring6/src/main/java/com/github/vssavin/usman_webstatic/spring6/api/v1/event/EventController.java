package com.github.vssavin.usman_webstatic.spring6.api.v1.event;

import com.github.vssavin.usman_webstatic_core.MessageKey;
import com.github.vssavin.usman_webstatic_core.UsmanLocaleConfig;
import com.github.vssavin.usman_webstatic_core.UsmanWebstaticBaseController;
import com.github.vssavin.usmancore.config.ArgumentsProcessedNotifier;
import com.github.vssavin.usmancore.config.UsmanConfigurer;
import com.github.vssavin.usmancore.config.UsmanSecureServiceArgumentsHandler;
import com.github.vssavin.usmancore.config.UsmanUrlsConfigurer;
import com.github.vssavin.usmancore.event.EventDto;
import com.github.vssavin.usmancore.event.EventFilter;
import com.github.vssavin.usmancore.event.EventType;
import com.github.vssavin.usmancore.security.SecureService;
import com.github.vssavin.usmancore.spring6.data.pagination.Paged;
import com.github.vssavin.usmancore.spring6.event.EventService;
import com.github.vssavin.usmancore.spring6.user.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Provides event management for administrators.
 *
 * @author vssavin on 25.12.2023.
 */
@Controller
@RequestMapping(EventController.EVENT_CONTROLLER_PATH)
public class EventController extends UsmanWebstaticBaseController implements ArgumentsProcessedNotifier {

    static final String EVENT_CONTROLLER_PATH = "/usman/v1/events";

    private static final String PAGE_EVENTS = "events";

    private static final String EVENTS_ATTRIBUTE = "events";

    private static final String EVENTS_TYPES_ATTRIBUTE = "eventTypes";

    private static final Set<String> IGNORED_PARAMS = Collections.singleton("_csrf");

    private final UsmanConfigurer usmanConfigurer;

    private final UsmanUrlsConfigurer urlsConfigurer;

    private SecureService secureService;

    private final UserSecurityService userSecurityService;

    private final EventService eventService;

    private final Set<String> pageLoginParams;

    private final Set<String> pageEventsParams;

    @Autowired
    public EventController(UsmanLocaleConfig localeConfig, UsmanConfigurer usmanConfigurer,
            UsmanUrlsConfigurer urlsConfigurer, UserSecurityService userSecurityService, EventService eventService) {
        this.secureService = usmanConfigurer.getSecureService();
        this.userSecurityService = userSecurityService;
        this.usmanConfigurer = usmanConfigurer;
        this.urlsConfigurer = urlsConfigurer;
        this.eventService = eventService;
        String pageLogin = urlsConfigurer.getLoginUrl().replace("/", "");
        pageLoginParams = localeConfig.forPage(pageLogin).getKeys();
        pageEventsParams = localeConfig.forPage(PAGE_EVENTS).getKeys();
    }

    @Override
    public void notifyArgumentsProcessed(Class<?> aClass) {
        if (aClass != null && UsmanSecureServiceArgumentsHandler.class.isAssignableFrom(aClass)) {
            this.secureService = usmanConfigurer.getSecureService();
        }
    }

    @GetMapping
    public ModelAndView findEvents(final HttpServletRequest request, final HttpServletResponse response,
            @ModelAttribute final EventFilter eventFilter,
            @RequestParam(required = false, defaultValue = "1") final int page,
            @RequestParam(required = false, defaultValue = "5") final int size,
            @RequestParam(required = false) final String lang) {

        ModelAndView modelAndView = new ModelAndView(PAGE_EVENTS);
        if (userSecurityService.isAuthorizedAdmin(request)) {
            Paged<EventDto> events = eventService.findEvents(eventFilter, page, size);
            modelAndView.addObject(EVENTS_ATTRIBUTE, events);
            modelAndView.addObject(EVENTS_TYPES_ATTRIBUTE, Arrays.asList(EventType.values()));
            modelAndView.addObject(USER_NAME_ATTRIBUTE, userSecurityService.getAuthorizedUserName(request));
        }
        else {
            modelAndView = getErrorModelAndView(urlsConfigurer.getLoginUrl(),
                    MessageKey.ADMIN_AUTHENTICATION_REQUIRED_MESSAGE, lang);
            addObjectsToModelAndView(modelAndView, pageLoginParams, secureService.getEncryptMethodName(), lang);
            response.setStatus(403);
            return modelAndView;
        }

        addObjectsToModelAndView(modelAndView, pageEventsParams, secureService.getEncryptMethodName(), lang);
        addObjectsToModelAndView(modelAndView, request.getParameterMap(), IGNORED_PARAMS);

        return modelAndView;
    }

}
