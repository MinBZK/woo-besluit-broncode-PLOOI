import {ProcessingError} from "./processing-error";

export class DocumentEventStatusResponse {
  verwerking: DocumentEvent;
  exceptie: ProcessingError;
}