package nl.overheid.koop.plooi.dcn.ronl;

import nl.overheid.koop.plooi.dcn.component.common.DiagnosticFilter;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.document.map.ConfigurableDocumentMapper;
import nl.overheid.koop.plooi.document.map.PlooiDocumentMapping;
import nl.overheid.koop.plooi.document.map.PlooiTikaMapping;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValueNormalizer;

public final class RonlShared {

    private RonlShared() {
        // Cannot instantiate util
    }

    public static final String RONL_PUBLISHER = "Rijksoverheid.nl";

    private static final String RONL_UNKNOWN_TYPE = "publicatie";

    public static final EnvelopeProcessing<Envelope> RONL_DIAGNOSTIC_FILTER = DiagnosticFilter.configure()
            .add(DiagnosticCode.EMPTY_ID) // RONL archive has no TOOI identifiers
            .add(DiagnosticCode.UNKNOWN_LABEL, "Documentsoort", RONL_UNKNOWN_TYPE)
            .add(DiagnosticCode.REQUIRED_DEFAULT, "Publisher")
            .add(DiagnosticCode.DISCARD);

    public static final PlooiDocumentMapping RONL_TIKA_MAPPER = ConfigurableDocumentMapper.configureWith(new PlooiTikaMapping())
            .addDocumentText(PlooiTikaMapping.DOCUMENT_TEXT)
            .mapper();

    public static final EnvelopeProcessing<PlooiEnvelope> RONL_NORMALIZER = PlooiDocumentValueNormalizer.configure()
            .useExtraDictionaryProperties("/nl/overheid/koop/plooi/dcn/ronl/waardelijsten_ronl.properties")
            .build();
}
