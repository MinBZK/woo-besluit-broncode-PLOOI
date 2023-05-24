CREATE TABLE Executions
(
	id varchar(36) primary key,
	source_name varchar(16) not null,
	trigger_type varchar(32) not null,
	trigger varchar(512) not null,
	time_created timestamp not null
);

CREATE INDEX Executions_source ON Executions(source_name, trigger_type, time_created);
CREATE INDEX Executions_time ON Executions(time_created);

CREATE TABLE DocumentEvents
(
	id varchar(36) primary key,
	source_name varchar(16) not null,
	external_id varchar(1024),
	internal_id varchar(64) not null,
	stage varchar(32) not null,
	severity varchar(32) not null,
	time_created timestamp not null,
	execution_id varchar(36) not null REFERENCES Executions(id)
);

CREATE INDEX DocumentEvents_internal_id ON DocumentEvents(internal_id, execution_id, stage);
CREATE INDEX DocumentEvents_external_id ON DocumentEvents(external_id, source_name);
CREATE INDEX DocumentEvents_execution ON DocumentEvents(execution_id, time_created);
CREATE INDEX DocumentEvents_time ON DocumentEvents(time_created);

ALTER TABLE Diagnostics
ADD COLUMN documentevent_id varchar(36) null REFERENCES DocumentEvents(id);
ALTER TABLE Diagnostics
ALTER COLUMN processing_error_id DROP NOT NULL;

ALTER TABLE Processingerrors
ADD COLUMN documentevent_id varchar(36) null REFERENCES DocumentEvents(id);
ALTER TABLE Processingerrors
ADD COLUMN execution_id varchar(36) null REFERENCES Executions(id);
