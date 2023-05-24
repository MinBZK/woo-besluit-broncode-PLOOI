CREATE TABLE RELATIES
(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    from_id varchar(256) not null,
    relation_type varchar(32) not null,
    to_id varchar(256) not null,
    title VARCHAR(1024),
    origin VARCHAR(32),
    timestamp TIMESTAMP WITH TIME ZONE
);

CREATE INDEX relaties_from_id ON RELATIES (from_id, origin);
CREATE INDEX relaties_to_id ON RELATIES (to_id, relation_type);
