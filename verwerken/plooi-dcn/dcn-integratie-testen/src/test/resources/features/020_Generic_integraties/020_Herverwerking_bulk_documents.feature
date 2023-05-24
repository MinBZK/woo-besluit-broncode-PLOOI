Feature: Herwerking bulk documenten

  This feature used oep as source.

  Goal:
  As a DCN Admin user
  I want to reprocess bulk documents already published and indexed in solr and check if the re-processed
  documents are updated to the required values.

  Functionality:
  Sometimes it is required that DCN admin user want re-process number of documents for a specific reason. For example when the publisher name is changed
  and you want show the documents with the new publisher name. Dcn support that by first searching for all the documents of that publisher,
  and apply "verwerk opnieuw" option. This will trigger the re-processing action on all these documents in (bulk) and update them again in solr.

  Background:
    Given environment for 'oep' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

#  Scenario: Check if documents list can re-processed from search result page
#    Given documents are available in the 'oep' server
#    When user search documents by index and type 'financiële' in 'title' search field
#    And user select action button verwerk opnieuw
#    Then all these documents will be re-processed in bulk and the timestamp of these documents will be updated in solr
