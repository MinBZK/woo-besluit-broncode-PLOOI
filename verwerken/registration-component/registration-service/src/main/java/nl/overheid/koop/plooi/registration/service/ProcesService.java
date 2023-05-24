package nl.overheid.koop.plooi.registration.service;

import java.time.OffsetDateTime;
import java.util.List;
import javax.persistence.Tuple;
import nl.overheid.koop.plooi.registration.api.ProcessenApiDelegate;
import nl.overheid.koop.plooi.registration.model.DiagnoseRepository;
import nl.overheid.koop.plooi.registration.model.ExceptieRepository;
import nl.overheid.koop.plooi.registration.model.Proces;
import nl.overheid.koop.plooi.registration.model.ProcesCounts;
import nl.overheid.koop.plooi.registration.model.ProcesEntity;
import nl.overheid.koop.plooi.registration.model.ProcesRepository;
import nl.overheid.koop.plooi.registration.model.RegistrationModelMapper;
import nl.overheid.koop.plooi.registration.model.Severity;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import nl.overheid.koop.plooi.registration.model.VerwerkingRepository;
import nl.overheid.koop.plooi.registration.service.error.ErrorResponseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProcesService implements ProcessenApiDelegate {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ProcesRepository procesRepository;
    private final VerwerkingRepository verwerkingRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final ExceptieRepository exceptieRepository;
    private final RegistrationModelMapper registrationModelMapper;

    public ProcesService(ProcesRepository procesRepos, VerwerkingRepository verwerkingRepos, DiagnoseRepository diagnoseRepos, ExceptieRepository exceptieRepos,
            RegistrationModelMapper modelMapper) {
        this.procesRepository = procesRepos;
        this.verwerkingRepository = verwerkingRepos;
        this.diagnoseRepository = diagnoseRepos;
        this.exceptieRepository = exceptieRepos;
        this.registrationModelMapper = modelMapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Page> getProcessen(List<String> ids, String sourceLabel, String triggerType, OffsetDateTime timeCreatedVan,
            OffsetDateTime timeCreatedTot, Boolean alleenFalend, Boolean zonderTellingen, Integer pageNr, Integer pageSize) {
        this.logger.debug("get processen pagina {}", pageNr);
        var pageable = PageRequest.of(pageNr, pageSize, Sort.by("timeCreated").descending());
        return ResponseEntity.ok(getProcessenWithCounts(pageable, ids, sourceLabel, triggerType, timeCreatedVan, timeCreatedTot,
                alleenFalend.booleanValue(), zonderTellingen.booleanValue()));
    }

    @Override
    public ResponseEntity<Proces> createProces(Proces proces) {
        this.logger.debug("create proces triggered by {} ", proces.getTrigger());
        if (StringUtils.isBlank(proces.getTriggerType())) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "triggerType is required");
        } else if (StringUtils.isBlank(proces.getTrigger())) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "trigger is required");
        }
        var procesEntity = this.procesRepository.save(new ProcesEntity(proces.getSourceLabel(), proces.getTriggerType(), proces.getTrigger()));
        return ResponseEntity.ok(this.registrationModelMapper.convertToProces(procesEntity));
    }

    @Override
    public ResponseEntity<Proces> getProces(String id) {
        this.logger.debug("get proces by id {}", id);
        return this.procesRepository
                .findById(id)
                .map(this.registrationModelMapper::convertToProces)
                .map(this::addExcepties)
                .map(this::addProcessCounts)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Verwerking> createVerwerking(String id, Verwerking verwerking) {
        this.logger.debug("Create verwerking for process id {}", id);
        if (StringUtils.isBlank(verwerking.getStage())) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "stage is required");
        }
        if (verwerking.getExceptie() == null) {
            verwerking.severity(Severity.OK);
            if (verwerking.getDiagnoses() != null && !verwerking.getDiagnoses().isEmpty()) {
                for (var severity : Severity.values()) {
                    if (verwerking.getDiagnoses().stream().anyMatch(d -> d.getSeverity() == severity)) {
                        verwerking.severity(severity);
                    }
                }
            }
        } else {
            verwerking.severity(Severity.EXCEPTION);
        }
        var vEntity = this.verwerkingRepository.save(this.registrationModelMapper.createVerwerkingEntity(id, verwerking));
        if (verwerking.getExceptie() != null) {
            this.logger.debug("Create exceptie for verwerking with id {}", vEntity.getId());
            this.exceptieRepository.save(this.registrationModelMapper.convertToExceptieEntity(vEntity.getId(), verwerking.getExceptie()));
        }
        if (verwerking.getDiagnoses() != null && !verwerking.getDiagnoses().isEmpty()) {
            for (var diagnose : verwerking.getDiagnoses()) {
                if (diagnose.getSeverity() == null) {
                    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "diagnose.severity is required");
                } else if (StringUtils.isBlank(diagnose.getCode())) {
                    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "diagnose.code is required");
                } else if (StringUtils.isBlank(diagnose.getMessage())) {
                    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "diagnose.message is required");
                }
                this.logger.debug("Create diagnose for verwerking with id {}", vEntity.getId());
                this.diagnoseRepository.save(this.registrationModelMapper.convertToDiagnoseEntity(vEntity.getId(), diagnose));
            }
        }
        return ResponseEntity.ok(this.registrationModelMapper.convertToVerwerking(vEntity));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Page> getProcesVerwerkingen(String id, Severity severity, Integer pageNr, Integer pageSize) {
        var page = PageRequest.of(pageNr, pageSize);
        this.logger.debug("get all verwerkingen for process with id {} ", id);
        var verwerkingEntities = severity == null
                ? this.verwerkingRepository.getVerwerkingByProcesId(id, page)
                : this.verwerkingRepository.getVerwerkingByProcesIdAndSeverity(id, severity, page);
        if (verwerkingEntities.isEmpty() && this.procesRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            var result = verwerkingEntities.getContent()
                    .stream()
                    .map(this.registrationModelMapper::convertToVerwerking)
                    .map(this::addDiagnoses)
                    .map(this::addExceptie)
                    .toList();
            return ResponseEntity.ok(new PageImpl<>(result, page, verwerkingEntities.getTotalPages()));
        }
    }

    private Proces addExcepties(Proces proces) {
        var page = PageRequest.of(0, 100);
        this.verwerkingRepository.getProcesExcepties(proces.getId(), page)
                .getContent()
                .stream()
                .map(this.registrationModelMapper::convertToExceptie)
                .forEach(proces::addExceptiesItem);
        return proces;
    }

    private Proces addProcessCounts(Proces proces) {
        this.logger.debug("add proces counts {}", proces.getId());
        var processCounts = this.verwerkingRepository.getProcesSeverityStatistieken(proces.getId());
        proces.setProcesCounts(this.registrationModelMapper.convertToProcesVerwerkingSeverityStats(processCounts));
        return proces;
    }

    private Page<Proces> getProcessenWithCounts(Pageable page, List<String> ids, String sourceLabel, String triggerType, OffsetDateTime timeCreatedVan,
            OffsetDateTime timeCreatedTot, boolean alleenFalend, boolean zonderTellingen) {
        var processen = this.procesRepository
                .findAll(ProcesRepository.querySpecification(ids, sourceLabel, triggerType, timeCreatedVan, timeCreatedTot, alleenFalend), page);
        var procesIds = zonderTellingen ? null : processen.map(ProcesEntity::getId).getContent();
        var documentCount = zonderTellingen ? null : this.verwerkingRepository.getVerwerkingCountOnProcesIds(procesIds);
        var processingErrorCount = zonderTellingen ? null : this.verwerkingRepository.getExceptiesOnProcessen(procesIds);
        var mappingErrorCount = zonderTellingen ? null : this.verwerkingRepository.getVerwerkingErrorsOnProcesIds(procesIds, Severity.ERROR);
        var verwerkingExceptionsCount = zonderTellingen ? null
                : this.verwerkingRepository.getVerwerkingErrorsOnProcesIds(procesIds, Severity.EXCEPTION);
        return new PageImpl<>(
                processen.getContent()
                        .stream()
                        .map(pe -> {
                            Proces proces = this.registrationModelMapper.convertToProces(pe);
                            if (!zonderTellingen) {
                                proces.setProcesCounts(new ProcesCounts()
                                        .documentCount(lookupProcesCounts(documentCount, pe))
                                        .errorCount(lookupProcesCounts(mappingErrorCount, pe))
                                        .exceptionCount(lookupProcesCounts(verwerkingExceptionsCount, pe))
                                        .procesExceptionCount(lookupProcesCounts(processingErrorCount, pe)));
                            }
                            return proces;
                        })
                        .toList(),
                page,
                processen.getTotalElements());
    }

    private Long lookupProcesCounts(List<Tuple> list, ProcesEntity proces) {
        return list.stream().filter(t -> proces.getId().equals(t.get(0))).map(t -> t.get(1)).map(Long.class::cast).findAny().orElse(0L);
    }

    private Verwerking addExceptie(Verwerking verwerking) {
        return verwerking.exceptie(this.registrationModelMapper.convertToExceptie(
                this.exceptieRepository.getFirstByVerwerkingId(verwerking.getId()).orElse(null)));
    }

    private Verwerking addDiagnoses(Verwerking verwerking) {
        var pageable = PageRequest.of(0, 100);
        this.diagnoseRepository
                .getDiagnosesByVerwerkingId(verwerking.getId(), pageable)
                .getContent()
                .forEach(d -> verwerking.addDiagnosesItem(this.registrationModelMapper.convertToDiagnose(d)));
        return verwerking;
    }
}
