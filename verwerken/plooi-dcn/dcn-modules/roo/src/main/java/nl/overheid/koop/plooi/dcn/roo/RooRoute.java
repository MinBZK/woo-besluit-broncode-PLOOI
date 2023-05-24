package nl.overheid.koop.plooi.dcn.roo;

import java.time.LocalDate;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.common.DefaultDocumentCollector;
import nl.overheid.koop.plooi.dcn.component.common.DiagnosticFilter;
import nl.overheid.koop.plooi.dcn.component.types.DocumentCollecting;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.route.common.CommonRoute;
import nl.overheid.koop.plooi.dcn.route.common.ErrorHandling;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import nl.overheid.koop.plooi.dcn.route.prep.IngressExecutionPrep;
import nl.overheid.koop.plooi.dcn.route.prep.PageQueryPrep;
import nl.overheid.koop.plooi.dcn.route.prep.PreviousIngressPrep;
import nl.overheid.koop.plooi.dcn.route.prep.StagePrep;
import nl.overheid.koop.plooi.dcn.route.processor.CollectingProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.EnvelopeProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.FileMappingProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.PreppingProcessor;
import nl.overheid.koop.plooi.document.map.ConfigurableDocumentMapper;
import nl.overheid.koop.plooi.document.map.ConfigurableFileMapper;
import nl.overheid.koop.plooi.document.map.ExtraMetadataMapping;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMappers;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMapping;
import nl.overheid.koop.plooi.document.map.PlooiXmlMapping;
import nl.overheid.koop.plooi.document.map.ResourceMapping;
import nl.overheid.koop.plooi.document.map.TextMapping;
import nl.overheid.koop.plooi.model.data.ExtraMetadataVeld;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.builder.Namespaces;
import org.springframework.stereotype.Component;

@Component
public class RooRoute extends RouteBuilder {

    private static final String SOURCE_LABEL = "roo";
    private static final String ID_PATH = "//originalData/record/resourceIdentifier";
    private static final String NAME_PATH = "//originalData/record/naam";
    private static final String ORG_GEGEVENS = "Organisatiegegevens";
    private static final String TOOI_DOCUMENT = "https://identifier.overheid.nl/tooi/def/thes/kern/c_9ecc0007";
    private static final String TOP_THEMA_URL = "https://identifier.overheid.nl/tooi/def/thes/top/c_f37fd49d";
    private static final String TOP_THEMA_VALUE = "Organisatie en bedrijfsvoering";
    private static final Namespaces SRU_NS = new Namespaces("sru", "http://docs.oasis-open.org/ns/search-ws/sruResponse");
    private static final PlooiXmlMapping XML_MAPPING = new PlooiXmlMapping();

    private final PublicatieClient publicatieClient;
    private final RegistrationClient registrationClient;

    public RooRoute(PublicatieClient publClient, RegistrationClient regClient) {
        this.publicatieClient = publClient;
        this.registrationClient = regClient;
        this.rooMappers.withPublicatieClient(publClient);
    }

    public static final EnvelopeProcessing<Envelope> ROO_DIAGNOSTIC_FILTER = DiagnosticFilter.configure()
            .add(DiagnosticCode.EMPTY_TEXT)
            .add(DiagnosticCode.DERIVED)
            .add(DiagnosticCode.DISCARD);

    public static final ConfigurableFileMapper ROO_FILEMAPPER = ConfigurableFileMapper.configureWith(XML_MAPPING)
            .mapIdentifier(ID_PATH)
            .setLabel("xml")
            .mapper();

    public static final DocumentCollecting ROO_COLLECTOR = new DefaultDocumentCollector(SOURCE_LABEL, false);

    static final PageQueryPrep ROO_PAGING_QUERY = new PageQueryPrep()
            .setBaseUrl("https://zoekservice.overheid.nl/sru/Search?x-connection=oo&operation=searchRetrieve&version=2.0&"
                    + "query=type=(%22Ministerie%22%20OR%20%22Provincie%22%20OR%20%22Gemeente%22%20OR%20%22Waterschap%22)&")
            .setPageOffsetParam("startRecord")
            .setPageSizeParam("maximumRecords")
            .setPageSize(10);

