interface EntitledLink {
    href: string;
    type: string;
    title: string;
}

interface Link {
    href: string;
    type: string;
}

interface Links {
    self: EntitledLink;
    next: EntitledLink;
    first: EntitledLink;
    last: EntitledLink;
}

interface ResultMeta {
    metadata: Link;
    document: Document;
}

export interface Result {
    documentsoort: string,
    identifiers: string[],
    officieleTitel: string,
    openbaarmakingsdatum: string,
    wijzigingsdatum: string,
    _links: ResultMeta;
}

interface Embedded {
    resultaten: Result[];
}

export interface SearchResults {
    _links: Links;
    _embedded: Embedded;
    aantalResultaten: number;
    aantalPaginas: number;
}
