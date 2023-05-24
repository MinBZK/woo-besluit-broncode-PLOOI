import { FileFormat } from "../models";
import { BaseClient } from "./baseClient";
import {Storage, StorageLocation} from '../utils/Storage';


export interface IDocumentApi {
    uploadDocument(locationHeader: string, format: FileFormat, fileBinaryString: ArrayBuffer): Promise<any>;
    updateDocument(locationHeader: string, format: FileFormat, fileBinaryString: ArrayBuffer): Promise<any>;
    depubliceerSession(id: string): Promise<{
        response: Response;
        json?: Document | undefined;
    }>;
}

export class MockDocumentApi implements IDocumentApi {
    uploadDocument(locationHeader: string, format: FileFormat, fileBinaryString: ArrayBuffer): Promise<any> {
        return Promise.resolve();
    }
    updateDocument(locationHeader: string, format: FileFormat, fileBinaryString: ArrayBuffer): Promise<any> {
        return Promise.resolve();
    }

    depubliceerSession(id: string): Promise<{ response: Response; json?: Document | undefined; }> {
        return Promise.resolve({ response: new Response() });
    }

}

export class DocumentApi extends BaseClient implements IDocumentApi {
    private storage:Storage=new Storage();
    constructor() {
        super(new Headers({
            "Content-type": 'application/json',
            "Authorization": "Bearer " + Storage.Get(StorageLocation.TOKEN)
        }), process.env.REACT_APP_DOCUMENT_API_ENDPOINT as string);
    }

    uploadDocument = (locationHeader: string, format: FileFormat, fileBinaryString: ArrayBuffer) => {
        return fetch(locationHeader, {
            credentials: 'include',
            headers: new Headers({
                'Content-Type': `application/${format.toLocaleLowerCase()}`,
                "Authorization": "Bearer " + Storage.Get(StorageLocation.TOKEN)
            }),
            body: fileBinaryString,
            method: 'POST'
        })
            .then(r => this.handleResponse('documenten', r));
    };

    updateDocument = (locationHeader: string, format: FileFormat, fileBinaryString: ArrayBuffer) => {
        return fetch(locationHeader, {
            credentials: 'include',
            headers: new Headers({
                'Content-Type': `application/${format.toLocaleLowerCase()}`,
                "Authorization": "Bearer " + Storage.Get(StorageLocation.TOKEN)
            }),
            body: fileBinaryString,
            method: 'PUT'
        })
            .then(r => this.handleResponse('documenten', r));
    };

    depubliceerSession = (id: string) => {
        return this.api<Document>('documenten', id, 'DELETE');
    }

}



