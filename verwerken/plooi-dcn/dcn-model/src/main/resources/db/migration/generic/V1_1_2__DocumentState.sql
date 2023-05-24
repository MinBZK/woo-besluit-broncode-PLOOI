CREATE TABLE DocumentState (
	internal_id varchar(64) primary key,
	last_execution_id varchar(36) not null REFERENCES Executions(id)
);

CREATE INDEX DocumentState_internal_id ON DocumentState(internal_id);