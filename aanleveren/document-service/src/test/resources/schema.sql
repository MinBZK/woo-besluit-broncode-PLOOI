CREATE TABLE IF NOT EXISTS TABLE_METADATA (
    ID bigint PRIMARY KEY,
    PID varchar,
    UUID varchar,
    TIMESTAMP timestamp,
    METADATA text,
    PROCESS_IDENTIFIER varchar
);