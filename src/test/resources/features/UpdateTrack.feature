Feature: Update track

  Scenario: Update an existing track
    Given the following tracks exist:
      | id                                   | name         | longitude | latitude |
      | e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a | Test Track 1 | -86.1374  | 42.4086  |
    And the user has a valid authorization token
    When the following track id 'e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a' is updated:
      | name         | longitude | latitude |
      | Test Track 1 | -89.1374  | 45.4086  |
    Then the request response status is 'OK'
    And the following tracks are returned:
      | name         | longitude | latitude |
      | Test Track 1 | -89.1374  | 45.4086  |
    And the following tracks will exist:
      | id                                   | name         | longitude | latitude |
      | e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a | Test Track 1 | -89.1374  | 45.4086  |
    And the application will log the following messages:
      | level | message                          |
      | INFO  | Updated track named Test Track 1 |

  Scenario: A track that does not exist cannot be updated
    Given the following tracks exist:
      | id                                   | name         | longitude | latitude |
      | e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a | Test Track 2 | -86.1374  | 42.4086  |
    And the user has a valid authorization token
    When the following track id 'ee424f27-73af-4c83-a645-018be76ec6e1' is updated:
      | name         | longitude | latitude |
      | Test Track 1 | -89.1374  | 45.4086  |
    Then the request response status is 'NOT_FOUND'
    And the following tracks will exist:
      | id                                   | name         | longitude | latitude |
      | e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a | Test Track 2 | -86.1374  | 42.4086  |
    And the application will log the following messages:
      | level | message                                   |
      | ERROR | A track named Test Track 1 does not exist |

  Scenario: A track cannot be updated with an invalid token
    Given the following tracks exist:
      | id                                   | name         | longitude | latitude |
      | e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a | Test Track 1 | -86.1374  | 42.4086  |
    And the user has an invalid authorization token
    When the following track id 'e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a' is updated:
      | name         | longitude | latitude |
      | Test Track 1 | -89.1374  | 45.4086  |
    Then the request response status is 'UNAUTHORIZED'
    And the following tracks will exist:
      | id                                   | name         | longitude | latitude |
      | e0e3dd05-561b-47a0-8e8d-ed9c2c47dc4a | Test Track 1 | -86.1374  | 42.4086  |
    And the application will log the following messages:
      | level | message |
