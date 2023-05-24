package nl.overheid.koop.plooi.dcn.component.types;

import java.util.ArrayList;
import java.util.List;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;

/**
 * Collects a {@link PlooiFile} into one or more {@link DeliveryEnvelope}s.
 * <p>
 * A typical application is for sources which attach one or more PDFs to a download page. The ConfigurableFileMapper
 * then adds each attachment as PlooiFile child. In here we then create a DeliveryEnvelope for each PDF, pairing it with
 * metadata of the documents which is typically found on the parent page.
 */
public interface DocumentCollecting extends EnvelopeProcessing<PlooiEnvelope> {

    /**
     * Collects a {@link PlooiFile} into one or more {@link DeliveryEnvelope}s.
     *
     * @param  file The PlooiFile to collect from
     * @return      {@link List} of resulting DeliveryEnvelopes (so typically this method is flatMapped)
     */
    List<DeliveryEnvelope> collect(PlooiFile file);

    /**
     * Convenience method which creates a {@link DeliveryEnvelope} with identifier based on the method's parameters.
     *
     * @param  file        Input (metadata) file to add to the new document
     * @param  source      The source label used to build the identifier
     * @param  externalIds The external identifiers used to build the identifier. At least 1 identifier is required
     * @return             {@link DeliveryEnvelope} with identifier set
     * @see                DcnIdentifierUtil#generateIdentifier(String, String...)
     */
    static DeliveryEnvelope createDeliveryEnvelope(PlooiFile file, String source, String... externalIds) {
        return new DeliveryEnvelope(source, externalIds).addPlooiFile(file);
    }

    /**
     * Convenience method which creates a list of {@link DeliveryEnvelope}s and adds the provided document to it.
     *
     * @param  plooiDocument Document to add to the list
     * @return               List of {@link DeliveryEnvelope}s with the provided document
     */
    static List<DeliveryEnvelope> createDocumentListWith(DeliveryEnvelope plooiDocument) {
        var documentList = new ArrayList<DeliveryEnvelope>();
        documentList.add(plooiDocument);
        return documentList;
    }

    /**
     * Optional method to further process a collected PLOOI document after it is mapped and normalized.
     * <p>
     * For the example above we might want to update the title and URL of the multiple PDFs in a download page
     *
     * @param  target The PlooiEnvelope to process
     * @return        The processed PlooiEnvelope
     */
    @Override
    default PlooiEnvelope process(PlooiEnvelope target) {
        return target;
    }
}
