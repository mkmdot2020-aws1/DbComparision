# DB Comparison

This small Spring Boot app runs the same SQL against an Oracle and a PostgreSQL database and compares the results.

Files added:

- [pom.xml](pom.xml)
- [src/main/resources/application.properties](src/main/resources/application.properties)
- [src/main/java/com/example/dbcomparison/DbComparisonApplication.java](src/main/java/com/example/dbcomparison/DbComparisonApplication.java)
- [src/main/java/com/example/dbcomparison/config/DataSourceConfig.java](src/main/java/com/example/dbcomparison/config/DataSourceConfig.java)
- [src/main/java/com/example/dbcomparison/service/DbCompareService.java](src/main/java/com/example/dbcomparison/service/DbCompareService.java)

Setup

1. Edit `src/main/resources/application.properties` and set your Oracle and Postgres connection URLs, usernames and passwords.
2. Set `comparison.sql` to the query you want to run on both databases.
3. If Maven cannot fetch the Oracle JDBC driver, download the `ojdbc8.jar` from Oracle and install it to your local Maven repository or add it to the project classpath.

Run

Build and run with Maven:

```bash
mvn clean package
mvn spring-boot:run
```

The app prints counts and differences (rows only in Oracle or only in Postgres).

Output file

- The comparison log is written to the file configured by `comparison.output` in `src/main/resources/application.properties` (default: `comparison-result.txt`).
# DbComparision