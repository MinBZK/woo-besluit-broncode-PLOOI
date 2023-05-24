import { WaardelijstItem } from "../../factories/waardelijst";

export interface Iterator<T> {
    current(): T;
    next(): T;
    key(): number;
    valid(): boolean;
    rewind(): void;
}

export interface Aggregator {
    getIterator(): Iterator<WaardelijstItem>;
}