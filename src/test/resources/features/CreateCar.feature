Feature: Create a new car

  Scenario: Create a new car
    Given the user has a valid authorization token
    When the following car is created:
      | year | make       | model |
      | 2001 | Volkswagen | Jetta |
    Then the request response status is 'CREATED'
    And the following cars are returned:
      | id | year | make       | model |
      | 1  | 2001 | Volkswagen | Jetta |
    And the following cars will exist:
      | id | yearManufactured | make       | model |
      | 1  | 2001             | Volkswagen | Jetta |
    And the application will log the following messages:
      | level | message                           |
      | INFO  | Created car 2001 Volkswagen Jetta |

  Scenario: Do not create duplicate cars
    Given the following cars exist:
      | yearManufactured | make       | model |
      | 2001             | Volkswagen | Jetta |
    And the user has a valid authorization token
    When the following car is created:
      | year | make       | model |
      | 2001 | Volkswagen | Jetta |
    Then the request response status is 'CONFLICT'
    And the following cars will exist:
      | id | yearManufactured | make       | model |
      | 1  | 2001             | Volkswagen | Jetta |
    And the application will log the following messages:
      | level | message                              |
      | ERROR | 2001 Volkswagen Jetta already exists |

  Scenario: A new car cannot be created with an invalid token
    Given the user has an invalid authorization token
    When the following car is created:
      | year | make       | model |
      | 2001 | Volkswagen | Jetta |
    Then the request response status is 'UNAUTHORIZED'
    And the following cars will exist:
      | id | name | longitude | latitude |
    And the application will log the following messages:
      | level | message |