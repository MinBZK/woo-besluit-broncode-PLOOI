package nl.overheid.koop.plooi.dcn.ronl.archief;

import nl.overheid.koop.plooi.dcn.component.common.DefaultDocumentCollector;
import nl.overheid.koop.plooi.dcn.component.types.DocumentCollecting;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.dcn.ronl.RonlShared;
import nl.overheid.koop.plooi.dcn.route.common.CommonRoute;
import nl.overheid.koop.plooi.dcn.route.common.EnrichedFileContentAggregationStrategy;
import nl.overheid.koop.plooi.dcn.route.common.EnvelopeDiagnosticAggregationStrategy;
import nl.overheid.koop.plooi.dcn.route.common.ErrorHandling;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import nl.overheid.koop.plooi.dcn.route.prep.AdhocExecutionPrep;
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
import nl.overheid.koop.plooi.document.map.PlooiXmlMapping;
import nl.overheid.koop.plooi.document.map.ResourceMapping;
import nl.overheid.koop.plooi.document.map.TextMapping;
import nl.overheid.koop.plooi.document.normalize.BeslisnotaDetector;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.language.OgnlExpression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Routes for performing downloads from the RONL-API. This calls the main API, and pages through the result.
 * <p>
 * The entry point is ronl-ingress. It can be triggered with a header lastmodifiedsince (yyyymmdd) to use that date for
 * the API calls. If that's not there, then a full download is done.
 */
@Component
public class RonlArchiefRoute extends RouteBuilder {

    static final String SOURCE_LABEL = "ronl-archief";

    private static final String FILE_ROUTE = SOURCE_LABEL + "-internal-filescan";
    private static final String RELATION = "relation";

    private static final PlooiXmlMapping XML_MAPPING = new PlooiXmlMapping();

    public static final ConfigurableFileMapper RONL_FILEMAPPER = ConfigurableFileMapper.configureWith(XML_MAPPING)
            .setLabel("xml")
            .mapBestandsnaam("work/@label")
            .mapIdentifier("/work/metadata/dcterms.identifier")
            .mapUrl("/work/metadata/dcterms.source")
            .mapTitel(TextMapping.plain("/work/metadata/dcterms.title"))
            .addChildMapper("/work/expression/manifestation", ConfigurableFileMapper.childConfiguration()
                    .mapLabel("@label") // always pdf, we map it anyways
                    .mapBestandsnaam("item/@label")
                    .mapTitel(TextMapping.plain("item/@label"))
                    .publish()) // shown pdf in portal
            .mapper();

    public static final DocumentCollecting RONL_COLLECTOR = new DefaultDocumentCollector(SOURCE_LABEL, false);

    public static final PlooiDocumentMapping RONL_META_MAPPER = ConfigurableDocumentMapper.configureWith(XML_MAPPING)
            .setAanbieder(RonlShared.RONL_PUBLISHER)
            .mapOfficieleTitel(TextMapping.plain("/work/metadata/dcterms.title"))
            .mapLanguage(ResourceMapping.term("/work/metadata/dcterms.language"))
            .mapOpsteller(ResourceMapping.term("/work/metadata/dcterms.creator"))
            .mapCreatiedatum("/work/metadata/dcterms.issued")
            .mapGeldigheidsstartdatum(DateMapping.isoDate("/work/metadata/dcterms.available"))
            .mapPublisher(ResourceMapping.term("/work/metadata/dcterms.publisher"))
            .mapOmschrijving(TextMapping.plain("/work/metadata/dcterms.description"))
            .addExtraMetadata(ExtraMetadataMapping.temp(RELATION, "/work/metadata/dcterms.relation"))
            .addThema(ResourceMapping.term("/work/metadata/dcterms.subject"))
            .mapDocumentsoort(ResourceMapping.term("/work/metadata/dcterms.type"))
            .mapVerantwoordelijke(ResourceMapping.term("/work/metadata/overheid.authority"))
            .mapper();
    public final PlooiDocumentMappers ronlMappers = new PlooiDocumentMappers()
            .addMapper("xml", RONL_META_MAPPER)
            .addMapper("pdf", RonlShared.RONL_TIKA_MAPPER);

