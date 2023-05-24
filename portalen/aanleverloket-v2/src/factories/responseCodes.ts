import { Controllers } from "../models";

export class ResponseCodesFactory {
    public create(
        controller: Controllers,
        responseCode: number
    ): string {
        switch (controller) {
            case "metadata":
                return MetaDataResponseCodeFactory.create(responseCode);

            case "documenten":
                return DocumentenResponseCodeFactory.create(responseCode);

            default:
                throw new Error("Not implemented.");
        }
    }
}

class MetaDataResponseCodeFactory {
    static create(responseCode: number): string {
        switch (responseCode) {
            case 400:
                return 'De aangeleverde metadata voldoen niet aan het schema';
            case 401:
                return 'Deze handeling is alleen mogelijk voor geautoriseerden'
            case 403:
                return 'U bent niet geautoriseerd voor deze actie'
            case 404:
                return 'De opgevraagde resource bestaat niet';
            case 500:
                return "Interne serverfout";
            default:
                return "Er is een fout opgetreden";
        }
    }
}

class DocumentenResponseCodeFactory {
    static create(responseCode: number): string {
        switch (responseCode) {
            case 400:
                return 'Het bestand kon niet verwerkt worden';
            case 401:
                return 'Deze handeling is alleen mogelijk voor geautoriseerden'
            case 403:
                return 'U bent niet geautoriseerd voor deze actie';
            case 404:
                return 'De opgevraagde resource bestaat niet';
            case 413:
                return 'Het bestand is te groot om verwerkt te kunnen worden'
            case 415:
                return 'Formaat niet ondersteund';
            case 500:
                return "Interne serverfout";
            default:
                return "Er is een fout opgetreden";
        }
    }
}