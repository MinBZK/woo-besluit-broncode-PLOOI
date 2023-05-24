Feature: Inlezen Roo bestanden

  This feature relates to user story 159854
  Check if roo sru load process results in expected executions and document information, as visible in DCN Admin

  As a DCN Admin user
  I want to see correct document information in the DCN Admin application
  So that I can check if the loading (ingress) and processing of the oep files have been executed successfully

  Background:
    Given environment for 'roo' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: Check for correct roo field mappings and verwerking for a specific file are visible in DCN Admin
    Given documents are available in the 'roo' server
    And the route is triggered
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    Then the Solr fields contains the following data
      | Solr fields                                  | Source XML fields  | test1                                                               |
      | external_id                                  | identifier         | https://identifier.overheid.nl/tooi/id/ministerie/mnre1045          |
      | id                                           | not available      | roo-0aa91110d67b0907de3322a27010c3ede0060b92_1                      |
      | dcn_id                                       | not available      | roo-0aa91110d67b0907de3322a27010c3ede0060b92                        |
      | identifier                                   | not available      | roo-0aa91110d67b0907de3322a27010c3ede0060b92                        |
      | creator                                      | resourceIdentifier | Economische Zaken en Klimaat                                        |
      | title                                        | naam               | Economische Zaken en Klimaat - Organisatiegegevens                  |
      | language                                     | not available      | Nederlands                                                          |
      | source                                       | url                | https://organisaties.overheid.nl/10621/Economische_Zaken_en_Klimaat |
      | issued                                       | startDatum         | 2010-10-14                                                          |
      | topthema                                     | not available      | Organisatie en bedrijfsvoering                                      |
      | extrametadata_plooi.displayfield.bezoekadres | not available      | Bezuidenhoutseweg 73, 2594 AC Den Haag                              |
      | extrametadata_plooi.displayfield.postadres   | not available      | Postbus 20401, 2500 EK Den Haag                                     |
    And Latest verwerking entries contain the following data
      | de_testnr | extIds                                                     | dcnId                                        | stage   | severity |
      | de1       | https://identifier.overheid.nl/tooi/id/ministerie/mnre1045 | roo-0aa91110d67b0907de3322a27010c3ede0060b92 | INGRESS | OK       |
      | de2       | https://identifier.overheid.nl/tooi/id/ministerie/mnre1045 | roo-0aa91110d67b0907de3322a27010c3ede0060b92 | PROCESS | OK       |
