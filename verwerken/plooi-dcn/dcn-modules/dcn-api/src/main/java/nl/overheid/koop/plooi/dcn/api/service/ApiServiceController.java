package nl.overheid.koop.plooi.dcn.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.dcn.route.common.CommonRoute;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import nl.overheid.koop.plooi.dcn.route.prep.IngressExecutionPrep;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentService;
import nl.overheid.koop.plooi.dcn.solr.models.DateRangeField;
import nl.overheid.koop.plooi.dcn.solr.models.SimpleField;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dcn/api")
public class ApiServiceController {

    static final String META = "metadata_v1";
    static final String[] API_DOCUMENT_FIELDS = {
            "identifier",
            "title",
            "creator",
            "verantwoordelijke",
            "publisher",
            "type",
            "type_id",
            "modified",
            "extrametadata_plooi.displayfield.identificatienummer",
    };

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AanleverenClient aanleverenClient;
    private final PublicatieClient publicatieClient;
    private final RegistrationClient registrationClient;
    private final ProducerTemplate producerTemplate;
    private final SolrDocumentService solrDocumentService;

    public ApiServiceController(AanleverenClient deliveryClient, PublicatieClient publClient, RegistrationClient regClient,
            CamelContext camelContext, SolrDocumentService solrDocumentService) {
        this.aanleverenClient = deliveryClient;
        this.publicatieClient = publClient;
        this.registrationClient = regClient;
        this.producerTemplate = camelContext.createProducerTemplate();
        this.solrDocumentService = solrDocumentService;
    }

    @GetMapping(path = "/{label}/{plooiId}")
    public ResponseEntity<Resource> getFile(@PathVariable String label, @PathVariable String plooiId) {
        Optional<Bestand> fileMeta = this.publicatieClient.getLatestVersion(plooiId)
                .getBestanden()
                .stream()
                .filter(f -> Objects.equals(label, f.getLabel()))
                .findAny();
        return fileMeta.isEmpty()
                ? ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build()
                : ResponseEntity
                        .ok()
                        .header(HttpHeaders.CONTENT_TYPE,
                                fileMeta.map(Bestand::getMimeType).get())
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.attachment().filename(fileMeta.map(Bestand::getBestandsnaam).get()).build().toString())
                        .body(new InputStreamResource(this.publicatieClient.getVersionedFile(plooiId, PublicatieClient.LATEST, label)));
    }

    /*
     * TODO request param verantwoordelijke moet hernoemd worden, maar deze is hardcoded in de API SearchService controller
     */
    @GetMapping(path = "/_zoek")
    public ResponseEntity getPublisherDocuments(@RequestParam("verantwoordelijke") String publisher,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
            @RequestParam(value = "pagina", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        var fd = fromDate != null ? fromDate.toInstant().toEpochMilli() : 0L;
        var td = toDate != null ? toDate.toInstant().toEpochMilli() : 0L;

        DateRangeField dateRangeField = new DateRangeField("available_start", fd, td);
        List<SimpleField> simpleFields = new ArrayList<>();
        simpleFields.add(new SimpleField("publisher_id", publisher));
        simpleFields.add(new SimpleField("source_label", DcnIdentifierUtil.PLOOI_API_SRC));
        SolrSearchRequest searchRequest = new SolrSearchRequest("", simpleFields, new ArrayList<>(), dateRangeField);
        return this.solrDocumentService.getDocuments(searchRequest, PageRequest.of(page, size), API_DOCUMENT_FIELDS);
    }

    @DeleteMapping(path = "/{plooiId}")
    public void delete(@PathVariable String plooiId) {
        produce(new PlooiEnvelope(DcnIdentifierUtil.PLOOI_API_SRC, plooiId), CommonRoute.DCN_DELETE_DOCUMENT);
    }

    @PostMapping(path = "/{plooiId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> accept(@PathVariable String plooiId, HttpServletRequest request) throws IOException {
        try {
            var aanleverRequest = this.aanleverenClient.createRequest(new Versie(), DcnIdentifierUtil.PLOOI_API_SRC, plooiId);
            for (var part : request.getParts()) {
                aanleverRequest.addPart("metadata".equals(part.getName()) ? META : part.getName(), () -> {
                    try {
                        return part.getInputStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            produce(register(aanleverRequest.post(), Stage.INGRESS, "Document received from API"), CommonRoute.DCN_PROCESS_DOCUMENT);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ServletException e) {
            return ResponseEntity.badRequest().body("Expecting multipart request");
        }
    }

    private void produce(PlooiEnvelope plooiDoc, String destinatioRoute) {
        if (!plooiDoc.status().isDiscarded()) {
            this.producerTemplate.sendBodyAndHeader(
                    Routing.external(destinatioRoute),
                    plooiDoc.getPlooiIntern().getDcnId(),
                    IngressExecutionPrep.DCN_EXECUTION_HEADER, plooiDoc.getProcesId());
        }
    }

    private PlooiEnvelope register(Optional<Plooi> stored, Stage stage, String trigger) {
        var proces = this.registrationClient.createProces(DcnIdentifierUtil.PLOOI_API_SRC, TriggerType.API.toString(), trigger);
        var plooiDoc = new PlooiEnvelope(stored.orElse(null));
        if (stored.isEmpty()) {
            this.logger.debug("{}, is discarded", plooiDoc);
            plooiDoc.status().discard().addDiagnose(DiagnosticCode.DISCARD, "Discarding duplicate document");
        }
        var verwerking = this.registrationClient.createProcesVerwerking(proces.getId(), new Verwerking()
                .procesId(proces.getId())
                .dcnId(plooiDoc.getPlooiIntern().getDcnId())
                .sourceLabel(plooiDoc.getPlooiIntern().getSourceLabel())
                .extIds(plooiDoc.getPlooiIntern().getExtId())
                .stage(stage.toString())
                .diagnoses(plooiDoc.status().getDiagnoses()));
        plooiDoc.procesId(proces.getId()).verwerkingId(verwerking.getId());
        this.logger.debug("Registered INGRESS event {} for {}", verwerking, plooiDoc);
        return plooiDoc;
    }
}
