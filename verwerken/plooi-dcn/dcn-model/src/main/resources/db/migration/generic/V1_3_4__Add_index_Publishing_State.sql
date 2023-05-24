DROP INDEX publishing_state_indexed;
CREATE INDEX publishing_state_indexed ON PublishingState (indexed,time_created);