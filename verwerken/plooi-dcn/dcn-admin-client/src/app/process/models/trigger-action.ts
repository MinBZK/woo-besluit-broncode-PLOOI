export class TriggerAction {
  trigger : Trigger
  action : Action;
  actionName: string;

    constructor(trigger: Trigger, action: Action, actionName: string) {
        this.trigger = trigger;
        this.action = action;
        this.actionName = actionName;
    }
}

export enum Trigger {
    DELETION= 'DELETION',
    REPROCESS = 'REPROCESS'
}

export enum Action {
    DELETION =  'dcn-delete-document',
    REPROCESS = 'dcn-process-document'
}

export enum ActionName {
    DELETION =  'trek document in',
    REPROCESS = 'verwerk opnieuw'
}


