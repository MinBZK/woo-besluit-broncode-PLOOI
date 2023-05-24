package nl.overheid.koop.plooi.model.data.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import nl.overheid.koop.plooi.model.data.Bestand;
import org.junit.jupiter.api.Test;

class PlooiFileUtilTest {

    @Test
    void populate() {
        doAssert(PlooiFileUtil.populate(new Bestand().bestandsnaam("file.pdf").label("pdf")), "file.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().bestandsnaam("some/file.pdf").label("pdf")), "some_file.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().bestandsnaam("meta!\\\"£$%^&*().pdf").label("pdf")), "meta___________.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().bestandsnaam("file").label("pdf")), "file.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().bestandsnaam("file.pdf")), "file.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().bestandsnaam("with\ttab.pdf")), "with_tab.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().bestandsnaam("file")), "file", "unknown");
        doAssert(PlooiFileUtil.populate(new Bestand()
                .bestandsnaam(
                        "concept-besluit-van-houdende-vaststelling-van-regels-ter-uitvoering-van-de-gezondheidswet-en-de-jeugdwet-over-de-openbaarmaking-van-informatie-over-naleving-en-uitvoering-van-regelgeving-besluit-openbaarmaking-toezicht-en-uitvoeringsgegevens-gezondheidswet-en-jeugdwet.pdf")
                .label("pdf")),
                "concept-besluit-van-houdende-vaststelling-van-regels-ter-uitvoering-van-de-gezondheidswet-en-de-jeugdwet-over-de-openbaarmaking--...-6c00cc8.pdf",
                "pdf");
        // First part of the name is the same, part after that produces different (hash) suffix
        doAssert(PlooiFileUtil.populate(new Bestand()
                .bestandsnaam(
                        "concept-besluit-van-houdende-vaststelling-van-regels-ter-uitvoering-van-de-gezondheidswet-en-de-jeugdwet-over-de-openbaarmaking-...-and-now-for-something-completely-different.pdf")
                .label("pdf")),
                "concept-besluit-van-houdende-vaststelling-van-regels-ter-uitvoering-van-de-gezondheidswet-en-de-jeugdwet-over-de-openbaarmaking--...-df30f09.pdf",
                "pdf");

        doAssert(PlooiFileUtil.populate(new Bestand().url("FILE.PDF")), "FILE.PDF", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().url("http://example.com/file.pdf")), "file.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().url("http://example.com/with%20space.pdf")), "with space.pdf", "pdf");
        doAssert(PlooiFileUtil.populate(new Bestand().url("http://example.com/dir/")), "dir", "unknown");

        doAssert(PlooiFileUtil.populate(new Bestand()), "UNKNOWN", "unknown");
        doAssert(PlooiFileUtil.populate(new Bestand().id("abcd")), "abcd", "unknown");
    }

    private void doAssert(Bestand plooiFile, String bestandsnaam, String label) {
        assertEquals(bestandsnaam, plooiFile.getBestandsnaam());
        assertEquals(label, plooiFile.getLabel());
    }
}
