package nl.overheid.koop.plooi.dcn.route.common;

import java.util.ArrayList;
import nl.overheid.koop.dcn.publishingstate.PublishingStateUpdateProcessing;
import nl.overheid.koop.plooi.dcn.component.common.IdentityGroupProcessing;
import nl.overheid.koop.plooi.dcn.model.IdentityGroupRepository;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.dcn.repository.store.PlooiDeliveryStoring;
import nl.overheid.koop.plooi.dcn.repository.store.PlooiManifestReading;
import nl.overheid.koop.plooi.dcn.repository.store.PlooiMetadataStoring;
import nl.overheid.koop.plooi.dcn.repository.store.PlooiRepositoryDeleting;
import nl.overheid.koop.plooi.dcn.route.common.httpexception.HttpOperationFailedProcessor;
import nl.overheid.koop.plooi.dcn.route.prep.AdhocExecutionPrep;
import nl.overheid.koop.plooi.dcn.route.prep.PageQueryPrep;
import nl.overheid.koop.plooi.dcn.route.prep.StagePrep;
import nl.overheid.koop.plooi.dcn.route.processor.EnvelopeProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.IdProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.IngressMetrics;
import nl.overheid.koop.plooi.dcn.route.processor.PreppingProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.SolrProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.register.ExceptieRegister;
import nl.overheid.koop.plooi.dcn.route.processor.register.VerwerkingRegister;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentService;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.language.OgnlExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Shared routes used by channel routes. Here we instantiate single instances of these routes, shared by all channel
 * routes.
 */
@Component
public class CommonRoute extends RouteBuilder {

    public static final String DCN_PROCESS_DOCUMENT = "dcn-process-document";
    public static final String DCN_PROCESS_ROUTER = "common-process-router";
    public static final String DCN_DELETE_DOCUMENT = "dcn-delete-document";
    public static final String DCN_UNDELETE_DOCUMENT = "dcn-undelete-document";
    public static final String COMMON_INGRESS_SUBROUTE = "common-ingress";
    public static final String COMMON_PROCESS_SUBROUTE = "common-process";
    public static final String COMMON_ERROR_ROUTE = "common-error";
    public static final String COMMON_BULK_ROUTER = "common-bulk-router";
    public static final String COMMON_HTTP_ROUTE = "common-http-route";
    public static final String COMMON_DOWNLOAD_FILES_ROUTE = "common-split-files-route";
    public static final String COMMON_ENRICH_HTTP_ROUTE = "common-enrich-http-route";

    public static final String BODY_IS_EMPTY_STRING = "request.body.isBlank()";
    public static final String BODY_DCN_ID = "request.body.plooiIntern.dcnId";
    public static final String BODY_DISCARDED = "request.body.status().discarded";
    public static final String DCN_ID_FROM_PATH = "request.headers.CamelFileParent.replaceAll('.*[/\\\\\\\\]','')";

    private static final String IGNORING_EMPTY = " ignoring empty message";

    private final ExceptieRegister processingErrorReg;
    private final AanleverenClient aanleverenClient;
    private final PublicatieClient publicatieClient;
    private final SolrDocumentService solrDocumentService;
    private final IdentityGroupRepository identityGroupRepository;
    private final PublishingStateRepository publishingStateRepository;
    private final RegistrationClient registrationClient;

    @SuppressWarnings("java:S107")
    public CommonRoute(AanleverenClient deliveryClient, PublicatieClient updateClient, RegistrationClient regClient, ExceptieRegister errorReg,
            SolrDocumentService solrSrvc, IdentityGroupRepository idenGrReposy, PlatformTransactionManager tm, PublishingStateRepository publStateRepos) {
        super();
        this.aanleverenClient = deliveryClient;
        this.publicatieClient = updateClient;
        this.registrationClient = regClient;
        this.processingErrorReg = errorReg;
        this.solrDocumentService = solrSrvc;
        this.identityGroupRepository = idenGrReposy;
        this.publishingStateRepository = publStateRepos;
    }

    @Override
    public void configure() {
        new ErrorHandling(this)
                .withHttpRedeliveries(ErrorHandling.DEFAULT_RETRIES)
                .configure();
        configureGeneralRoutes();
        configureCommonRoutes();
    }

