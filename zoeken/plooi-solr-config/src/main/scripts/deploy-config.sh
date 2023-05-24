#!/bin/bash

log() {
	echo `date --rfc-3339=seconds` $1
}
solr_admin() {
	log "Executing $1 for $SOLR_COLLECTION collection"
	curl -s "http://$SOLR_HOST/solr/admin/collections?action=$1&$2"
}

until $(curl -f -s -o /dev/null "http://$SOLR_HOST/solr/admin/collections?action=CLUSTERSTATUS"); do
    sleep 10 && log "Waiting for http://$SOLR_HOST/solr to start..."
done

log "Starting deployment of PLOOI configset"
solr zk upconfig -z $ZK_HOST -d ./plooi -n $SOLR_COLLECTION

plooi_status=$(curl -s -o /dev/null -w "%{http_code}" "http://$SOLR_HOST/solr/$SOLR_COLLECTION/admin/file")
if [ $plooi_status -eq 200 ]; then
	solr_admin RELOAD "name=$SOLR_COLLECTION"
elif [ $plooi_status -eq 404 ]; then
	solr_admin CREATE "name=$SOLR_COLLECTION&collection.configName=$SOLR_COLLECTION&numShards=1"
else
	log "Something wrong with $SOLR_COLLECTION collection: $plooi_status"
	exit 1
fi

if ! (solr_admin LISTALIASES "" | grep '"plooi":'); then
    solr_admin CREATEALIAS "name=plooi&collections=$SOLR_COLLECTION"
fi

log "Done"
