import { SearchResults } from "../models/search-result";
import { BaseClient } from "./baseClient";
import {Storage, StorageLocation} from '../utils/Storage';

export interface ISearchApi {
    searchOrgDocuments(orgId: string, page?: number): Promise<SearchResults>;
}

export class SearchApi extends BaseClient implements ISearchApi {
    constructor() {
        super(new Headers({
            "Content-type": 'application/json',
            "Aanlever-Token": "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
            "Cache-Control": "no-cache, no-store, must-revalidate",
            "Pragma": "no-cache",
            "Expires": "0",
            "Authorization": "Bearer " + Storage.Get(StorageLocation.TOKEN)
        }),
            process.env.REACT_APP_ZOEKEN_API_ENDPOINT as string
        );
    }

    searchOrgDocuments = async (orgId: string, page?: number) => {
        const params = `publisher=${orgId}&pagina=${page ?? 0}`;
        const { json } = await this.api<SearchResults>('_zoek', undefined, 'GET', undefined, params);

        return (json as SearchResults);
    };
}

export class MockSearchApi implements ISearchApi {
    searchOrgDocuments = async (orgId: string, page?: number) => {
        if (page !== undefined && page < 0)
            throw new Error('Ongeldige pagina');

        const json = {
            "_links": {
                "self": {
                    "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/_zoek?verantwoordelijke=https://identifier.overheid.nl/tooi/id/ministerie/mnre1018&pagina=1",
                    "type": "application/hal+json",
                    "title": "Resultatenlijst voor ministerie van Defensie, pagina 1"
                },
                "next": {
                    "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/_zoek?verantwoordelijke=https://identifier.overheid.nl/tooi/id/ministerie/mnre1018&pagina=2",
                    "type": "application/hal+json",
                    "title": "Resultatenlijst voor ministerie van Defensie, pagina 2"
                },
                "first": {
                    "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/_zoek?verantwoordelijke=https://identifier.overheid.nl/tooi/id/ministerie/mnre1018&pagina=1",
                    "type": "application/hal+json",
                    "title": "Resultatenlijst voor ministerie van Defensie, pagina 1"
                },
                "last": {
                    "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/_zoek?verantwoordelijke=https://identifier.overheid.nl/tooi/id/ministerie/mnre1018&pagina=1181",
                    "type": "application/hal+json",
                    "title": "Resultatenlijst voor ministerie van Defensie, pagina 1181"
                }
            },
            "_embedded": {
                "resultaten": [
                    {
                        "documentsoort": "Kamerstuk",
                        "identifiers": ["doc-123e4567-e89b-12d3-a456-426614174777"],
                        "officieleTitel": "Besluit Defensie mandaat attachés 2020, Ministerie van Defensie",
                        "openbaarmakingsdatum": "2022-12-01T00:00:00.000+00:00",
                        "wijzigingsdatum": "2022-12-01T00:00:00.000+00:00",
                        "_links": {
                            "document": {
                                // "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/documenten/oep-4941355cc29f7a18d86b00576336208753577d1f",
                                // "type": "application/pdf" 
                            },
                            "metadata": {
                                "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/metadata/oep-4941355cc29f7a18d86b00576336208753577d1f",
                                "type": "application/json"
                            }
                        }
                    },
                    {
                        "documentsoort": "Kamerstuk",
                        "identifiers": ["doc-123e4567-e89b-12d3-a456-426614174777"],
                        "officieleTitel": "Besluit Defensie volmacht en machtiging lokale werknemers, Ministerie van Defensie",
                        "openbaarmakingsdatum": "2022-12-01T00:00:00.000+00:00",
                        "wijzigingsdatum": "2022-12-01T00:00:00.000+00:00",
                        "_links": {
                            "document": {
                                // "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/documenten/oep-4941355cc29f7a18d86b00576336208753577d1f",
                                // "type": "application/pdf" 
                            },
                            "metadata": {
                                "href": "https://open.overheid.nl/overheid/openbaarmaken/api/v0/metadata/oep-ce4330baf66351b74785a5c759b9fe8d5cf90037",
                                "type": "application/json"
                            }
                        }
                    },

                ]
            },
            "aantalResultaten": 2362,
            "aantalPaginas": 1181
        } as SearchResults;

        return json;
    };
}

