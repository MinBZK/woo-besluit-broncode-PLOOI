Feature: Documenten bulk intrekken

  This feature relates to user story 156945 and used oep as source.
  Assumed is that it is sufficient to validate this for oep only, as this is generic (not source specific) code.

  Goal:
  As a DCN Admin user
  I want to delete bulk documents already indexed in solr

  Functionality:
  Sometimes it is required that DCN admin user want unpublish number of documents for a specific reason. For example when documents contains private data.
  Dcn admin support that by first searching for all the documents, and apply "delete" option.
  This will will trigger the unpublish action on all these documents in (bulk) and delete them in solr. In order to publish them again, user need to revisited them
  one by one and republish them again.

  Background:
    Given environment for 'oep' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

#  Scenario: Check if list documents can be deleted from search result screen and re-publish it again one by one
#    Given documents are available in the 'oep' server
#    When user search documents by index and type 'financiële' in 'title' search field
#    And user select action button intrekken
#    Then all these documents will be deleted in bulk from solr
#    And click on opnieuw publiceer action button in every document details page will trigger 'redo' and publish document back in solr
