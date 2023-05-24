package nl.overheid.koop.plooi.repository.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.JSONListBinding;
import nl.overheid.koop.plooi.model.data.util.PlooiBinding;
import nl.overheid.koop.plooi.model.data.util.PlooiBindingException;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.service.error.ResponseErrorHelper;

final class PlooiBindingsHelper {

    private PlooiBindingsHelper() {
    }

    public static Plooi unmarshalPlooi(String dcnId, InputStream plooiStream) {
        return unmarshal(dcnId, plooiStream, PlooiBindings.plooiBinding());
    }

    public static Versie unmarshalVersion(InputStream versionStream) {
        return unmarshal(null, versionStream, PlooiBindings.versionBinding());
    }

    public static byte[] marshalPlooi(String dcnId, Plooi plooi) {
        return marshal(dcnId, plooi, PlooiBindings.plooiBinding());
    }

    public static byte[] marshalVersion(String dcnId, Versie version) {
        return marshal(dcnId, version, PlooiBindings.versionBinding());
    }

    public static byte[] marshalFile(String dcnId, Bestand file) {
        return marshal(dcnId, file, PlooiBindings.fileBinding());
    }

    public static byte[] marshalRelations(String dcnId, List<Relatie> relations) {
        return marshal(dcnId, relations, PlooiBindings.relationsBinding());
    }

    private static final PlooiBinding<List<String>> LIST_BINDING = new JSONListBinding<>(String.class);

    public static List<String> unmarshalStrings(String dcnId, InputStream stringsStream) {
        return unmarshal(dcnId, stringsStream, LIST_BINDING);
    }

    public static byte[] marshalStrings(String dcnId, List<String> strings) {
        return marshal(dcnId, strings, LIST_BINDING);
    }

    private static <T> T unmarshal(String dcnId, InputStream toUnmarshal, PlooiBinding<T> binding) {
        try {
            return binding.unmarshalFromStream(toUnmarshal);
        } catch (PlooiBindingException e) {
            throw ResponseErrorHelper.inputError(dcnId, e);
        }
    }

    private static <T> byte[] marshal(String dcnId, T toMarshal, PlooiBinding<T> binding) {
        try {
            return binding.marshalToString(toMarshal).getBytes(StandardCharsets.UTF_8);
        } catch (PlooiBindingException e) {
            throw ResponseErrorHelper.internalError(dcnId, e);
        }
    }
}
