Feature: Validate search service 

  The Search service accepts a search query from a client.
  The client may be internal (e.g. aanleverloket or Admin tool) or external (e.g. DPC or portal).
  The search query contains one or more defined document parameters. 
  The Search service translates the search parameters to a Solr query and, based on this, retrieves the corresponding DCN identifiers (result list)
  These DCN identifiers are then sent to the process endpoint as indicated by the client in the request 

  As a developer
  I want to validate that the search service provides the correct response based on Hergebruik API request parameters
  So that I can release the hergebruik API to in- and external clients  

  Background:
    Given solr is up and running
    And 'solr-testfiles.json' is loaded in Solr
    And search service is up and running

  Scenario: Basic search
    When user searches on '*'
    And performing search
    Then the number of search results is 3
  
  Scenario: Search with single filter
    When user searches on '*'
    And filter param toonNietGepubliceerd set to 'false'
    And filter list on 'classificatiecollectie.documentsoorten'
      | https://identifier.overheid.nl/tooi/def/thes/kern/c_03c52ba0 |
    And performing search
    Then the number of search results is 1
    And search results include the following documents
      | document.pid |
      | testdoc-1    |
  
  Scenario: Search with multiple filters
    When user searches on '*'
    And filter param toonNietGepubliceerd set to 'false'
    And filter list on 'classificatiecollectie.documentsoorten'
      | https://identifier.overheid.nl/tooi/def/thes/kern/c_03c52ba0 |
      | https://identifier.overheid.nl/tooi/def/thes/kern/c_056a75e1 |
    And filter object on 'plooiIntern.sourceLabel': 'oep'
    And filter object on 'plooiIntern.aanbieder': 'officielebekendmakingen.nl'
    And filter date on 'openbaarmakingsdatum' van: '2023-01-01'
    And filter date on 'mutatiedatumtijd' van: '2023-01-01T00:00:00.000Z' tot: '2023-01-11T00:00:00.000Z'
    And performing search
    Then the number of search results is 2
    And search results include the following documents
      | document.pid |
      | testdoc-1    |
      | testdoc-2    |
  
  Scenario: Search with toonNietGepubliceerd set to true
    When user searches on '*'
    And filter param toonNietGepubliceerd set to 'true'
    And filter list on 'verantwoordelijke'
      | https://identifier.overheid.nl/tooi/id/ministerie/mnre1058 |
    And performing search
    Then the number of search results is 2
    And search results include the following documents
      | document.pid                  |
      | testdoc-3-published-in-future |
      | testdoc-4-already-published   |
    And ensure that pid 'testdoc-3-published-in-future' publicatiestatus is 'latere-publicatie'
    And ensure that pid 'testdoc-4-already-published' publicatiestatus is 'gepubliceerd'

  Scenario: Search with toonNietGepubliceerd set to false
    When user searches on '*'
    And filter param toonNietGepubliceerd set to 'false'
    And filter list on 'verantwoordelijke'
      | https://identifier.overheid.nl/tooi/id/ministerie/mnre1058 |
    And performing search
    Then the number of search results is 1
    And search results include the following documents
      | document.pid                  |
      | testdoc-4-already-published   |
    And ensure that pid 'testdoc-4-already-published' publicatiestatus is 'gepubliceerd'
