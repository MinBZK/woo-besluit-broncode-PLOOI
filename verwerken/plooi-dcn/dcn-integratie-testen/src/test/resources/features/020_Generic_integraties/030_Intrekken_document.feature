Feature: Intrekken van enkel document

  This feature used oep as source but is applicable on any source.

  Goal:
  As a DCN Admin user
  I want to un-publish a documents that already indexed in solr.

  Functionality:
  when user search for a specific document by id, DCN admin will redirect the user to the document details page of that document.
  Under in de page, user can apply different actions, one of them is un-publish. it is used in different cases,
  for example when document contains confidential information. Once the document un-published, user can publish it again.

  Background:

    Given environment for 'oep' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: A document can be deleted from document details screen and after that it can be re-publish it again

    Given documents are available in the 'oep' server
    When user searches for document with 'dcn_id' is 'oep-ccd8f730dd00406ba9ca99e79e306b21da15f421' in the Zoek documenten pane of the DCN Admin application
    And he clicks on the 'INTREKKING' action
    Then a 'DELETION' proces is created
    And the 'timestamp' field in the Solr document details is updated
#    When he clicks on the 'HERPUBLICATIE' action
#    Then a 'UNDELETION' proces is created
#    And the 'timestamp' field in the Solr document details is updated
