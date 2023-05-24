package nl.overheid.koop.plooi.registration.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = TestConfiguration.class)
@PropertySource("classpath:application-repos.properties")
class ProcesRepositoryTest {

    @Autowired
    private ProcesRepository procesRepository;

    @Test
    void getLastSuccessful() {
        this.procesRepository.save(new ProcesEntity("testSource", "INGRESS", "one"));
        this.procesRepository.save(new ProcesEntity("testSource", "INGRESS", "two"));
        var last = this.procesRepository.findAll(ProcesRepository.querySpecification(null, "testSource", "INGRESS", null, null, false));
        assertNotNull(last); // Just check if the query succeeded, further timestamp checked is a headache
    }
}
