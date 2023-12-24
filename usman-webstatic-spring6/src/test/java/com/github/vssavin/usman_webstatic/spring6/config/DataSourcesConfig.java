package com.github.vssavin.usman_webstatic.spring6.config;

import com.github.vssavin.usmancore.config.UsmanDataSourceConfig;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

/**
 * @author vssavin on 17.12.2023.
 */
@Configuration
@Import(UsmanDataSourceConfig.class)
public class DataSourcesConfig {

    private DataSource appDataSource;

    @Bean
    @Primary
    public DataSource appDatasource() {
        if (appDataSource == null) {
            appDataSource = new EmbeddedDatabaseBuilder().generateUniqueName(true)
                .setType(H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("init_test.sql")
                .build();
        }
        return appDataSource;
    }

    @Bean("usmanDataSource")
    @Profile("usman-test")
    public DataSource usmanDataSourceTest() {
        return appDatasource();
    }

}
