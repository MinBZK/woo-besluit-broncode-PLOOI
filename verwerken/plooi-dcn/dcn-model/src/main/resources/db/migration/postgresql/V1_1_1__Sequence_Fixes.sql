-- Updates the database sequence. This does not work on H2, so we skip this in junit tests
SELECT setval('diagnostics_id_seq', COALESCE(max(id), 1)) FROM diagnostics;
SELECT setval('documents_id_seq', COALESCE(max(id), 1)) FROM documents;
SELECT setval('processingerrors_id_seq', COALESCE(max(id), 1)) FROM processingerrors;
