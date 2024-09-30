package com.ClinicaDelCalzado_BackEnd.config;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final String DATA_SOURCE_URL = "jdbc:mysql://%s/%s?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";

    @SneakyThrows
    @Bean
    @Qualifier("datasource")
    @Profile({"prod"})
    public DataSource getDataSource(final @Value("${spring.datasource.driver-class-name}") String driver,
                                    final @Value("${spring.datasource.url}") String jdbcUrl,
                                    final @Value("${spring.datasource.db}") String db,
                                    final @Value("${spring.datasource.username}") String user,
                                    final @Value("${spring.datasource.password}") String password) {
        return buildDataSource(driver, jdbcUrl, db, user, password);
    }

    @SneakyThrows
    @Bean
    @Qualifier("datasource")
    @Profile({"local"})
    public DataSource getDataSourceLocal(final @Value("${spring.datasource.driver-class-name}") String driver,
                                         final @Value("${spring.datasource.url}") String jdbcUrl,
                                         final @Value("${spring.datasource.db}") String db,
                                         final @Value("${spring.datasource.username}") String user,
                                         final @Value("${spring.datasource.password}") String password) {
        return buildDataSource(driver, jdbcUrl, db, user, password);
    }

    private DataSource buildDataSource(String driver, String jdbcUrl, String db, String user, String password){
        return DataSourceBuilder.create()
                .driverClassName(driver)
                .url(String.format(DATA_SOURCE_URL, jdbcUrl, db))
                .username(user)
                .password(password)
                .build();
    }

}
