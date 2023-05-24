CREATE OR REPLACE FUNCTION document_events_insert_trigger_fnc()
	RETURNS trigger AS
$$

	BEGIN
		INSERT INTO DocumentState (internal_id, last_execution_id)
			 VALUES(NEW."internal_id",NEW."execution_id")
		ON CONFLICT (internal_id) 
		DO 
		   UPDATE SET last_execution_id = EXCLUDED.last_execution_id;
		RETURN NEW;
	END;

$$

LANGUAGE 'plpgsql';


CREATE TRIGGER document_events_insert_trigger
  AFTER INSERT
  ON DocumentEvents
  FOR EACH ROW
  EXECUTE PROCEDURE document_events_insert_trigger_fnc();