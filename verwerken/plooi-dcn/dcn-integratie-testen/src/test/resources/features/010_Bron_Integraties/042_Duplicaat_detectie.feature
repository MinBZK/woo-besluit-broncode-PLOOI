Feature: detecteren van duplicaat geval

  This feature relates to user story 155328 and used oep as source.
  Assumed is that it is sufficient to validate this for oep only, as this is generic (not source specific) code.

  Goal:
  As a DCN Admin user
  I want to see if a document contains (one or more) pdf files that also have been sent (are available) as part of other documents
  So that I can determine if extra actions are required, e.g. when changing or removing (de-publishing) one of these documents

  Functionality:
  if a document with dcn_identifier x is available in DCN Admin
  and its source pdf is also sent / available as part of another document with dcn_identifier y (same pdf hash)
  then both dcn_identifiers (x and y) shall be available in table 'dcn-duplicate_table'
  and for each of these documents button 'Toon duplicaten' shall be shown in the DCN Admin document detail screen

#  Background:
#    Given environment for 'oep' 'ingress' up and running
#    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS
#
#  Scenario: Check if button "toon duplicaten" is available in document details screen of the document that has duplicate relation
#    Given documents are available in the 'oep' server
#    When user searches for document with id 'oep-ccd8f730dd00406ba9ca99e79e306b21da15f421' in the Zoek documenten pane of the DCN Admin application
#    Then click on toon duplicaten open table will contain the following data
#      | testnr      | toId                                     | relationType        |
#      | de-ahtk-dup | b457f8bcd8c160609ce6e743165234f06345a69d | IDENTITEITS_GROEPEN |
