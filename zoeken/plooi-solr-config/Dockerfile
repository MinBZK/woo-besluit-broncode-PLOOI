
ARG REGISTRY
ARG SOLR_VERSION
FROM ${REGISTRY:-}solr:${SOLR_VERSION:-latest}

ENV ZK_HOST=${ZK_HOST:-localhost:9983}                 		\
    SOLR_HOST=${SOLR_HOST:-localhost:8983}                  \
    SOLR_COLLECTION=${SOLR_COLLECTION:-plooi}

WORKDIR /var/configset

COPY --chown=$SOLR_USER src/main/conf/ plooi/
COPY --chown=$SOLR_USER src/main/scripts/deploy-config.sh .
COPY --chown=$SOLR_USER src/main/scripts/backup-job.sh .
RUN chmod -R 777 ./backup-job.sh 


CMD ["sh", "deploy-config.sh"]
