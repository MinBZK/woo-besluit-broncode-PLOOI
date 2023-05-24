Feature: Inlezen Ronl Archief bestanden

  Ronl Archief processing is a one-time action to load archived data from rijksoverheid.nl (Ronl) into PLOOI. This archive
  contains documents which are no longer available on Ronl. Ronl cleans op their site and archives content once a year.
  KOOP has downloaded and centrally stored that archive and PLOOI has loaded it on the initial roll-out.

  As a DCN Admin user, I want to see correct document information in the DCN Admin application
  So that I can check if the loading (ingress) and processing of the ronl archief files have been executed successfully

  Background:
    Given environment for 'ronl-archief' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: Check for correct Solr field mappings for a specific file are visible in DCN Admin

    The Ronl archive contains XML files with metadata and (PDF) content stored in a FRBR directory structure.
    DCN traverses that directory en creates a PLOOI document for each manifest.xml file found.

    Given documents are available in the 'ronl-archief' directory
    And the route is triggered
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    Then the Solr fields contains the following data
      | Solr fields     | Source XML fields   | Example values 1                                        | Example values 2                                        |
      | external_id     | dcterms.identifier  | plooicb-2011-5424                                       | plooicb-2002-321                                        |
      | id              | not available       | ronl-archief-2ef99a07-dbe5-4efa-9f79-b6131a3e17ac_1     | ronl-archief-c60ba358c864d507a65e8650c2be7f0fe1a9f0bd_1 |
      | identifier      | PLOOI identifer     | ronl-archief-2ef99a07-dbe5-4efa-9f79-b6131a3e17ac       | ronl-archief-c60ba358c864d507a65e8650c2be7f0fe1a9f0bd   |
      | dcn_id          | DCN identifer       | ronl-archief-5ac2b8ef929dd2368b44651f62061c0d17643d48   | ronl-archief-c60ba358c864d507a65e8650c2be7f0fe1a9f0bd   |
      | creator         | dcterms.creator     | ministerie van Justitie en Veiligheid                   | Onbekend                                                |
      | description     | dcterms.description | Het onderwerp draagmoederschap heeft door de ...        |                                                         |
      | issued          | dcterms.issued      | 2011-03-01                                              | 2002-11-08                                              |
      | available_start | dcterms.available   | 2011-03-01                                              | 2002-11-08                                              |
      | topthema        | dcterms.subject     | gezin en kinderen                                       | Onbekend                                                |
      | title           | dcterms.title       | Brief Tweede Kamer: Draagmoederschap                    | Veiligheidsraadresolutie 1441                           |
      | type            | dcterms.type        | brief                                                   | besluit                                                 |
    And Latest verwerking entries contain the following data
      | de_testnr | extIds            | dcnId                                                 | stage   | severity |
      | de_11     | plooicb-2011-5424 | ronl-archief-5ac2b8ef929dd2368b44651f62061c0d17643d48 | INGRESS | OK       |
      | de_12     | plooicb-2011-5424 | ronl-archief-5ac2b8ef929dd2368b44651f62061c0d17643d48 | PROCESS | INFO     |
      | de_21     | plooicb-2002-321  | ronl-archief-c60ba358c864d507a65e8650c2be7f0fe1a9f0bd | INGRESS | OK       |
      | de_22     | plooicb-2002-321  | ronl-archief-c60ba358c864d507a65e8650c2be7f0fe1a9f0bd | PROCESS | WARNING  |
    And Diagnoses entries contain the following data
      | testnr | id              | severity | code             | sourceLabel      | target    |
      | diag11 | de_12           | INFO     | ALTLABEL         | Draagmoederschap | Thema     |
      | diag21 | de_22           | INFO     | REQUIRED_DEFAULT | Onbekend         | Thema     |
      | diag22 | de_22           | INFO     | REQUIRED_DEFAULT | Onbekend         | Opsteller |
      | diag23 | de_22           | WARNING  | UNKNOWN_LABEL    | Regering         | Opsteller |