    public static final EnvelopeProcessing<PlooiEnvelope> RONL_RELATIONAL = target -> {
        ExtraMetadataMapping.getTempValues(target.getDocumentMeta().getExtraMetadata(), RELATION)
                .forEach(r -> target.addRelation(
                        new Relatie()
                                .relation(DcnIdentifierUtil.generateDcnId(SOURCE_LABEL, r))
                                .titel(r)
                                .role(RelationType.BIJLAGE.getUri())));
        return target;
    };

    private final String archiveRoot;
    private final PublicatieClient publicatieClient;
    private final RegistrationClient registrationClient;

    public RonlArchiefRoute(@Value("${dcn.ronl.archief.root:/export/dcn/ronl-archief}") String root,
            PublicatieClient publClient, RegistrationClient regClient) {
        this.archiveRoot = root;
        this.publicatieClient = publClient;
        this.registrationClient = regClient;
        this.ronlMappers.withPublicatieClient(publClient);
    }

    // @formatter:off No Eclipse formatting down here
    @Override
    public void configure() {
        new ErrorHandling(this).configure();

        from(Routing.internal(Routing.ingress(SOURCE_LABEL))).routeId(Routing.ingress(SOURCE_LABEL))
                .process(new PreppingProcessor(
                                new AdhocExecutionPrep(this.registrationClient, SOURCE_LABEL, TriggerType.INGRESS, "Ingressing archive from " + this.archiveRoot)))
                .to("controlbus:route?action=start&async=true&routeId=" + FILE_ROUTE);

        from("file:" + this.archiveRoot + "?noop=true&include=manifest.xml&idempotentRepository=#infiniteIdempotentRepository&recursive=true&sendEmptyMessageWhenIdle=true").routeId(FILE_ROUTE)
                .noAutoStartup() // The ingress route triggers the startup
                .choice()
                    .when().simple("${body} == null")
                        .to("controlbus:route?action=stop&async=true&routeId=" + FILE_ROUTE)
                    .otherwise()
                        .process(new PreppingProcessor(
                                new StagePrep(Stage.INGRESS),
                                new PreviousIngressPrep(this.registrationClient, SOURCE_LABEL, e -> true)))
                        .process(new FileMappingProcessor(RONL_FILEMAPPER))
                        .process(new CollectingProcessor(RONL_COLLECTOR))
                        .split(body())
                            .process(new EnvelopeProcessor<>(RonlShared.RONL_DIAGNOSTIC_FILTER))
                            .split(new OgnlExpression("request.body.plooiFiles"), new EnvelopeDiagnosticAggregationStrategy())
                                .choice()
                                    .when().ognl("!request.body.hasContent()")
                                        .pollEnrich(simple("file:${header.CamelFileParent}?noop=true&recursive=true&fileName=RAW(${body.getFile().getBestandsnaam()})"), -1, new EnrichedFileContentAggregationStrategy(), false)
                                .end()
                            .end()
                            .to(Routing.internal(CommonRoute.COMMON_INGRESS_SUBROUTE))
                        .end()
                .end();

        from(Routing.external(Routing.process(SOURCE_LABEL))).routeId(Routing.process(SOURCE_LABEL))
                .process(Routing.restoreFromManifest(this.publicatieClient))
                .choice()
                    .when().ognl(CommonRoute.BODY_DISCARDED)
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Discarding not allowed ${body}")
                    .otherwise()
                        .process(new EnvelopeProcessor<>(RonlShared.RONL_DIAGNOSTIC_FILTER))
                        .process(new EnvelopeProcessor<>(this.ronlMappers))
                        .process(new EnvelopeProcessor<>(new BeslisnotaDetector()))
                        .process(new EnvelopeProcessor<>(RonlShared.RONL_NORMALIZER))
                        .process(new EnvelopeProcessor<>(RONL_RELATIONAL))
                        .to(Routing.internal(CommonRoute.COMMON_PROCESS_SUBROUTE))
                .end(); // end choice
    }
}
