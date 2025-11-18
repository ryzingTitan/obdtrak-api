Feature: Delete track

  Scenario: Delete an existing track
    Given the following tracks exist:
      | id                                   | name         | longitude | latitude |
      | fa2d9e99-f6c2-4a90-9573-760e2e29fd6c | Test Track 1 | -86.1374  | 42.4086  |
      | 04d36f86-5f7c-42c5-a87a-7a9ad1418bf0 | Test Track 2 | -90.1374  | 45.4086  |
    And the user has a valid authorization token
    When the track with id '04d36f86-5f7c-42c5-a87a-7a9ad1418bf0' is deleted
    Then the request response status is 'OK'
    And the following tracks will exist:
      | id                                   | name         | longitude | latitude |
      | fa2d9e99-f6c2-4a90-9573-760e2e29fd6c | Test Track 1 | -86.1374  | 42.4086  |
    And the application will log the following messages:
      | level | message                                                    |
      | INFO  | Deleted track with id 04d36f86-5f7c-42c5-a87a-7a9ad1418bf0 |

  Scenario: Deleting a track that does not exist will not throw an error
    Given the following tracks exist:
      | name         | longitude | latitude |
      | Test Track 2 | -86.1374  | 42.4086  |
    And the user has a valid authorization token
    When the track with id 'fa2d9e99-f6c2-4a90-9573-760e2e29fd6c' is deleted
    Then the request response status is 'OK'
    And the following tracks will exist:
      | id                                   | name         | longitude | latitude |
      | 04d36f86-5f7c-42c5-a87a-7a9ad1418bf0 | Test Track 2 | -86.1374  | 42.4086  |
    And the application will log the following messages:
      | level | message                                                    |
      | INFO  | Deleted track with id fa2d9e99-f6c2-4a90-9573-760e2e29fd6c |

  Scenario: A track cannot be deleted with an invalid token
    Given the following tracks exist:
      | id                                   | name         | longitude | latitude |
      | fa2d9e99-f6c2-4a90-9573-760e2e29fd6c | Test Track 1 | -86.1374  | 42.4086  |
      | 04d36f86-5f7c-42c5-a87a-7a9ad1418bf0 | Test Track 2 | -90.1374  | 45.4086  |
    And the user has an invalid authorization token
    When the track with id 'fa2d9e99-f6c2-4a90-9573-760e2e29fd6c' is deleted
    Then the request response status is 'UNAUTHORIZED'
    And the following tracks will exist:
      | id                                   | name         | longitude | latitude |
      | fa2d9e99-f6c2-4a90-9573-760e2e29fd6c | Test Track 1 | -86.1374  | 42.4086  |
      | 04d36f86-5f7c-42c5-a87a-7a9ad1418bf0 | Test Track 2 | -90.1374  | 45.4086  |
    And the application will log the following messages:
      | level | message |