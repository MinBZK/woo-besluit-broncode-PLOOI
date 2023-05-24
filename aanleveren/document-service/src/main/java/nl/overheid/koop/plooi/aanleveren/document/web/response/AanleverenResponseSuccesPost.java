package nl.overheid.koop.plooi.aanleveren.document.web.response;

public record AanleverenResponseSuccesPost(
        String title,
        int status,
        String detail,
        String openbaarmakingsdatum,
        AanleverenResponseDocument document) {
}
