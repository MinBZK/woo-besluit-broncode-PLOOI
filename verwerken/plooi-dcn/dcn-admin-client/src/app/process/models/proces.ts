import {ProcesCounts} from "./proces-counts";

export class Proces {
    timeCreated : Date;
    id : string;
    sourceLabel : string;
    triggerType : string;
    trigger: string;
    procesCounts: ProcesCounts;
    excepties: any[];
}