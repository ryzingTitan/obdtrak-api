Feature: Create a new track

  Scenario: Create a new track
    Given the user has a valid authorization token
    When the following track is created:
      | name         | longitude | latitude |
      | Test Track 1 | -86.1374  | 42.4086  |
    Then the request response status is 'CREATED'
    And the following tracks are returned:
      | id | name         | longitude | latitude |
      | 1  | Test Track 1 | -86.1374  | 42.4086  |
    And the following tracks will exist:
      | id | name         | longitude | latitude |
      | 1  | Test Track 1 | -86.1374  | 42.4086  |
    And the application will log the following messages:
      | level | message                          |
      | INFO  | Created track named Test Track 1 |

  Scenario: Do not create duplicate tracks
    Given the following tracks exist:
      | name         | longitude | latitude |
      | Test Track 2 | -90.1374  | 45.4086  |
    And the user has a valid authorization token
    When the following track is created:
      | name         | longitude | latitude |
      | Test Track 2 | -86.1374  | 42.4086  |
    Then the request response status is 'CONFLICT'
    And the following tracks will exist:
      | id | name         | longitude | latitude |
      | 1  | Test Track 2 | -90.1374  | 45.4086  |
    And the application will log the following messages:
      | level | message                                   |
      | ERROR | A track already exists named Test Track 2 |

  Scenario: A new track cannot be created with an invalid token
    Given the user has an invalid authorization token
    When the following track is created:
      | name         | longitude | latitude |
      | Test Track 1 | -86.1374  | 42.4086  |
    Then the request response status is 'UNAUTHORIZED'
    And the following tracks will exist:
      | id | name | longitude | latitude |
    And the application will log the following messages:
      | level | message |