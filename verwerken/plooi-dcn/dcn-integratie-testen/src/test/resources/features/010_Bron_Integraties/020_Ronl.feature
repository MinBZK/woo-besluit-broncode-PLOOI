Feature: Inlezen ronl bestanden

  Rijksoverheid.nl (ronl) publishes documents during the day and provides a feed to query new updates to the site.
  PLOOI tracks ronl by querying this feed every half hour.

  As a DCN Admin user
  I want to see correct document information in the DCN Admin application
  So that I can check if the loading (ingress) and processing of the ronl files have been executed successfully

  Background:
    Given environment for 'ronl' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: RONL documents with and without PDF

    An ronl document may or may not contain a PDF. An example of a document without PDF is a press conference,
    where is full text is in the ronl page itself. Examples of documents with PDF are kamerstukken and documents
    provided by the departments. Documents may have multiple PDFs, this is demonstrated in the next scenario.

    Given documents are available in the 'ronl' server
    And the route is triggered
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    Then the Solr fields contains the following data
      | Solr fields       | Source XML fields           | Persconferentie                                                         | Rapport                                                        | Beslisnota                                                            |
      | external_id       | id                          | 02dbce21-c2a6-4310-9ed5-aab7b27467ac                                    | 5afce6ec-a4ed-4c62-a6d5-168a59247678                           | e16712e7-b88c-4b3e-899e-f5ab4e26b2dc                                  |
      | id                | PLOOI Identifier, generated | ronl-1df1e6db16b1cb344ec81fcd79a25d45f9c47cb7_1                         | ronl-05c7b417-53f3-4ff4-8445-d0627a0aa160_1                    | ronl-69323ea986c7c1db3cba42d1725d3af5d9365c1c_1                       |
      | dcn_id            | DCN Identifier, generated   | ronl-1df1e6db16b1cb344ec81fcd79a25d45f9c47cb7                           | ronl-16bac8e0eb30525cc2655d53d4fb50b5acabfb30                  | ronl-69323ea986c7c1db3cba42d1725d3af5d9365c1c                         |
      | title             | title                       | Letterlijke tekst persconferentie na ministerraad 17 november 2017      | Advies nieuwe media                                            | Beslisnota Ondertekening voorhangbrieven AMvB studiesucces mbo        |
      | description       | introduction                | Letterlijke tekst van de persconferentie van viceminister-president ... | Advies over auteursrecht, naburige rechten en de nieuwe media. | In een beslisnota staat achtergrondinformatie die bewindspersonen ... |
      | creator           | creators                    | ministerie van Algemene Zaken                                           | ministerie van Algemene Zaken                                  | ministerie van Onderwijs, Cultuur en Wetenschap                       |
      | verantwoordelijke | authorities                 | ministerie van Algemene Zaken                                           | ministerie van Justitie en Veiligheid                          | ministerie van Onderwijs, Cultuur en Wetenschap                       |
      | language          | language                    | Nederlands                                                              | Nederlands                                                     | Nederlands                                                            |
      | type              | type                        | persbericht                                                             | rapport                                                        | beslisnota                                                            |
      | topthema          | subjects                    | Onbekend                                                                | Onbekend                                                       | beroepsonderwijs                                                      |
      | issued            | issued                      | 2017-11-17                                                              | 1998-08-31                                                     | 2022-04-01                                                            |
      | available_start   | available                   | 2017-11-17                                                              | 2021-07-13                                                     | 2022-04-01                                                            |
      | source            | n.a.                        |                                                                         |                                                                |                                                                       |
    And Latest verwerking entries contain the following data
      | de_testnr          | extIds                               | dcnId                                         | stage   | severity |
      | de-persconf-ingr   | 02dbce21-c2a6-4310-9ed5-aab7b27467ac | ronl-1df1e6db16b1cb344ec81fcd79a25d45f9c47cb7 | INGRESS | OK       |
      | de-persconf-proc   | 02dbce21-c2a6-4310-9ed5-aab7b27467ac | ronl-1df1e6db16b1cb344ec81fcd79a25d45f9c47cb7 | PROCESS | INFO     |
      | de-rapport-ingr    | 5afce6ec-a4ed-4c62-a6d5-168a59247678 | ronl-16bac8e0eb30525cc2655d53d4fb50b5acabfb30 | INGRESS | OK       |
      | de-rapport-proc    | 5afce6ec-a4ed-4c62-a6d5-168a59247678 | ronl-16bac8e0eb30525cc2655d53d4fb50b5acabfb30 | PROCESS | INFO     |
      | de-beslisnota-ingr | e16712e7-b88c-4b3e-899e-f5ab4e26b2dc | ronl-69323ea986c7c1db3cba42d1725d3af5d9365c1c | INGRESS | OK       |
      | de-beslisnota-proc | e16712e7-b88c-4b3e-899e-f5ab4e26b2dc | ronl-69323ea986c7c1db3cba42d1725d3af5d9365c1c | PROCESS | INFO     |
    And Diagnoses entries contain the following data
      | testnr            | verwerkingId       | severity | code             | sourceLabel                       | target        |
      | diag-persconf-1   | de-persconf-proc   | INFO     | REQUIRED_DEFAULT | Onbekend                          | Thema         |
      | diag-persconf-2   | de-persconf-proc   | INFO     | ALTLABEL         | mediatekst                        | Documentsoort |
      | diag-rapport-1    | de-rapport-proc    | INFO     | REQUIRED_DEFAULT | Onbekend                          | Thema         |
      | diag-beslisnota-1 | de-beslisnota-proc | INFO     | ALTLABEL         | Middelbaar beroepsonderwijs (mbo) | Thema         |

  Scenario: RONL bundles (and parts)

    An ronl bundle is a document which has multiple PDFs. For such case we create a single "bundle" document
    and a separate "part" document for each PDF. The bundle document has no (PDF) content itself and just refers
    to the parts. Each part refers back to the bundle. The parts also have the same metadata as the bundle.

    So, in addition to the above:

    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    Then the Solr fields contains the following data
      | Solr fields       | Source XML fields           | Bundle                                                                                           | Part 1                                             | Part 2                                                        |
      | external_id       | id                          | 0acb97b8-b3cf-4654-92c2-0fa4199c100c                                                             | 0acb97b8-b3cf-4654-92c2-0fa4199c100c               | 0acb97b8-b3cf-4654-92c2-0fa4199c100c                          |
      | id                | PLOOI Identifier, generated | ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3_1                                                      | ronl-26cd903f-53da-4d64-8ca6-74e91b6baaca_1        | ronl-98043096-0324-4e14-9736-bb97f82fac78_1                   |
      | dcn_id            | DCN Identifier, generated   | ronl-228036432630c580dcdddefd9c3a6de8e2587c1b                                                    | ronl-684333ccbc1afcdb275529c54261dff4061180ea      | ronl-a83c9806d87ce33e632c253eb2166cc937f0bef1                 |
      | title             | title                       | Akte van aanstelling 001474                                                                      | Akte van aanstelling 001474 - Akte van aanstelling | Akte van aanstelling 001474 - Supplement akte van aanstelling |
      | creator           | creators                    | ministerie van Algemene Zaken                                                                    | ministerie van Algemene Zaken                      | ministerie van Algemene Zaken                                 |
      | verantwoordelijke | authorities                 | ministerie van Defensie                                                                          | ministerie van Defensie                            | ministerie van Defensie                                       |
      | language          | language                    | Nederlands                                                                                       | Nederlands                                         | Nederlands                                                    |
      | type              | type                        | Onbekend                                                                                         | Onbekend                                           | Onbekend                                                      |
      | topthema          | subjects                    | arbeidsverhoudingen                                                                              | arbeidsverhoudingen                                | arbeidsverhoudingen                                           |
      | issued            | issued                      | 1985-08-05                                                                                       | 1985-08-05                                         | 1985-08-05                                                    |
      | available_start   | available                   | 2019-12-17                                                                                       | 2019-12-17                                         | 2019-12-17                                                    |
      | haspart           | link to parts               | ronl-26cd903f-53da-4d64-8ca6-74e91b6baaca_1, ronl-98043096-0324-4e14-9736-bb97f82fac78_1         |                                                    |                                                               |
      | ispartof          | link to bundle              |                                                                                                  | ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3_1        | ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3_1                   |
    And Latest verwerking entries contain the following data
      | de_testnr      | extIds                                                                           | dcnId                                         | stage   | severity |
      | de-bundle-ingr | 0acb97b8-b3cf-4654-92c2-0fa4199c100c                                             | ronl-228036432630c580dcdddefd9c3a6de8e2587c1b | INGRESS | OK       |
      | de-bundle-proc | 0acb97b8-b3cf-4654-92c2-0fa4199c100c                                             | ronl-228036432630c580dcdddefd9c3a6de8e2587c1b | PROCESS | INFO     |
      | de-part1-ingr  | 0acb97b8-b3cf-4654-92c2-0fa4199c100c, Akte van aanstelling_001474.pdf            | ronl-684333ccbc1afcdb275529c54261dff4061180ea | INGRESS | OK       |
      | de-part1-proc  | 0acb97b8-b3cf-4654-92c2-0fa4199c100c, Akte van aanstelling_001474.pdf            | ronl-684333ccbc1afcdb275529c54261dff4061180ea | PROCESS | INFO     |
      | de-part2-ingr  | 0acb97b8-b3cf-4654-92c2-0fa4199c100c, Supplement akte van aanstelling_001474.pdf | ronl-a83c9806d87ce33e632c253eb2166cc937f0bef1 | INGRESS | OK       |
      | de-part2-proc  | 0acb97b8-b3cf-4654-92c2-0fa4199c100c, Supplement akte van aanstelling_001474.pdf | ronl-a83c9806d87ce33e632c253eb2166cc937f0bef1 | PROCESS | INFO     |
# Indexing diagnostics are not (yet?) written to the registration service
#    And Diagnoses entries contain the following data
#      | testnr        | verwerkingId    | severity | code       | message                   |
#      | diag-bundle-1 | de-bundle-proc  | INFO     | EMPTY_TEXT | No document text to index |

  Scenario: PLOOIed ronl document

    Ronl introduced a process where after a document is published on PLOOI, they update the document, add a
    link to the PDF on PLOOI to the description and remove their version of the PDF. In order to not corrupt
    PLOOI content, DCN detects such updates and ignores them, sticking to the original version.

    So, in addition to the above:

    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    Then Latest verwerking entries contain the following data
      | de_testnr       | extIds                               | dcnId                                    | stage   | severity |
      | de-plooied-ingr | cb0edf75-382a-4048-82bb-6ae83aa615ce | ronl-15dff0cef5d206e6ac605a9935be664d5a2c256e | INGRESS | INFO     |
    And Diagnoses entries contain the following data
      | testnr         | verwerkingId    | severity | code    | message                                  |
      | diag-plooied-1 | de-plooied-ingr | INFO     | DISCARD | Discarding document with plooi reference |
