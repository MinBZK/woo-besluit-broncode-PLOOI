import { Aggregator, Iterator } from "../ui/interfaces/Iterator";
import { WaardelijstItem } from "./waardelijst";


class WaardelijstCollectionIterator implements Iterator<WaardelijstItem> {
    private collection: WaardelijstCollection;

    private position: number = 0;

    private reverse: boolean = false;

    constructor(collection: WaardelijstCollection, reverse: boolean = false) {
        this.collection = collection;
        this.reverse = reverse;

        if (reverse) {
            this.position = collection.getCount() - 1;
        }
    }

    public rewind() {
        this.position = this.reverse ?
            this.collection.getCount() - 1 :
            0;
    }

    public current(): WaardelijstItem {
        return this.collection.getItems()[this.position];
    }

    public key(): number {
        return this.position;
    }

    public next(): WaardelijstItem {
        const item = this.collection.getItems()[this.position];
        this.position += this.reverse ? -1 : 1;
        return item;
    }

    public valid(): boolean {
        if (this.reverse) {
            return this.position >= 0;
        }

        return this.position < this.collection.getCount();
    }
}

export class WaardelijstCollection implements Aggregator {
    private items: WaardelijstItem[] = [];

    public getItems(): WaardelijstItem[] {
        return this.items;
    }

    public getCount(): number {
        return this.items.length;
    }

    public addItem(item: WaardelijstItem): void {
        this.items.push(item);
    }

    public addItems(items: WaardelijstItem[]): void {
        this.items = items;
    }

    public getIterator(): Iterator<WaardelijstItem> {
        return new WaardelijstCollectionIterator(this);
    }

    public getReverseIterator(): Iterator<WaardelijstItem> {
        return new WaardelijstCollectionIterator(this, true);
    }
}