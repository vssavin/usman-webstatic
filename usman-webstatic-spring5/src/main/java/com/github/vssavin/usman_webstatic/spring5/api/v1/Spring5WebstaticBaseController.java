package com.github.vssavin.usman_webstatic.spring5.api.v1;

import com.github.vssavin.usman_webstatic_core.UsmanWebstaticBaseController;
import com.github.vssavin.usmancore.config.UsmanUrlsConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Base class for spring5 controllers that use
 * {@link org.springframework.web.servlet.ModelAndView} as their return type.
 *
 * @author vssavin on 23.12.2023.
 */
public class Spring5WebstaticBaseController extends UsmanWebstaticBaseController {

    protected final UsmanUrlsConfigurer urlsConfigurer;

    public Spring5WebstaticBaseController(UsmanUrlsConfigurer urlsConfigurer) {
        this.urlsConfigurer = urlsConfigurer;
    }

    /**
     * Generates a {@link org.springframework.web.servlet.ModelAndView} object for the
     * forbidden response
     * @param request the request to check headers
     * @return the {@link org.springframework.web.servlet.ModelAndView} object with
     * forbidden params
     */
    protected ModelAndView getForbiddenModelAndView(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null) {
            referer = urlsConfigurer.getSuccessUrl();
        }
        ModelAndView modelAndView = new ModelAndView(REDIRECT_PREFIX + referer);
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        return modelAndView;
    }

}
