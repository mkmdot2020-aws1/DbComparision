package com.example.dbcomparison.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${oracle.datasource.url}")
    private String oracleUrl;
    @Value("${oracle.datasource.username}")
    private String oracleUser;
    @Value("${oracle.datasource.password}")
    private String oraclePass;
    @Value("${oracle.datasource.driver-class-name}")
    private String oracleDriver;

    @Value("${postgres.datasource.url}")
    private String pgUrl;
    @Value("${postgres.datasource.username}")
    private String pgUser;
    @Value("${postgres.datasource.password}")
    private String pgPass;
    @Value("${postgres.datasource.driver-class-name}")
    private String pgDriver;

    @Bean(name = "oracleDataSource")
    public DataSource oracleDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(oracleDriver);
        ds.setUrl(oracleUrl);
        ds.setUsername(oracleUser);
        ds.setPassword(oraclePass);
        return ds;
    }

    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(pgDriver);
        ds.setUrl(pgUrl);
        ds.setUsername(pgUser);
        ds.setPassword(pgPass);
        return ds;
    }

    @Bean(name = "oracleJdbcTemplate")
    public JdbcTemplate oracleJdbcTemplate(DataSource oracleDataSource) {
        return new JdbcTemplate(oracleDataSource);
    }

    @Bean(name = "postgresJdbcTemplate")
    public JdbcTemplate postgresJdbcTemplate(DataSource postgresDataSource) {
        return new JdbcTemplate(postgresDataSource);
    }
}
