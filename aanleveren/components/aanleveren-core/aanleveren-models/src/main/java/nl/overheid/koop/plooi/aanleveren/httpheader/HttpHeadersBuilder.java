package nl.overheid.koop.plooi.aanleveren.httpheader;

import org.springframework.http.HttpHeaders;

import java.util.Locale;

public class HttpHeadersBuilder {
    private final HttpHeaders headers = new HttpHeaders();

    public static HttpHeadersBuilder builder() {
        return new HttpHeadersBuilder();
    }

    public HttpHeadersBuilder apiVersion() {
        headers.add("Api-Version", "1.0.0");
        return this;
    }

    public HttpHeadersBuilder contentLanguage() {
        headers.setContentLanguage(Locale.forLanguageTag("nl"));
        return this;
    }

    public HttpHeadersBuilder xFrameOptions() {
        headers.add("X-Frame-Options", "SAMEORIGIN");
        return this;
    }

    public HttpHeadersBuilder xContentTypeOptions() {
        headers.add("X-Content-Type-Options", "nosniff");
        return this;
    }

    public HttpHeadersBuilder contentSecurityPolicy() {
        headers.add("Content-Security-Policy", "default-src self; frame-ancestors self;");
        return this;
    }

    public HttpHeadersBuilder referrerPolicy() {
        headers.add("Referrer-Policy", "same-origin");
        return this;
    }

    public HttpHeadersBuilder cacheControl() {
        headers.add("Cache-Control", "public, max-age=86400");
        return this;
    }

    public HttpHeaders build() {
        return headers;
    }
}
