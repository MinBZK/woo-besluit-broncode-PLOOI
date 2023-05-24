package nl.overheid.koop.plooi.dcn.api.service;

import nl.overheid.koop.plooi.dcn.component.common.DiagnosticFilter;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.route.common.CommonRoute;
import nl.overheid.koop.plooi.dcn.route.common.ErrorHandling;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import nl.overheid.koop.plooi.dcn.route.processor.EnvelopeProcessor;
import nl.overheid.koop.plooi.document.map.ConfigurableDocumentMapper;
import nl.overheid.koop.plooi.document.map.NativeDocumentMapper;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMappers;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMapping;
import nl.overheid.koop.plooi.document.map.PlooiTikaMapping;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValueNormalizer;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ApiProcessRoute extends RouteBuilder {

    static final String PROVIDER = "Aanleverloket";

    public static final EnvelopeProcessing<Envelope> API_DIAGNOSTIC_FILTER = DiagnosticFilter.configure();

    public static final PlooiDocumentMapping API_META_MAPPER = new NativeDocumentMapper();

    public static final PlooiDocumentMapping API_TIKA_MAPPER = ConfigurableDocumentMapper.configureWith(new PlooiTikaMapping())
            .addDocumentText(PlooiTikaMapping.DOCUMENT_TEXT)
            .mapper();

    public final PlooiDocumentMappers apiMappers = new PlooiDocumentMappers()
            .addMapper(ApiServiceController.META, API_META_MAPPER)
            .addCatchallMapper(API_TIKA_MAPPER);

    public static final EnvelopeProcessing<PlooiEnvelope> API_NORMALIZER = PlooiDocumentValueNormalizer.configure().build();

    public static final EnvelopeProcessing<PlooiEnvelope> API_POSTMAP = target -> {
        var plooiIntern = target.getPlooiIntern();
        plooiIntern.setAanbieder(PROVIDER);
        return target;
    };

    private final PublicatieClient publicatieClient;

    public ApiProcessRoute(PublicatieClient publClient) {
        this.publicatieClient = publClient;
        this.apiMappers.withPublicatieClient(publClient);
    }

    // @formatter:off No Eclipse formatting down here
    @Override
    public void configure() throws Exception {
        new ErrorHandling(this).configure();

        from(Routing.external(Routing.process(DcnIdentifierUtil.PLOOI_API_SRC))).routeId(Routing.process(DcnIdentifierUtil.PLOOI_API_SRC))
                .process(Routing.restoreFromManifest(this.publicatieClient))
                .choice()
                    .when().ognl(CommonRoute.BODY_DISCARDED)
                        .log(LoggingLevel.DEBUG, this.log.getName(), "Discarding not allowed ${body}")
                    .otherwise()
                        .process(new EnvelopeProcessor<>(API_DIAGNOSTIC_FILTER))
                        .process(new EnvelopeProcessor<>(this.apiMappers))
                        .process(new EnvelopeProcessor<>(API_NORMALIZER))
                        .process(new EnvelopeProcessor<>(API_POSTMAP))
                        .to(Routing.internal(CommonRoute.COMMON_PROCESS_SUBROUTE))
                .end(); // end choice
    }
}
