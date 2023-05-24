package nl.overheid.koop.plooi.aanleveren.zoeken.infrastructure.dcn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DcnClientConfigurationTest {

    @Autowired
    ApplicationContext context;

    @Test
    void searchClient() {
        assertTrue(context.containsBean("searchClient"));
    }
}
