CREATE TABLE ProcessingError
(
    id serial primary key,
    time_updated timestamp not null,
    time_created timestamp not null,

    status varchar(256) null,

    from_route varchar(256) null,
    exception_class varchar(256) null,
    exception_message varchar(256) null,
    exception_response text null,
    exception_stacktrace text null,
    status_code int null,
    status_text varchar(256),

    source_name varchar(256) null,
    external_id varchar(2048) null,
    internal_id varchar(256) null,

    document_location varchar(256) null,
    metadata_location varchar(256) null,

    message_body text null
);

CREATE INDEX ProcessingError_from_route ON ProcessingError(from_route);
CREATE INDEX ProcessingError_exception ON ProcessingError(exception_class);
CREATE INDEX ProcessingError_status ON ProcessingError(status);
CREATE INDEX ProcessingError_internal_id ON ProcessingError(internal_id);
CREATE INDEX ProcessingError_External_id ON ProcessingError(external_id);
CREATE INDEX ProcessingError_source_name_external_id ON ProcessingError(source_name, external_id);
