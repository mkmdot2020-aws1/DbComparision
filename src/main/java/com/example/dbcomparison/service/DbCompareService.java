package com.example.dbcomparison.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DbCompareService {

    private final JdbcTemplate oracleJdbc;
    private final JdbcTemplate postgresJdbc;

    @Value("${comparison.sql}")
    private String comparisonSql;

    @Value("${comparison.output:comparison-result.txt}")
    private String outputFile;

    public DbCompareService(JdbcTemplate oracleJdbcTemplate, JdbcTemplate postgresJdbcTemplate) {
        this.oracleJdbc = oracleJdbcTemplate;
        this.postgresJdbc = postgresJdbcTemplate;
    }

    public void runComparison() {
        List<String> log = new ArrayList<>();

        log.add("Running SQL on Oracle: " + comparisonSql);
        System.out.println("Running SQL on Oracle: " + comparisonSql);
        List<Map<String, Object>> oracleRows = queryList(oracleJdbc, comparisonSql);

        log.add("Running SQL on Postgres: " + comparisonSql);
        System.out.println("Running SQL on Postgres: " + comparisonSql);
        List<Map<String, Object>> pgRows = queryList(postgresJdbc, comparisonSql);

        appendComparisonLog(log, oracleRows, pgRows);

        // write log to file
        Path out = Paths.get(outputFile);
        try {
            Files.createDirectories(out.getParent() == null ? Paths.get(".") : out.getParent());
            Files.write(out, log, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Comparison written to: " + out.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write comparison to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Map<String, Object>> queryList(JdbcTemplate jdbc, String sql) {
        return jdbc.query(sql, (rs) -> {
            List<Map<String, Object>> rows = new ArrayList<>();
            int cols = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= cols; i++) {
                    String col = rs.getMetaData().getColumnLabel(i);
                    Object val = rs.getObject(i);
                    row.put(col, val);
                }
                rows.add(row);
            }
            return rows;
        });
    }

    private void appendComparisonLog(List<String> log, List<Map<String, Object>> a, List<Map<String, Object>> b) {
        Set<String> setA = a.stream().map(this::rowToKey).collect(Collectors.toSet());
        Set<String> setB = b.stream().map(this::rowToKey).collect(Collectors.toSet());

        Set<String> onlyInA = setA.stream().filter(x -> !setB.contains(x)).collect(Collectors.toSet());
        Set<String> onlyInB = setB.stream().filter(x -> !setA.contains(x)).collect(Collectors.toSet());

        log.add("Oracle rows: " + a.size());
        log.add("Postgres rows: " + b.size());

        System.out.println("Oracle rows: " + a.size());
        System.out.println("Postgres rows: " + b.size());

        if (onlyInA.isEmpty() && onlyInB.isEmpty()) {
            log.add("Results are identical (as sets of rows).");
            System.out.println("Results are identical (as sets of rows).");
            return;
        }

        if (!onlyInA.isEmpty()) {
            log.add("Rows only in Oracle:");
            onlyInA.forEach(r -> log.add("  " + r));
            System.out.println("Rows only in Oracle:");
            onlyInA.forEach(r -> System.out.println("  " + r));
        }

        if (!onlyInB.isEmpty()) {
            log.add("Rows only in Postgres:");
            onlyInB.forEach(r -> log.add("  " + r));
            System.out.println("Rows only in Postgres:");
            onlyInB.forEach(r -> System.out.println("  " + r));
        }
    }

    private String rowToKey(Map<String, Object> row) {
        // deterministic string representation: key1=val1|key2=val2
        return row.entrySet().stream()
                .map(e -> e.getKey() + "=" + String.valueOf(e.getValue()))
                .collect(Collectors.joining("|"));
    }
}
