package nl.overheid.koop.plooi.registration.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationModelMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ModelMapper modelMapper;

    public RegistrationModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Exceptie convertToExceptie(ExceptieEntity ee) {
        this.logger.trace("Convert exceptie entity {} to exceptie", ee);
        return ee == null ? null : this.modelMapper.map(ee, Exceptie.class);
    }

    public ExceptieEntity convertToExceptieEntity(String verwerkingId, Exceptie exceptie) {
        if (exceptie != null) {
            this.logger.trace("Create ExceptieEntity from Exceptie {}", exceptie);
            ExceptieEntity entity = new ExceptieEntity(verwerkingId, exceptie.getFromRoute());
            entity.setStatusText(exceptie.getStatusText());
            entity.setStatusCode(exceptie.getStatusCode());
            entity.setExceptionClass(exceptie.getExceptionClass());
            entity.setMessageBody(exceptie.getMessageBody());
            entity.setExceptionMessage(exceptie.getExceptionMessage());
            entity.setExceptionStacktrace(exceptie.getExceptionStacktrace());
            return entity;
        }
        return null;
    }

    public Proces convertToProces(ProcesEntity pe) {
        this.logger.trace("convert proces from ProcesEntity {}", pe);
        return pe == null ? null : this.modelMapper.map(pe, Proces.class);
    }

    public ProcesCounts convertToProcesVerwerkingSeverityStats(ProcesVerwerkingSeverityStats es) {
        return es == null ? null : this.modelMapper.map(es, ProcesCounts.class);
    }

    public VerwerkingEntity createVerwerkingEntity(String procesId, Verwerking v) {
        var extId = Optional
                .ofNullable(v.getExtIds())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.joining(", "));
        return new VerwerkingEntity(procesId, v.getSourceLabel(), v.getDcnId(), extId, v.getStage(), v.getSeverity());
    }

    public Verwerking convertToVerwerking(VerwerkingEntity ve) {
        this.logger.trace("convert verwerking entity {} to verwerking ", ve);
        return ve == null ? null
                : new Verwerking()
                        .id(ve.getId())
                        .procesId(ve.getProcesId())
                        .dcnId(ve.getDcnId())
                        .sourceLabel(ve.getSourceLabel())
                        .extIds(StringUtils.isBlank(ve.getExtId()) ? null : List.of(ve.getExtId().split(", *")))
                        .stage(ve.getStage())
                        .severity(ve.getSeverity())
                        .timeCreated(ve.getTimeCreated());
    }

    public VerwerkingStatus convertToVerwerkingStatus(VerwerkingStatusEntity vse) {
        return this.modelMapper.map(vse, VerwerkingStatus.class);
    }

    public DiagnoseEntity convertToDiagnoseEntity(String verwerkingId, Diagnose d) {
        this.logger.trace("Create DiagnoseEntity from Diagnose {}", d);
        return new DiagnoseEntity(verwerkingId, d.getCode(), d.getSeverity(), d.getMessage(), d.getSourceId(), d.getSourceLabel(), d.getTarget());
    }

    public Diagnose convertToDiagnose(DiagnoseEntity de) {
        return this.modelMapper.map(de, Diagnose.class);
    }
}
