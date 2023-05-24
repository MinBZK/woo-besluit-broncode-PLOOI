package nl.overheid.koop.plooi.registration.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import javax.transaction.Transactional;
import nl.overheid.koop.plooi.registration.DateTimeFormatConfiguration;
import nl.overheid.koop.plooi.registration.api.ProcessenApiController;
import nl.overheid.koop.plooi.registration.api.VerwerkingenApiController;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import nl.overheid.koop.plooi.registration.model.Exceptie;
import nl.overheid.koop.plooi.registration.model.ExceptieRepository;
import nl.overheid.koop.plooi.registration.model.Proces;
import nl.overheid.koop.plooi.registration.model.ProcesRepository;
import nl.overheid.koop.plooi.registration.model.Severity;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import nl.overheid.koop.plooi.registration.service.error.RegistrationExceptionHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = { VerwerkingenApiController.class, VerwerkingService.class,
        ProcessenApiController.class, ProcesService.class,
        RegistrationExceptionHandler.class, DateTimeFormatConfiguration.class,
        ExceptieRepository.class, ProcesRepository.class, })
@ContextConfiguration(classes = IntegratedTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VerwerkingIntegratedTest {

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
    private String procesId;

    @BeforeAll
    void init() throws Exception {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
        this.procesId = MAPPER.readValue(this.mockMvc
                .perform(post("/processen")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Proces().sourceLabel("VerwerkingIntegratedTest").triggerType("TEST").trigger("trigger"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), Proces.class).getId();
    }

    @Test
    @Transactional
    void createVerwerking_OK_success() throws Exception {
        this.mockMvc
                .perform(post(String.format("/processen/%s/verwerkingen", this.procesId))
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Verwerking()
                                .stage("test")
                                .dcnId("test-1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dcnId").value("test-1234"))
                .andExpect(jsonPath("$.severity").value("OK"));
    }

    @Test
    @Transactional
    void createVerwerking_diagnoses_success() throws Exception {
        this.mockMvc
                .perform(post(String.format("/processen/%s/verwerkingen", this.procesId))
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Verwerking()
                                .stage("test")
                                .dcnId("test-1234")
                                .procesId("ignored")
                                .addDiagnosesItem(new Diagnose()
                                        .code("info")
                                        .severity(Severity.INFO)
                                        .message("something interesting"))
                                .addDiagnosesItem(new Diagnose()
                                        .code("warn")
                                        .severity(Severity.WARNING)
                                        .message("something fishy")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dcnId").value("test-1234"))
                .andExpect(jsonPath("$.procesId").value(this.procesId))
                .andExpect(jsonPath("$.severity").value("WARNING"));
    }

    @Test
    @Transactional
    void createVerwerking_diagnoses_incomplete() throws Exception {
        this.mockMvc
                .perform(post(String.format("/processen/%s/verwerkingen", this.procesId))
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Verwerking()
                                .stage("test")
                                .dcnId("test-1234")
                                .addDiagnosesItem(new Diagnose()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("diagnose.severity is required"));
    }

    @Test
    @Transactional
    void createVerwerking_exception_success() throws Exception {
        this.mockMvc
                .perform(post(String.format("/processen/%s/verwerkingen", this.procesId))
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Verwerking()
                                .stage("test")
                                .dcnId("test-1234")
                                .addDiagnosesItem(new Diagnose()
                                        .code("info")
                                        .severity(Severity.INFO)
                                        .message("something interesting"))
                                .exceptie(new Exceptie()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dcnId").value("test-1234"))
                .andExpect(jsonPath("$.severity").value("EXCEPTION"));
    }

    @Test
    @Transactional
    void createVerwerking_illegal() throws Exception {
        this.mockMvc
                .perform(post(String.format("/processen/%s/verwerkingen", this.procesId))
                        .contentType("application/json")
                        .content("bogus"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void createVerwerking_incomplete() throws Exception {
        this.mockMvc
                .perform(post(String.format("/processen/%s/verwerkingen", this.procesId))
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(new Verwerking())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("stage is required"));
    }

    @Test
    void getProcesVerwerkingen_unknown() throws Exception {
        this.mockMvc
                .perform(get("/processen/not_existing/verwerkingen"))
                .andExpect(status().isNotFound());
    }

    @Nested
    @Transactional
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AfterVerwerkingCreation {
        private String firstId;
        private String secondId;
        private String thirdId;

        @BeforeAll
        void createVerwerking() throws Exception {
            this.firstId = MAPPER.readValue(VerwerkingIntegratedTest.this.mockMvc
                    .perform(post(String.format("/processen/%s/verwerkingen", VerwerkingIntegratedTest.this.procesId))
                            .contentType("application/json")
                            .content(MAPPER.writeValueAsString(new Verwerking()
                                    .stage("test")
                                    .dcnId("test-4567"))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), Verwerking.class).getId();
            Thread.sleep(100);
            this.secondId = MAPPER.readValue(VerwerkingIntegratedTest.this.mockMvc
                    .perform(post(String.format("/processen/%s/verwerkingen", VerwerkingIntegratedTest.this.procesId))
                            .contentType("application/json")
                            .content(MAPPER.writeValueAsString(new Verwerking()
                                    .stage("test")
                                    .dcnId("test-4567")
                                    .addDiagnosesItem(new Diagnose()
                                            .code("warn")
                                            .severity(Severity.WARNING)
                                            .message("something fishy")))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), Verwerking.class).getId();
            Thread.sleep(100);
            this.thirdId = MAPPER.readValue(VerwerkingIntegratedTest.this.mockMvc
                    .perform(post(String.format("/processen/%s/verwerkingen", VerwerkingIntegratedTest.this.procesId))
                            .contentType("application/json")
                            .content(MAPPER.writeValueAsString(new Verwerking()
                                    .stage("test")
                                    .dcnId("test-7890")
                                    .sourceLabel("source")
                                    .extIds(List.of("one", "two", "three"))
                                    .exceptie(new Exceptie().exceptionClass("java.lang.FakeException")))))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), Verwerking.class).getId();
        }

        @Test
        void getProcesVerwerkingen_succes() throws Exception {
            VerwerkingIntegratedTest.this.mockMvc
                    .perform(get(String.format("/processen/%s/verwerkingen", VerwerkingIntegratedTest.this.procesId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(this.thirdId))
                    .andExpect(jsonPath("$.content[0].dcnId").value("test-7890"))
                    .andExpect(jsonPath("$.content[0].sourceLabel").value("source"))
                    .andExpect(jsonPath("$.content[0].extIds[0]").value("one"))
                    .andExpect(jsonPath("$.content[0].extIds[1]").value("two"))
                    .andExpect(jsonPath("$.content[0].extIds[2]").value("three"))
                    .andExpect(jsonPath("$.content[0].stage").value("test"))
                    .andExpect(jsonPath("$.content[0].severity").value("EXCEPTION"))
                    .andExpect(jsonPath("$.content[1].id").value(this.secondId))
                    .andExpect(jsonPath("$.content[1].dcnId").value("test-4567"))
                    .andExpect(jsonPath("$.content[1].sourceLabel").doesNotExist())
                    .andExpect(jsonPath("$.content[1].stage").value("test"))
                    .andExpect(jsonPath("$.content[1].severity").value("WARNING"))
                    .andExpect(jsonPath("$.content[2].id").value(this.firstId))
                    .andExpect(jsonPath("$.content[2].dcnId").value("test-4567"))
                    .andExpect(jsonPath("$.content[2].sourceLabel").doesNotExist())
                    .andExpect(jsonPath("$.content[2].stage").value("test"))
                    .andExpect(jsonPath("$.content[2].severity").value("OK"))
                    .andExpect(jsonPath("$.content[3]").doesNotExist());
        }

        @Test
        void getProcesVerwerkingen_bySeverity() throws Exception {
            VerwerkingIntegratedTest.this.mockMvc
                    .perform(get(String.format("/processen/%s/verwerkingen?severity=WARNING", VerwerkingIntegratedTest.this.procesId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(this.secondId))
                    .andExpect(jsonPath("$.content[0].dcnId").value("test-4567"))
                    .andExpect(jsonPath("$.content[0].severity").value("WARNING"))
                    .andExpect(jsonPath("$.content[0].diagnoses[0].severity").value("WARNING"))
                    .andExpect(jsonPath("$.content[0].diagnoses[0].code").value("warn"))
                    .andExpect(jsonPath("$.content[0].diagnoses[0].message").value("something fishy"))
                    .andExpect(jsonPath("$.content[1]").doesNotExist());
        }

        @Test()
        void getVerwerking_success() throws Exception {
            VerwerkingIntegratedTest.this.mockMvc
                    .perform(get("/verwerkingen/" + this.thirdId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(this.thirdId))
                    .andExpect(jsonPath("$.dcnId").value("test-7890"))
                    .andExpect(jsonPath("$.sourceLabel").value("source"))
                    .andExpect(jsonPath("$.stage").value("test"))
                    .andExpect(jsonPath("$.severity").value("EXCEPTION"))
                    .andExpect(jsonPath("$.exceptie.exceptionClass").value("java.lang.FakeException"));
        }

        @Test()
        void getVerwerkingen_success() throws Exception {
            VerwerkingIntegratedTest.this.mockMvc
                    .perform(get("/verwerkingen?dcnId=test-4567"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(this.secondId))
                    .andExpect(jsonPath("$.content[0].dcnId").value("test-4567"))
                    .andExpect(jsonPath("$.content[0].sourceLabel").doesNotExist())
                    .andExpect(jsonPath("$.content[0].stage").value("test"))
                    .andExpect(jsonPath("$.content[0].severity").value("WARNING"))
                    .andExpect(jsonPath("$.content[1].id").value(this.firstId))
                    .andExpect(jsonPath("$.content[1].dcnId").value("test-4567"))
                    .andExpect(jsonPath("$.content[1].sourceLabel").doesNotExist())
                    .andExpect(jsonPath("$.content[1].stage").value("test"))
                    .andExpect(jsonPath("$.content[1].severity").value("OK"))
                    .andExpect(jsonPath("$.content[2]").doesNotExist());
        }

        @Test()
        void getVerwerkingStatus_success() throws Exception {
            VerwerkingIntegratedTest.this.mockMvc
                    .perform(get("/verwerkingen/status?dcnId=test-4567"))
                    .andExpect(status().isNotFound()); // The Verwerkingstatus is not filled because the trigger is missing
        }
    }
}
