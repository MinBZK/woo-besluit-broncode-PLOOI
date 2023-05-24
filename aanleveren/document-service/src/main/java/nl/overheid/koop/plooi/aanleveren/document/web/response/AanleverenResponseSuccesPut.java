package nl.overheid.koop.plooi.aanleveren.document.web.response;

public record AanleverenResponseSuccesPut(
        String title,
        int status,
        String detail,
        String wijzigingsdatum,
        AanleverenResponseDocument document) {
}
