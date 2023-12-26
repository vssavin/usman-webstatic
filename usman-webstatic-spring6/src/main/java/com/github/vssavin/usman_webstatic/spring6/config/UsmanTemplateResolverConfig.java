package com.github.vssavin.usman_webstatic.spring6.config;

import com.github.vssavin.usmancore.config.UsmanConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Configuration of user management template resolvers.
 *
 * @author vssavin on 19.12.2023.
 */
@Configuration
public class UsmanTemplateResolverConfig implements WebMvcConfigurer {

    private final Logger log = LoggerFactory.getLogger(UsmanTemplateResolverConfig.class);

    private final UsmanConfigurer usmanConfigurer;

    public UsmanTemplateResolverConfig(UsmanConfigurer usmanConfigurer) {
        this.usmanConfigurer = usmanConfigurer;
    }

    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver usmanTemplateResolver) {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(usmanTemplateResolver);
        return springTemplateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver usmanTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/template/usman/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        int order = getResolverOrder();
        if (order < 1) {
            throw new IllegalArgumentException("Template order should be greater than 0!");
        }
        templateResolver.setOrder(order);
        templateResolver.setCheckExistence(true);

        return templateResolver;
    }

    @Bean
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setOrder(getResolverOrder());
        viewResolver.setCharacterEncoding("UTF-8");
        templateEngine.addTemplateResolver(usmanTemplateResolver());
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        usmanConfigurer.getResourceHandlers()
            .forEach((handler, locations) -> registry.addResourceHandler(handler).addResourceLocations(locations));
    }

    private int getResolverOrder() {
        String orderString = System.getProperty("usman.templateResolver.order");
        if (orderString == null) {
            orderString = System.getenv("usman.templateResolver.order");
        }
        int order = 1;
        if (orderString != null) {
            try {
                order = Integer.parseInt(orderString);
            }
            catch (NumberFormatException nfe) {
                log.error("Template resolver order should be integer value!");
            }
        }

        return order;
    }

}
