
TOC
---
- [1 Introduction](#1-introduction) <br/>
- [2 Architecture](#2-functionality) <br/>
- [3 Functionality](#3-functionality) <br/>
- [4 Running IT Tests](#4-runnint-it-tests) <br/>


 1 Introduction
---------------
Placeholder


[TOC](#toc)


 2 Architecture
---------------
Placeholder


[TOC](#toc)


 3 Funtionality
---------------
Placeholder


[TOC](#toc)


 4 Running IT Tests
------------------------------------
To run the IT tests against docker locally;

1. mvn package jib:dockerBuild -DskipTests --offline
2. docker compose -f search-integration-tests/src/main/resources/docker-compose.yml up
3. run CucumberIT class, see tests pass
4. docker compose -f search-integration-tests/src/main/resources/docker-compose.yml down

To run the IT tests against docker locally and to debug the Search Service;

Run configurations for IT tests should include;

-DSEARCH_SERVICE_BASE_URL=http://localhost:8030
-DSEARCH_ACTUATOR_BASE_URL=http://localhost:8031
-DSOLR_CORE_URL=http://localhost:18983/solr/plooi

Run configurations for Search Service should include;

-DSOLR_URL=http://localhost:18983/solr

1. mvn package jib:dockerBuild -DskipTests --offline
2. docker compose -f docker compose -f search-integration-tests/src/main/resources/docker-compose-local.yml up
3. run Search Service in debug mode
4. run CucumberIT class, see tests pass
5. docker compose -f docker compose -f search-integration-tests/src/main/resources/docker-compose-local.yml down


[TOC](#toc)

