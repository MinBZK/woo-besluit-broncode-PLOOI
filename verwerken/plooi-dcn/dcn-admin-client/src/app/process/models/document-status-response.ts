import {Document} from './document';
import {ProcessingError} from './processing-error';

export class DocumentStatusResponse {
    document : Document;
    processingError : ProcessingError;


    constructor(document: Document, processingError: ProcessingError) {
        this.document = document;
        this.processingError = processingError;
    }
}