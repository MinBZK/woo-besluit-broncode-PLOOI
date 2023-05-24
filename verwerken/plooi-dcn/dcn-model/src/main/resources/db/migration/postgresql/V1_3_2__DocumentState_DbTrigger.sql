CREATE OR REPLACE FUNCTION document_events_insert_trigger_fnc()
    RETURNS trigger AS $$
BEGIN
    IF NEW.internal_id IS NOT null THEN
        INSERT INTO DocumentState (internal_id, source_name, external_id, time_created, time_updated, last_severity, last_stage, last_execution_id)
            VALUES (NEW.internal_id, NEW.source_name, NEW.external_id, NEW.time_created, NEW.time_created, NEW.severity, NEW.stage, NEW.execution_id)
        ON CONFLICT (internal_id) DO
            UPDATE SET
                time_updated = EXCLUDED.time_updated,
                last_severity = EXCLUDED.last_severity,
                last_stage = EXCLUDED.last_stage,
                last_execution_id = EXCLUDED.last_execution_id;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';
