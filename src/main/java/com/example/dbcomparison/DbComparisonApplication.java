package com.example.dbcomparison;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DbComparisonApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbComparisonApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(service.DbCompareService compareService) {
        return args -> {
            compareService.runComparison();
        };
    }
}
