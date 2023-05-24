package nl.overheid.koop.plooi.model.data.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class DcnIdentifierUtilTest {

    @Test
    void generateDcnId_noExternalIds() {
        assertThrows(IllegalArgumentException.class, () -> DcnIdentifierUtil.generateDcnId("src"));
    }

    @Test
    void generateDcnId() {
        assertEquals("src-e6b23c55d439870c2edff231e011a9c10f400f4c",
                DcnIdentifierUtil.generateDcnId("src", "1234"));
        assertEquals("src-d74f420fa81a2c72960d07ca6711ea7ad7dfb308",
                DcnIdentifierUtil.generateDcnId("src", "1234", "56789"));
        assertEquals("ronl-archief-0add339dcc8da6f3fe92c469e042ba61c41e763a",
                DcnIdentifierUtil.generateDcnId("ronl-archief", "1234"));
    }

    @Test
    void generateDcnId_API() {
        assertEquals("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a",
                DcnIdentifierUtil.generateDcnId(DcnIdentifierUtil.PLOOI_API_SRC, "b36dda59-1b79-4893-882e-45032b0b728a"));
    }

    @Test
    void generateDcnId_API_illegal() {
        assertThrows(IllegalArgumentException.class,
                () -> DcnIdentifierUtil.generateDcnId(DcnIdentifierUtil.PLOOI_API_SRC, "illegal-format"));
    }

    @Test
    void toDcnId() {
        assertEquals("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a", DcnIdentifierUtil.toDcn("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a"));
        assertEquals("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a", DcnIdentifierUtil.toDcn("b36dda59-1b79-4893-882e-45032b0b728a"));
        assertEquals("ronl-archief-AAAAAA55d439870c2edff231e011a9c10f400f4c", DcnIdentifierUtil.toDcn("ronl-archief-AAAAAA55d439870c2edff231e011a9c10f400f4c"));
        assertEquals(null, DcnIdentifierUtil.toDcn(null));
    }

    @Test
    void extractSource() {
        assertEquals("src",
                DcnIdentifierUtil.extractSource("src-e6b23c55d439870c2edff231e011a9c10f400f4c"));
        assertEquals("ronl-archief",
                DcnIdentifierUtil.extractSource("ronl-archief-AAAAAA55d439870c2edff231e011a9c10f400f4c"));
    }

    @Test
    void extractSource_API() {
        assertEquals(DcnIdentifierUtil.PLOOI_API_SRC,
                DcnIdentifierUtil.extractSource("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a"));
    }

    @Test
    void extractHash() {
        assertEquals("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                DcnIdentifierUtil.extractHash("src-e6b23c55d439870c2edff231e011a9c10f400f4c"));
        assertEquals("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                DcnIdentifierUtil.extractHash("ronl-archief-0add339dcc8da6f3fe92c469e042ba61c41e763a"));
    }

    @Test
    void extractHash_API() {
        assertEquals("b36dda59-1b79-4893-882e-45032b0b728a",
                DcnIdentifierUtil.extractHash("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a"));
    }

    @Test
    void extractHash_empty() {
        assertNull(DcnIdentifierUtil.extractHash(null));
        assertNull(DcnIdentifierUtil.extractHash(""));
        assertNull(DcnIdentifierUtil.extractHash("  "));
    }

    @Test
    void extractHash_illegal() {
        assertThrows(IllegalArgumentException.class, () -> DcnIdentifierUtil.extractHash("somestring"));
        assertThrows(IllegalArgumentException.class, () -> DcnIdentifierUtil.extractHash("tooshort-25d902c24283ab8cfbac54dfa101ad"));
        assertThrows(IllegalArgumentException.class, () -> DcnIdentifierUtil.extractHash("nothex-zzd902c24283ab8cfbac54dfa101ad31"));
    }
}
