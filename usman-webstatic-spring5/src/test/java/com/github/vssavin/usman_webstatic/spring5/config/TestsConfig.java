package com.github.vssavin.usman_webstatic.spring5.config;

import com.github.vssavin.usmancore.config.SqlScriptExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.sql.DataSource;

/**
 * @author vssavin on 22.12.2023.
 */
@Configuration
public class TestsConfig {

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        // add properties here
        return new RequestMappingHandlerMapping();
    }

    @Bean
    @Primary
    public SqlScriptExecutor sqlScriptExecutor(DataSource usmanDataSource) {
        return new SqlScriptExecutor(usmanDataSource);
    }

}
