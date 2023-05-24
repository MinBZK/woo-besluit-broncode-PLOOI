Feature: Aanlevering API

  Aanlevering API (dcn-api on our side) accepts a multi-part POST request. The multi-part POST request can contain
  different parts with different manifestLabels, for example a pdf document or a json metadata file can be a part
  of this POST request. Regarding to these parts, either the pdf file or the metadata file or both of them can be
  updated within the same POST request.

  As a DCN Admin user
  I want to see correct document information in the DCN Admin application
  So that I can check if the processing of the Aanlevering API files have been executed successfully

  Background:
    Given environment for 'plooi-api' 'process' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: Check that metadata + pdf file is processed with solr mappings and document event fields

    POST a request to API that has both metadata and pdf file and ensure that metadata json and pdf file
    updates the SOLR data, regarding document events on database are created and also both of these
    pdf file and metadata are saved to DCN can be retrieved via API.

    Given document 'f46d9054-d03f-4b70-8e4f-d1274294682b' is sent to the Aanlevering API, with parts
      | manifestLabel | fileName            | contentType      |
      | metadata      | test_api_plooi.json | application/json |
      | document      | test-pdf-1.pdf      | application/pdf  |
    Then status code is 201
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    Then the Solr fields contains the following data
      | Solr fields                                          | Source JSON fields                                   | Document                                                                          |
      | external_id                                          | Provided Aanlever id (pId)                           | f46d9054-d03f-4b70-8e4f-d1274294682b                                              |
      | id                                                   | PLOOI Identifier, generated                          | f46d9054-d03f-4b70-8e4f-d1274294682b_1                                            |
      | dcn_id                                               | DCN Identifier, generated                            | plooi-api-f46d9054-d03f-4b70-8e4f-d1274294682b                                    |
      | title                                                | $.document.titelcollectie.officieleTitel             | 's Gravenhage Kapvergunning Hooigracht onder nummer ...                           |
      | description                                          | $.document.omschrijvingen                            | omschrijving                                                                      |
      | verantwoordelijke                                    | $.document.verantwoordelijke                         | gemeente Den Haag                                                                 |
      | publisher                                            | $.document.publisher                                 | gemeente Den Haag                                                                 |
      | creator                                              | $.document.opsteller or $.document.naamOpsteller     | Gemeente Den Haag                                                                 |
      | issued                                               | $.document.creatiedatum                              | 2021-11-08                                                                        |
      | language                                             | $.document.language                                  | Nederlands                                                                        |
      | type                                                 | $.document.classificatiecollectie.documentsoorten[*] | vergunning                                                                        |
      | topthema                                             | $.document.classificatiecollectie.themas[*]          | natuur en milieu                                                                  |
      | extrametadata_plooi.displayfield.identificatienummer | $.document.identifiers[*]                            | https://documenten.denhaag.nl/documenten/doc-123e4567-e89b-12d3-a456-426614174777 |
      | extrametadata_plooi.displayfield.aggregatiekenmerk   | $.document.aggregatiekenmerk                         | aggregatiekenmerk                                                                 |
      | extrametadata_prefix.key                             | $.document.extraMetadata[*].velden[*]                |                                                                                 1 |
    And Latest verwerking entries contain the following data
      | de_testnr                                 | extIds                               | dcnId                                          | stage   | severity |
      | f46d9054-d03f-4b70-8e4f-d1274294682b-ingr | f46d9054-d03f-4b70-8e4f-d1274294682b | plooi-api-f46d9054-d03f-4b70-8e4f-d1274294682b | INGRESS | OK       |
      | f46d9054-d03f-4b70-8e4f-d1274294682b-proc | f46d9054-d03f-4b70-8e4f-d1274294682b | plooi-api-f46d9054-d03f-4b70-8e4f-d1274294682b | PROCESS | OK       |
    And for document 'f46d9054-d03f-4b70-8e4f-d1274294682b' the following parts can be retrieved
      | testnr | manifestLabel | fileName            |
      | tst1   | metadata_v1   | UNKNOWN.metadata_v1 |
      | tst2   | document      | UNKNOWN.document    |

  Scenario: Update metadata and check that metadata + pdf file is processed with solr mappings and document event fields

    POST a request to API that updates only metadata of the existing document and ensure that metadata json is updated
    and also both existing pdf file and updated metadata are saved to DCN and can be retrieved via API.

    Given document 'f46d9054-d03f-4b70-8e4f-d1274294682b' is sent to the Aanlevering API, with parts
      | manifestLabel | fileName                    | contentType      |
      | metadata      | test_api_plooi_updated.json | application/json |
    Then status code is 201
    And for document 'f46d9054-d03f-4b70-8e4f-d1274294682b' the following parts can be retrieved
      | testnr | manifestLabel | fileName                    |
      | tst1   | metadata_v1   | UNKNOWN.metadata_v1 |
      | tst2   | document      | UNKNOWN.document    |

  Scenario: Update pdf and check that metadata + pdf file is processed with solr mappings and document event fields

    POST a request to API that updates only pdf file of the existing document and ensure that pdf file is updated
    and also both existing metadata and updated pdf file are saved to DCN and can be retrieved via API.

    Given document 'f46d9054-d03f-4b70-8e4f-d1274294682b' is sent to the Aanlevering API, with parts
      | manifestLabel | fileName       | contentType     |
      | document      | test-pdf-2.pdf | application/pdf |
    Then status code is 201
    And for document 'f46d9054-d03f-4b70-8e4f-d1274294682b' the following parts can be retrieved
      | testnr | manifestLabel | fileName                    |
      | tst1   | metadata_v1   | UNKNOWN.metadata_v1 |
      | tst2   | document      | UNKNOWN.document    |
