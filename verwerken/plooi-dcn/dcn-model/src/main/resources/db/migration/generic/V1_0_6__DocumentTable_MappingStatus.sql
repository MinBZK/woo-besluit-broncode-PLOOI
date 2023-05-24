ALTER TABLE document ADD COLUMN mapping_status varchar(100) NULL;
UPDATE Document SET mapping_status='NEW';
CREATE INDEX Document_mapping_status ON Document(mapping_status);
ALTER TABLE document ALTER COLUMN mapping_status SET NOT NULL;
