package nl.overheid.koop.plooi.dcn.oep;

import java.util.ArrayList;
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
import nl.overheid.koop.plooi.document.map.DateMapping;
import nl.overheid.koop.plooi.document.map.ExtraMetadataMapping;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMappers;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMapping;
import nl.overheid.koop.plooi.document.map.PlooiTikaMapping;
import nl.overheid.koop.plooi.document.map.PlooiXmlMapping;
import nl.overheid.koop.plooi.document.map.ResourceMapping;
import nl.overheid.koop.plooi.document.map.TextMapping;
import nl.overheid.koop.plooi.document.normalize.BeslisnotaDetector;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValueNormalizer;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.builder.Namespaces;
import org.springframework.stereotype.Component;

@Component
public class OepSruRoute extends RouteBuilder {

    static final String SOURCE_LABEL = "oep";
    private static final Namespaces SRU_NS = new Namespaces("sru", "http://docs.oasis-open.org/ns/search-ws/sruResponse");

    private static final String VERGADERDATUM = "vergaderdatum";
    private static final String VERGADERJAAR = "vergaderjaar";
    private static final String INDIENER = "indiener";
    private static final String SUBSOORT = "documentsubsoort";
    private static final String DATUMONDERTEKENING = "datumOndertekening";
    private static final String ISVERVANGENDOOR = "isVervangenDoor";
    private static final String DOSSIERTITEL = "kst.dossiertitel";
    private static final String ONDERNUMMER = "kst.ondernummer";

    private static final PlooiXmlMapping XML_MAPPING = new PlooiXmlMapping();

    static final PageQueryPrep OEP_PAGING_QUERY = new PageQueryPrep()
            .setBaseUrl("https://repository.overheid.nl/sru?operation=searchRetrieve&version=2.0&recordSchema=gzd&")
            .setSinceDateParam( // OEP has a quite complex query, we just hack it all in here
                    "query=c.product-area==\"officielepublicaties\" "
                            + "and ( "
                            + "w.organisatietype==\"staten generaal\" "
                            + "and ( "
                            + "w.documentstatus==\"Opgemaakt\" "
                            + "or w.documentstatus==\"Opgemaakt na onopgemaakt\" "
                            + "or dt.type==\"Bijlage\" "
                            + ")"
                            + ") or ( "
                            + "w.publicatienaam==\"Staatsblad\" "
//                        + "or w.publicatienaam==\"Staatscourant\""
                            + ") "
                            + "and dt.date>=\"2021-07-01\" "
                            + "and dt.modified>")
            .setSinceDateFormat("\"yyyy-MM-dd\"")
            .setPageOffsetParam("startRecord")
            .setPageSizeParam("maximumRecords")
            .setPageSize(10);

    public static final ConfigurableFileMapper OEP_FILEMAPPER = ConfigurableFileMapper.configureWith(XML_MAPPING)
            .mapIdentifier("//originalData/meta/owmskern/identifier")
            .mapUrl("//enrichedData/itemUrl[@manifestation='metadataowms']")
            .setLabel("metadataowms")
            .addChildMapper("//enrichedData/itemUrl[@manifestation='xml']", ConfigurableFileMapper.childConfiguration()
                    .mapUrl(".")
                    .setLabel("xml"))
            .addChildMapper("//enrichedData/itemUrl[@manifestation='html']", ConfigurableFileMapper.childConfiguration()
                    .mapUrl(".")
                    .setLabel("html"))
            .addChildMapper("//enrichedData/itemUrl[@manifestation='pdf']", ConfigurableFileMapper.childConfiguration()
                    .mapUrl(".")
                    .setLabel("pdf")
                    .publish())
            .discardContent() // after mapping the SRU record discard it, because we get the "real" meta xml from the metadataowms url above
            .mapper();

    public static final DocumentCollecting OEP_COLLECTOR = new DefaultDocumentCollector(SOURCE_LABEL, false);

