CREATE OR REPLACE FUNCTION verwerking_insert_trigger_fnc()
    RETURNS trigger AS $$
BEGIN
    IF NEW.dcn_id IS NOT null THEN
        INSERT INTO verwerkingstatus (dcn_id, source_label, ext_id, time_created, time_updated, last_severity, last_stage, last_proces_id)
        VALUES (NEW.dcn_id, NEW.source_label, NEW.ext_id, NEW.time_created, NEW.time_created, NEW.severity, NEW.stage, NEW.proces_id)
        ON CONFLICT (dcn_id) DO
            UPDATE SET
                       time_updated = EXCLUDED.time_updated,
                       last_severity = EXCLUDED.last_severity,
                       last_stage = EXCLUDED.last_stage,
                       last_proces_id = EXCLUDED.last_proces_id;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER verwerking_insert_trigger
    AFTER INSERT
    ON verwerkingen
    FOR EACH ROW
    EXECUTE PROCEDURE verwerking_insert_trigger_fnc();