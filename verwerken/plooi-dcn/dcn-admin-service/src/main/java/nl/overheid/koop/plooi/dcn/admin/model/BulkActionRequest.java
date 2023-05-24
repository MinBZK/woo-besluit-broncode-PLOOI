package nl.overheid.koop.plooi.dcn.admin.model;

import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;

public record BulkActionRequest(TriggerType triggerType, SolrSearchRequest filter, String reason, String action) {

    public String triggerTypeName() {
        return this.triggerType.toString();
    }
}
