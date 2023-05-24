ALTER TABLE documents ADD COLUMN dcn_id varchar(256) NULL;
CREATE INDEX Documents_Dcn_id ON Documents(dcn_id);
