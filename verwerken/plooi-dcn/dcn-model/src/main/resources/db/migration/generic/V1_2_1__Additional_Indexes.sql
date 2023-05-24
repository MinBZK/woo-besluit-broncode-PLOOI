-- Additional Indexes for performance increase in mapping errors-warnings query
CREATE INDEX diagnostics_severity_documentevent_id ON DIAGNOSTICS (severity, documentevent_id);

CREATE UNIQUE INDEX documentevents_internal_id_stage_dcn_seq ON DocumentEvents (internal_id, stage, dcn_seq);