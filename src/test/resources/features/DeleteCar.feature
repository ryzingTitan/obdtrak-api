Feature: Delete car

  Scenario: Delete an existing car
    Given the following cars exist:
      | id                                   | yearManufactured | make       | model    |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta    |
      | 2ae74a24-6a2b-4702-8917-649031a9c069 | 1999             | Chevrolet  | Corvette |
    And the user has a valid authorization token
    When the car with id '2ae74a24-6a2b-4702-8917-649031a9c069' is deleted
    Then the request response status is 'NO_CONTENT'
    And the following cars will exist:
      | id                                   | yearManufactured | make       | model |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta |
    And the application will log the following messages:
      | level | message                                                  |
      | INFO  | Deleted car with id 2ae74a24-6a2b-4702-8917-649031a9c069 |

  Scenario: Deleting a car that does not exist will not throw an error
    Given the following cars exist:
      | id                                   | yearManufactured | make       | model |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta |
    And the user has a valid authorization token
    When the car with id '2ae74a24-6a2b-4702-8917-649031a9c069' is deleted
    Then the request response status is 'NO_CONTENT'
    And the following cars will exist:
      | id                                   | yearManufactured | make       | model |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta |
    And the application will log the following messages:
      | level | message                                                  |
      | INFO  | Deleted car with id 2ae74a24-6a2b-4702-8917-649031a9c069 |

  Scenario: A car cannot be deleted with an invalid token
    Given the following cars exist:
      | id                                   | yearManufactured | make       | model    |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta    |
      | 2ae74a24-6a2b-4702-8917-649031a9c069 | 1999             | Chevrolet  | Corvette |
    And the user has an invalid authorization token
    When the car with id '2ae74a24-6a2b-4702-8917-649031a9c069' is deleted
    Then the request response status is 'UNAUTHORIZED'
    And the following cars will exist:
      | id                                   | yearManufactured | make       | model    |
      | 0058ca98-5c7c-4742-a93d-f29f8445baf2 | 2001             | Volkswagen | Jetta    |
      | 2ae74a24-6a2b-4702-8917-649031a9c069 | 1999             | Chevrolet  | Corvette |
    And the application will log the following messages:
      | level | message |