DROP INDEX DocumentState_internal_id;
CREATE INDEX DocumentState_internal_id ON DocumentState(internal_id, last_execution_id);

DROP INDEX documentevents_internal_id_stage_dcn_seq;
CREATE UNIQUE INDEX documentevents_internal_id_seq ON DocumentEvents (internal_id, dcn_seq);
