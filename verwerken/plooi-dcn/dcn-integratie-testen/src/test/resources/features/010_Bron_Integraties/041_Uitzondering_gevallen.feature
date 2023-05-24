Feature: Inlezen oep bestanden bijzonder gevallen

  This feature relates to user story 160300
  Check if process (in)valid documents results in diagnostics and processing errors, as visible in DCN Admin

  As a DCN Admin user
  I want to see correct document information in the DCN Admin application
  So that I can check if the loading (ingress) and processing of the oep files have been executed

  Background:
    Given environment for 'oep' 'ingress' up and running
    And the user successfully logged in to DCN Admin Tools as SSSSSSSSSS with password SSSSSSSSSS

  Scenario: Check that Info diagnostics for a specific file are visible in DCN Admin

    Diagnostics are created when 'anticipated' errors are found in the processing of a document.
    For example: mapping mistakes because of inconstency between input values and supported list of values (TOOI).
    Info diagnostic have a low severity.
    Example is the case where a value is provided which can be mapped to a TOOI supported value via a list of synonyms.

    Given documents are available in the 'oep' server
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    And Latest verwerking entries contain the following data
      | de_testnr    | extIds      | dcnId                                        | stage   | severity |
      | de-bijl-ingr | blg-1007826 | oep-f18ea79c4b78ffe4aa6b8803c7b2f9dfb0c2d059 | INGRESS | OK       |
      | de-bijl-proc | blg-1007826 | oep-f18ea79c4b78ffe4aa6b8803c7b2f9dfb0c2d059 | PROCESS | INFO     |
    Then Diagnoses entries contain the following data
      | testnr      | verwerkingId | severity | code     | sourceLabel                      | target            |
      | diag-bijl-1 | de-bijl-proc | INFO     | ALTLABEL | Tweede Kamer der Staten-Generaal | Verantwoordelijke |
      | diag-bijl-4 | de-bijl-proc | INFO     | ALTLABEL | Tweede Kamer der Staten-Generaal | Opsteller         |

  Scenario: Check for warnings diagnostic for a specific file are visible in DCN Admin

    Diagnostics are created when 'anticipated' errors are found in the processing of a document.
    For example: mapping mistakes because of inconstency between input values and supported list of values (TOOI).
    Warning diagnostic have a medium severity.
    Example is the case where a mandatory value is missing in the source but where the document can be processed anyway.
    In those case such a value may be mapped to 'unknown'.

    Given documents are available in the 'oep' server
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    And Latest verwerking entries contain the following data
      | de_testnr   | extIds        | dcnId                                        | stage   | severity |
      | de-kst-ingr | kst-32545-143 | oep-ccd8f730dd00406ba9ca99e79e306b21da15f421 | INGRESS | OK       |
      | de-kst-proc | kst-32545-143 | oep-ccd8f730dd00406ba9ca99e79e306b21da15f421 | PROCESS | WARNING  |
    Then Diagnoses entries contain the following data
      | testnr     | verwerkingId | severity | code          | sourceLabel | target            |
      | diag-kst-1 | de-kst-proc  | WARNING  | UNKNOWN_LABEL | overige     | Verantwoordelijke |
      | diag-kst-2 | de-kst-proc  | WARNING  | UNKNOWN_LABEL | overige     | Opsteller         |

  Scenario: Check for processing error for a specific file are visible in DCN Admin

    Processing errors are created when 'not anticipated' errors are found in the processing of a document which prevents the processing of the document.
    For example: 'HttpOperationFailed (..)'

    Given documents are available in the 'oep' server
    When user searches for the 'id' in the Zoek documenten pane of the DCN Admin application
    And Latest verwerking entries contain the following data
      | de_testnr    | extIds      | dcnId                                        | stage   | severity  |
      | de-ahtk-exec | blg-1025203 | oep-05c3e20f1ead41d1103579fc4167aeca46010072 | INGRESS | EXCEPTION |
    Then Exceptie contain the following data
      | de_testnr    | exceptionMessage                                   | exceptionClass                          |
      | de-ahtk-exec | HttpException: httbroken protocol is not supported | org.apache.camel.CamelExchangeException |
