Feature: Get sessions

  Background:
    Given the following tracks exist:
      | id                                   | name       | longitude | latitude |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | Test Track | -90.1374  | 45.4086  |
    And the following cars exist:
      | id                                   | yearManufactured | make       | model |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta |
    And the following sessions exist:
      | id                                   | userEmail      | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | a957bd2b-0a7a-445d-8c14-e0b5a241e47d | test@test.com  | Test          | Tester       | 2022-09-18T14:15:47.968Z | 2022-09-18T14:17:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test2@test.com | Test 2        | Tester       | 2022-09-18T14:15:47.968Z | 2022-09-18T14:17:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
      | df97393f-8252-4d3a-8669-9931c116e6f1 | test@test.com  | Test          | Tester       | 2022-09-18T14:15:47.965Z | 2022-09-18T14:15:49.968Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |

  Scenario: Get all sessions for active user
    Given the user has a valid authorization token
    When the sessions are retrieved for user 'test@test.com'
    Then the request response status is 'OK'
    And the following sessions are returned:
      | id                                   | startTime                | endTime                  | trackName  | trackLongitude | trackLatitude | carYear | carMake    | carModel |
      | a957bd2b-0a7a-445d-8c14-e0b5a241e47d | 2022-09-18T14:15:47.968Z | 2022-09-18T14:17:48.962Z | Test Track | -90.1374       | 45.4086       | 2001    | Volkswagen | Jetta    |
      | df97393f-8252-4d3a-8669-9931c116e6f1 | 2022-09-18T14:15:47.965Z | 2022-09-18T14:15:49.968Z | Test Track | -90.1374       | 45.4086       | 2001    | Volkswagen | Jetta    |
    And the application will log the following messages:
      | level | message                                         |
      | INFO  | Retrieving all sessions for user: test@test.com |

  Scenario: Sessions cannot be retrieved with an invalid authorization token
    Given the user has an invalid authorization token
    When the sessions are retrieved for user 'test@test.com'
    Then the request response status is 'UNAUTHORIZED'
    And the following sessions are returned:
      | id | startTime | endTime | trackName | trackLongitude | trackLatitude | carYear | carMake | carModel |
    And the application will log the following messages:
      | level | message |