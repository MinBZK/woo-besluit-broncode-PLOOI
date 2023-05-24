
FROM SSSSSSSSSSSSSSSSSS/docker-hub-proxy/library/tomcat:9-jdk17-openjdk-slim-bullseye

ARG XSLWEB_VERSION=v4.0.1
# Note (01.02.2023) - when using XSLWeb v4.2.0 something goes wrong on searchresults.xml
# that results in dates no longer being shown on the search results pages. For some reason the
# xslt select on dcterms:available/start does not work

ARG HEAPMEMORY=512M

ENV XSLWEB_DOWNLOAD_URL=https://SSSSSSSSSSSSSSSSSS/xslweb-war-$XSLWEB_VERSION.tar.gz \
    CATALINA_OPTS="-Xmx${HEAPMEMORY} -Dxslweb.home=/usr/local/xslweb/home -Dlogback.configurationFile=/usr/local/xslweb/home/config/logback.xml" \
    USER=tomcat

RUN apt-get update && apt-get upgrade -y \
 && apt-get install -y curl unzip \
 && curl $XSLWEB_DOWNLOAD_URL --output /tmp/xslweb-war.tar.gz \
 && tar -xzvf /tmp/xslweb-war.tar.gz -C /tmp xslweb.war \
 && unzip -q /tmp/xslweb.war -d ${CATALINA_HOME}/webapps/ROOT \
 && rm /tmp/xslweb-war.tar.gz /tmp/xslweb.war \
 && mkdir -p --mode 777 /usr/local/xslweb/home/.cache \
 && useradd -d ${CATALINA_HOME} --uid 1000 ${USER} \
 && apt-get remove -y curl unzip \
 && apt-get autoremove -y \
    ;

COPY home /usr/local/xslweb/home

USER ${USER}
EXPOSE 8080

CMD ["catalina.sh", "run"]
