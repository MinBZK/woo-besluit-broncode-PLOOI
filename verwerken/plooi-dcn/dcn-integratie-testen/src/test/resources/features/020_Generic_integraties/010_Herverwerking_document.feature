Feature: Herverwerking van enkel document

  This feature used oep as source but is applicable on any source.

  Goal:
  As a DCN Admin user
  I want to reprocess a document that is already indexed and published in solr.

  Functionality:
  when user search for a specific document by id, DCN admin will redirect the user to the document details page of that document.
  Under in de page, user can apply different actions, one is re-process. It is used in different cases,
  for example when we want process a document against new update value list or the publisher name is changed.

  Background:

    Given environment for 'oep' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: A document can be re-processed via the document details screen

    Given documents are available in the 'oep' server
    And the route is triggered
    When user searches for document with 'dcn_id' is 'oep-ccd8f730dd00406ba9ca99e79e306b21da15f421' in the Zoek documenten pane of the DCN Admin application
    And he clicks on the 'VERWERKING' action
    Then a 'REPROCESS' proces is created
    And the 'timestamp' field in the Solr document details is updated
