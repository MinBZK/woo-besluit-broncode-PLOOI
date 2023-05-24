ALTER TABLE DocumentEvents RENAME TO DocumentEventsOld;

CREATE TABLE DocumentEvents
(
    dcn_seq bigserial primary key,
    id varchar(36) UNIQUE,
    source_name varchar(16) not null,
    external_id varchar(1024),
    internal_id varchar(64) not null,
    stage varchar(32) not null,
    severity varchar(32) not null,
    time_created timestamp not null,
    execution_id varchar(36) not null REFERENCES Executions(id)
);

drop INDEX DocumentEvents_internal_id;
CREATE INDEX DocumentEvents_internal_id ON DocumentEvents(internal_id, execution_id, stage);

drop INDEX DocumentEvents_external_id;
CREATE INDEX DocumentEvents_external_id ON DocumentEvents(external_id, source_name);

drop INDEX DocumentEvents_execution;
CREATE INDEX DocumentEvents_execution ON DocumentEvents(execution_id, time_created);

drop INDEX DocumentEvents_time;
CREATE INDEX DocumentEvents_time ON DocumentEvents(time_created);

drop INDEX documentevents_execution_id_and_source_name;
CREATE INDEX documentevents_execution_id_and_source_name ON documentevents (execution_id, source_name);

CREATE INDEX DocumentEvents_id ON DocumentEvents(id);

INSERT INTO
    DocumentEvents (id, source_name, external_id, internal_id, stage, severity, time_created, execution_id)
SELECT id, source_name, external_id, internal_id, stage, severity, time_created, execution_id FROM DocumentEventsOld order by time_created;


ALTER TABLE processingerrors DROP CONSTRAINT IF EXISTS processingerrors_documentevent_id_fkey;

ALTER TABLE Processingerrors
    ADD CONSTRAINT processingerrors_documentevent_id_fkey FOREIGN KEY (documentevent_id) REFERENCES DocumentEvents (id);

ALTER TABLE diagnostics DROP CONSTRAINT IF EXISTS diagnostics_documentevent_id_fkey;

ALTER TABLE diagnostics
    ADD CONSTRAINT diagnostics_documentevent_id_fkey FOREIGN KEY (documentevent_id) REFERENCES DocumentEvents (id);

