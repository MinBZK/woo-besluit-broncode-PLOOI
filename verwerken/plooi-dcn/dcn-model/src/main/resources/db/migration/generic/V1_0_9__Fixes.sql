-- Aligns types in the database with the Entities (int vs bigint pks)

-- Diagnostic -> Diagnostics PK serial to bigserial update
CREATE TABLE Diagnostics
(
	id bigserial primary key,
	processing_error_id bigint not null,

    code varchar(256) null,
	source_id varchar(256) null,
	source_label varchar(256) null,
	target_element_name varchar(256) null,
	message text null
);

CREATE INDEX Diagnostics_processing_error_id ON Diagnostics(processing_error_id);
CREATE INDEX Diagnostics_code ON Diagnostics(code);
CREATE INDEX Diagnostics_processing_source_id ON Diagnostics(source_id);
CREATE INDEX Diagnostics_processing_source_label ON Diagnostics(source_label);
CREATE INDEX Diagnostics_target_element_name  ON Diagnostics(target_element_name);

INSERT INTO
	Diagnostics (id, processing_error_id, code, source_id, source_label, target_element_name, message)
		  SELECT id, processing_error_id, code, source_id, source_label, target_element_name, message FROM Diagnostic;


-- Document -> Documents PK serial to bigserial update
CREATE TABLE Documents
(
	id bigserial primary key,
	time_updated timestamp not null,
	time_created timestamp not null,
	status varchar(100) not null,

	source_name varchar(256) not null,
	external_id varchar(2048) not null,
	internal_id varchar(256) not null,

	document_location varchar(256) null,
	metadata_location varchar(256) null,
	mapping_status varchar(100) not null
);

CREATE UNIQUE INDEX Documents_internal_id ON Documents(internal_id);
CREATE INDEX Documents_External_id ON Documents(external_id);
CREATE INDEX Documents_source_name_external_id ON Documents(source_name, external_id);
CREATE INDEX Documents_status ON Documents(status);
CREATE INDEX Documents_mapping_status ON Documents(mapping_status);

INSERT INTO
	Documents (id, time_updated, time_created, status, mapping_status, source_name, external_id, internal_id, document_location, metadata_location)
        SELECT id, time_updated, time_created, status, mapping_status, source_name, external_id, internal_id, document_location, metadata_location FROM Document;


-- ProcessingError -> ProcessingErrors PK serial to bigserial update
CREATE TABLE ProcessingErrors
(
    id bigserial primary key,
    time_updated timestamp not null,
    time_created timestamp not null,

    status varchar(256) null,

    from_route varchar(256) null,
    exception_class varchar(256) null,
    exception_message text null,
    exception_response text null,
    exception_stacktrace text null,
    status_code int null,
    status_text varchar(256),

    source_name varchar(256) null,
    external_id varchar(2048) null,
    internal_id varchar(256) null,

    document_location varchar(256) null,
    metadata_location varchar(256) null,

    message_body text null,
    diagnostics text NULL
);

CREATE INDEX ProcessingErrors_from_route ON ProcessingErrors(from_route);
CREATE INDEX ProcessingErrors_exception ON ProcessingErrors(exception_class);
CREATE INDEX ProcessingErrors_status ON ProcessingErrors(status);
CREATE INDEX ProcessingErrors_internal_id ON ProcessingErrors(internal_id);
CREATE INDEX ProcessingErrors_External_id ON ProcessingErrors(external_id);
CREATE INDEX ProcessingErrors_source_name_external_id ON ProcessingErrors(source_name, external_id);

INSERT INTO
	ProcessingErrors (id, time_updated, time_created, status, from_route, exception_class, exception_message, exception_response, exception_stacktrace, status_code, status_text, source_name, external_id, internal_id, document_location, metadata_location, message_body)
        	   SELECT id, time_updated, time_created, status, from_route, exception_class, exception_message, exception_response, exception_stacktrace, status_code, status_text, source_name, external_id, internal_id, document_location, metadata_location, message_body FROM ProcessingError;


-- Drop migrated tables
--DROP TABLE Diagnostic;
--DROP TABLE ProcessingError;
--DROP TABLE Document;

