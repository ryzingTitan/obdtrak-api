Feature: Get sessions

  Background:
    Given the following tracks exist:
      | id                                   | name       | longitude | latitude |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | Test Track | -90.1374  | 45.4086  |
    And the following cars exist:
      | id                                   | yearManufactured | make       | model |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta |
    And the following sessions exist:
      | userEmail      | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | test@test.com  | Test          | Tester       | 2022-09-18T14:15:47.968Z | 2022-09-18T14:17:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
      | test2@test.com | Test 2        | Tester       | 2022-09-18T14:15:47.968Z | 2022-09-18T14:17:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
      | test@test.com  | Test          | Tester       | 2022-09-18T14:15:47.965Z | 2022-09-18T14:15:49.968Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |

  Scenario: Get sessions for active user
    Given the user has a valid authorization token
    When the sessions are retrieved for user 'test@test.com'
    Then the request response status is 'OK'
    And the following sessions are returned:
      | id | startTime                | endTime                  | trackName  | trackLongitude | trackLatitude | carYear | carMake    | carModel |
      | 1  | 2022-09-18T14:15:47.968Z | 2022-09-18T14:17:48.962Z | Test Track | -90.1374       | 45.4086       | 2001    | Volkswagen | Jetta    |
      | 3  | 2022-09-18T14:15:47.965Z | 2022-09-18T14:15:49.968Z | Test Track | -90.1374       | 45.4086       | 2001    | Volkswagen | Jetta    |
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