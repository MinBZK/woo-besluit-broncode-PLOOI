package nl.overheid.koop.plooi.aanleveren.httpheader;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpHeadersBuilderTest {

    @Test
    void build() {
        HttpHeaders build = HttpHeadersBuilder.builder()
                .apiVersion()
                .contentLanguage()
                .xFrameOptions()
                .xContentTypeOptions()
                .contentSecurityPolicy()
                .referrerPolicy()
                .build();

        assertEquals(List.of("1.0.0"), build.get("Api-Version"));
        assertEquals(Locale.forLanguageTag("nl"), build.getContentLanguage());
        assertEquals(List.of("SAMEORIGIN"), build.get("X-Frame-Options"));
        assertEquals(List.of("nosniff"), build.get("X-Content-Type-Options"));
        assertEquals(List.of("default-src self; frame-ancestors self;"), build.get("Content-Security-Policy"));
        assertEquals(List.of("same-origin"), build.get("Referrer-Policy"));
    }
}
