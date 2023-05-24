import {Option} from "./option";

export class OptionField {
    name : string;
    options : Option [] = [];

    constructor(name: string, options: Option[]) {
        this.name = name;
        this.options = options;
    }
}