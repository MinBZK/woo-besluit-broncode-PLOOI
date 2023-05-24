import { Metadata } from "../models";
import { BaseClient } from "./baseClient";
import {Storage, StorageLocation} from '../utils/Storage';

export interface IMetadataApi {
    createMetadataSession(meta: Metadata): Promise<{uploadUrl: string, pid: string}>;
    updateMetadataSession(meta: Metadata, id: string): Promise<Metadata>;
    getMetadataSession(id: string): Promise<Metadata>;
}

const stringToDate = ( value: string) => {
    var pattern = /(\d{2})\.(\d{2})\.(\d{4})/;
    return new Date(value.replace(pattern,'$3-$2-$1'));
  }

export const mockDocument = {
    document: {
        classificatiecollectie: {
            documentsoorten: [],
            themas: []
        },
        documenthandelingen: [{
            soortHandeling:{id:'https://identifier.overheid.nl/tooi/def/thes/kern/c_dfcee535', label:"ontvangst"},
            atTime: "2022-04-24T14:15:22Z", 
            wasAssociatedWith:{id:'https://identifier.overheid.nl/tooi/id/ministerie/mnre1034', label:'ministerie van Binnenlandse Zaken en Koninkrijksrelaties'}, 
        }],
        identifiers: [],
        language: { id: '' },
        publisher: { id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034", label:"ministerie van Binnenlandse Zaken en Koninkrijksrelaties" },
        titelcollectie: {
            officieleTitel: "titel"
        },
        geldigheid : {
            begindatum: stringToDate("2022-04-24T14:15:22Z"),
            einddatum: stringToDate("2022-04-25T14:15:22Z")
        },
        pid:"https://open.overheid.nl/overheid/openbaarmaken/api/v0/metadata/oep-4941355cc29f7a18d86b00576336208753577d1f"
    }
}

export class MockMetadataApi implements IMetadataApi {
    createMetadataSession(meta: Metadata): Promise<{uploadUrl: string, pid: string}> {
        return Promise.resolve({uploadUrl: "https://uploadmetadocument.com",  pid: "123"});
    }
    updateMetadataSession(meta: Metadata, id: string): Promise<Metadata> {
        return Promise.resolve(mockDocument);
    }
    getMetadataSession(id: string): Promise<Metadata> {
        return Promise.resolve(mockDocument);
    }

}

export class MetadataApi extends BaseClient implements IMetadataApi {

    constructor() {
        super(new Headers({
            "Content-type": 'application/json',
            "Authorization": "Bearer " + Storage.Get(StorageLocation.TOKEN)
        }),
            process.env.REACT_APP_METADATA_API_ENDPOINT as string
        );
    }

    //Location header will be returned for upload
    createMetadataSession = async (meta: Metadata): Promise<{uploadUrl: string, pid: string}> => {
        const { json } = await this.api<Metadata>('metadata', undefined, 'POST', meta);

        if (!json)
            throw new Error("error");

        const pidComonents = (json.document.pid as string).split('/');
        const pid = pidComonents[pidComonents.length - 1];

        return {uploadUrl: `${process.env.REACT_APP_DOCUMENT_API_ENDPOINT}/documenten/${pid}`, pid};
    }

    updateMetadataSession = async (meta: Metadata, id: string) => {
        const { json } = await this.api<Metadata>('metadata', id, 'PUT', meta);

        if (!json)
            throw new Error("Error");

        return json;
    }

    getMetadataSession = async (id: string) => {
        const { json } = await this.api<Metadata>('metadata', id, 'GET', undefined);

        if (!json)
            throw new Error("Error");

        return json;
    };

}

