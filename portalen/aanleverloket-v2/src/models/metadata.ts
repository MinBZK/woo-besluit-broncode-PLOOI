import { Identifier } from ".";

//SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS

export interface Titelcollectie {
    officieleTitel: string;
    verkorteTitels?: string[];
    alternatieveTitels?: string[];
}

export interface Classificatiecollectie {
    documentsoorten: Identifier[];
    themas: Identifier[];
}

export interface Documenthandelingen {
    soortHandeling: Identifier;
    atTime: string;                       //date-time
    wasAssociatedWith: Identifier;
}

export interface Velden {
    key: string;
    values: string[];
}

export interface ExtraMetadata {
    namespace?: string;
    velden: Velden[];
}

export interface Documentrelaties{      //hoofddoc heeft geen relatie nog
    relation: string;   //pid hoofddoc
    role: string;       //waardelijst id (bijlage bij)
   // titel: string;      //titel hoofddoc
}

export interface Geldigheid{
    begindatum?: Date;      //date-time
    einddatum?: Date;       //date-time
}

interface Document {
    openbaarmakingsdatum?: Date;
    wijzigingsdatum?: Date;
    creatiedatum?: string;
    publisher: Identifier;
    verantwoordelijke?: Identifier;
    opsteller?: Identifier;
    naamOpsteller?: string;
    identifiers: string[];
    language: Identifier;
    titelcollectie: Titelcollectie;
    omschrijvingen?: string[];
    classificatiecollectie: Classificatiecollectie;
    format?: Identifier;
    onderwerpen?: string[];
    aggregatiekenmerk?: string;
    documenthandelingen: Documenthandelingen[];
    // redenVerijderingVervanging?: string;                //update of verwijdering 
    geldigheid?: Geldigheid;
    weblocatie?: string;
    extraMetadata?: ExtraMetadata[];
    label?: string;
    pid?: string;
}

export interface Metadata {
    document: Document;
    documentrelaties?: Documentrelaties;
}