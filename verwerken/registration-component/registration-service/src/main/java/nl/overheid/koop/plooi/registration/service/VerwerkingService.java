package nl.overheid.koop.plooi.registration.service;

import java.util.List;
import nl.overheid.koop.plooi.registration.api.VerwerkingenApiDelegate;
import nl.overheid.koop.plooi.registration.model.DiagnoseRepository;
import nl.overheid.koop.plooi.registration.model.ExceptieRepository;
import nl.overheid.koop.plooi.registration.model.RegistrationModelMapper;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import nl.overheid.koop.plooi.registration.model.VerwerkingRepository;
import nl.overheid.koop.plooi.registration.model.VerwerkingStatus;
import nl.overheid.koop.plooi.registration.model.VerwerkingStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VerwerkingService implements VerwerkingenApiDelegate {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final VerwerkingRepository verwerkingRepository;
    private final VerwerkingStatusRepository verwerkingStatusRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final ExceptieRepository exceptieRepository;
    private final RegistrationModelMapper registrationModelMapper;

    public VerwerkingService(VerwerkingRepository verwerkingRepos, VerwerkingStatusRepository verwerkingStatusRepos, DiagnoseRepository diagnoseRepos,
            ExceptieRepository exceptieRepos, RegistrationModelMapper modelMaper) {
        this.verwerkingRepository = verwerkingRepos;
        this.verwerkingStatusRepository = verwerkingStatusRepos;
        this.diagnoseRepository = diagnoseRepos;
        this.exceptieRepository = exceptieRepos;
        this.registrationModelMapper = modelMaper;
    }

    @Override
    public ResponseEntity<Verwerking> getVerwerking(String id) {
        this.logger.debug("get verwerking with id {}", id);
        return this.verwerkingRepository.findById(id)
                .map(this.registrationModelMapper::convertToVerwerking)
                .map(this::addDiagnosess)
                .map(this::addExceptie)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<VerwerkingStatus> getVerwerkingStatus(String dcnId) {
        this.logger.debug("get verwerking status for verwerking with dcnId = {}", dcnId);
        return this.verwerkingStatusRepository
                .findById(dcnId)
                .map(ve -> ResponseEntity.ok(this.registrationModelMapper.convertToVerwerkingStatus(ve)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ResponseEntity<Page> getVerwerkingen(String dcnId, Integer pageNr, Integer pageSize) {
        var page = PageRequest.of(pageNr, pageSize);
        this.logger.debug("get all verwerkingen by dcnId = {}", dcnId);
        var result = this.verwerkingRepository
                .getVerwerkingenByDcnId(dcnId, page)
                .getContent()
                .stream()
                .map(this.registrationModelMapper::convertToVerwerking)
                .toList();
        return ResponseEntity.ok(new PageImpl<>(result, page, pageNr));
    }

    @Override
    public ResponseEntity<List<String>> getDcnIdsByBron(String sourceName) {
        this.logger.debug("get verwerking dcnId's list for {}", sourceName);
        return ResponseEntity.ok(this.verwerkingRepository.getVerwerkingDcnIdsBySourceName(sourceName));
    }

    private Verwerking addExceptie(Verwerking verwerking) {
        return verwerking.exceptie(this.registrationModelMapper.convertToExceptie(
                this.exceptieRepository.getFirstByVerwerkingId(verwerking.getId()).orElse(null)));
    }

    private Verwerking addDiagnosess(Verwerking verwerking) {
        var pageable = PageRequest.of(0, 100);
        this.diagnoseRepository
                .getDiagnosesByVerwerkingId(verwerking.getId(), pageable)
                .getContent()
                .forEach(d -> verwerking.addDiagnosesItem(this.registrationModelMapper.convertToDiagnose(d)));
        return verwerking;
    }
}
