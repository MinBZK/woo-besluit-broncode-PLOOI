#!/bin/bash

log() {
	echo `date --rfc-3339=seconds` $1
}

echo "Backup job starting"
request_id=$(date +"%Y%m%d%H%M%S")
job_status=$(curl -s -o /dev/null -w "%{http_code}" "http://${SOLR_HOST}/solr/admin/collections?action=BACKUP&collection=$SOLR_COLLECTION&name=$SOLR_COLLECTION&location=/bitnami/solr-backups&maxNumBackupPoints=12&async=${request_id}");

log "Backup started with the Request ID: ${request_id}"

    if [ $job_status -eq 200 ]; then
    log "Backup job succeed, HTTP Status Code: $job_status";
	exit 0;
else
    log "Backup job failed, HTTP Status Code: $job_status";
	exit 1;
fi
