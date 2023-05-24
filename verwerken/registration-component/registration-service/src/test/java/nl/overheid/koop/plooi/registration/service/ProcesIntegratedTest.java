package nl.overheid.koop.plooi.registration.service;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import javax.transaction.Transactional;
import nl.overheid.koop.plooi.registration.DateTimeFormatConfiguration;
import nl.overheid.koop.plooi.registration.api.ProcessenApiController;
import nl.overheid.koop.plooi.registration.model.ExceptieRepository;
import nl.overheid.koop.plooi.registration.model.Proces;
import nl.overheid.koop.plooi.registration.model.ProcesRepository;
import nl.overheid.koop.plooi.registration.service.error.RegistrationExceptionHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = { ProcessenApiController.class, ProcesService.class,
        RegistrationExceptionHandler.class, DateTimeFormatConfiguration.class,
        ExceptieRepository.class, ProcesRepository.class, })
@ContextConfiguration(classes = IntegratedTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProcesIntegratedTest {

    private static ObjectMapper MAPPER;
    static {
        MAPPER = JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .addModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void init() throws IOException {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    @Transactional
    void createProces_success() throws Exception {
        this.mockMvc
                .perform(post("/processen")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Proces().triggerType("TYPE").trigger("trigger"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trigger").value("trigger"));
    }

    @Test
    @Transactional
    void createProces_illegal() throws Exception {
        this.mockMvc
                .perform(post("/processen")
                        .contentType("application/json")
                        .content("bogus"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void createProces_incomplete() throws Exception {
        this.mockMvc
                .perform(post("/processen")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Proces())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("triggerType is required"));
    }

    @Test
    void getProces_unknown() throws Exception {
        this.mockMvc
                .perform(get("/processen/not_existing"))
                .andExpect(status().isNotFound());
    }

    @Nested
    @Transactional
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AfterProcesCreation {
        private String firstId;
        private String secondId;

        @BeforeAll
        void createProces() throws Exception {
            this.firstId = MAPPER.readValue(ProcesIntegratedTest.this.mockMvc
                    .perform(post("/processen")
                            .contentType("application/json")
                            .content(MAPPER.writeValueAsString(new Proces().sourceLabel("source").triggerType("TEST").trigger("first"))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), Proces.class).getId();
            Thread.sleep(100);
            this.secondId = MAPPER.readValue(ProcesIntegratedTest.this.mockMvc
                    .perform(post("/processen")
                            .contentType("application/json")
                            .content(MAPPER.writeValueAsString(new Proces().sourceLabel("source").triggerType("TEST").trigger("second"))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), Proces.class).getId();
            Thread.sleep(100);
            ProcesIntegratedTest.this.mockMvc
                    .perform(post("/processen")
                            .contentType("application/json")
                            .content(MAPPER.writeValueAsString(new Proces().sourceLabel("othersource").triggerType("TEST").trigger("third"))))
                    .andExpect(status().isOk());
        }

        @Test()
        void getProces_success() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get("/processen/" + this.firstId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(this.firstId))
                    .andExpect(jsonPath("$.triggerType").value("TEST"))
                    .andExpect(jsonPath("$.trigger").value("first"))
                    .andExpect(jsonPath("$.sourceLabel").value("source"))
                    .andExpect(jsonPath("$.timeCreated").exists())
                    .andExpect(jsonPath("$.excepties").doesNotExist())
                    .andExpect(jsonPath("$.procesCounts").exists());
        }

        @Test
        void getProcessen_all() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get("/processen"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)))
                    .andExpect(jsonPath("$.content[0].trigger").value("third"))
                    .andExpect(jsonPath("$.content[1].trigger").value("second"))
                    .andExpect(jsonPath("$.content[2].trigger").value("first"));
        }

        @Test
        void getProcessen_ids() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get(String.format("/processen?ids=%s&ids=%s", this.firstId, this.secondId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content[0].trigger").value("second"))
                    .andExpect(jsonPath("$.content[1].trigger").value("first"));
        }

        @Test
        void getProcessen_source() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get("/processen?sourceLabel=source"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content[0].trigger").value("second"))
                    .andExpect(jsonPath("$.content[1].trigger").value("first"));
        }

        @Test
        void getProcessen_since() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get("/processen?triggerType=TEST&timeCreated.van=2007-12-03T10:15:30Z"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(3));
        }

        @Test
        void getProcessen_till() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get("/processen?triggerType=TEST&timeCreated.tot=2099-12-31T23:59:59Z"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(3));
        }

        @Test
        void getProcessen_errorsOnly() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get("/processen?alleenFalend=true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        void getLaatsteSuccessfulProces() throws Exception {
            ProcesIntegratedTest.this.mockMvc
                    .perform(get("/processen?sourceLabel=source&triggerType=TEST&zonderTellingen=true&size=1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numberOfElements").value(1))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content[0].trigger").value("second"));
        }
    }
}
