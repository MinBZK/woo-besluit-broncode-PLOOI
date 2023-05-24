
In dit project vind je de source code van de PLOOI DCN module.
PLOOI DCN bestaat uit een enkele Spring Boot applicatie die zowel de verwerkingsjobs (Camel routes)
als de Admin tool draait.

DCN is een Java applicatie die gebouwd wordt met Maven. De belangrijkste uitvoer van de build is
een dcn-application jar en een Docker container die deze jar draait.

De Docker container kan lokaal gebouwd worden met het volgende commando:

    mvn package jib:dockerBuild

Hierbij wordt een container gebouwd die lokaal gedraaid kan worden met

    docker run plooi-dcn

Bij het bouwen van de container probeert Jib de base image van Docker Hub te downloaden.
Wanneer er geen netwerkverbinding is dan gaat dit fout, maar met de offline optie instrueren we Jib
om lokaal gecachte images te gebruiken (mits deze al eerder niet-offline gedownload is):

    mvn package jib:dockerBuild --offline

We kunnen Jib instrueren om de gebouwde image in een repository te laden met behulp van de `build`
goal. Dit is standaard de Docker Hub repository, maar we kunnen ook een afwijkende repository
gebruiken, bijvoorbeeld de Standaard Platform Harbor repository:

    mvn package jib:build -Ddocker.registry.org=SSSSSSSSSSSSSSSSSS/koop-plooi/

Behalve de repository ondersteunt Jib nog enkele andere configuratie properties:

 * `docker.registry.org`: Configureert een repository om de gebouwde container naar te publiceren;
 * `docker.registry.hub`: Configureert een Docker Hub mirror om de base image van de laden;
 * `docker.tag.version`:  Configureert een versie tag voor de gebouwde image (default is "latest").
 * `docker.tag.extra`:    Configureert een extra tag voor de gebouwde image. Deze wordt gebruikt om
                            de laatste release image als "latest" te taggen.

Het project bevat ook een acceptatie test op basis van Cucumber, zie dcn-integratie-testen/README.md.
Bouwen van het project inclusief uitvoering van deze acceptatie test kan met behulp van het volgende
commando:

    mvn clean install jib:dockerBuild -fae -P integration-tests -P integration-tests-compose
