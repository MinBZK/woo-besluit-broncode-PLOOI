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
import nl.overheid.koop.plooi.registration.model.RegistrationModelMapper;
import nl.overheid.koop.plooi.registration.model.Severity;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import nl.overheid.koop.plooi.registration.model.VerwerkingEntity;
import nl.overheid.koop.plooi.registration.model.VerwerkingRepository;
import nl.overheid.koop.plooi.registration.model.VerwerkingStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class VerwerkingServiceTest {

    private PageRequest pageable;
    @Mock
    private VerwerkingRepository repository;

    @Mock
    private VerwerkingStatusRepository verwerkingStatusRepository;

    @Mock(lenient = true)
    private DiagnoseRepository diagnoseRepository;

    @Mock(lenient = true)
    private ExceptieRepository exceptieRepository;
    private VerwerkingService service;

    private RegistrationModelMapper modelMapper;

    @BeforeEach
    void initiateAdminService() {
        this.modelMapper = new RegistrationModelMapper(new ModelMapper());
        this.pageable = PageRequest.of(0, 1);
        this.service = new VerwerkingService(this.repository, this.verwerkingStatusRepository, this.diagnoseRepository,
                this.exceptieRepository, this.modelMapper);
    }

    @Test
    void getVerwerkingBydcnIdTest() {
        String dcnId = "dcn_id_1";
        VerwerkingEntity ve1 = new VerwerkingEntity("exe-1234", "oep", dcnId, "external_123", "PROCESS", Severity.INFO);
        VerwerkingEntity ve2 = new VerwerkingEntity("exe-1235", "oep", dcnId, "external_123", "PROCESS", Severity.OK);

        final PageImpl<VerwerkingEntity> result = new PageImpl<>(List.of(ve1, ve2));
        when(this.repository.getVerwerkingenByDcnId(dcnId, this.pageable)).thenReturn(result);
        when(this.diagnoseRepository.getDiagnosesByVerwerkingId(any(), any())).thenReturn(Page.empty());
        when(this.exceptieRepository.getFirstByVerwerkingId(anyString())).thenReturn(Optional.empty());

        var response = this.service.getVerwerkingen(dcnId, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody();
        assertNotNull(response);
        assertEquals(2, response.getTotalElements(), 2);

        @SuppressWarnings("unchecked")
        List<Verwerking> list = response.getContent();
        assertEquals("oep", list.get(0).getSourceLabel());
        assertEquals(List.of("external_123"), list.get(0).getExtIds());
        assertEquals(Severity.INFO, list.get(0).getSeverity());
    }
}
