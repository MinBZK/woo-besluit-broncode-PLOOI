package nl.overheid.koop.plooi.model.data.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Versie;
import org.junit.jupiter.api.Test;

class AggregatedVersionTest {

    @Test
    void getVersion_empty() {
        assertFalse(AggregatedVersion.aggregateVersion(new ArrayList<Versie>(), 1).isPresent());
        assertFalse(AggregatedVersion.aggregateVersion(null, 1).isPresent());
    }

    @Test
    void getVersion_single() {
        var versies = List.of(
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.AANLEVERING)
                        .openbaarmakingsdatum(LocalDate.of(2023, 1, 1))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml")));
        assertFalse(AggregatedVersion.aggregateVersion(versies, 0).isPresent());
        var v1 = AggregatedVersion.aggregateVersion(versies, 1).orElseThrow();
        assertEquals(1, v1.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.AANLEVERING, v1.getVersie().getOorzaak());
        assertEquals("2023-01-01", v1.getVersie().getOpenbaarmakingsdatum().toString());
        assertNull(v1.getVersie().getWijzigingsdatum());
        assertEquals("2023-01-01T12:00Z", v1.getVersie().getMutatiedatumtijd().toString());
        assertFalse(AggregatedVersion.aggregateVersion(versies, 2).isPresent());
    }

