CREATE OR REPLACE FUNCTION document_events_insert_trigger_fnc()
	RETURNS trigger AS
$$

	BEGIN
        IF NEW."internal_id" IS NOT null THEN
			INSERT INTO DocumentState (internal_id, last_execution_id)
				 VALUES (NEW."internal_id", NEW."execution_id")
			ON CONFLICT (internal_id)
			DO
			   UPDATE SET last_execution_id = EXCLUDED.last_execution_id;
        END IF;
        RETURN NEW;
    END;

$$

LANGUAGE 'plpgsql';
