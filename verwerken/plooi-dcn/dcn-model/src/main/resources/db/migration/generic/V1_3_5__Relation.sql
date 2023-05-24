ALTER TABLE Relations
ADD COLUMN title VARCHAR(1024);
ALTER TABLE Relations
ADD COLUMN origin VARCHAR(32);
ALTER TABLE Relations
ADD COLUMN timestamp TIMESTAMP WITH TIME ZONE;

DROP INDEX relations_from_id;
CREATE INDEX relations_from_id ON Relations (from_id, origin);
