package com.github.vssavin.usman_webstatic.spring5.config;

import com.github.vssavin.usmancore.config.*;
import com.github.vssavin.usmancore.spring5.config.DefaultSecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author vssavin on 17.12.2023.
 */
@Configuration
@ComponentScan({ "com.github.vssavin.usmancore.*", "com.github.vssavin.usman_webstatic.spring5.*" })
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.github.vssavin.usmancore.*")
@EnableWebSecurity
@Import({ DefaultSecurityConfig.class, TestsConfig.class })
public class ApplicationConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public JavaMailSender emailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public UsmanConfigurer usmanConfigurer(UsmanUrlsConfigurer urlsConfigurer, OAuth2Config oAuth2Config,
            List<PermissionPathsContainer> permissionPathsContainerList) {

        UsmanConfigurer usmanConfigurer = new UsmanConfigurer(urlsConfigurer, oAuth2Config,
                permissionPathsContainerList);

        usmanConfigurer.permission(new AuthorizedUrlPermission("/index.html", Permission.ANY_USER))
            .permission(new AuthorizedUrlPermission("/index", Permission.ANY_USER));

        return usmanConfigurer.configure();
    }

    @Bean
    public UsmanUrlsConfigurer usmanUrlsConfigurer() {

        UsmanUrlsConfigurer usmanUrlsConfigurer = new UsmanUrlsConfigurer();

        usmanUrlsConfigurer.successUrl("/index.html").adminSuccessUrl("/usman/v1/admin");

        return usmanUrlsConfigurer.configure();
    }

    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
        FilterRegistrationBean<HiddenHttpMethodFilter> filterBean = new FilterRegistrationBean<>(
                new HiddenHttpMethodFilter());
        filterBean.setUrlPatterns(Collections.singletonList("/*"));
        return filterBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("routingDatasource") DataSource routingDatasource, DatabaseConfig databaseConfig) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        try {
            em.setDataSource(routingDatasource);
            em.setPackagesToScan("com.github.vssavin.usmancore");

            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            String hibernateDialect = databaseConfig.getDialect();

            Properties additionalProperties = new Properties();
            additionalProperties.put("hibernate.dialect", hibernateDialect);
            em.setJpaProperties(additionalProperties);
        }
        catch (Exception e) {
            log.error("Creating LocalContainerEntityManagerFactoryBean error!", e);
        }

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * An example bean to print an encoded password in a specified file. Used in the
     * ${@link com.github.vssavin.usmancore.config.UsmanPasswordEncodingArgumentsHandler}
     * class.
     * @return PrintStream bean.
     */
    @Bean
    public PrintStream passwordPrintStream() {
        try {
            return new PrintStream(new FileOutputStream("password-output.txt", true));
        }
        catch (FileNotFoundException e) {
            log.error("Initialize passwordPrintStream bean error!", e);
        }

        return null;
    }

}