    // @formatter:off No Eclipse formatting down here
    private void configureGeneralRoutes() {
        from(Routing.external(DCN_PROCESS_DOCUMENT)).routeId(DCN_PROCESS_DOCUMENT)
                .split(body())
                .choice()
                    .when().ognl(BODY_IS_EMPTY_STRING)
                        .log(LoggingLevel.INFO, this.log.getName(), DCN_PROCESS_DOCUMENT + IGNORING_EMPTY)
                    .otherwise()
                        .process(new PreppingProcessor(
                                new AdhocExecutionPrep(this.registrationClient, TriggerType.REPROCESS)))
                        .to(Routing.internal(DCN_PROCESS_ROUTER))
                .end(); // end choice
        from(Routing.external(DCN_DELETE_DOCUMENT)).routeId(DCN_DELETE_DOCUMENT)
                .choice()
                    .when().ognl(BODY_IS_EMPTY_STRING)
                        .log(LoggingLevel.INFO, this.log.getName(), DCN_DELETE_DOCUMENT + IGNORING_EMPTY)
                    .otherwise()
                        .process(new PreppingProcessor(
                                new StagePrep(Stage.DELETE),
                                new AdhocExecutionPrep(this.registrationClient,TriggerType.DELETION)))
                        .process(new IdProcessor(new PlooiRepositoryDeleting(this.publicatieClient, OorzaakEnum.INTREKKING)))
                        .split(body()) // PlooiRepositoryDeleting returns the identifiers of the requested document and of the parts
                            .process(new IdProcessor(new PlooiManifestReading(this.publicatieClient)))
                            .process(new VerwerkingRegister(this.registrationClient))
                            .transform().ognl(BODY_DCN_ID)
                            .to(Routing.external(SolrRoute.SOLR_DELETE_ROUTE))
                        .end() // end split body
                .end(); // end choice
        from(Routing.external(DCN_UNDELETE_DOCUMENT)).routeId(DCN_UNDELETE_DOCUMENT)
                .choice()
                    .when().ognl(BODY_IS_EMPTY_STRING)
                        .log(LoggingLevel.INFO, this.log.getName(), DCN_UNDELETE_DOCUMENT + IGNORING_EMPTY)
                    .otherwise()
                        .process(new PreppingProcessor(
                                new StagePrep(Stage.UNDELETE),
                                new AdhocExecutionPrep(this.registrationClient, TriggerType.UNDELETION)))
                        .process(new IdProcessor(new PlooiRepositoryDeleting(this.publicatieClient, OorzaakEnum.HERPUBLICATIE)))
                        .split(body())
                            .process(new VerwerkingRegister(this.registrationClient))
                            .to(Routing.internal(DCN_PROCESS_ROUTER))
                        .end() // end split body
                .end(); // end choice
        from(Routing.external(COMMON_BULK_ROUTER)).routeId(COMMON_BULK_ROUTER)
                .loopDoWhile(exchange -> !exchange.getIn().getBody(ArrayList.class).isEmpty())
                    .process(new SolrProcessor(this.solrDocumentService))
                    .split(body())
                        .toD(Routing.external("${header.action}"))
                    .end()
                .end();
    }