    @Test
    void getVersion_multi() {
        var versies = List.of(
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.AANLEVERING)
                        .openbaarmakingsdatum(LocalDate.of(2023, 1, 1))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml"))
                        .addBestandenItem(new Bestand().label("doc").bestandsnaam("1.pdf")),
                new Versie().nummer(2)
                        .oorzaak(Versie.OorzaakEnum.WIJZIGING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 2))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 2), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("doc").bestandsnaam("2.pdf")),
                new Versie().nummer(3)
                        .oorzaak(Versie.OorzaakEnum.WIJZIGING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 3))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 3), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("2.xml")),
                new Versie().nummer(4)
                        .oorzaak(Versie.OorzaakEnum.WIJZIGING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 4))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 4), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("doc").bestandsnaam("3.pdf")));
        assertFalse(AggregatedVersion.aggregateVersion(versies, 0).isPresent());
        var v1 = AggregatedVersion.aggregateVersion(versies, 1).orElseThrow();
        assertEquals(1, v1.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.AANLEVERING, v1.getVersie().getOorzaak());
        assertEquals("2023-01-01", v1.getVersie().getOpenbaarmakingsdatum().toString());
        assertNull(v1.getVersie().getWijzigingsdatum());
        assertEquals("2023-01-01T12:00Z", v1.getVersie().getMutatiedatumtijd().toString());
        assertEquals(1, v1.getFile("meta").orElseThrow().getLeft());
        assertEquals("1.xml", v1.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertEquals(1, v1.getFile("doc").orElseThrow().getLeft());
        assertEquals("1.pdf", v1.getFile("doc").orElseThrow().getRight().getBestandsnaam());
        var v2 = AggregatedVersion.aggregateVersion(versies, 2).orElseThrow();
        assertEquals(2, v2.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.WIJZIGING, v2.getVersie().getOorzaak());
        assertEquals("2023-01-01", v2.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-02", v2.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-02T12:00Z", v2.getVersie().getMutatiedatumtijd().toString());
        assertEquals(1, v2.getFile("meta").orElseThrow().getLeft());
        assertEquals("1.xml", v2.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertEquals(2, v2.getFile("doc").orElseThrow().getLeft());
        assertEquals("2.pdf", v2.getFile("doc").orElseThrow().getRight().getBestandsnaam());
        var v3 = AggregatedVersion.aggregateVersion(versies, 3).orElseThrow();
        assertEquals(3, v3.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.WIJZIGING, v3.getVersie().getOorzaak());
        assertEquals("2023-01-01", v3.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-03", v3.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-03T12:00Z", v3.getVersie().getMutatiedatumtijd().toString());
        assertEquals(3, v3.getFile("meta").orElseThrow().getLeft());
        assertEquals("2.xml", v3.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertEquals(2, v3.getFile("doc").orElseThrow().getLeft());
        assertEquals("2.pdf", v3.getFile("doc").orElseThrow().getRight().getBestandsnaam());
        var v4 = AggregatedVersion.aggregateVersion(versies, 4).orElseThrow();
        assertEquals(4, v4.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.WIJZIGING, v4.getVersie().getOorzaak());
        assertEquals("2023-01-01", v4.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-04", v4.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-04T12:00Z", v4.getVersie().getMutatiedatumtijd().toString());
        assertEquals(3, v4.getFile("meta").orElseThrow().getLeft());
        assertEquals("2.xml", v4.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertEquals(4, v4.getFile("doc").orElseThrow().getLeft());
        assertEquals("3.pdf", v4.getFile("doc").orElseThrow().getRight().getBestandsnaam());
        assertFalse(AggregatedVersion.aggregateVersion(versies, 5).isPresent());
    }

    @Test
    void getVersion_deleted() {
        var versies = List.of(
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.AANLEVERING)
                        .openbaarmakingsdatum(LocalDate.of(2023, 1, 1))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml")),
                new Versie()
                        .oorzaak(Versie.OorzaakEnum.INTREKKING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 2))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 2), LocalTime.NOON, ZoneOffset.UTC)));
        assertFalse(AggregatedVersion.aggregateVersion(versies, 0).isPresent());
        var v1 = AggregatedVersion.aggregateVersion(versies, 1).orElseThrow();
        assertEquals(1, v1.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.INTREKKING, v1.getVersie().getOorzaak());
        assertEquals("2023-01-01", v1.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-02", v1.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-02T12:00Z", v1.getVersie().getMutatiedatumtijd().toString());
        assertEquals(1, v1.getFile("meta").orElseThrow().getLeft());
    }

    @Test
    void getVersion_republished_deletion() {
        var versies = List.of(
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.AANLEVERING)
                        .openbaarmakingsdatum(LocalDate.of(2023, 1, 1))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml")),
                new Versie()
                        .oorzaak(Versie.OorzaakEnum.INTREKKING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 2))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 2), LocalTime.NOON, ZoneOffset.UTC)),
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.HERPUBLICATIE)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 3))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 3), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml")));
        var v1 = AggregatedVersion.aggregateVersion(versies, 1).orElseThrow();
        assertEquals(1, v1.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.HERPUBLICATIE, v1.getVersie().getOorzaak());
        assertEquals("2023-01-01", v1.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-03", v1.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-03T12:00Z", v1.getVersie().getMutatiedatumtijd().toString());
        assertEquals(1, v1.getFile("meta").orElseThrow().getLeft());
        assertEquals("1.xml", v1.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertEquals(v1, AggregatedVersion.aggregateVersion(versies, -1).orElseThrow());
        assertFalse(AggregatedVersion.aggregateVersion(versies, 2).isPresent());
    }

    @Test
    void getVersion_republished_old_version() {
        var versies = List.of(
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.AANLEVERING)
                        .openbaarmakingsdatum(LocalDate.of(2023, 1, 1))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml")),
                new Versie().nummer(2)
                        .oorzaak(Versie.OorzaakEnum.WIJZIGING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 2))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 2), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("2.xml")),
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.HERPUBLICATIE)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 3))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 3), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml")));
        var v1 = AggregatedVersion.aggregateVersion(versies, 1).orElseThrow();
        assertEquals(1, v1.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.HERPUBLICATIE, v1.getVersie().getOorzaak());
        assertEquals("2023-01-01", v1.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-03", v1.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-03T12:00Z", v1.getVersie().getMutatiedatumtijd().toString());
        assertEquals(1, v1.getFile("meta").orElseThrow().getLeft());
        assertEquals("1.xml", v1.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertEquals(v1, AggregatedVersion.aggregateVersion(versies, -1).orElseThrow());
        var v2 = AggregatedVersion.aggregateVersion(versies, 2).orElseThrow();
        assertEquals(2, v2.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.WIJZIGING, v2.getVersie().getOorzaak());
        assertEquals("2023-01-01", v2.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-02", v2.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-02T12:00Z", v2.getVersie().getMutatiedatumtijd().toString());
        assertEquals(2, v2.getFile("meta").orElseThrow().getLeft());
        assertEquals("2.xml", v2.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertFalse(AggregatedVersion.aggregateVersion(versies, 3).isPresent());
    }

    @Test
    void getVersion_republished_via_update() {
        var versies = List.of(
                new Versie().nummer(1)
                        .oorzaak(Versie.OorzaakEnum.AANLEVERING)
                        .openbaarmakingsdatum(LocalDate.of(2023, 1, 1))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 1), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("1.xml")),
                new Versie().nummer(2)
                        .oorzaak(Versie.OorzaakEnum.WIJZIGING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 2))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 2), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("2.xml")),
                new Versie()
                        .oorzaak(Versie.OorzaakEnum.INTREKKING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 3))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 3), LocalTime.NOON, ZoneOffset.UTC)),
                new Versie().nummer(3)
                        .oorzaak(Versie.OorzaakEnum.WIJZIGING)
                        .wijzigingsdatum(LocalDate.of(2023, 1, 4))
                        .mutatiedatumtijd(OffsetDateTime.of(LocalDate.of(2023, 1, 4), LocalTime.NOON, ZoneOffset.UTC))
                        .addBestandenItem(new Bestand().label("meta").bestandsnaam("3.xml")));
        var v1 = AggregatedVersion.aggregateVersion(versies, 1).orElseThrow();
        assertEquals(1, v1.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.INTREKKING, v1.getVersie().getOorzaak());
        assertEquals("2023-01-01", v1.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-03", v1.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-03T12:00Z", v1.getVersie().getMutatiedatumtijd().toString());
        assertEquals(1, v1.getFile("meta").orElseThrow().getLeft());
        assertEquals("1.xml", v1.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        var v2 = AggregatedVersion.aggregateVersion(versies, 2).orElseThrow();
        assertEquals(2, v2.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.INTREKKING, v2.getVersie().getOorzaak());
        assertEquals("2023-01-01", v2.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-03", v2.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-03T12:00Z", v2.getVersie().getMutatiedatumtijd().toString());
        assertEquals(2, v2.getFile("meta").orElseThrow().getLeft());
        assertEquals("2.xml", v2.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        var v3 = AggregatedVersion.aggregateVersion(versies, 3).orElseThrow();
        assertEquals(3, v3.getVersie().getNummer());
        assertEquals(Versie.OorzaakEnum.WIJZIGING, v3.getVersie().getOorzaak());
        assertEquals("2023-01-01", v3.getVersie().getOpenbaarmakingsdatum().toString());
        assertEquals("2023-01-04", v3.getVersie().getWijzigingsdatum().toString());
        assertEquals("2023-01-04T12:00Z", v3.getVersie().getMutatiedatumtijd().toString());
        assertEquals(3, v3.getFile("meta").orElseThrow().getLeft());
        assertEquals("3.xml", v3.getFile("meta").orElseThrow().getRight().getBestandsnaam());
        assertFalse(AggregatedVersion.aggregateVersion(versies, 4).isPresent());
    }
}