    public static final PlooiDocumentMapping ROO_META_MAPPER = ConfigurableDocumentMapper.configureWith(XML_MAPPING)
            .setAanbieder("Register van Overheidsorganisaties")
            .mapOfficieleTitel(TextMapping.plain(NAME_PATH))
            .mapOmschrijving(TextMapping.plain("//originalData/record/beschrijving"))
            .mapWeblocatie("//originalData/record/url")
            .mapCreatiedatum("//originalData/record/startDatum")
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield("identificatienummer", "originalData/record/resourceIdentifier"))
            .addExtraMetadata(ExtraMetadataMapping.temp("adress.straat", "originalData/record/bezoekAdres/straat"))
            .addExtraMetadata(ExtraMetadataMapping.temp("adress.huisnummer", "originalData/record/bezoekAdres/huisnummer"))
            .addExtraMetadata(ExtraMetadataMapping.temp("adress.postcode", "originalData/record/bezoekAdres/postcode"))
            .addExtraMetadata(ExtraMetadataMapping.temp("adress.plaats", "originalData/record/bezoekAdres/plaats"))
            .addExtraMetadata(ExtraMetadataMapping.temp("postadres.postbus", "originalData/record/postAdres/postbus"))
            .addExtraMetadata(ExtraMetadataMapping.temp("postadres.postcode", "originalData/record/postAdres/postcode"))
            .addExtraMetadata(ExtraMetadataMapping.temp("postadres.plaats", "originalData/record/postAdres/plaats"))
            .addExtraMetadata(ExtraMetadataMapping.temp("datumMutatie", "//originalData/record/datumMutatie"))
            .mapOpsteller(ResourceMapping.full(NAME_PATH, null, null, ID_PATH))
            .mapPublisher(ResourceMapping.full(NAME_PATH, null, null, ID_PATH))
            .mapVerantwoordelijke(ResourceMapping.full(NAME_PATH, null, null, ID_PATH))
            .mapper();

    public static final EnvelopeProcessing<PlooiEnvelope> ROO_POSTMAP = target -> {
        target.getClassificatiecollectie()
                .addDocumentsoortenItem(new IdentifiedResource().id(TOOI_DOCUMENT).label(ORG_GEGEVENS))
                .addThemasItem(new IdentifiedResource().id(TOP_THEMA_URL).label(TOP_THEMA_VALUE));
        target.getTitelcollectie()
                .setOfficieleTitel(String.format("%s - %s", target.getTitelcollectie().getOfficieleTitel(), ORG_GEGEVENS));

        var extraMetadata = target.getDocumentMeta().getExtraMetadata();
        var displayExtraMetadataVelden = ExtraMetadataMapping
                .basicDisplayfield(null, null)
                .locateIn(extraMetadata)
                .getVelden();
        displayExtraMetadataVelden.add(new ExtraMetadataVeld()
                .key("bezoekadres")
                .values(List.of(String.format("%s %s, %s %s",
                        ExtraMetadataMapping.getTempValue(extraMetadata, "adress.straat", ""),
                        ExtraMetadataMapping.getTempValue(extraMetadata, "adress.huisnummer", ""),
                        ExtraMetadataMapping.getTempValue(extraMetadata, "adress.postcode", ""),
                        ExtraMetadataMapping.getTempValue(extraMetadata, "adress.plaats", "")))));
        displayExtraMetadataVelden.add(new ExtraMetadataVeld()
                .key("postadres")
                .values(List.of(String.format("%s, %s %s",
                        ExtraMetadataMapping.getTempValue(extraMetadata, "postadres.postbus", ""),
                        ExtraMetadataMapping.getTempValue(extraMetadata, "postadres.postcode", ""),
                        ExtraMetadataMapping.getTempValue(extraMetadata, "postadres.plaats", "")))));
        if (target.getDocumentMeta().getCreatiedatum() == null) {
            target.getDocumentMeta()
                    .setCreatiedatum(
                            ExtraMetadataMapping.getTempValue(target.getDocumentMeta().getExtraMetadata(), "datumMutatie", LocalDate.now().toString()));
        }
        return target;
    };

    public final PlooiDocumentMappers rooMappers = new PlooiDocumentMappers()
            .addMapper("xml", ROO_META_MAPPER);

    // @formatter:off No Eclipse formatting down here
    @Override
    public void configure() throws Exception {

        new ErrorHandling(this)
                .withHttpRedeliveries(ErrorHandling.DEFAULT_RETRIES)
                .configure();

        from(Routing.scheduleEndpoint(SOURCE_LABEL)).routeId(Routing.schedule(SOURCE_LABEL))
                .to(Routing.internal(Routing.ingress(SOURCE_LABEL)));

        from(Routing.internal(Routing.ingress(SOURCE_LABEL))).routeId(Routing.ingress(SOURCE_LABEL))
                .streamCaching()
                .process(new PreppingProcessor(
                        new StagePrep(Stage.INGRESS),
                        new PreviousIngressPrep(SOURCE_LABEL, this.registrationClient),
                        ROO_PAGING_QUERY,
                        new IngressExecutionPrep(this.registrationClient, SOURCE_LABEL, Exchange.HTTP_URI)))
                .setHeader(PageQueryPrep.DCN_OFFSET_VALUE_HEADER, constant(-1))
                .loopDoWhile(header(PageQueryPrep.DCN_OFFSET_VALUE_HEADER).isNotEqualTo(0))
                    .bean(ROO_PAGING_QUERY, PageQueryPrep.PREPARE_NEXT_METHOD)
                    .to(Routing.internal(CommonRoute.COMMON_HTTP_ROUTE))
                    .setHeader(PageQueryPrep.DCN_OFFSET_VALUE_HEADER).xpath("//sru:nextRecordPosition/text()", Integer.class, SRU_NS)
                    .split().tokenizeXML("originalData").streaming()
                        .process(new FileMappingProcessor(ROO_FILEMAPPER))
                        .process(new CollectingProcessor(ROO_COLLECTOR))
                        .process(new EnvelopeProcessor<>(ROO_DIAGNOSTIC_FILTER))
                        .to(Routing.internal(CommonRoute.COMMON_INGRESS_SUBROUTE))
                    .end()
                .end()
                .bean(ROO_PAGING_QUERY, PageQueryPrep.FINISH_METHOD);

        from(Routing.external(Routing.process(SOURCE_LABEL))).routeId(Routing.process(SOURCE_LABEL))
                .process(Routing.restoreFromManifest(this.publicatieClient))
                .choice()
                    .when().ognl(CommonRoute.BODY_DISCARDED)
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Discarding not allowed ${body}")
                    .otherwise()
                        .process(new EnvelopeProcessor<>(ROO_DIAGNOSTIC_FILTER))
                        .process(new EnvelopeProcessor<>(this.rooMappers))
                        .process(new EnvelopeProcessor<>(ROO_POSTMAP))
                        .to(Routing.internal(CommonRoute.COMMON_PROCESS_SUBROUTE))
                .end(); // end choice
    }
}
