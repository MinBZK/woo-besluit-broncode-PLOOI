package nl.overheid.koop.plooi.registration.service;

import java.util.List;
import javax.persistence.Tuple;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import nl.overheid.koop.plooi.dcn.process.service.VerwerkenClient;
import nl.overheid.koop.plooi.registration.api.StatistiekenApiDelegate;
import nl.overheid.koop.plooi.registration.model.ExceptieRepository;
import nl.overheid.koop.plooi.registration.model.LastVerwerkingState;
import nl.overheid.koop.plooi.registration.model.Severity;
import nl.overheid.koop.plooi.registration.model.VerwerkingRepository;
import nl.overheid.koop.plooi.registration.model.VerwerkingStatistieken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StatistiekenService implements StatistiekenApiDelegate {

    public static final int DEFAUL_PAGE_SIZE = 1000;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final VerwerkingRepository verwerkingRepository;
    private final ExceptieRepository exceptieRepository;
    private final VerwerkenClient verwerkenClient;

    public StatistiekenService(VerwerkingRepository verwerkingRepository, ExceptieRepository exceptieRepository, VerwerkenClient verwerkenClient) {
        this.verwerkingRepository = verwerkingRepository;
        this.exceptieRepository = exceptieRepository;
        this.verwerkenClient = verwerkenClient;
    }

    @Override
    public ResponseEntity<List<VerwerkingStatistieken>> getVerwerkingStatistieken() {
        this.logger.debug("get verwerkingen statistieken");
        return ResponseEntity.ok(getVerwerkingenStatistieken());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Page> getVerwerkingExcepties(String sourceName, String exception, Integer page, Integer size) {
        var pageable = PageRequest.of(page, size);
        if (StringUtils.isBlank(sourceName)) {
            this.logger.debug("get verwerkingen overview with excepties");
            return ResponseEntity.ok(this.verwerkingRepository.getLaatsteStatusVerwerkingen(pageable));
        } else if (StringUtils.isBlank(exception)) {
            this.logger.debug("get verwerkingen overview with excepties for source {}", sourceName);
            return ResponseEntity.ok(this.verwerkingRepository.getLaatsteStatusVerwerkingenBySource(sourceName, pageable));
        } else {
            this.logger.debug("get verwerkingen overview with excepties for source {} and expection {}", sourceName, exception);
            return ResponseEntity.ok(this.verwerkingRepository.getLaatsteStatusVerwerkingenBySourceAndExceptie(sourceName, exception, pageable));
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Page> getLaatsteVerwerkingDiagnoses(String sourceName, Severity severity, Integer page, Integer size) {
        var pageable = PageRequest.of(page, size);
        this.logger.debug("Verwerkingen overview with diagnoses for source {} and severity {}", sourceName, severity);
        return ResponseEntity.ok(this.verwerkingRepository.getLatestMappingErrorSummaryBySourceAndSeverity(sourceName, severity, pageable));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Void> procesVerwekingActie(String sourceName, String target, String sourceLabel, Severity severity, String procesId, String actie) {
        Pageable pageRequest = PageRequest.of(0, DEFAUL_PAGE_SIZE);
        this.logger.debug("For process {} , apply {} on verwerkingen for source {} and severity {}", procesId, actie, sourceLabel, severity);
        var resultPage =  this.verwerkingRepository.getVerwerkingenWithLatestDiagnoses(sourceName, target, sourceLabel, severity, pageRequest);
        while (!resultPage.isEmpty()) {
            pageRequest = pageRequest.next();
            this.verwerkenClient.process(VerwerkingActies.valueOf(actie), procesId,  resultPage.stream().map(LastVerwerkingState::getdcnId).toList());
            resultPage = this.verwerkingRepository.getVerwerkingenWithLatestDiagnoses(sourceName, target, sourceLabel, severity, pageRequest);
        }
        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Page> getVerwerkingenWithDiagnoses(String sourceName, String target, String sourceLabel, Severity severity, Integer page,
            Integer size) {
        this.logger.debug("get verwerkingen overview with diagnoses for source {}, target {}, {} and serverity {}", sourceName, target, sourceLabel, severity);
        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(this.verwerkingRepository.getVerwerkingenWithLatestDiagnoses(sourceName, target, sourceLabel, severity, pageable));
    }

    @Override
    public ResponseEntity<List<String>> getExceptiesByBron(String sourceLabel) {
        this.logger.debug("get all exceptions source : {}", sourceLabel);
        return ResponseEntity.ok(this.exceptieRepository.getExceptiesByBron(sourceLabel));
    }

    private List<VerwerkingStatistieken> getVerwerkingenStatistieken() {
        var latestExceptieCounts = this.verwerkingRepository.getLatestEventSeverityCountPerSourceBySeverity(Severity.EXCEPTION);
        var latestMappingErrorCount = this.verwerkingRepository.getLatestDiagnoseSeverityCountPerSourceBySeverity(Severity.ERROR);
        var latestMappingWarningCount = this.verwerkingRepository.getLatestDiagnoseSeverityCountPerSourceBySeverity(Severity.WARNING);
        return this.verwerkingRepository.getSourceNames()
                .stream()
                .map(sourceName -> new VerwerkingStatistieken()
                        .sourceName(sourceName)
                        .mappingErrorCount(lookupVerwerkingCounts(latestMappingErrorCount, sourceName))
                        .processingErrorCount(lookupVerwerkingCounts(latestExceptieCounts, sourceName))
                        .mappingWarningCount(lookupVerwerkingCounts(latestMappingWarningCount, sourceName)))
                .toList();
    }

    private Long lookupVerwerkingCounts(List<Tuple> list, String sourceName) {
        return list.stream().filter(t -> t.get(0) != null && t.get(0).equals(sourceName)).map(t -> t.get(1)).map(Long.class::cast).findAny().orElse(0L);
    }
}
