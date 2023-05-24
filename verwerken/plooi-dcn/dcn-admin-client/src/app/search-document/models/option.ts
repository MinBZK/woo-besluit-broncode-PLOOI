export class Option {
    name : string;
    count: number;
    selected : boolean;

    constructor(name: string, count: number) {
        this.name = name;
        this.count = count;
        this.selected = false;
    }
}