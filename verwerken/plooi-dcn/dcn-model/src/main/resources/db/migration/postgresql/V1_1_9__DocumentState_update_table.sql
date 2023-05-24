DELETE  FROM documentstate;

INSERT INTO documentstate
SELECT  internal_id, execution_id
FROM    documentevents
WHERE  dcn_seq IN (
    SELECT  max(dcn_seq)
    FROM    documentevents
    GROUP BY internal_id);
