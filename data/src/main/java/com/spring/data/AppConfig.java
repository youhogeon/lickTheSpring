package com.spring.data;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public DataSource dataSource(@Value("${db.url}") String url, @Value("${db.id}") String id, @Value("${db.password}") String password) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(id);
        config.setPassword(password);

        return new HikariDataSource(config);
    }

}