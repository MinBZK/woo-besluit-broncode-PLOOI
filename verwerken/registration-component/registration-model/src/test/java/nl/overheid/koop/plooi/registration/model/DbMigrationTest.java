package nl.overheid.koop.plooi.registration.model;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Tests on the Flyway migrations via Spring Boot startup. Setting spring.jpa.hibernate.ddl-auto=validate ensures that
 * the tables produces are validates with the JPA Entity definitions.
 */
@SpringBootTest
@EnableAutoConfiguration
@PropertySource("classpath:application-flyway.properties")
class DbMigrationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void migrate() {
        var tables = this.jdbc.queryForList("SHOW TABLES");
        this.logger.debug("tables: {}", tables);
        assertTrue(tables.stream().filter(t -> "flyway_schema_history".equals(t.get("TABLE_NAME"))).findAny().isPresent(),
                "NoFlyway table found in in database");
        assertTrue(tables.size() > 1,
                "Not really much tables");
        assertTrue(this.jdbc.queryForList("select * from Processen").isEmpty());
    }

    @Configuration
    public static class DbMigrationTestConfiguration {
    }
}
