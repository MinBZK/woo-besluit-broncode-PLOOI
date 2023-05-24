package nl.overheid.koop.plooi.model.data.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import org.apache.commons.lang3.tuple.Pair;

public final class AggregatedVersion {

    private final Versie versie;
    private final List<Pair<Integer, Bestand>> files = new ArrayList<>();

    public static Optional<AggregatedVersion> aggregateLatestVersion(List<Versie> from) {
        return aggregateVersion(from, -1);
    }

    public static Optional<AggregatedVersion> aggregateVersion(List<Versie> from, int versionNumber) {
        Optional<AggregatedVersion> aggregated = Optional.empty();
        if (from != null) {
            for (int i = from.size() - 1; i >= 0; i--) {
                var current = from.get(i);
                // Objects.requireNonNullElse below; current.nummer==0 can only happen for intrekking, but let's prevent NPE anyway
                if ((aggregated.isEmpty()
                        && (OorzaakEnum.INTREKKING == current.getOorzaak()
                                || versionNumber < 0 || versionNumber == Objects.requireNonNullElse(current.getNummer(), Integer.valueOf(-2)).intValue()))) {
                    aggregated = Optional.of(new AggregatedVersion(current));
                } else if (aggregated.isPresent()
                        && current.getNummer() != null // Ignoring obsolete intrekking
                        && (versionNumber < 0 || versionNumber >= current.getNummer().intValue())) {
                    aggregated.get().merge(current);
                }
            }
        }
        return aggregated.filter(av -> !(av.getVersie().getOorzaak() == OorzaakEnum.INTREKKING
                && (av.getVersie().getNummer() == null || av.getVersie().getNummer() < versionNumber)));
    }

    private static final List<OorzaakEnum> NOT_ALLOWED = List.of(OorzaakEnum.INTREKKING);

    public static boolean isAllowed(List<Versie> from) {
        var latest = aggregateVersion(from, -1);
        return latest.isPresent() && !NOT_ALLOWED.contains(latest.get().getVersie().getOorzaak());
    }

    public static Versie copy(Versie toCopy) {
        return new Versie()
                .nummer(toCopy.getNummer())
                .oorzaak(toCopy.getOorzaak())
                .redenVerwijderingVervanging(toCopy.getRedenVerwijderingVervanging())
                .mutatiedatumtijd(toCopy.getMutatiedatumtijd())
                .openbaarmakingsdatum(toCopy.getOpenbaarmakingsdatum())
                .wijzigingsdatum(toCopy.getWijzigingsdatum())
                .zichtbaarheidsdatumtijd(toCopy.getZichtbaarheidsdatumtijd())
                .blokkades(toCopy.getBlokkades());
    }

    private AggregatedVersion(Versie last) {
        this.versie = copy(last);
        merge(last);
    }

    private Predicate<Bestand> labelNotExisting = file -> this.files.stream().noneMatch(p -> p.getRight().getLabel().equals(file.getLabel()));

    private void merge(Versie current) {
        this.versie.setOpenbaarmakingsdatum(current.getOpenbaarmakingsdatum());
        if (this.versie.getNummer() == null) {
            this.versie.nummer(current.getNummer());
        }
        if (current.getBestanden() != null) {
            this.files.addAll(current.getBestanden().stream().filter(this.labelNotExisting).map(file -> Pair.of(current.getNummer(), file)).toList());
        }
    }

    public Versie getVersie() {
        return this.versie;
    }

    public Optional<Pair<Integer, Bestand>> getFile(String label) {
        return this.files.stream().filter(p -> Objects.requireNonNull(label, "label is required").equals(p.getRight().getLabel())).findFirst();
    }

    public List<String> getLabels() {
        return this.files.stream().map(Pair::getRight).map(Bestand::getLabel).toList();
    }

    public Versie toVersie() {
        return this.versie.bestanden(new ArrayList<>(this.files.stream().map(Pair::getRight).toList()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.versie, this.files);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AggregatedVersion otherAggregated
                && Objects.equals(this.versie, otherAggregated.versie)
                && Objects.equals(this.files, otherAggregated.files);
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName())
                .append(" ")
                .append(this.versie.getOorzaak())
                .append(" versie ")
                .append(this.versie.getNummer())
                .append(" with ")
                .append(this.files.size())
                .append(" bestanden")
                .toString();
    }
}
