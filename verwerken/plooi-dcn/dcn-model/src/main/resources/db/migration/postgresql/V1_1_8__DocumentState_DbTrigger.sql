DROP TRIGGER IF EXISTS document_events_insert_trigger
ON documenteventsold;

CREATE TRIGGER document_events_insert_trigger
  AFTER INSERT
  ON DocumentEvents
  FOR EACH ROW
  EXECUTE PROCEDURE document_events_insert_trigger_fnc();
