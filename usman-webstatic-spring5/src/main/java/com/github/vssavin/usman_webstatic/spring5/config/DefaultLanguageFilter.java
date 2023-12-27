package com.github.vssavin.usman_webstatic.spring5.config;

import com.github.vssavin.usman_webstatic_core.UsmanWebstaticConfigurer;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Arrays;

/**
 * Provides a default language selection if no language is specified.
 *
 * @author vssavin on 27.12.2023.
 */
@Component
public class DefaultLanguageFilter implements Filter {

    private final UsmanWebstaticConfigurer configurer;

    public DefaultLanguageFilter(UsmanWebstaticConfigurer configurer) {
        this.configurer = configurer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String lang = request.getParameter("lang");
        if (lang == null || "null".equals(lang)) {
            LanguageFilteredRequest languageFilteredRequest = new LanguageFilteredRequest(request);
            chain.doFilter(languageFilteredRequest, response);
        }
        else {
            chain.doFilter(request, response);
        }
    }

    private class LanguageFilteredRequest extends HttpServletRequestWrapper {

        public LanguageFilteredRequest(ServletRequest request) {
            super((HttpServletRequest) request);
        }

        @Override
        public String getParameter(String paramName) {
            String value = super.getParameter(paramName);

            if ((value == null || "null".equals(value)) && "lang".equals(paramName)) {
                return configurer.getDefaultLanguage();
            }

            return value;
        }

        @Override
        public String[] getParameterValues(String paramName) {
            String[] values = super.getParameterValues(paramName);

            boolean containsNull = values == null || Arrays.asList(values).contains("null");

            if (((values == null || containsNull) || values.length == 0) && "lang".equals(paramName)) {
                return new String[] { configurer.getDefaultLanguage() };
            }

            return values;
        }

    }

}
