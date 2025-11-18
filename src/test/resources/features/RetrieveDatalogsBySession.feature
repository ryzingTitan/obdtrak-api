Feature: Retrieve datalogs by session id

  Background:
    Given the following tracks exist:
      | id                                   | name       | longitude | latitude |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | Test Track | -90.1374  | 45.4086  |
    And the following cars exist:
      | id                                   | yearManufactured | make       | model |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta |
    And the following sessions exist:
      | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | test@test.com | Test          | Tester       | 2022-09-18T18:14:47.968Z | 2022-09-18T18:17:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |

  Scenario: Retrieve datalogs for a session with a single datalog
    Given the following datalogs exist:
      | sessionId | timestamp                | longitude          | latitude           | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004 | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             | 14.7         |
    And the user has a valid authorization token
    When the datalogs for session with id 1 are retrieved
    Then the request response status is 'OK'
    And the following datalogs are returned:
      | sessionId | timestamp                | longitude          | latitude           | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004 | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             | 14.7         |
    And the application will log the following messages:
      | level | message                               |
      | INFO  | Retrieving datalogs for session id: 1 |

  Scenario: Retrieve datalogs for a session with multiple datalogs
    Given the following datalogs exist:
      | sessionId | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T14:15:46.962Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             | 14.7         |
      | 1         | 2022-09-18T14:15:47.968Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 165                | 5500      | 80    | 75.0             | 15.8         |
    And the user has a valid authorization token
    When the datalogs for session with id 1 are retrieved
    Then the request response status is 'OK'
    And the following datalogs are returned:
      | sessionId | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T14:15:46.962Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             | 14.7         |
      | 1         | 2022-09-18T14:15:47.968Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 165                | 5500      | 80    | 75.0             | 15.8         |
    And the application will log the following messages:
      | level | message                               |
      | INFO  | Retrieving datalogs for session id: 1 |

  Scenario: Retrieve datalogs with null data
    Given the following datalogs exist:
      | sessionId | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  |               | 150                |           | 85    |                  |              |
      | 1         | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    |                      | 15.6          | 165                | 5000      |       | 75.6             | 15.8         |
      | 1         | 2022-09-18T18:15:49.965Z | -86.14162          | 42.406800000000004  | 186.8    | 130                  | 15.0          |                    | 5500      | 80    | 75.0             | 14.7         |
    And the user has a valid authorization token
    When the datalogs for session with id 1 are retrieved
    Then the request response status is 'OK'
    And the following datalogs are returned:
      | sessionId | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  |               | 150                |           | 85    |                  |              |
      | 1         | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    |                      | 15.6          | 165                | 5000      |       | 75.6             | 15.8         |
      | 1         | 2022-09-18T18:15:49.965Z | -86.14162          | 42.406800000000004  | 186.8    | 130                  | 15.0          |                    | 5500      | 80    | 75.0             | 14.7         |
    And the application will log the following messages:
      | level | message                               |
      | INFO  | Retrieving datalogs for session id: 1 |

  Scenario: Retrieve datalogs for a session that does not exist
    Given the following datalogs exist:
      | sessionId | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             | 15.8         |
      | 1         | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15            | 165                | 5500      | 80    | 75.0             | 14.9         |
    And the user has a valid authorization token
    When the datalogs for session with id 2 are retrieved
    Then the request response status is 'OK'
    And the following datalogs are returned:
      | sessionId | timestamp | intakeAirTemperature | longitude | latitude | altitude | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
    And the application will log the following messages:
      | level | message                               |
      | INFO  | Retrieving datalogs for session id: 2 |

  Scenario: Backwards compatibility for datalogs without air fuel ratio data
    Given the following datalogs exist:
      | sessionId | timestamp                | longitude          | latitude           | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004 | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             |
    And the user has a valid authorization token
    When the datalogs for session with id 1 are retrieved
    Then the request response status is 'OK'
    And the following datalogs are returned:
      | sessionId | timestamp                | longitude          | latitude           | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004 | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             |              |
    And the application will log the following messages:
      | level | message                               |
      | INFO  | Retrieving datalogs for session id: 1 |

  Scenario: Datalogs cannot be read with an invalid authorization token
    Given the following datalogs exist:
      | sessionId | timestamp                | longitude          | latitude           | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1         | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004 | 188.4    | 123                  | 15.6          | 150                | 5000      | 85    | 75.6             | 14.7         |
    And the user has an invalid authorization token
    When the datalogs for session with id 1 are retrieved
    Then the request response status is 'UNAUTHORIZED'
    And the following datalogs are returned:
      | sessionId | timestamp | longitude | latitude | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
    And the application will log the following messages:
      | level | message |
