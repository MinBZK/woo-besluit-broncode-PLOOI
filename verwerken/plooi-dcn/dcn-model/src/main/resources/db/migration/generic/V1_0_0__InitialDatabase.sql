CREATE TABLE Document
(
	id serial primary key,
	time_updated timestamp not null,
	time_created timestamp not null,

	source_name varchar(256) not null,
	external_id varchar(2048) not null,
	internal_id varchar(256) not null,

	document_location varchar(256) not null,
	metadata_location varchar(256) not null
);

CREATE UNIQUE INDEX Document_internal_id ON Document(internal_id);
CREATE INDEX Document_External_id ON Document(external_id);
CREATE INDEX Document_source_name_external_id ON Document(source_name, external_id);
