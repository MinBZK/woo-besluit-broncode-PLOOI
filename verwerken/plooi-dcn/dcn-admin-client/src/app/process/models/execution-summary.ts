import {Proces} from "./proces";
import {ProcesVerwerkingsSeverityStatus} from "./procesVerwerkingsSeverityStatus";

export class ExecutionSummary {
    proces : Proces;
    procesVerwerkingsSeverityStatus : ProcesVerwerkingsSeverityStatus;
    processingErrorCount : number;
}