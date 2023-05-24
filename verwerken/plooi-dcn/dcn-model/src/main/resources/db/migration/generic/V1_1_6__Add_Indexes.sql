-- Add indexes in Processingerrors for performance
CREATE INDEX processingerrors_execution_id ON processingerrors (execution_id);
CREATE INDEX processingerrors_documentevent_id ON processingerrors (documentevent_id);

-- Add index in Diagnostics for performance
CREATE INDEX diagnostics_documentevent_id ON diagnostics (documentevent_id);

--Add index in Documentevents for performance
CREATE INDEX documentevents_execution_id_and_source_name ON documentevents (execution_id, source_name);