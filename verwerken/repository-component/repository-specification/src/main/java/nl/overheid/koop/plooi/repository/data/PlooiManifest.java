package nl.overheid.koop.plooi.repository.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Versie;

public class PlooiManifest {

    private Plooi plooi;

    public PlooiManifest(String dcnId, String src, String... extId) {
        this(dcnId, src, List.of(extId));
    }

    public PlooiManifest(String dcnId, String src, List<String> extIds) {
        this(new Plooi());
        if (extIds.isEmpty()) {
            throw new IllegalArgumentException("At least 1 externalIds is required for " + src);
        } else {
            this.plooi.plooiIntern(new PlooiIntern().dcnId(dcnId).sourceLabel(src).extId(extIds));
        }
    }

    public PlooiManifest(Plooi p) {
        this.plooi = p;
    }

    public final PlooiIntern getPlooiIntern() {
        return getOrSet(this.plooi::getPlooiIntern,
                this.plooi::setPlooiIntern,
                PlooiIntern::new);
    }

    public final List<Versie> getVersies() {
        return getOrSet(this.plooi::getVersies,
                this.plooi::setVersies,
                ArrayList::new);
    }

    public final Plooi getPlooi() {
        return this.plooi;
    }

    @Override
    public String toString() {
        var str = new StringBuilder(getClass().getSimpleName()).append(" ").append(getPlooiIntern().getDcnId());
        str.append(" with ")
                .append(this.plooi.getVersies() == null
                        ? 0
                        : this.plooi.getVersies().size())
                .append(" versions");
        return str.toString();
    }

    protected final <T> T getOrSet(Supplier<T> getter, Consumer<T> setter, Supplier<T> supplier) {
        T to = getter.get();
        if (to == null) {
            to = supplier.get();
            setter.accept(to);
        }
        return to;
    }
}
