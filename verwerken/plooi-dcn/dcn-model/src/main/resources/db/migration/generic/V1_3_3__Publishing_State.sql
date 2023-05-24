CREATE TABLE PublishingState
(
    dcn_id varchar(256) not null,
    indexed varchar(32) not null,
    time_created TIMESTAMP WITH TIME ZONE not null,
    time_updated TIMESTAMP WITH TIME ZONE not null
);

CREATE INDEX publishing_state_dcn_id ON PublishingState (dcn_id);
CREATE INDEX publishing_state_indexed ON PublishingState (indexed);