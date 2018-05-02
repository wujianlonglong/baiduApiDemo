package com.wjl.baiduapi.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class JdbcConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties aliDataSourceProperties() {
        log.info("=============ali-inner数据库datasouce===========");
        return new DataSourceProperties();
    }

    @Bean(name = "aliDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource aliDataSource() {
        return aliDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }


    @Bean(name = "aliJdbcTemplate")
    public JdbcTemplate aliJdbcTemplate() {
        return new JdbcTemplate(aliDataSource());
    }


    @Bean
    @Primary
    @ConfigurationProperties(prefix = "local.datasource")
    public DataSourceProperties localDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "localDataSource")
    @Primary
    public DataSource localDataSource() {
        return localDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


    @Bean(name = "localJdbcTemplate")
    public JdbcTemplate localJdbcTemplate() {
        return new JdbcTemplate(localDataSource());
    }

}
