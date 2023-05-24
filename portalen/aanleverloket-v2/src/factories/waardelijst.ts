import { Identifier } from '../models';
import { StringSanitizer } from '../utils/StringSanitizer';

const themalijst = require("../assets/waardelijsten/themas.json");
const talenijst = require("../assets/waardelijsten/talen.json");
const ministerielijst = require("../assets/waardelijsten/rwc_ministeries_compleet_1.json");
const documentsoortenlijst = require("../assets/waardelijsten/documentsoorten.json");
const documenthandelingenlijst = require("../assets/waardelijsten/documenthandelingen.json");

export interface IdentifierWithChildren extends Identifier {
    children?: IdentifierWithChildren[];
}

export interface WaardelijstItem {
    item: Identifier;
    children?: WaardelijstItem[]
}

export class WaardelijstFactory {
    private sanitizer: StringSanitizer;

    constructor() {
        this.sanitizer = new StringSanitizer();
    }

    private mapWaardelijstItem = (item: any, ctx: any): IdentifierWithChildren => ({
        id: this.sanitizer.sanitizeLdId(item, ctx),
        label: this.sanitizer.sanitizeLdPrefLabel(item),
    });

    private traverseTree = (data: any[], ctx: any): IdentifierWithChildren[] => {
        const items = [...data];
        let tree = items.map(item => this.mapWaardelijstItem(item, ctx));

        items.forEach(originalItem => {
            const mappedItem = this.mapWaardelijstItem(originalItem, ctx);

            if (!originalItem.broader)
                return;

            const parent = tree.find(i => i.id === this.sanitizer.sanitizeLdId(originalItem['broader'], ctx));

            if (!parent)
                return;

            if (!parent.children)
                parent.children = [];

            parent.children.push(mappedItem);
            tree = tree.filter(i => i.id !== mappedItem.id);
        });

        return tree;

    };

    private traverseTreeSkos = (waardelijst: any[], labelIdentifier: string): IdentifierWithChildren[] => {
        const allParentItems = waardelijst.filter((list: { hasOwnProperty: (arg0: string) => any; }) => list.hasOwnProperty("http://www.w3.org/2004/02/skos/core#member"));

        let tree: IdentifierWithChildren[] = waardelijst.map((m: {
            [x: string]: {
                [x: string]: string;
            }[];
        }) => {
            if (!m.hasOwnProperty("@id"))
                return undefined;

            const item: any = { id: m["@id"] }
            if (m.hasOwnProperty(labelIdentifier))
                item.label = m[labelIdentifier][0]['@value'] ?? undefined;

            return item;

        }).filter((item) => item !== undefined);

        if (allParentItems.length === 0)
            return tree;

        const markedForDelete: string[] = [];

        waardelijst.forEach(item => {
            const itemId = item['@id'];

            const parent = waardelijst.find((i) => {
                const members = i["http://www.w3.org/2004/02/skos/core#member"];
                return members && members.some((r: any) => r["@id"] === item['@id']);
            });

            //Zelf parent
            if (!parent)
                return;

            const parentId = parent['@id'];
            const parentInTree = tree.find(i => i.id === parentId);
            const childInTree = tree.find(i => i.id === itemId);

            if (!parentInTree || !childInTree)
                return;

            if (!parentInTree.children)
                parentInTree.children = [];

            parentInTree.children.push(childInTree);
            markedForDelete.push(childInTree.id);
        });

        const newTree = tree.filter(t => markedForDelete.every(x => x !== t.id));
        return newTree;
    }

    private graphContextlijst = (jsonLijst: any): IdentifierWithChildren[] => {
        const data = jsonLijst['@graph'];
        const context = jsonLijst['@context'];
        const normalizedArray = this.traverseTree(data, context);

        return normalizedArray;
    }

    private skosLijst = (jsonLijst: any[]): IdentifierWithChildren[] => {
        const prefLabel = "http://www.w3.org/2004/02/skos/core#prefLabel";
        const rdfLabel = "http://www.w3.org/2000/01/rdf-schema#label";

        if (jsonLijst.length === 0)
            return [];

        let labelIdentifier = "";

        if (jsonLijst[0].hasOwnProperty(prefLabel))
            labelIdentifier = prefLabel;
        else if (jsonLijst[0].hasOwnProperty(rdfLabel))
            labelIdentifier = rdfLabel;

        const normalizedArray = this.traverseTreeSkos(jsonLijst, labelIdentifier);

        return normalizedArray;
    }

    public createThemaLijst = () => this.graphContextlijst(themalijst);
    public createTaalLijst = () => this.skosLijst(talenijst);
    public createMinisterieLijst = () => this.skosLijst(ministerielijst);
    public createDocumentsoortenLijst = () => this.skosLijst(documentsoortenlijst);
    public createDocumenthandelingenLijst = () => this.skosLijst(documenthandelingenlijst);

}