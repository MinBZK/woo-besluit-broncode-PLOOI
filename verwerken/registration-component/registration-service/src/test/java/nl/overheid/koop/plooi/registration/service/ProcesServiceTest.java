package nl.overheid.koop.plooi.registration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import nl.overheid.koop.plooi.registration.model.DiagnoseRepository;
import nl.overheid.koop.plooi.registration.model.ExceptieRepository;
import nl.overheid.koop.plooi.registration.model.Proces;
import nl.overheid.koop.plooi.registration.model.ProcesEntity;
import nl.overheid.koop.plooi.registration.model.ProcesRepository;
import nl.overheid.koop.plooi.registration.model.RegistrationModelMapper;
import nl.overheid.koop.plooi.registration.model.Severity;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import nl.overheid.koop.plooi.registration.model.VerwerkingEntity;
import nl.overheid.koop.plooi.registration.model.VerwerkingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ProcesServiceTest {

    @Mock
    private ProcesRepository processEntityRepository;

    @Mock
    private VerwerkingRepository verwerkingRepository;
    @Mock
    private DiagnoseRepository diagnoseRepository;
    @Mock
    private ExceptieRepository exceptieRepository;
    @Mock
    private RegistrationModelMapper registrationModelMapper;
    private ProcesService service;
    private PageRequest pageable;

    @BeforeEach
    void initiateProcesService() {
        this.pageable = PageRequest.of(0, 10);
        this.registrationModelMapper = new RegistrationModelMapper(new ModelMapper());
        this.service = new ProcesService(this.processEntityRepository, this.verwerkingRepository, this.diagnoseRepository, this.exceptieRepository,
                this.registrationModelMapper);
    }

    @Test
    @Disabled
    void getAllProcess() {
        ProcesEntity pe1 = new ProcesEntity("oep", "REPROCESS", "api");
        ProcesEntity pe2 = new ProcesEntity("oep", "REPROCESS", "aanleverloket");
        final PageImpl<ProcesEntity> result = new PageImpl<>(List.of(pe1, pe2));
//        when(this.processEntityRepository.getReferenceById(this.pageable)).thenReturn(result);

        var page = this.service.getProcessen(null, null, null, null, null, false, false, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody();
        assertNotNull(page);
        assertEquals(page.getTotalElements(), result.getTotalElements());
        assertNotNull(page.getContent());
    }

    @Test
    void createProcessTest() {
        Proces proces = new Proces().trigger("oep").triggerType("REPROCESS").sourceLabel("api");
        ProcesEntity procesEntity = new ProcesEntity("oep", "REPROCESS", "api");

        when(this.processEntityRepository.save(any(ProcesEntity.class))).thenReturn(procesEntity);

        proces = this.service.createProces(proces).getBody();

        assertNotNull(proces);
        assertEquals(proces.getSourceLabel(), procesEntity.getSourceLabel());
        assertEquals(proces.getTriggerType(), procesEntity.getTriggerType());
        assertEquals(proces.getTrigger(), procesEntity.getTrigger());
    }

    @Test
    void getProcesTest() {
        ProcesEntity procesEntity = new ProcesEntity("oep", "REPROCESS", "api");
        when(this.processEntityRepository.findById("prcesses_id")).thenReturn(Optional.of(procesEntity));
        when(this.verwerkingRepository.getProcesExcepties(any(), any())).thenReturn(Page.empty());
        when(this.processEntityRepository.findById("prcesses_id")).thenReturn(Optional.of(procesEntity));

        var proces = this.service.getProces("prcesses_id").getBody();

        assertNotNull(proces);
        assertNotNull(proces.getId());
        assertEquals(proces.getSourceLabel(), procesEntity.getSourceLabel());
        assertEquals(proces.getTriggerType(), procesEntity.getTriggerType());
        assertEquals(proces.getTrigger(), procesEntity.getTrigger());
    }

    @Test
    void createVerwerkingTest() {
        VerwerkingEntity ve1 = new VerwerkingEntity("exe-1234", "oep", "dcnId", "external_123", "PROCESS", Severity.INFO);
        when(this.verwerkingRepository.save(any(VerwerkingEntity.class))).thenReturn(ve1);
        Verwerking verwerking = new Verwerking().stage("stage");
        assertEquals(HttpStatus.OK, this.service.createVerwerking("exe-1234", verwerking).getStatusCode());
    }

    @Test
    void getProcesVerwerkingenTest() {
        VerwerkingEntity ve = new VerwerkingEntity("exe-1234", "oep", "dcn_id_1", "external_123", "PROCESS", Severity.INFO);
//        ExceptieEntity exp = new ExceptieEntity("dcn_id_1", "oep");
        final PageImpl<VerwerkingEntity> result = new PageImpl<>(List.of(ve));
        when(this.diagnoseRepository
                .getDiagnosesByVerwerkingId(any(), any())).thenReturn(Page.empty());
        when(this.exceptieRepository
                .getFirstByVerwerkingId(anyString())).thenReturn(Optional.empty());
        when(this.verwerkingRepository.getVerwerkingByProcesId("prc_1", this.pageable)).thenReturn(result);
        assertEquals(this.service.getProcesVerwerkingen("prc_1", null, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody().getTotalElements(),
                result.getTotalElements());
    }

    @Test
    @Disabled
    void getLaasteSuccessfulProces() {
        ProcesEntity procesEntity = new ProcesEntity("oep", "REPROCESS", "api");
//        when(this.processEntityRepository.getLastSuccessful(procesEntity.getSourceLabel(), procesEntity.getTrigger())).thenReturn(Optional.of(procesEntity));
        var proces = (Proces) this.service.getProcessen(null, procesEntity.getSourceLabel(), procesEntity.getTrigger(), null, null, false, false, 0, 1)
                .getBody()
                .toList()
                .get(0);
        assertNotNull(proces);
        assertNotNull(proces.getId());
        assertEquals(proces.getSourceLabel(), procesEntity.getSourceLabel());
        assertEquals(proces.getTriggerType(), procesEntity.getTriggerType());
        assertEquals(proces.getTrigger(), procesEntity.getTrigger());
    }

}
