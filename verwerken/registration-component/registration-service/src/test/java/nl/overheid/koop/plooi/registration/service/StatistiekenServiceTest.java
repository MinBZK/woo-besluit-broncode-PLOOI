package nl.overheid.koop.plooi.registration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.util.List;
import nl.overheid.koop.plooi.dcn.process.service.VerwerkenClient;
import nl.overheid.koop.plooi.registration.model.ExceptieEntity;
import nl.overheid.koop.plooi.registration.model.ExceptieRepository;
import nl.overheid.koop.plooi.registration.model.LastVerwerkingState;
import nl.overheid.koop.plooi.registration.model.Severity;
import nl.overheid.koop.plooi.registration.model.SeveritySummaryResponse;
import nl.overheid.koop.plooi.registration.model.VerwerkingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class StatistiekenServiceTest {

    private PageRequest pageable;
    @Mock
    private VerwerkingRepository repository;
    @Mock
    private ExceptieRepository exceptieRepository;

    @Mock
    private VerwerkenClient verwerkenClient;

    private StatistiekenService service;

    @BeforeEach
    void initiateExceptieService() {
        this.service = new StatistiekenService(this.repository, this.exceptieRepository, this.verwerkenClient);
    }

    @Test
    void getVerwerkingExceptiesTest() {
        this.pageable = PageRequest.of(0, 10);
        ExceptieEntity ve1 = new ExceptieEntity("ver_123", "route1");
        ExceptieEntity ve2 = new ExceptieEntity("ver_124", "route2");

        final PageImpl<ExceptieEntity> result = new PageImpl<>(List.of(ve1, ve2));
        when(this.repository.getLaatsteStatusVerwerkingen(this.pageable)).thenReturn(result);
        assertEquals(result, this.service.getVerwerkingExcepties(null, null, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody());
    }

    @Test
    void getVerwerkingExceptiesWithSourceTest() {
        String bron = "api";
        this.pageable = PageRequest.of(0, 10);
        ExceptieEntity ve1 = new ExceptieEntity("ver_123", "route1");
        ExceptieEntity ve2 = new ExceptieEntity("ver_124", "route2");

        final PageImpl<ExceptieEntity> result = new PageImpl<>(List.of(ve1, ve2));
        when(this.repository.getLaatsteStatusVerwerkingenBySource(bron, this.pageable)).thenReturn(result);
        assertEquals(result, this.service.getVerwerkingExcepties(bron, null, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody());
    }

    @Test
    void getVerwerkingExceptiesWithSourceAndExceptieTest() {
        String bron = "api";
        String exceptie = "NPE";
        this.pageable = PageRequest.of(0, 10);
        ExceptieEntity ve1 = new ExceptieEntity("ver_123", "route1");
        ExceptieEntity ve2 = new ExceptieEntity("ver_124", "route2");

        final PageImpl<ExceptieEntity> result = new PageImpl<>(List.of(ve1, ve2));
        when(this.repository.getLaatsteStatusVerwerkingenBySourceAndExceptie(bron, exceptie, this.pageable)).thenReturn(result);
        assertEquals(result, this.service.getVerwerkingExcepties(bron, exceptie, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody());
    }

    @Test
    void getLaatsteVerwerkingDiagnoses() {
        String bron = "api";
        this.pageable = PageRequest.of(0, 10);
        SeveritySummaryResponse ve1 = new SeveritySummaryResponse("target1", "sourceLabel1", 10L);
        SeveritySummaryResponse ve2 = new SeveritySummaryResponse("target2", "sourceLabel2", 20L);

        final PageImpl<SeveritySummaryResponse> result = new PageImpl<>(List.of(ve1, ve2));
        when(this.repository.getLatestMappingErrorSummaryBySourceAndSeverity(bron, Severity.OK, this.pageable)).thenReturn(result);
        assertEquals(result,
                this.service.getLaatsteVerwerkingDiagnoses(bron, Severity.OK, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody());
    }

    @Test
    void getVerwerkingenWithDiagnoses() {
        String bron = "api";
        this.pageable = PageRequest.of(0, 10);
        LastVerwerkingState ve1 = new LastVerwerkingState(OffsetDateTime.now(), "sourceLabel1");
        LastVerwerkingState ve2 = new LastVerwerkingState(OffsetDateTime.now(), "sourceLabel2");

        final PageImpl<LastVerwerkingState> result = new PageImpl<>(List.of(ve1, ve2));
        when(this.repository.getVerwerkingenWithLatestDiagnoses(bron, "", "", Severity.OK, this.pageable)).thenReturn(result);
        assertEquals(result,
                this.service.getVerwerkingenWithDiagnoses(bron, "", "", Severity.OK, this.pageable.getPageNumber(), this.pageable.getPageSize()).getBody());
    }

    @Test
    void getExceptiesByBron() {
        String bron = "api";
        var result = List.of("exc_id", "exc_id_2");
        when(this.exceptieRepository.getExceptiesByBron(bron)).thenReturn(result);
        assertEquals(result, this.service.getExceptiesByBron(bron).getBody());
    }

    @Test
    void procesVerwekingActionWithEmptyPage() {
        when(this.repository.getVerwerkingenWithLatestDiagnoses(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));
        this.service.procesVerwekingActie("admin", "Document", "sourceLabel", Severity.ERROR, "123", "VERWERKING");
        verify(verwerkenClient, times(0)).process(any(), any(), any());
    }

    @Test
    void procesVerwekingActionWithMutliPage() {
        LastVerwerkingState ve1 = new LastVerwerkingState(OffsetDateTime.now(), "oep-12345678");
        LastVerwerkingState ve2 = new LastVerwerkingState(OffsetDateTime.now(), "ronl-98765432");
        when(this.repository.getVerwerkingenWithLatestDiagnoses(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(ve1, ve2)))
                .thenReturn(new PageImpl<>(List.of(ve1, ve2)))
                .thenReturn(new PageImpl<>(List.of()));
        this.service.procesVerwekingActie("admin", "Document", "sourceLabel", Severity.ERROR, "123", "VERWERKING");
        verify(verwerkenClient, times(2)).process(any(), any(), any());
    }
}
