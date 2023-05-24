export class ProcessingError {
    id : number;
    verwerkingId : string;
    timeCreated : Date;
    timeUpdated : Date;
    status : string;
    fromRoute : string;
    exceptionClass : string;
    exceptionMessage : string;
    exceptionResponse : string;
    exceptionStacktrace : string;
    statusCode : number;
    statusText : string;

    sourceName : string;
    externalId : string;
    internalId : string;
    metadataLocation : string;
    messageBody : string;
    diagnostics : string;
}
