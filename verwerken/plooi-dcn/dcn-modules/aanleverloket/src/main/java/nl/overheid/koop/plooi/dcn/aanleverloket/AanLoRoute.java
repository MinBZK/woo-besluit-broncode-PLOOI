package nl.overheid.koop.plooi.dcn.aanleverloket;

import java.nio.charset.StandardCharsets;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.common.DiagnosticFilter;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.DocumentCollecting;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.dcn.route.common.CommonRoute;
import nl.overheid.koop.plooi.dcn.route.common.ErrorHandling;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import nl.overheid.koop.plooi.dcn.route.prep.AdhocExecutionPrep;
import nl.overheid.koop.plooi.dcn.route.prep.PreviousIngressPrep;
import nl.overheid.koop.plooi.dcn.route.prep.StagePrep;
import nl.overheid.koop.plooi.dcn.route.processor.CollectingProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.EnvelopeProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.PreppingProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.register.VerwerkingRegister;
import nl.overheid.koop.plooi.document.map.ConfigurableDocumentMapper;
import nl.overheid.koop.plooi.document.map.DateMapping;
import nl.overheid.koop.plooi.document.map.ExtraMetadataMapping;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMappers;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMapping;
import nl.overheid.koop.plooi.document.map.PlooiTikaMapping;
import nl.overheid.koop.plooi.document.map.PlooiXmlMapping;
import nl.overheid.koop.plooi.document.map.ResourceMapping;
import nl.overheid.koop.plooi.document.map.TextMapping;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValueNormalizer;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Caamel routes for performing downloads from the Aanleverloket directory.
 * <p>
 * The entry point is aanlo-ingress. It is a sheduled execution path
 */
@Component
public class AanLoRoute extends RouteBuilder {

    static final String SOURCE_LABEL = "aanleverloket";

    private static final String FILE_ROUTE = SOURCE_LABEL + "-internal-filescan";

    private static final String DELETION = SOURCE_LABEL + "-internal-deletion";

    private static final PlooiXmlMapping XML_MAPPING = new PlooiXmlMapping();

    public static final EnvelopeProcessing<Envelope> AANLO_DIAGNOSTIC_FILTER = DiagnosticFilter.configure()
            .add(DiagnosticCode.EMPTY_ID); // Aanleverloket has no TOOI identifiers

    public static final PlooiDocumentMapping AANLO_TIKA_MAPPER = ConfigurableDocumentMapper.configureWith(new PlooiTikaMapping())
            .addDocumentText(PlooiTikaMapping.DOCUMENT_TEXT)
            .mapper();

    public static final EnvelopeProcessing<PlooiEnvelope> AANLO_NORMALIZER = PlooiDocumentValueNormalizer.configure()
            .useExtraDictionaryProperties("/nl/overheid/koop/plooi/dcn/aanleverloket/waardelijsten_aanleverloket.properties")
            .build();

    public static final DocumentCollecting AANLO_COLLECTOR = new AanLoDocumentCollector(SOURCE_LABEL);

    @SuppressWarnings("java:S1192") // Nothing wrong with repeating source fields in a mapper
    public static final PlooiDocumentMapping AANLO_META_MAPPER = ConfigurableDocumentMapper.configureWith(XML_MAPPING)
            .setAanbieder(SOURCE_LABEL)
            .mapOfficieleTitel(TextMapping.plain("//metadata[@key='dcterms.title']"))
            .mapLanguage(ResourceMapping.term("//metadata[@key='dcterms.language']"))
            .mapOpsteller(ResourceMapping.term("//metadata[@key='dcterms.creator']"))
            .mapCreatiedatum("//metadata[@key='dcterms.issued']")
            .mapGeldigheidsstartdatum(DateMapping.isoDate("//metadata[@key='dcterms.available']"))
            .mapOmschrijving(TextMapping.plain("//metadata[@key='dcterms.description']"))
            .addThema(ResourceMapping.term("//metadata[@key='dcterms.subject']"))
            .mapDocumentsoort(ResourceMapping.term("//metadata[@key='dcterms.type']"))
            .addExtraMetadata(ExtraMetadataMapping.dynamicDisplayfield("//metadata[contains(@key, 'plooi.displayfield.')]", "./@key", "."))
            .mapVerantwoordelijke(ResourceMapping.term("//metadata[@key='dcterms.creator']"))
            .mapper();
    public final PlooiDocumentMappers aanleverloketMappers = new PlooiDocumentMappers()
            .addMapper("xml", AANLO_META_MAPPER)
            .addMapper("pdf", AANLO_TIKA_MAPPER);

