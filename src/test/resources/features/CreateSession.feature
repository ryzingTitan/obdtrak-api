Feature: Create session

  Background:
    Given the following tracks exist:
      | id                                   | name       | longitude | latitude |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | Test Track | -90.1374  | 45.4086  |
    And the following cars exist:
      | id                                   | yearManufactured | make       | model |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta |

  Scenario: Create a new session with a single datalog with valid session data
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude           | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004 | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'CREATED'
    And the following sessions will exist:
      | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:47.968Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs will exist:
      | id | sessionId                            | timestamp                | longitude          | latitude           | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004 | 188.4    | 123                  | 16.5          | 95                 | 3500      | 74    | 5.6              | 17.5         |
    And the application will log the following messages:
      | level | message                                                                                                  |
      | INFO  | Beginning to parse file: testFile.txt                                                                    |
      | INFO  | File parsing completed for file: testFile.txt                                                            |
      | INFO  | Session created for user test@test.com and timestamp 2022-09-18T18:15:47.968Z - 2022-09-18T18:15:47.968Z |

  Scenario: Create a new session with multiple datalogs with valid session data
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.7                         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'CREATED'
    And the following sessions will exist:
      | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs will exist:
      | id | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 16.5          | 95                 | 3500      | 74    | 5.6              | 17.5         |
      | 2  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 98                 | 2500      | 79    | 7.0              | 14.7         |
    And the application will log the following messages:
      | level | message                                                                                                  |
      | INFO  | Beginning to parse file: testFile.txt                                                                    |
      | INFO  | File parsing completed for file: testFile.txt                                                            |
      | INFO  | Session created for user test@test.com and timestamp 2022-09-18T18:15:47.968Z - 2022-09-18T18:15:48.962Z |

  Scenario: Create a new session with missing air fuel ratio data column
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'CREATED'
    And the following sessions will exist:
      | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs will exist:
      | id | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 16.5          | 95                 | 3500      | 74    | 5.6              |              |
      | 2  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 98                 | 2500      | 79    | 7.0              |              |
    And the application will log the following messages:
      | level | message                                                                                                  |
      | INFO  | Beginning to parse file: testFile.txt                                                                    |
      | INFO  | File parsing completed for file: testFile.txt                                                            |
      | INFO  | Session created for user test@test.com and timestamp 2022-09-18T18:15:47.968Z - 2022-09-18T18:15:48.962Z |

  Scenario: Create a new session with datalogs with invalid session data
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 166.2                          | -               | 123.8                      | 74.56            | 5.6                            | -                               | -                            |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 95.9                           | 3500.35         | -                          | -                | 7                              | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:49.965 | -86.14162          | 42.406800000000004  | 186.8    | -                              | 2500            | 130                        | 79               | -                              | 15.0                            | 14.8                         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'CREATED'
    And the following sessions will exist:
      | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs will exist:
      | id | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  |               | 166                |           | 74    | 5.6              |              |
      | 2  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    |                      | 16.5          | 95                 | 3500      |       | 7.0              | 17.5         |
      | 3  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:49.965Z | -86.14162          | 42.406800000000004  | 186.8    | 130                  | 15.0          |                    | 2500      | 79    |                  | 14.8         |
    And the application will log the following messages:
      | level | message                                                                                                  |
      | INFO  | Beginning to parse file: testFile.txt                                                                    |
      | INFO  | File parsing completed for file: testFile.txt                                                            |
      | INFO  | Session created for user test@test.com and timestamp 2022-09-18T18:15:47.968Z - 2022-09-18T18:15:49.965Z |

  Scenario: Create a new session with datalogs with unparseable data rows
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 166.2                          | -               | 123.8                      | 74.56            | 5.6                            | -                               | -                            |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 95.9                           | 3500.35         | -                          | -                | 7                              | 16.5                            | 17.5                         |
      | Device Time              | abc                | def                 | ghi      | jkl                            | mno             | qpr                        | st               | uv                             | wx                              | yz                           |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'CREATED'
    And the following sessions will exist:
      | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs will exist:
      | id | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  |               | 166                |           | 74    | 5.6              |              |
      | 2  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    |                      | 16.5          | 95                 | 3500      |       | 7.0              | 17.5         |
    And the application will log the following messages:
      | level | message                                                                                                                            |
      | INFO  | Beginning to parse file: testFile.txt                                                                                              |
      | ERROR | Unable to parse row: Device Time,abc,def,ghi,jkl,mno,qpr,st,uv,wx,yz with error: Text 'Device Time' could not be parsed at index 0 |
      | INFO  | File parsing completed for file: testFile.txt                                                                                      |
      | INFO  | Session created for user test@test.com and timestamp 2022-09-18T18:15:47.968Z - 2022-09-18T18:15:48.962Z                           |

  Scenario: Do not overwrite data for other users when creating a new session
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.9                         |
    And the following sessions exist:
      | id                                   | userEmail      | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test2@test.com | test 2        | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs exist:
      | sessionId                            | timestamp                | longitude         | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999 | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'CREATED'
    And the following sessions will exist:
      | id                                   | userEmail      | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test2@test.com | test 2        | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
      | 00000000-0000-0000-0000-000000000000 | test@test.com  | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs will exist:
      | id | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1  | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
      | 2  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 16.5          | 95                 | 3500      | 74    | 5.6              | 17.5         |
      | 3  | 00000000-0000-0000-0000-000000000000 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 98                 | 2500      | 79    | 7.0              | 14.9         |
    And the application will log the following messages:
      | level | message                                                                                                  |
      | INFO  | Beginning to parse file: testFile.txt                                                                    |
      | INFO  | File parsing completed for file: testFile.txt                                                            |
      | INFO  | Session created for user test@test.com and timestamp 2022-09-18T18:15:47.968Z - 2022-09-18T18:15:48.962Z |

  Scenario: Do not create duplicate sessions for a user
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.9                         |
    And the following sessions exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs exist:
      | sessionId                            | timestamp                | longitude         | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | 86.14162999999999 | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'CONFLICT'
    And the following sessions will exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:48.962Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following datalogs will exist:
      | id | sessionId                            | timestamp                | longitude         | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1  | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | 86.14162999999999 | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the application will log the following messages:
      | level | message                                                                                                           |
      | INFO  | Beginning to parse file: testFile.txt                                                                             |
      | INFO  | File parsing completed for file: testFile.txt                                                                     |
      | ERROR | A session already exists for user test@test.com and timestamp 2022-09-18T18:15:47.968Z - 2022-09-18T18:15:48.962Z |

  Scenario: A new session cannot be created with an invalid token
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude           | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004 | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
    And the user has an invalid authorization token
    When the file is uploaded for a session with the following data:
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'UNAUTHORIZED'
    And the following sessions will exist:
      | id | userEmail | userFirstName | userLastName | startTime | endTime | trackId | carId |
    And the following datalogs will exist:
      | sessionId | timestamp | longitude | latitude | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
    And the application will log the following messages:
      | level | message |