package nl.overheid.koop.plooi.registration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class ExceptieRepositoryTest {

    @Autowired
    private ExceptieRepository exceptieRepository;

    @Test
    void getExceptiesByBron() {
        var e1 = this.exceptieRepository.save(new ExceptieEntity("ver_1", "api"));
        var e2 = this.exceptieRepository.save(new ExceptieEntity("ver_1", "oep"));
        assertNotNull(e1.getId());
        assertNotNull(e2.getId());

        var excepties = this.exceptieRepository.getExceptiesByBron("oep");
        assertEquals(0, excepties.size());
    }
}
