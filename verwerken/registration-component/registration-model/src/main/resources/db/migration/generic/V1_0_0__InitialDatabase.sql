CREATE TABLE Processen
(
    id varchar(36) primary key,
    source_label varchar(16),
    trigger_type varchar(32) not null,
    trigger varchar(512) not null,
    time_created TIMESTAMP WITH TIME ZONE not null
);

CREATE INDEX Processen_source ON Processen(source_label, trigger_type, time_created);
CREATE INDEX Processen_time ON Processen(time_created);


CREATE TABLE Verwerkingen
(
    dcn_seq bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id varchar(36) UNIQUE,
    source_label varchar(16),
    ext_id varchar(1024),
    dcn_id varchar(64),
    stage varchar(32) not null,
    severity varchar(32) not null,
    time_created TIMESTAMP WITH TIME ZONE not null,
    proces_id varchar(36) not null REFERENCES Processen(id)
);

CREATE INDEX Verwerkingen_dcn_id ON Verwerkingen(dcn_id, proces_id, stage);
CREATE INDEX Verwerkingen_external_id ON Verwerkingen(ext_id, source_label);
CREATE INDEX Verwerkingen_proces ON Verwerkingen(proces_id, time_created);
CREATE INDEX Verwerkingen_time ON Verwerkingen(time_created);
CREATE INDEX verwerkingen_proces_id_and_source_name ON Verwerkingen(proces_id, source_label);
CREATE INDEX Verwerkingen_id ON Verwerkingen(id);
CREATE UNIQUE INDEX Verwerkingen_dcn_id_seq ON Verwerkingen (dcn_id, dcn_seq);
CREATE INDEX Verwerkingen_source ON Verwerkingen (source_label);


CREATE TABLE Excepties
(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    time_created TIMESTAMP WITH TIME ZONE not null ,
    from_route varchar(256) null,
    exception_class varchar(256) null,
    exception_message text null,
    exception_stacktrace text null,
    status_code int null,
    status_text varchar(256),
    message_body text null,
    verwerking_id varchar(36) null REFERENCES Verwerkingen(id),
    proces_id varchar(36) null REFERENCES Processen(id)
);

CREATE INDEX Excepties_from_route ON Excepties(from_route);
CREATE INDEX Excepties_exception ON Excepties(exception_class);
CREATE INDEX Excepties_verwerking_id ON Excepties(verwerking_id);
CREATE INDEX Excepties_proces_id ON Excepties(proces_id);


CREATE TABLE Diagnoses
(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    verwerking_id varchar(36) REFERENCES Verwerkingen(id),
    code varchar(256) null,
    source_id varchar(256) null,
    source_label varchar(256) null,
    target_element_name varchar(256) null,
    severity varchar(32) not null,
    message text null
);

CREATE INDEX Diagnoses_code ON Diagnoses(code);
CREATE INDEX Diagnoses_processing_source_id ON Diagnoses(source_id);
CREATE INDEX Diagnoses_processing_source_label ON Diagnoses(source_label);
CREATE INDEX Diagnoses_target_element_name  ON Diagnoses(target_element_name);
CREATE INDEX Diagnoses_severity_proces_id ON Diagnoses (severity, verwerking_id);
CREATE INDEX Diagnoses_verwerking_id ON Diagnoses (verwerking_id);


CREATE TABLE Verwerkingstatus
(
    dcn_id varchar(64) primary key,
    last_proces_id varchar(36) REFERENCES processen(id),
    source_label varchar(16) null,
    ext_id varchar(1024),
    time_created TIMESTAMP WITH TIME ZONE,
    time_updated TIMESTAMP WITH TIME ZONE,
    last_severity varchar(36),
    last_stage varchar(36)
);

CREATE INDEX Verwerkingstatus_dcn_id ON Verwerkingstatus(dcn_id, last_proces_id);