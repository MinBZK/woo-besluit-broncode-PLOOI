DROP TABLE RELATIONS;

CREATE TABLE RELATIONS
(
    id bigserial primary key,
    from_id varchar(256) not null,
    relation_type varchar(32) not null,
    to_id varchar(256) not null
);

CREATE INDEX relations_from_id ON relations (from_id, relation_type);
CREATE INDEX relations_to_id ON relations (to_id, relation_type);