    public static final EnvelopeProcessing<Envelope> OEP_DIAGNOSTIC_FILTER = DiagnosticFilter.configure()
            .add(DiagnosticCode.EMPTY_ID) // OEP does not provide resource identifiers
            .add(DiagnosticCode.REQUIRED_DEFAULT, "Publisher")
            .add(DiagnosticCode.DISCARD);

    public static final PlooiDocumentMapping OEP_META_MAPPER = ConfigurableDocumentMapper.configureWith(XML_MAPPING)
            .setAanbieder("officielebekendmakingen.nl")
            .mapWeblocatie("//owmskern/identifier")
            .setWeblocatieTemplate("https://zoek.officielebekendmakingen.nl/%s.html")
            .mapIdentifier("//owmskern/identifier")
            .mapOfficieleTitel(TextMapping.plain("//owmskern/title"))
            .mapLanguage(ResourceMapping.term("//owmskern/language"))
            .mapOpsteller(ResourceMapping.owms("//owmskern/creator"))
            .mapVerantwoordelijke(ResourceMapping.owms("//owmskern/creator"))
            .mapAlternatieveTitel(TextMapping.plain("//owmsmantel/alternative"))
            .mapCreatiedatum("//owmsmantel/issued")
            .mapGeldigheidsstartdatum(DateMapping.isoDate("//owmsmantel/available"))
            .addThema(ResourceMapping.owms("//oep/category"))
            .mapDocumentsoort(ResourceMapping.owms("//oep/publicationName"))
            .mapAggregatiekenmerk("//oep/dossiernummer")
//            .mapAggregatiekenmerk("//oep/behandeldDossier")
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(VERGADERDATUM, "//oep/datumVergadering"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(VERGADERJAAR, "//oep/vergaderjaar"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(INDIENER, "//oep/indiener"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(SUBSOORT, "//owmskern/type[@scheme='OVERHEIDop.KamerstukTypen']"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(SUBSOORT, "//owmskern/type[@scheme='OVERHEIDop.AanhangselTypen']"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(SUBSOORT, "//owmskern/type[@scheme='OVERHEIDop.KamervraagTypen']"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(SUBSOORT, "//owmskern/type[@scheme='OVERHEIDop.HandelingTypen']"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(SUBSOORT, "//owmskern/type[@scheme='OVERHEIDop.Parlementair' and text()='Bijlage']"))
            .addExtraMetadata(ExtraMetadataMapping.temp(DOSSIERTITEL, "//oep/dossiertitel"))
            .addExtraMetadata(ExtraMetadataMapping.temp(ONDERNUMMER, "//oep/ondernummer"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(DATUMONDERTEKENING, "//oep/overheidop.datumOndertekening"))
            .addExtraMetadata(ExtraMetadataMapping.basicDisplayfield(ISVERVANGENDOOR, "//owmsmantel/isReplacedBy"))
            .mapper();
    public static final PlooiDocumentMapping OEP_TIKA_MAPPER = ConfigurableDocumentMapper.configureWith(new PlooiTikaMapping())
            .addDocumentText(PlooiTikaMapping.DOCUMENT_TEXT)
            .mapper();
    public final PlooiDocumentMappers oepMappers = new PlooiDocumentMappers()
            .addMapper("metadataowms", OEP_META_MAPPER)
            .addAltMappers(List.of("xml", "html", "pdf"), OEP_TIKA_MAPPER);

    public static final EnvelopeProcessing<PlooiEnvelope> OEP_PREMAP = target -> {
        BeslisnotaDetector.handleBeslisnota(target);
        // Strip parent level from topthema provided by OEP
        target.getClassificatiecollectie().getThemas().forEach(t -> t.setLabel(t.getLabel().replaceFirst(".*\\|\\s*", "")));
        return target;
    };

    public static final EnvelopeProcessing<PlooiEnvelope> OEP_NORMALIZER = PlooiDocumentValueNormalizer.configure()
            .useExtraDictionaryProperties("/nl/overheid/koop/plooi/dcn/oep/waardelijsten_oep.properties")
            .build();

    public static final EnvelopeProcessing<PlooiEnvelope> OEP_POSTMAP = target -> {
        // Set title for kamerstukken
        var meta = target.getDocumentMeta();
        if (meta.getAggregatiekenmerk() != null
                && target.getClassificatiecollectie().getDocumentsoorten().stream().anyMatch(srt -> "Kamerstuk".equals(srt.getLabel()))) {
            var titels = target.getTitelcollectie();
            (meta.getOmschrijvingen() == null ? meta.omschrijvingen(new ArrayList<>()) : meta).getOmschrijvingen()
                    .add(titels.getOfficieleTitel());
            titels.setOfficieleTitel(String.format("%s, nr. %s - %s",
                    meta.getAggregatiekenmerk(),
                    ExtraMetadataMapping.getTempValue(meta.getExtraMetadata(), ONDERNUMMER, "-"),
                    ExtraMetadataMapping.getTempValue(meta.getExtraMetadata(), DOSSIERTITEL, titels.getOfficieleTitel())));
        }
        return target;
    };

    private final PublicatieClient publicatieClient;
    private final RegistrationClient registrationClient;

    public OepSruRoute(PublicatieClient publClient, RegistrationClient regClient) {
        this.publicatieClient = publClient;
        this.registrationClient = regClient;
        this.oepMappers.withPublicatieClient(publClient);
    }

    // @formatter:off No Eclipse formatting down here
    @Override
    public void configure() {
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
                        OEP_PAGING_QUERY,
                        new IngressExecutionPrep(this.registrationClient, SOURCE_LABEL, Exchange.HTTP_URI)))
                .setHeader(PageQueryPrep.DCN_OFFSET_VALUE_HEADER, constant(Integer.valueOf(-1)))
                .loopDoWhile(header(PageQueryPrep.DCN_OFFSET_VALUE_HEADER).isNotEqualTo(0))
                    .bean(OEP_PAGING_QUERY, PageQueryPrep.PREPARE_NEXT_METHOD)
                    .to(Routing.internal(CommonRoute.COMMON_HTTP_ROUTE))
                    .setHeader(PageQueryPrep.DCN_OFFSET_VALUE_HEADER).xpath("//sru:nextRecordPosition/text()", Integer.class, SRU_NS)
                    .split().tokenizeXML("sru:record").streaming()
                        .process(new FileMappingProcessor(OEP_FILEMAPPER))
                        .process(new CollectingProcessor(OEP_COLLECTOR))
                        .process(new EnvelopeProcessor<>(OEP_DIAGNOSTIC_FILTER))
                        .to(Routing.internal(CommonRoute.COMMON_INGRESS_SUBROUTE))
                    .end()
                .end()
                .bean(OEP_PAGING_QUERY, PageQueryPrep.FINISH_METHOD);

        from(Routing.external(Routing.process(SOURCE_LABEL))).routeId(Routing.process(SOURCE_LABEL))
                .process(Routing.restoreFromManifest(this.publicatieClient))
                .choice()
                    .when().ognl(CommonRoute.BODY_DISCARDED)
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Discarding not allowed ${body}")
                    .otherwise()
                        .process(new EnvelopeProcessor<>(OEP_DIAGNOSTIC_FILTER))
                        .process(new EnvelopeProcessor<>(this.oepMappers))
                        .process(new EnvelopeProcessor<>(OEP_PREMAP))
                        .process(new EnvelopeProcessor<>(OEP_NORMALIZER))
                        .process(new EnvelopeProcessor<>(OEP_POSTMAP))
                        .to(Routing.internal(CommonRoute.COMMON_PROCESS_SUBROUTE))
                .end(); // end choice
    }
}
