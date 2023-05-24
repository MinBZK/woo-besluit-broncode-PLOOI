import { Classificatiecollectie, Metadata, Identifier, Titelcollectie } from "../models";
import { Documenthandelingen, Documentrelaties, ExtraMetadata, Geldigheid } from "../models/metadata";
// import { FileTypes } from "../models/waardelijsten";

export class MetadataFactory {
    public create = (

        publisher: Identifier,
        identifiers: string[],
        language: Identifier,
        titelcollectie: Titelcollectie,
        classificatiecollectie: Classificatiecollectie,
        documenthandelingen: Documenthandelingen[],

        // openbaarmakingsdatum?: Date,
        // wijzigingsdatum?: Date,
        creatiedatum?: string,
        verantwoordelijke?: Identifier,
        // opsteller?: Identifier,
        naamOpsteller?: string,
        omschrijvingen?: string[],
        format?: Identifier,
        onderwerpen?: string[],
        aggregatiekenmerk?: string,
        // redenVerijderingVervanging?: string, 
        documentrelaties?: Documentrelaties,
        geldigheid?: Geldigheid,
        // weblocatie?: string,
        // extraMetadata?: ExtraMetadata[],
        pid?: string,

    ): Metadata => {
        // const d = openbaarmakingsdatum ? openbaarmakingsdatum : new Date(Date.now());
        // const d = openbaarmakingsdatum ? openbaarmakingsdatum : new Date(Date.now()).toJSON().split('T')[0];
        
        return {
            document: {
                publisher,
                identifiers,
                language,
                titelcollectie,
                classificatiecollectie,
                documenthandelingen,
        
                // openbaarmakingsdatum: d,
                // wijzigingsdatum: new Date(Date.now()),
                creatiedatum,
                verantwoordelijke,
                // opsteller,
                naamOpsteller,
                omschrijvingen,
                format,
                onderwerpen,
                aggregatiekenmerk,
                // redenVerijderingVervanging, 
                geldigheid,
                // weblocatie,
                // extraMetadata,
                pid,
            },
             documentrelaties
        }
    };
}