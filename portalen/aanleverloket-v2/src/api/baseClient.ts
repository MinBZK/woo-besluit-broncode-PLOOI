import { ResponseCodesFactory } from "../factories";
import { Controllers } from "../models";
import { useAppSelector } from "../store/hooks";
import { selectAuth } from "../store/selectors";
import { StringSanitizer } from "../utils/StringSanitizer";

export abstract class BaseClient {
    protected readonly _apiEndpoint: string;
    private readonly _sanitizer: StringSanitizer;
    private readonly _headers: Headers;
    private readonly _responseFactory: ResponseCodesFactory;

    constructor(defaultRequestHeaders: Headers, apiEndpoint: string) {
        this._sanitizer = new StringSanitizer();
        this._responseFactory = new ResponseCodesFactory();

        this._apiEndpoint = apiEndpoint;
        this._headers = defaultRequestHeaders;
    }

    protected api = <T>(controller: Controllers, id?: string, method?: 'GET' | 'POST' | 'PUT' | 'DELETE', body?: any, queryParams?: string) => {
        const sanitizedUrl = this._sanitizer.sanitizeApiEndpoint(this._apiEndpoint, controller, id, queryParams);
        const json = body ? JSON.stringify(body) : undefined;

        return fetch(sanitizedUrl, {
            credentials: 'include',
            method: method ?? 'GET',
            body: json,
            headers: this._headers
        })
            .then(r => this.handleResponse<T>(controller, r));
    }

    protected handleResponse = async <T>(controller: Controllers, response: Response): Promise<{ response: Response, json?: T }> => {
        if (!response.ok) {
            throw Error(this._responseFactory.create(controller, response.status));
        }

        const contentType = response.headers.get("content-type");

        if (!contentType)
            return { response: response };

        const isJsonContent = (contentType.indexOf("application/json") !== -1 || contentType.indexOf("application/hal+json") !== -1);
        if (contentType && isJsonContent) {
            return {
                json: await response.json(),
                response: response
            };
        }

        return {
            response: response
        }
    }

}