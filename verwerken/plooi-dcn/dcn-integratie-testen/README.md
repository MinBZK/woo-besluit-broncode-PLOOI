
# DCN Cucumber Tests

This is a maven project which contains Cucumber integration testing

The test needs a full stack to be running before it can be executed. The stack consists of
 * plooi-dcn
 * solr
 * portgesql
 * mockserver (mocking replies from the oep, ronl, etc services)

This stack can be started via Docker Compose;

    docker compose -f dcn-integratie-testen/src/main/resources/docker-compose.yml up

Then the test can be executed via Maven:

    mvn verify -Djacoco.port=18082 -pl dcn-integratie-testen -P integration-tests

or by running the `nl.overheid.koop.plooi.dcn.integration.test.CucumberIT` test in your
favorite IDE.

And afterwards stop and destroy the stack via

    docker compose -f dcn-integratie-testen/src/main/resources/docker-compose.yml down

Starting the stack and running the test via separate commands gives us the opportunity to
query DCN Admin, Solr and/or Postgresql after of during the tests.
When that's not needed, all we need to execute is just a single command:

    mvn verify -Djacoco.port=18082 -pl dcn-integratie-testen -P integration-tests -P integration-tests-compose

## Cucumber

Tests are written using the [Cucumber Framework](https://cucumber.io/docs/installation/java/).

## Mockserver

Where DCN normally connects to external sources like www.rijksopverheid.nl, during these tests it
connects to a [mock server](https://www.mock-server.com). There are a few things you need to know
about mock server integration while writing tests:
 * The mock service is running in a separate container;
 * The tests are connecting to the mock service to post expectations, which are fixed replies for
   given requests. To build these expectations, it scans the `src/test/resources/test-data`
   directory and creates one for each file found. The URL path to match is set equal to the file
   path (incl. file name). Where the actual path to match is more complicated (like having a query
   parameters, etc.), is can be configured in
   `src/test/resources/test-data/urlmapping.properties`.
 * Where a test response contains a link that has to be followed (like an SRU reply referring to
   PDFs etc.), it has to be rewritten to have the path match the expectation above. The host and
   port part has to be replaced by "__MOCK_SERVICE__". While executing the test replaces these
   strings with the actual mock server hostname and port. These are configured via the property
   `integration.mock.server.internal`
 * DCN has `PageQueryPrep` rewriting URLs to connect to the mock service. The mock service
   hostname and port are configured via the environment variable `MOCK_SERVICE`.
