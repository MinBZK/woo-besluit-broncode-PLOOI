Feature: Inlezen oep bestanden

  This feature relates to user story 147362
  Check if oep sru load process results in expected executions and document information, as visible in DCN Admin

  As a DCN Admin user
  I want to see correct document information in the DCN Admin application
  So that I can check if the loading (ingress) and processing of the oep files have been executed successfully

  Background:
    Given environment for 'oep' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: Check for correct oep field mappings and verwerking for a specific file are visible in DCN Admin
    Given documents are available in the 'oep' server
    And the route is triggered
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    Then the Solr fields contains the following data
      | Solr fields                                          | Source XML fields                       | Kamerstuk                                                  | Kamerstuk Bijlage Beslisnota                             | Aanhangsel Tweede Kamer                                                          |
      | external_id                                          | dcterms.identifier                      | kst-32545-143                                              | blg-1007826                                              | ah-tk-20202021-3235                                                              |
      | id                                                   | PLOOI Identifier, generated             | oep-10384a20-7b6e-40e5-9ed3-979250cd0214_1                 | oep-e20bfd97-1c87-40e1-aa16-ca18778ed019_1               | oep-32a72cf4-7072-4052-a7da-6e67b82145c0_1                                       |
      | dcn_id                                               | DCN Identifier, generated               | oep-ccd8f730dd00406ba9ca99e79e306b21da15f421               | oep-f18ea79c4b78ffe4aa6b8803c7b2f9dfb0c2d059             | oep-bb23fe3ce3e1b55f31a023136c85401b2d7ec9e1                                     |
      | title                                                | dcterms.title                           | 32545, nr. 143 - Wet- en regelgeving financiële markten    | 33037, nr. 430 - Beslisnota inzake aangenomen moties ... | Antwoord op vragen van de leden Van Weyenberg, Romke de Jong en Kat over de Tozo |
      | description                                          | dcterms.title for Kamerstuk, else empty | Wet- en regelgeving financiële markten; Motie; ...         | Beslisnota inzake aangenomen moties en reactie op ...    |                                                                                  |
      | creator                                              | dcterms.creator                         | Onbekend                                                   | Tweede Kamer                                             | Tweede Kamer                                                                     |
      | language                                             | dcterms.language                        | Nederlands                                                 | Nederlands                                               | Nederlands                                                                       |
      | type                                                 | overheidop:publicationName              | Kamerstuk                                                  | beslisnota                                               | Kamervraag met antwoord                                                          |
      | extrametadata_plooi.displayfield.documentsubsoort    | dcterms.type scheme="..."               | Motie                                                      | Bijlage                                                  | Antwoord                                                                         |
      | topthema                                             | overheid:categorie                      | economie, markttoezicht                                    | Onbekend                                                 | ondernemen, ruimte en infrastructuur                                             |
      | issued                                               | dcterms.issued                          | 2021-07-06                                                 |                                                          | 2021-06-21                                                                       |
      | available_start                                      | dcterms.available                       | 2021-07-07                                                 | 2021-12-03                                               | 2021-06-21                                                                       |
      | source                                               | dcterms.identifier                      | https://zoek.officielebekendmakingen.nl/kst-32545-143.html | https://zoek.officielebekendmakingen.nl/blg-1007826.html | https://zoek.officielebekendmakingen.nl/ah-tk-20202021-3235.html                 |
      | extrametadata_plooi.displayfield.identificatienummer | dcterms.identifier                      | kst-32545-143                                              | blg-1007826                                              | ah-tk-20202021-3235                                                              |
      | extrametadata_plooi.displayfield.aggregatiekenmerk   | overheidop:dossiernummer                |                                                      32545 |                                                    33037 |                                                                                  |
      | extrametadata_plooi.displayfield.indiener            | overheidop:indiener                     | I. (Inge) van Dijk, P.A. Grinwis                           |                                                          | S.P.R.A. van Weyenberg, R.H. (Romke) de Jong, H. Kat                             |
      | extrametadata_plooi.displayfield.vergaderjaar        | overheidop:vergaderjaar                 | 2020-2021                                                  | 2021-2022                                                | 2020-2021                                                                        |
      | extrametadata_plooi.displayfield.vergaderdatum       | overheidop:datumVergadering             |                                                            |                                                          |                                                                                  |
    And Latest verwerking entries contain the following data
      | de_testnr    | extIds              | dcnId                                        | stage   | severity |
      | de-kst-ingr  | kst-32545-143       | oep-ccd8f730dd00406ba9ca99e79e306b21da15f421 | INGRESS | OK       |
      | de-kst-proc  | kst-32545-143       | oep-ccd8f730dd00406ba9ca99e79e306b21da15f421 | PROCESS | WARNING  |
      | de-bijl-ingr | blg-1007826         | oep-f18ea79c4b78ffe4aa6b8803c7b2f9dfb0c2d059 | INGRESS | OK       |
      | de-bijl-proc | blg-1007826         | oep-f18ea79c4b78ffe4aa6b8803c7b2f9dfb0c2d059 | PROCESS | INFO     |
      | de-ahtk-ingr | ah-tk-20202021-3235 | oep-bb23fe3ce3e1b55f31a023136c85401b2d7ec9e1 | INGRESS | OK       |
      | de-ahtk-proc | ah-tk-20202021-3235 | oep-bb23fe3ce3e1b55f31a023136c85401b2d7ec9e1 | PROCESS | INFO     |
