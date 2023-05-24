package nl.overheid.koop.plooi.model.data.util;

import java.util.List;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie;

public class PlooiBindings {

    private static final PlooiBinding<Plooi> PLOOI = new JSONBinding<>(Plooi.class);
    private static final PlooiBinding<Versie> VERSION = new JSONBinding<>(Versie.class);
    private static final PlooiBinding<Bestand> FILE = new JSONBinding<>(Bestand.class);
    private static final PlooiBinding<List<Bestand>> FILES = new JSONListBinding<>(Bestand.class);
    private static final PlooiBinding<List<Relatie>> RELATIONS = new JSONListBinding<>(Relatie.class);

    private PlooiBindings() {
    }

    public static PlooiBinding<Plooi> plooiBinding() {
        return PLOOI;
    }

    public static PlooiBinding<Versie> versionBinding() {
        return VERSION;
    }

    public static PlooiBinding<Bestand> fileBinding() {
        return FILE;
    }

    public static PlooiBinding<List<Bestand>> filesBinding() {
        return FILES;
    }

    public static PlooiBinding<List<Relatie>> relationsBinding() {
        return RELATIONS;
    }
}
