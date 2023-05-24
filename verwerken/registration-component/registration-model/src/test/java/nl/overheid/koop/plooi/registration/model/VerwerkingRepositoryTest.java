package nl.overheid.koop.plooi.registration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = TestConfiguration.class)
@PropertySource("classpath:application-repos.properties")
class VerwerkingRepositoryTest {

    @Autowired
    private VerwerkingRepository verwerkingRepository;
    @Autowired
    private ProcesRepository procesRepository;
    @Autowired
    private ExceptieRepository exceptieRepository;
    @Autowired
    private DiagnoseRepository diagnoseRepository;
    @Autowired
    private VerwerkingStatusRepository verwerkingStatusRepository;

    @Test
    void getVerwerkingenByDcnIdTest() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));

        var exec2 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var event4 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));

        var verwerkingResult = this.verwerkingRepository.getVerwerkingenByDcnId("id-1234", PageRequest.of(0, 5));
        assertEquals(2, verwerkingResult.getTotalElements());
        assertTrue(verwerkingResult.getContent().containsAll(List.of(event3, event1)));
        assertTrue(verwerkingResult.getContent().indexOf(event3) < verwerkingResult.getContent().indexOf(event1));
        assertFalse(verwerkingResult.getContent().containsAll(List.of(event2, event4)));
    }

    @Test
    void getVerwerkingInternalIdsBySourceName() {
        var proces1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var verwerking1 = this.verwerkingRepository
                .save(new VerwerkingEntity(proces1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var verwerking2 = this.verwerkingRepository
                .save(new VerwerkingEntity(proces1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(verwerking1.getDcnId(), verwerking1.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(verwerking2.getDcnId(), verwerking2.getProcesId()));

        var proces2 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var verwerking3 = this.verwerkingRepository
                .save(new VerwerkingEntity(proces2.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var verwerking4 = this.verwerkingRepository
                .save(new VerwerkingEntity(proces2.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(verwerking3.getDcnId(), verwerking3.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(verwerking4.getDcnId(), verwerking4.getProcesId()));

        var proces3 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var verwerking5 = this.verwerkingRepository
                .save(new VerwerkingEntity(proces3.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var verwerking6 = this.verwerkingRepository
                .save(new VerwerkingEntity(proces3.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(verwerking5.getDcnId(), verwerking5.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(verwerking6.getDcnId(), verwerking6.getProcesId()));

        var verwerkingDcnIds = this.verwerkingRepository.getVerwerkingDcnIdsBySourceName("testSource1");
        assertEquals(2, verwerkingDcnIds.size());
        assertTrue(verwerkingDcnIds.containsAll(Arrays.asList("id-1234", "id-1235")));
    }

    @Test
    @Disabled("The quite complicated native Postgres query does not compile on H2")
    void getSourceNames() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));

        var exec2 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-1234", "extId1", "INGRESS", Severity.OK));

        var exec3 = this.procesRepository.save(new ProcesEntity("testSource3", "INGRESS", "one"));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource3", "id-1234", "extId1", "INGRESS", Severity.OK));

        var exec4 = this.procesRepository.save(new ProcesEntity("testSource4", "INGRESS", "one"));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec4.getId(), "testSource4", "id-1234", "extId1", "INGRESS", Severity.OK));

        var sourceNames = this.verwerkingRepository.getSourceNames();
        assertEquals(4, sourceNames.size());
        assertTrue(sourceNames.containsAll(Arrays.asList("testSource1", "testSource2", "testSource3", "testSource4")));
    }

    @Test
    void getVerwerkingsWithLatestExceptie() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event1.getDcnId(), event1.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event2.getDcnId(), event2.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event3.getDcnId(), event3.getProcesId()));

        var pe1 = this.exceptieRepository.save(new ExceptieEntity(event1.getId(), "test"));
        var pe2 = this.exceptieRepository.save(new ExceptieEntity(event3.getId(), "test"));

        var verwerkingStatusResponses = this.verwerkingRepository.getLaatsteStatusVerwerkingen(PageRequest.of(0, 10));
        assertEquals(2, verwerkingStatusResponses.getTotalElements());

        // Because the order is important, we can retrieve the latest event first and use list index
        var responseList = verwerkingStatusResponses.getContent();
        assertEquals(pe2.getId(), responseList.get(0).getId());
        assertEquals(pe1.getId(), responseList.get(1).getId());
    }

    @Test
    void getVerwerkingStatusResponseWithLatestExceptieBySource() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event1.getDcnId(), event1.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event2.getDcnId(), event2.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event3.getDcnId(), event3.getProcesId()));

        var pe1 = this.exceptieRepository.save(new ExceptieEntity(event1.getId(), "test"));
        var pe2 = this.exceptieRepository.save(new ExceptieEntity(event3.getId(), "test"));

        var exec2 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        var event4 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-1237", "extId2", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event4.getDcnId(), event4.getProcesId()));

        this.exceptieRepository.save(new ExceptieEntity(event4.getId(), "test"));

        var verwerkingStatusResponses = this.verwerkingRepository.getLaatsteStatusVerwerkingenBySource("testSource1", PageRequest.of(0, 5));
        assertEquals(2, verwerkingStatusResponses.getTotalElements());
        // Because the order is important, we can retrieve the latest first and use list index
        var responseList = verwerkingStatusResponses.getContent();
        assertEquals(pe2.getId(), responseList.get(0).getId());
        assertEquals(pe1.getId(), responseList.get(1).getId());
    }

    @Test
    void getLatestMappingErrorSummaryBySourceAndSeverity() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.INFO));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.INFO));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "INGRESS", Severity.INFO));
        var event4 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1237", "extId4", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event1.getDcnId(), event1.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event2.getDcnId(), event2.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event3.getDcnId(), event3.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event4.getDcnId(), event4.getProcesId()));

        this.diagnoseRepository
                .save(new DiagnoseEntity(event1.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel1", "targetElementName1"));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event1.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel2", "targetElementName2"));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event1.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel3", "targetElementName3"));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event1.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel1", "targetElementName4"));

        var mappingErrorSummaryResults = this.verwerkingRepository.getLatestMappingErrorSummaryBySourceAndSeverity("testSource1", Severity.ERROR,
                PageRequest.of(0, 5));

        assertEquals(2, mappingErrorSummaryResults.getTotalElements());

        var summaryResponses = mappingErrorSummaryResults.getContent();
        assertEquals(1L, summaryResponses.get(0).getCount());
        assertEquals("sourceLabel1", summaryResponses.get(0).getSourceLabel());
        assertEquals("targetElementName1", summaryResponses.get(0).getTargetElementName());

        assertEquals(1L, summaryResponses.get(1).getCount());
        assertEquals("sourceLabel2", summaryResponses.get(1).getSourceLabel());
        assertEquals("targetElementName2", summaryResponses.get(1).getTargetElementName());
    }

    @Test
    void getVerwerkingenWithLatestDiagnoses() {
        // proces 1 with testSource 1
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event1.getDcnId(), event1.getProcesId()));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event1.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel", "targetElementName"));

        // proces 2 with testSource 2
        var exec2 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-1234", "extId1", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event2.getDcnId(), event2.getProcesId()));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event2.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel", "targetElementName"));

        // proces 3 with testSource 2
        var exec3 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-1234", "extId1", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event3.getDcnId(), event3.getProcesId()));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event3.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel", "targetElementName"));

        var event4 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-1234", "extId1", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event4.getDcnId(), event4.getProcesId()));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event4.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel-2", "targetElementName-2"));

        var event5 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-1234", "extId1", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event5.getDcnId(), event5.getProcesId()));
        this.diagnoseRepository.save(new DiagnoseEntity(event5.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel", "targetElementName"));

        var verwerkingResultsWarning = this.verwerkingRepository.getVerwerkingenWithLatestDiagnoses("testSource2", "targetElementName",
                "sourceLabel", Severity.WARNING,
                PageRequest.of(0, 5));

        var verwerkingResultsError = this.verwerkingRepository.getVerwerkingenWithLatestDiagnoses("testSource2", "targetElementName", "sourceLabel",
                Severity.ERROR,
                PageRequest.of(0, 5));

        assertEquals(1, verwerkingResultsWarning.getTotalElements());
        var verwerkingsDiagWarning = verwerkingResultsWarning.getContent();
        assertEquals(verwerkingsDiagWarning.get(0).getdcnId(), event3.getDcnId());
        var verwerkingsDiagError = verwerkingResultsError.getContent();
        assertEquals(1, verwerkingResultsError.getTotalElements());
        assertEquals(verwerkingsDiagError.get(0).getdcnId(), event5.getDcnId());
    }

    @Test
    void getLatestMappingSeverityCountPerSourceBySeverity() {
        // proces 1 with testSource 1
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event1.getDcnId(), event1.getProcesId()));

        this.diagnoseRepository
                .save(new DiagnoseEntity(event1.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel1", "targetElementName1"));

        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event2.getDcnId(), event2.getProcesId()));

        this.diagnoseRepository
                .save(new DiagnoseEntity(event2.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel2", "targetElementName2"));

        // proces 2 with testSource 2
        var exec2 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-2234", "extId1", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event3.getDcnId(), event3.getProcesId()));

        this.diagnoseRepository
                .save(new DiagnoseEntity(event3.getId(), "UNKNOWN_LABEL", Severity.WARNING, "message", null, "sourceLabel3", "targetElementName3"));

        // proces 3 with testSource 2
        var exec3 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        var event4 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-2234", "extId21", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event4.getDcnId(), event4.getProcesId()));

        this.diagnoseRepository
                .save(new DiagnoseEntity(event4.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel4", "targetElementName4"));

        var event5 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-2235", "extId22", "INGRESS", Severity.INFO));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event5.getDcnId(), event5.getProcesId()));

        this.diagnoseRepository
                .save(new DiagnoseEntity(event5.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel5", "targetElementName5"));

        var groupCountWarningResults = this.verwerkingRepository.getLatestDiagnoseSeverityCountPerSourceBySeverity(Severity.WARNING);
        var groupCountErrorResults = this.verwerkingRepository.getLatestDiagnoseSeverityCountPerSourceBySeverity(Severity.ERROR);

        // warnings expected:
        //
        // one entry for testSource1
        // testSource1 should have 1 count of warning
        assertEquals(1, groupCountWarningResults.size());
        assertEquals(1L, groupCountWarningResults.get(0).get(1));
        assertEquals("testSource1", groupCountWarningResults.get(0).get(0));

        // errors expected:
        //
        // two entries for testSource1 and testSource2
        // testSource1 should have 1 count of error
        // testSource2 should have 2 count of error
        assertEquals(2, groupCountErrorResults.size());
        assertEquals(1L, groupCountErrorResults.get(0).get(1));
        assertEquals("testSource1", groupCountErrorResults.get(0).get(0));
        assertEquals(2L, groupCountErrorResults.get(1).get(1));
        assertEquals("testSource2", groupCountErrorResults.get(1).get(0));
    }

    @Test
    void getLatestExceptieCountPerSource() {
        // proces1, testSource1, 3 events and 3 processing erros
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.EXCEPTION));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.EXCEPTION));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "INGRESS", Severity.EXCEPTION));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event1.getDcnId(), exec1.getId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event2.getDcnId(), exec1.getId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event3.getDcnId(), exec1.getId()));

        // proces2, testSource2, 1 events with no processing error for "id-1235", 'hiding' that document in previous exec
        var exec2 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event4 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-1235", "extId2", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event4.getDcnId(), exec2.getId()));

        // proces3, testSource2, 2 events with one processing error for "id-2231"
        var exec3 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        var event5 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-2231", "extId21", "INGRESS", Severity.EXCEPTION));
        var event6 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-2232", "extId22", "INGRESS", Severity.OK));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event5.getDcnId(), exec3.getId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event6.getDcnId(), exec3.getId()));

        var groupCountErrorResults = this.verwerkingRepository.getLatestEventSeverityCountPerSourceBySeverity(Severity.EXCEPTION);

        // errors expected:
        //
        // two entries for testSource1
        // one entry for testSource2
        assertEquals(2, groupCountErrorResults.size());
        assertEquals(2L, groupCountErrorResults.get(0).get(1));
        assertEquals("testSource1", groupCountErrorResults.get(0).get(0));
        assertEquals(1L, groupCountErrorResults.get(1).get(1));
        assertEquals("testSource2", groupCountErrorResults.get(1).get(0));
    }

    @Test
    void getProcesVerwerkingenById() {
        // proces1, testSource1, 2 events for same document
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId2", "INGRESS", Severity.OK));

        // proces2, testSource1, 2 events for same document
        var exec2 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-1234", "extId1", "INGRESS", Severity.OK));
        var event4 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-1234", "extId2", "INGRESS", Severity.OK));

        var verwerkingenQuery = this.verwerkingRepository.getVerwerkingByProcesId(exec1.getId(), PageRequest.of(0, 5));
        var verwerkingen = verwerkingenQuery.getContent();
        assertEquals(2, verwerkingenQuery.getTotalElements());
        assertTrue(verwerkingen.contains(event1));
        assertTrue(verwerkingen.contains(event2));
        assertFalse(verwerkingen.contains(event3));
        assertFalse(verwerkingen.contains(event4));
        assertEquals(exec1.getId(), verwerkingen.get(0).getProcesId());
        assertEquals(exec1.getId(), verwerkingen.get(1).getProcesId());
    }

    @Test
    void getMappingErrorsOnProcesId() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));
        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.INFO));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event1.getId(), "EMPTY_TEXT", Severity.OK, "message", null, null, null));

        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.INFO));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event2.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel2", "targetElementName2"));

        // proces 2 with testSource 2
        var exec2 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource2", "id-2234", "extId1", "INGRESS", Severity.INFO));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event3.getId(), "EMPTY_TEXT", Severity.INFO, "message", null, "sourceLabel3", "targetElementName3"));

        // proces 3 with testSource 2
        var exec3 = this.procesRepository.save(new ProcesEntity("testSource2", "INGRESS", "one"));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-2234", "extId21", "INGRESS", Severity.ERROR));

        var event5 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec3.getId(), "testSource2", "id-2235", "extId22", "INGRESS", Severity.INFO));
        this.diagnoseRepository
                .save(new DiagnoseEntity(event5.getId(), "REQUIRED", Severity.ERROR, "message", null, "sourceLabel5", "targetElementName5"));

        var groupCountResponse = this.verwerkingRepository.getVerwerkingErrorsOnProcesIds(List.of(exec1.getId(), exec2.getId(), exec3.getId()), Severity.ERROR);
        assertEquals(1, groupCountResponse.size());
        assertEquals(exec3.getId(), groupCountResponse.get(0).get(0));
        assertEquals(1L, groupCountResponse.get(0).get(1));
    }

    @Test
    void getVerwerkingCountOnProcesId() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));

        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "INGRESS", Severity.OK));

        // Save documentState records
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event1.getDcnId(), event1.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event2.getDcnId(), event2.getProcesId()));
        this.verwerkingStatusRepository.save(new VerwerkingStatusEntity(event3.getDcnId(), event3.getProcesId()));

        var counts = this.verwerkingRepository.getVerwerkingCountOnProcesIds(List.of(exec1.getId()));
        assertEquals(1, counts.size());
        // assertEquals(3L, counts.get(0).get(1));
    }

    @Test
    void getProcesSeverityStatistieken() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource", "INGRESS", "one"));

        // 3 distinct internal ids, 1 OK, 2 INFO, 3 WARNING, 0 ERROR
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.INFO));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "INGRESS", Severity.INFO));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.WARNING));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.WARNING));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "INGRESS", Severity.WARNING));

        var procesDocumentSeverityStats = this.verwerkingRepository.getProcesSeverityStatistieken(exec1.getId());

        assertEquals(6, procesDocumentSeverityStats.getTotalCount());
        assertEquals(1, procesDocumentSeverityStats.getOkCount());
        assertEquals(2, procesDocumentSeverityStats.getInfoCount());
        assertEquals(3, procesDocumentSeverityStats.getWarningCount());
        assertEquals(0, procesDocumentSeverityStats.getErrorCount());
    }

    @Test
    void getProcesDocumentStatistieken() {
        // Proces1: 3 distinct internal ids, 6 document events, 2 processing errors
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource", "INGRESS", "one"));

        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1234", "extId1", "PROCESS", Severity.INFO));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId2", "INGRESS", Severity.OK));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1235", "extId1", "PROCESS", Severity.WARNING));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId2", "INGRESS", Severity.OK));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), "testSource1", "id-1236", "extId3", "PROCESS", Severity.ERROR));

        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.EXCEPTION));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.EXCEPTION));

        // Proces2: 1 distinct internal ids, 2 document events, 0 processing errors
        var exec2 = this.procesRepository.save(new ProcesEntity("testSource", "INGRESS", "two"));

        this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource1", "id-1234", "extId1", "INGRESS", Severity.OK));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec2.getId(), "testSource1", "id-1234", "extId1", "PROCESS", Severity.INFO));

        // test Proces1
        var procesVerwerkingStats1 = this.verwerkingRepository.getProcesSeverityStatistieken(exec1.getId());
        assertEquals(6, procesVerwerkingStats1.getTotalCount());
        assertEquals(3, procesVerwerkingStats1.getDocumentCount());

        var procesProcessErrorStats1 = this.verwerkingRepository.getExceptiesOnProcessen(List.of(exec1.getId()));
        assertEquals(1, procesProcessErrorStats1.size());
        assertEquals(2L, procesProcessErrorStats1.get(0).get(1));

        // test Proces2
        var procesVerwerkingStats2 = this.verwerkingRepository.getProcesSeverityStatistieken(exec2.getId());
        assertEquals(1, procesVerwerkingStats2.getDocumentCount());
        assertEquals(2, procesVerwerkingStats2.getTotalCount());

        var procesProcessErrorStats2 = this.verwerkingRepository.getExceptiesOnProcessen(List.of(exec2.getId()));
        assertEquals(0, procesProcessErrorStats2.size());
    }

    @Test
    void getExceptiesOnProcesIds() {
        // proces1, testSource1, 3 events and 3 processing errors
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));

        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.EXCEPTION));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.EXCEPTION));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.EXCEPTION));
        var groupCountResponse = this.verwerkingRepository.getExceptiesOnProcessen(List.of(exec1.getId()));
        assertEquals(1, groupCountResponse.size());
        assertEquals(3L, groupCountResponse.get(0).get(1));
    }

    @Test
    void getVerwerkingStatusResponseByProcesId() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));

        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, "123", null, "INGRESS", Severity.EXCEPTION));
        this.exceptieRepository.save(new ExceptieEntity(event1.getId(), "test"));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, "124", null, "INGRESS", Severity.INFO));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.OK));
        var verwerkingEntities = this.verwerkingRepository.getVerwerkingByProcesId(exec1.getId(), PageRequest.of(0, 5));
        assertEquals(2, verwerkingEntities.getContent().size());
        assertEquals(2, verwerkingEntities.getContent().size());
    }

    @Test
    void getVerwerkingStatusResponseByProcesIdAndSeverity() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));

        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, "123", null, "INGRESS", Severity.EXCEPTION));
        this.exceptieRepository.save(new ExceptieEntity(event1.getId(), "test"));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, "124", null, "INGRESS", Severity.INFO));
        this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.OK));
        var verwerkingStatusResponses = this.verwerkingRepository.getVerwerkingByProcesIdAndSeverity(exec1.getId(),
                Severity.EXCEPTION, PageRequest.of(0, 5));
        assertEquals(1, verwerkingStatusResponses.getContent().size());
        assertEquals(1, verwerkingStatusResponses.getContent().stream().filter(Objects::nonNull).count());
        assertEquals(1, verwerkingStatusResponses.getContent().stream().filter(Objects::nonNull).count());

        var verwerkingStatusResponsesInfo = this.verwerkingRepository.getVerwerkingByProcesIdAndSeverity(exec1.getId(), Severity.INFO,
                PageRequest.of(0, 5));
        assertEquals(1, verwerkingStatusResponsesInfo.getContent().size());
    }

    @Test
    void getEventStatusResponseByProcesId() {
        var exec1 = this.procesRepository.save(new ProcesEntity("testSource1", "INGRESS", "one"));

        var event1 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.EXCEPTION));
        var event2 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.INFO));
        var event3 = this.verwerkingRepository
                .save(new VerwerkingEntity(exec1.getId(), null, null, null, "INGRESS", Severity.OK));

        this.exceptieRepository.save(new ExceptieEntity(event1.getId(), null));
        this.exceptieRepository.save(new ExceptieEntity(event2.getId(), null));
        this.exceptieRepository.save(new ExceptieEntity(event3.getId(), null));

        var verwerkingStatusResponses = this.verwerkingRepository.getProcesExcepties(exec1.getId(), PageRequest.of(0, 5));
        assertEquals(3, verwerkingStatusResponses.getContent().size());
        assertEquals(3, verwerkingStatusResponses.getContent().stream().filter(Objects::nonNull).count());
        assertEquals(3, verwerkingStatusResponses.getContent().stream().filter(Objects::nonNull).count());
    }
}
