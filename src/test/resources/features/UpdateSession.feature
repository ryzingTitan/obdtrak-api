Feature: Update session

  Background:
    Given the following tracks exist:
      | id                                   | name         | longitude | latitude |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | Test Track   | -90.1374  | 45.4086  |
      | 5f9e1546-d95d-49fd-9e1f-f761ad9526a7 | Test Track 2 | -85.3850  | 43.0956  |
    And the following cars exist:
      | id                                   | yearManufactured | make       | model    |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta    |
      | 2ae74a24-6a2b-4702-8917-649031a9c069 | 1999             | Chevrolet  | Corvette |

  Scenario: Update an existing session
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.9                         |
    And the following sessions exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | a1ce63a6-4ec4-41fa-b2f5-08a03f0d64ff | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 100                  | 16.5          | 95                 | 4500      | 74    | 5.6              | 14.7         |
      | 2da8c66c-5c5d-426d-a333-bf5ffca8705c | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data and session id 'edc62dac-3b41-4b0e-8e2c-864c7c91fec4':
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | 5f9e1546-d95d-49fd-9e1f-f761ad9526a7 | 2ae74a24-6a2b-4702-8917-649031a9c069 | test          | tester       | test@test.com |
    Then the request response status is 'OK'
    And the following sessions will exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | 5f9e1546-d95d-49fd-9e1f-f761ad9526a7 | 2ae74a24-6a2b-4702-8917-649031a9c069 |
    And the following records will exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 00000000-0000-0000-0000-000000000000 | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 16.5          | 95                 | 3500      | 74    | 5.6              | 17.5         |
      | 00000000-0000-0000-0000-000000000000 | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 98                 | 2500      | 79    | 7.0              | 14.9         |
    And the application will log the following messages:
      | level | message                                              |
      | INFO  | Beginning to parse file: testFile.txt                |
      | INFO  | File parsing completed for file: testFile.txt        |
      | INFO  | Session edc62dac-3b41-4b0e-8e2c-864c7c91fec4 updated |

  Scenario: Update an existing session when file contains unparseable session data
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.9                         |
      | Device Time              | abc                | def                 | ghi      | jkl                            | mno             | qpr                        | st               | uv                             | wx                              | yz                           |
    And the following sessions exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | a1ce63a6-4ec4-41fa-b2f5-08a03f0d64ff | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 100                  | 16.5          | 95                 | 4500      | 74    | 5.6              | 14.7         |
      | 2da8c66c-5c5d-426d-a333-bf5ffca8705c | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data and session id 'edc62dac-3b41-4b0e-8e2c-864c7c91fec4':
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'OK'
    And the following sessions will exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records will exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 00000000-0000-0000-0000-000000000000 | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 16.5          | 95                 | 3500      | 74    | 5.6              | 17.5         |
      | 00000000-0000-0000-0000-000000000000 | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 98                 | 2500      | 79    | 7.0              | 14.9         |
    And the application will log the following messages:
      | level | message                                                                                                                            |
      | INFO  | Beginning to parse file: testFile.txt                                                                                              |
      | ERROR | Unable to parse row: Device Time,abc,def,ghi,jkl,mno,qpr,st,uv,wx,yz with error: Text 'Device Time' could not be parsed at index 0 |
      | INFO  | File parsing completed for file: testFile.txt                                                                                      |
      | INFO  | Session edc62dac-3b41-4b0e-8e2c-864c7c91fec4 updated                                                                               |

  Scenario: Only update records that belong to the current user when updating a session
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.9                         |
    And the following sessions exist:
      | id                                   | userEmail      | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com  | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
      | b8fe160d-ead5-473d-927d-950ed37c2e50 | test2@test.com | test 2        | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | a1ce63a6-4ec4-41fa-b2f5-08a03f0d64ff | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 100                  | 16.5          | 95                 | 4500      | 74    | 5.6              | 14.7         |
      | 2da8c66c-5c5d-426d-a333-bf5ffca8705c | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
      | 1358edd4-b896-4186-a787-e89f88394283 | b8fe160d-ead5-473d-927d-950ed37c2e50 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data and session id 'edc62dac-3b41-4b0e-8e2c-864c7c91fec4':
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'OK'
    And the following sessions will exist:
      | id                                   | userEmail      | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com  | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
      | b8fe160d-ead5-473d-927d-950ed37c2e50 | test2@test.com | test 2        | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records will exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | 1358edd4-b896-4186-a787-e89f88394283 | b8fe160d-ead5-473d-927d-950ed37c2e50 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
      | 00000000-0000-0000-0000-000000000000 | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 123                  | 16.5          | 95                 | 3500      | 74    | 5.6              | 17.5         |
      | 00000000-0000-0000-0000-000000000000 | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 15.0          | 98                 | 2500      | 79    | 7.0              | 14.9         |
    And the application will log the following messages:
      | level | message                                              |
      | INFO  | Beginning to parse file: testFile.txt                |
      | INFO  | File parsing completed for file: testFile.txt        |
      | INFO  | Session edc62dac-3b41-4b0e-8e2c-864c7c91fec4 updated |

  Scenario: A session that does not exist cannot be updated
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.9                         |
    And the following sessions exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | a1ce63a6-4ec4-41fa-b2f5-08a03f0d64ff | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 100                  | 16.5          | 95                 | 4500      | 74    | 5.6              | 14.7         |
      | 2da8c66c-5c5d-426d-a333-bf5ffca8705c | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the user has a valid authorization token
    When the file is uploaded for a session with the following data and session id 'b8fe160d-ead5-473d-927d-950ed37c2e50':
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'NOT_FOUND'
    And the following sessions will exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records will exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | a1ce63a6-4ec4-41fa-b2f5-08a03f0d64ff | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 100                  | 16.5          | 95                 | 4500      | 74    | 5.6              | 14.7         |
      | 2da8c66c-5c5d-426d-a333-bf5ffca8705c | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the application will log the following messages:
      | level | message                                                        |
      | ERROR | Session id b8fe160d-ead5-473d-927d-950ed37c2e50 does not exist |

  Scenario: A session cannot be updated with an invalid token
    Given a file with the following rows:
      | Device Time              | Longitude          | Latitude            | Altitude | Engine Coolant Temperature(°F) | Engine RPM(rpm) | Intake Air Temperature(°F) | Speed (OBD)(mph) | Throttle Position(Manifold)(%) | Turbo Boost & Vacuum Gauge(psi) | Air Fuel Ratio(Measured)(:1) |
      | 18-Sep-2022 14:15:47.968 | -86.14170333333335 | 42.406800000000004  | 188.4    | 95.9                           | 3500.35         | 123.8                      | 74.56            | 5.6                            | 16.5                            | 17.5                         |
      | 18-Sep-2022 14:15:48.962 | 86.14162999999999  | -42.406816666666664 | 188.0    | 98                             | 2500            | 130                        | 79               | 7                              | 15                              | 14.9                         |
    And the following sessions exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | a1ce63a6-4ec4-41fa-b2f5-08a03f0d64ff | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 100                  | 16.5          | 95                 | 4500      | 74    | 5.6              | 14.7         |
      | 2da8c66c-5c5d-426d-a333-bf5ffca8705c | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the user has an invalid authorization token
    When the file is uploaded for a session with the following data and session id 'edc62dac-3b41-4b0e-8e2c-864c7c91fec4':
      | trackId                              | carId                                | userFirstName | userLastName | userEmail     |
      | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | test          | tester       | test@test.com |
    Then the request response status is 'UNAUTHORIZED'
    And the following sessions will exist:
      | id                                   | userEmail     | userFirstName | userLastName | startTime                | endTime                  | trackId                              | carId                                |
      | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | test@test.com | test          | tester       | 2022-09-18T18:15:47.968Z | 2022-09-18T18:15:49.965Z | b3bb8336-c262-40e0-8bd0-c7f2bb091ff5 | 0058ca98-5c7c-4742-a93d-f29f8445baf2 |
    And the following records will exist:
      | id                                   | sessionId                            | timestamp                | longitude          | latitude            | altitude | intakeAirTemperature | boostPressure | coolantTemperature | engineRpm | speed | throttlePosition | airFuelRatio |
      | a1ce63a6-4ec4-41fa-b2f5-08a03f0d64ff | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:47.968Z | -86.14170333333335 | 42.406800000000004  | 188.4    | 100                  | 16.5          | 95                 | 4500      | 74    | 5.6              | 14.7         |
      | 2da8c66c-5c5d-426d-a333-bf5ffca8705c | edc62dac-3b41-4b0e-8e2c-864c7c91fec4 | 2022-09-18T18:15:48.962Z | 86.14162999999999  | -42.406816666666664 | 188.0    | 130                  | 9.0           | 98                 | 1500      | 79    | 7.0              | 15.8         |
    And the application will log the following messages:
      | level | message |
