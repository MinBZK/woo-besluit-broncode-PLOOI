package nl.overheid.koop.plooi.registration.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class RegistrationModelMapperTest {

    private RegistrationModelMapper registrationModelMapper;
    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void initiateRegistrationModelMapper() {
        this.registrationModelMapper = new RegistrationModelMapper(this.modelMapper);
    }

    @Test
    void convertExceptieEntityToExceptieTest() {
        ExceptieEntity exceptieEntity = new ExceptieEntity("ver_1", "api");
        exceptieEntity.setMessageBody("Body");
        exceptieEntity.setExceptionClass("NullPointer");
        exceptieEntity.setStatusText("Status");

        Exceptie exceptie = this.registrationModelMapper.convertToExceptie(exceptieEntity);

        assertEquals(exceptieEntity.getMessageBody(), exceptie.getMessageBody());
        assertEquals(exceptieEntity.getExceptionClass(), exceptie.getExceptionClass());
        assertEquals(exceptieEntity.getStatusText(), exceptie.getStatusText());
        assertEquals(exceptieEntity.getFromRoute(), exceptie.getFromRoute());
    }

    @Test
    void getConvertExceptieToExceptieEntityTest() {
        Exceptie exceptie = new Exceptie()
                .messageBody("message")
                .exceptionClass("null pointer")
                .fromRoute("fromRoute")
                .statusText("statusText");
        ExceptieEntity entity = this.registrationModelMapper.convertToExceptieEntity("ver_id", exceptie);

        assertEquals(exceptie.getMessageBody(), entity.getMessageBody());
        assertEquals(exceptie.getExceptionClass(), entity.getExceptionClass());
        assertEquals(exceptie.getStatusText(), entity.getStatusText());
        assertEquals(exceptie.getFromRoute(), entity.getFromRoute());
    }

    @Test
    void convertVerwerkingEnitityToVerwerking() {
        VerwerkingEntity verwerkingEntity = new VerwerkingEntity("exe-1234", "oep", "dcn_id", "external_123", "PROCESS", Severity.INFO);
        Verwerking verwerking = this.registrationModelMapper.convertToVerwerking(verwerkingEntity);

        assertEquals(verwerking.getDcnId(), verwerkingEntity.getDcnId());
        assertEquals(verwerking.getExtIds(), List.of(verwerkingEntity.getExtId()));
        assertEquals(verwerking.getSeverity(), verwerkingEntity.getSeverity());
        assertEquals(verwerking.getSourceLabel(), verwerkingEntity.getSourceLabel());
        assertEquals(verwerking.getProcesId(), verwerking.getProcesId());
    }

    @Test
    void convertProcesEntityToProces() {
        ProcesEntity procesEntity = new ProcesEntity("source", "triggerType", "trigger");
        Proces proces = this.registrationModelMapper.convertToProces(procesEntity);

        assertEquals(proces.getId(), procesEntity.getId());
        assertEquals(proces.getTrigger(), procesEntity.getTrigger());
        assertEquals(proces.getTriggerType(), procesEntity.getTriggerType());
        assertEquals(proces.getSourceLabel(), procesEntity.getSourceLabel());
    }
}
