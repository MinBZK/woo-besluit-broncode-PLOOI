import { Controllers, Identifier } from "../models";

export const ensureSlash = (value: string) => value.charAt(value.length - 1) === '/' ? value : `${value}/`;

export class StringSanitizer {
    private ensureNoSlash = (value: string) => {
        const firstChar = value.charAt(0);
        const lastChar = value.charAt(value.length - 1);

        if (firstChar === '/' && lastChar === '/')
            return value.substring(1, value.length - 2);


        if (firstChar === '/')
            return value.substring(1);


        if (lastChar === '/')
            return value.substring(0, value.length - 1);

        return value;
    }

    public sanitizeApiEndpoint = (apiEndpoint: string, controller: Controllers, id?: string, query?: string) => {
        const sanitizedApiEndpoint = this.ensureNoSlash(apiEndpoint);
        const sanitizedController = this.ensureNoSlash(controller);
        let url = "";

        if (id)
            url = `${sanitizedApiEndpoint}/${sanitizedController}/${id}`;
        else
            url = `${sanitizedApiEndpoint}/${sanitizedController}`;

        if (query)
            url += `?${query}`;

        return url;
    }

    public sanitizeWaardelijstItem = (item: Identifier): Identifier => ({
        ...item,
        label: item.label?.split(' (')[0]
    })

    public sanitizeTrefwoorden = (value: string): string[] => value.split(',').map(v => v.trim());

    public sanitizeLdId = (LdItem: any, context: any) => {
        const itemId = LdItem['@id'] ?? LdItem;

        const prefixKey = itemId.split(":")[0];
        const trailingId = itemId.split(":")[1];
        const prefixValue = context[prefixKey];

        return `${prefixValue}${trailingId}`;
    }

    public sanitizeLdPrefLabel = (item: any) => Array.isArray(item.prefLabel) ? item.prefLabel[0]['@value'] : item.prefLabel['@value'];


    public capitalizeFirstLetter = (s: string) => {
        if (!s)
            return ""

        return s.charAt(0).toUpperCase() + s.slice(1);
    }
}