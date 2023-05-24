import { AuthApi, IAuthApi, MockAuthApi } from './auth';
import { DocumentApi, IDocumentApi, MockDocumentApi } from './document';
import { IMetadataApi, MetadataApi, MockMetadataApi } from './metadata';
import { ISearchApi, MockSearchApi, SearchApi } from './search';

export class ApiFactory {
    public static isTestSuite = false;
    public static createSearchApi = (): ISearchApi => !ApiFactory.isTestSuite ? new SearchApi() : new MockSearchApi();
    public static createDocumentApi = (): IDocumentApi => !ApiFactory.isTestSuite ? new DocumentApi() : new MockDocumentApi();
    public static createMetadataApi = (): IMetadataApi => !ApiFactory.isTestSuite ? new MetadataApi() : new MockMetadataApi();
    public static createAuthApi = (): IAuthApi => !ApiFactory.isTestSuite ? new AuthApi() : new MockAuthApi();
}