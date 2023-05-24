CREATE TABLE IDENTITYGROUPS
(
    id varchar(256) not null,
    hash varchar(256) not null
);

CREATE TABLE RELATIONS
(
    from_id varchar(256) not null,
    relation_type varchar(32) not null,
    to_id varchar(256) not null
);
