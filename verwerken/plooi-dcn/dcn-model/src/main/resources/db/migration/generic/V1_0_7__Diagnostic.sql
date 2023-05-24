CREATE TABLE Diagnostic
(
	id serial primary key,
	processing_error_id bigint not null,

    code varchar(256) null,
	source_id varchar(256) null,
	source_label varchar(256) null,
	target_element_name varchar(256) null,
	message text null
);

CREATE INDEX Diagnostic_processing_error_id ON Diagnostic(processing_error_id);
CREATE INDEX Diagnostic_code ON Diagnostic(code);
CREATE INDEX Diagnostic_processing_source_id ON Diagnostic(source_id);
CREATE INDEX Diagnostic_processing_source_label ON Diagnostic(source_label);
CREATE INDEX Diagnostic_target_element_name  ON Diagnostic(target_element_name);

