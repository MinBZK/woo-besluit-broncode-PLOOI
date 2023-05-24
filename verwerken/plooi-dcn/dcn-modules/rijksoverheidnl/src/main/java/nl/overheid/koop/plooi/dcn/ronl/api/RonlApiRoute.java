package nl.overheid.koop.plooi.dcn.ronl.api;

import java.util.Map;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.ronl.RonlShared;
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
import nl.overheid.koop.plooi.document.map.PlooiDocumentMappers;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMapping;
import nl.overheid.koop.plooi.document.map.PlooiXmlMapping;
import nl.overheid.koop.plooi.document.map.ResourceMapping;
import nl.overheid.koop.plooi.document.map.TextMapping;
import nl.overheid.koop.plooi.document.normalize.BeslisnotaDetector;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Routes for performing downloads from the RONL-API. This calls the main API, and pages through the result.
 * <p>
 * The entry point is ronl-ingress. It can be triggered with a header lastmodifiedsince (yyyymmdd) to use that date for
 * the API calls. If that's not there, then a full download is done.
 */
@Component
public class RonlApiRoute extends RouteBuilder {

    static final String SOURCE_LABEL = "ronl";

    private static final String DOCUMENTS_FOUND = "dcn_documentsFound";

    private static final PlooiXmlMapping XML_MAPPING = new PlooiXmlMapping();

    private static final PageQueryPrep RONL_PAGING_QUERY = new PageQueryPrep()
            .setBaseUrl("https://opendata.rijksoverheid.nl/v1/sources/rijksoverheid/documents") // we're only interested in the documents, not in other types
            .setSinceDateParam("lastmodifiedsince")
            .setSinceDateFormat("yyyyMMdd")
            .setPageOffsetParam("offset")
            .setPageSizeParam("rows")
            .setPageSize(100);

    public static final ConfigurableFileMapper RONL_FILEMAPPER = ConfigurableFileMapper.configureWith(XML_MAPPING)
            .mapIdentifier("/document/id")
            .mapUrl("/document/dataurl")
            .mapTitel(TextMapping.plain("/document/title"))
            .setLabel("xml")
            .addChildMapper("/document/files/file", ConfigurableFileMapper.childConfiguration()
                    .mapUrl("fileurl")
                    .mapBestandsnaam("filename")
                    .mapTitel(TextMapping.plain("filetitle"))
                    .publish())
            .mapper();

    public final RonlDocumentCollector ronlCollector = new RonlDocumentCollector(SOURCE_LABEL, true);

    public static final PlooiDocumentMapping RONL_META_MAPPER = ConfigurableDocumentMapper.configureWith(XML_MAPPING)
            .setAanbieder(RonlShared.RONL_PUBLISHER)
            .mapOfficieleTitel(TextMapping.plain("/document/title"))
            .mapLanguage(ResourceMapping.term("/document/languages/language"))
            .mapOpsteller(ResourceMapping.owms("/document/creators/*"))
            .mapVerantwoordelijke(ResourceMapping.owms("/document/authorities/*"))
            .mapCreatiedatum("/document/issued")
            .mapGeldigheidsstartdatum(DateMapping.isoDateTime("/document/available"))
            .mapOmschrijving(TextMapping.embeddedHTML("/document/introduction"))
            .addThema(ResourceMapping.owms("/document/subjects/subject"))
            .mapDocumentsoort(ResourceMapping.owms("/document/type"))
            .addBodyTekst(TextMapping.embeddedHTML("/document/content/contentblock/*"), Map.of("paragraphtitle", "h2", "paragraph", "div"))
            .mapper();
    public final PlooiDocumentMappers ronlMappers = new PlooiDocumentMappers()
            .addMapper("xml", RONL_META_MAPPER)
            .addMapper("pdf", RonlShared.RONL_TIKA_MAPPER);

    private final PublicatieClient publicatieClient;
    private final RegistrationClient registrationClient;

    public RonlApiRoute(PublicatieClient publClient, RegistrationClient regClient) {
        this.publicatieClient = publClient;
        this.registrationClient = regClient;
        this.ronlMappers.withPublicatieClient(publClient);
        this.ronlCollector.withPublicatieClient(publClient);
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
                        RONL_PAGING_QUERY,
                        new IngressExecutionPrep(this.registrationClient, SOURCE_LABEL, Exchange.HTTP_URI)))
                .setHeader(DOCUMENTS_FOUND).constant(Integer.MAX_VALUE)
                .loopDoWhile(header(DOCUMENTS_FOUND).isGreaterThan(0))
                    .bean(RONL_PAGING_QUERY, PageQueryPrep.PREPARE_NEXT_METHOD)
                    .to(Routing.internal(CommonRoute.COMMON_HTTP_ROUTE))
                    .setHeader(DOCUMENTS_FOUND).xpath("count(//document)", Integer.class)
                    .split().tokenizeXML("document").streaming()
                        .setHeader(Exchange.HTTP_URI).xpath("/document/dataurl/text()")
                        .delay(100)
                            .to(Routing.internal(CommonRoute.COMMON_HTTP_ROUTE))
                        .end()
                        .process(new FileMappingProcessor(RONL_FILEMAPPER))
                        .process(new CollectingProcessor(this.ronlCollector))
                        .process(new EnvelopeProcessor<>(RonlShared.RONL_DIAGNOSTIC_FILTER))
                        .to(Routing.internal(CommonRoute.COMMON_INGRESS_SUBROUTE))
                    .end()
                .end()
                .bean(RONL_PAGING_QUERY, PageQueryPrep.FINISH_METHOD);

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
                        .process(new EnvelopeProcessor<>(this.ronlCollector))
                        .to(Routing.internal(CommonRoute.COMMON_PROCESS_SUBROUTE))
                .end(); // end choice
    }
}
