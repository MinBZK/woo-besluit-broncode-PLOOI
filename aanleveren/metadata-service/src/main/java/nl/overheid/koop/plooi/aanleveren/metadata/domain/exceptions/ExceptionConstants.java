package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionConstants {

    private static final String RAADPLEEG_DOCUMENTATIE = " Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).";

    /* HttpStatus 400 (Identifier Fout) */
    public static final String TITLE_IDENTIFIER_NIET_GELDIG = "Validatiefout";

    /* HttpStatus 400 (Parse Fout)*/
    public static final String DETAIL_INVALID_JSON = "Het aangeleverde bestand kan niet als json ingelezen worden." +
        RAADPLEEG_DOCUMENTATIE;

    /* HttpStatus 403 */
    public static final String TITLE_AUTORISATIE_FOUT = "Autorisatiefout";

    /* HttpStatus 404 */
    public static final String TITLE_NIET_GEVONDEN_FOUT = "Niet-gevonden-fout";

    /* HttpStatus 409 */
    public static final String TITLE_CONFLICT_FOUT = "Conflictfout";

    /* HttpStatus 500 */
    public static final String MESSAGE_METADATA_NIET_VERWERKBAAR = "De opgevraagde Metadata kan niet verwerkt worden";
    public static final String MESSAGE_SERVICE_ZOEKEN_NIET_BESCHIKBAAR = "De service voor het opzoeken van de Metadata is niet beschikbaar";
    public static final String MESSAGE_SERVICE_VERVANGEN_NIET_BESCHIKBAAR = "De service voor het vervangen van de Metadata is niet beschikbaar";

    /* Logging Informatie Standaard */
    public static final String LOG_MESSAGE = "Validatie Metadata Afgerond";
    public static final String PROCESSIDENTIFIER = "ProcessIdentifier";
    public static final String MEASURINGPOINT = "measuringpoint";
    public static final String MEASURINGPOINT_ADDED = "MeasuringPoint marker added";

    /* Diverse Informatie */
    public static final String ERROR_MESSAGE_NOT_IN_ENUMERATION = "does not have a value in the enumeration";
}
