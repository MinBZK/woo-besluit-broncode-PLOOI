import {SolrSearchFilter} from "./solr-search-filter";

export class BulkActionRequest {
  triggerType: string;
  filter: SolrSearchFilter
  action : string;
  reason : string;

    constructor(triggerType: string, filter: SolrSearchFilter, action: string, reason: string) {
        this.triggerType = triggerType;
        this.filter = filter;
        this.action = action;
        this.reason = reason;
    }
}