    /**
     * Common routes, included in all custom routes. Currently a single instance of these common routes is shared by all
     * custom routes. When we start distributing custom routes across different VMs (e.g. Docker containers per source),
     * then the configure method below needs to be called from the custom route's configure() method.
     * <p>
     * These routes are included wit a line like '.to(Routing.internal(CommonRoute.COMMON_INGRESS_SUBROUTE))'. They end with
     * passing the message to the next stage.
     */
    private void configureCommonRoutes() {
        /*
         * Deals with standard stuff after the source specific ingression is done; downloads not yet downloaded files, stores
         * everything, logs the document receipt in the database and forwards the DCN id to the processing queue for follow
         * up.
         */
        from(Routing.internal(COMMON_INGRESS_SUBROUTE)).routeId(COMMON_INGRESS_SUBROUTE)
                .split(body()) // body might be an single envelope, or list as produced by the collector
                    .to(Routing.internal(COMMON_DOWNLOAD_FILES_ROUTE))
                    .process(new EnvelopeProcessor<>(new PlooiDeliveryStoring(this.aanleverenClient,this.publicatieClient, this.publishingStateRepository)))
                    .process(new VerwerkingRegister(this.registrationClient))
                    .process(new IngressMetrics())
                    .choice()
                        .when().ognl(BODY_DISCARDED)
                            .process(new EnvelopeProcessor<>(new PublishingStateUpdateProcessing(this.publishingStateRepository, PublishingState.State.OK)))
                            .log(LoggingLevel.DEBUG, this.log.getName(), "Discarding ${body} after ingress")
                        .otherwise()
                            .transform().ognl(BODY_DCN_ID)
                            .to(Routing.internal(DCN_PROCESS_ROUTER))
                    .end() // end choice
                .end(); // end split body

        /*
         * Routes a message with an DCN identifier to the right processing endpoint
         */
        from(Routing.internal(DCN_PROCESS_ROUTER)).routeId(DCN_PROCESS_ROUTER)
                .process(new PreppingProcessor(new StagePrep(Stage.PROCESS)))
                .toD("language:ognl:"
                        + "@nl.overheid.koop.plooi.dcn.route.common.Routing@external("
                        + "@nl.overheid.koop.plooi.dcn.route.common.Routing@process("
                        + "@nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil@extractSource(request.body)))");

        /*
         * Deals with the standard stuff after the source specific processing is done; validation, storage, etc. and forwards
         * the DCN id to the Solr index queue for indexing.
         */
        from(Routing.internal(COMMON_PROCESS_SUBROUTE)).routeId(COMMON_PROCESS_SUBROUTE)
                .process(new EnvelopeProcessor<>(new PlooiDocumentValidator()))
                .choice()
                    .when().ognl(BODY_DISCARDED)
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Skipping further processing of ${body} after validation")
                    .otherwise()
                        .process(new EnvelopeProcessor<>(new IdentityGroupProcessing(this.identityGroupRepository)))
                        .process(new EnvelopeProcessor<>(new PlooiMetadataStoring(this.publicatieClient, this.publishingStateRepository)))
                .end() // end choice
                .process(new VerwerkingRegister(this.registrationClient))
                .choice()
                    .when().ognl(BODY_DISCARDED)
                        .process(new EnvelopeProcessor<>(new PublishingStateUpdateProcessing(this.publishingStateRepository, PublishingState.State.OK)))
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Discarding ${body} after processing")
                    .when().simple("{{dcn.skip.indexing:false}}")
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Indexing is skipped")
                    .otherwise()
                        .transform().ognl(BODY_DCN_ID)
                        .to(Routing.external(SolrRoute.SOLR_INDEX_ROUTE))
                .end(); // end choice

        /*
         * Error handling route, called from ErrorHandling
         */
        from(Routing.internal(COMMON_ERROR_ROUTE)).routeId(COMMON_ERROR_ROUTE)
                .bean(this.processingErrorReg);

        /*
         * Download all files in a DeliveryEnvelope
         */
        from(Routing.internal(COMMON_DOWNLOAD_FILES_ROUTE)).routeId(COMMON_DOWNLOAD_FILES_ROUTE)
                .errorHandler(noErrorHandler()) // Pass exceptions to calling route
                .split(new OgnlExpression("request.body.plooiFiles"), new EnvelopeDiagnosticAggregationStrategy())
                    .stopOnException() // Break and throw if an exception occurs
                    .choice()
                        .when().ognl("request.body.getFile().getUrl() != null && !request.body.hasContent()" )
                            .to(Routing.internal(COMMON_ENRICH_HTTP_ROUTE))
                        .otherwise()
                            .log(LoggingLevel.DEBUG, this.log.getName(), "Not downloading ${body}")
                    .end() // end choice
                .end(); // end split plooiFiles

        /*
         * Enrich with HTTP route, called for enriching the data with an HTTP response
         */
        from(Routing.internal(COMMON_ENRICH_HTTP_ROUTE)).routeId(COMMON_ENRICH_HTTP_ROUTE)
                .errorHandler(noErrorHandler())
                .setHeader(Exchange.HTTP_URI).ognl("request.body.getFile().getUrl()")
                .enrich(PageQueryPrep.HTTP_URI_WITH_PROXY, new EnrichedFileContentAggregationStrategy())
                .process(new HttpOperationFailedProcessor());

        /*
         * Direct HTTP route, called for HTTP consume
         */
        from(Routing.internal(COMMON_HTTP_ROUTE)).routeId(COMMON_HTTP_ROUTE)
                .errorHandler(noErrorHandler())
                .to(PageQueryPrep.HTTP_URI_WITH_PROXY)
                .process(new HttpOperationFailedProcessor());
    }
}