    public static final EnvelopeProcessing<PlooiEnvelope> AANLO_POSTMAPPING = target -> {
        if (target.getDocumentMeta().getExtraMetadata() != null) {
            target.getDocumentMeta()
                    .getExtraMetadata()
                    .stream()
                    .filter(emi -> ExtraMetadataMapping.DISPLAYFIELD_PREFIX.equals(emi.getPrefix()))
                    .flatMap(emi -> emi.getVelden().stream())
                    .forEach(emvi -> emvi.setKey(emvi.getKey().replace(ExtraMetadataMapping.DISPLAYFIELD_PREFIX.concat("."), "")));
        }
        return target;
    };

    public static final Predicate REMOVE_CHECK = exchange -> new String(
            ((DeliveryEnvelope) exchange.getIn().getBody(List.class).get(0))
                    .getPlooiFiles()
                    .stream()
                    .filter(f -> "xml".equals(f.getFile().getLabel()))
                    .findAny()
                    .orElseThrow()
                    .getContent(),
            StandardCharsets.UTF_8).contains("<verwijderenwork");

    private final PublicatieClient publicatieClient;
    private final RegistrationClient registrationClient;

    public AanLoRoute(PublicatieClient publClient, RegistrationClient regClient) {
        this.publicatieClient = publClient;
        this.registrationClient = regClient;
        this.aanleverloketMappers.withPublicatieClient(publClient);
    }

    // @formatter:off No Eclipse formatting down here
    @Override
    public void configure() {
        new ErrorHandling(this).configure();

        from("sftp:{{dcn.aanlo.ftp.host}}?"
                + "username={{dcn.aanlo.ftp.username}}&"
                + "password={{dcn.aanlo.ftp.password}}&"
                + "preMove=./in-progress&"
                + "move=../processed&"
                + "moveFailed=../failed&"
                + "serverHostKeys=ssh-rsa&"
                + "scheduler=quartz&"
                + "scheduler.cron={{dcn.schedule.aanlo}}")
                        .routeId(FILE_ROUTE)
                        .process(new PreppingProcessor(
                                new StagePrep(Stage.INGRESS),
                                new PreviousIngressPrep(SOURCE_LABEL, this.registrationClient),
                                new AdhocExecutionPrep(this.registrationClient,SOURCE_LABEL, TriggerType.INGRESS, "Ingressing from aanleverloket")))
                        .process(exchange -> exchange.getIn()
                                .setBody(new PlooiFile(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), "zip").content(exchange.getIn()
                                        .getMandatoryBody(byte[].class))))
                        .process(new CollectingProcessor(AANLO_COLLECTOR))
                        .process(new EnvelopeProcessor<>(AANLO_DIAGNOSTIC_FILTER))
                        .choice()
                            .when(REMOVE_CHECK) // Check op verwijder
                               .to(Routing.internal(DELETION))
                            .otherwise() // Doe toevoegen of bijwerken
                               .to(Routing.internal(CommonRoute.COMMON_INGRESS_SUBROUTE))
                        .end();

        from(Routing.external(Routing.process(SOURCE_LABEL))).routeId(Routing.process(SOURCE_LABEL))
                .process(Routing.restoreFromManifest(this.publicatieClient))
                .choice()
                    .when().ognl(CommonRoute.BODY_DISCARDED)
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Discarding not allowed ${body}")
                    .otherwise()
                        .process(new EnvelopeProcessor<>(AANLO_DIAGNOSTIC_FILTER))
                        .process(new EnvelopeProcessor<>(this.aanleverloketMappers))
                        .process(new EnvelopeProcessor<>(AANLO_POSTMAPPING))
                        .process(new EnvelopeProcessor<>(AANLO_NORMALIZER))
                        .to(Routing.internal(CommonRoute.COMMON_PROCESS_SUBROUTE));

        from(Routing.internal(DELETION)).routeId(DELETION)
                .split(body())
                    .process(new VerwerkingRegister(this.registrationClient))
                    .transform().ognl(CommonRoute.BODY_DCN_ID)
                    .to(Routing.external(CommonRoute.DCN_DELETE_DOCUMENT))
                .end();
    }
}