ALTER TABLE document ADD COLUMN status varchar(100) NULL;
UPDATE Document SET status='REGISTERED';
CREATE INDEX Document_status ON Document(status);
ALTER TABLE document ALTER COLUMN status SET NOT NULL;

