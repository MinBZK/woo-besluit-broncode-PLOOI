select * from executions;

select * from documentevents;

select * from documentevents as e join diagnostics as d on e.id = d.documentevent_id;

select * from processingerrors;