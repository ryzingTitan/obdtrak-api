Feature: Update an existing car

  Scenario: Update an existing car
    Given the following cars exist:
      | yearManufactured | make       | model |
      | 2001             | Volkswagen | Jetta |
    And the user has a valid authorization token
    When the following car is updated:
      | id | year | make       | model |
      | 1  | 2001 | Volkswagen | Golf  |
    Then the request response status is 'OK'
    And the following cars are returned:
      | id | year | make       | model |
      | 1  | 2001 | Volkswagen | Golf  |
    And the following cars will exist:
      | id | yearManufactured | make       | model |
      | 1  | 2001             | Volkswagen | Golf  |
    And the application will log the following messages:
      | level | message                          |
      | INFO  | Updated car 2001 Volkswagen Golf |

  Scenario: A car that does not exist cannot be updated
    Given the following cars exist:
      | yearManufactured | make       | model |
      | 2001             | Volkswagen | Jetta |
    And the user has a valid authorization token
    When the following car is updated:
      | id | year | make       | model |
      | 2  | 2001 | Volkswagen | Golf  |
    Then the request response status is 'NOT_FOUND'
    And the following cars will exist:
      | id | yearManufactured | make       | model |
      | 1  | 2001             | Volkswagen | Jetta |
    And the application will log the following messages:
      | level | message                             |
      | ERROR | 2001 Volkswagen Golf does not exist |

  Scenario: A car cannot be updated with an invalid token
    Given the following cars exist:
      | yearManufactured | make       | model |
      | 2001             | Volkswagen | Jetta |
    And the user has an invalid authorization token
    When the following car is updated:
      | id | year | make       | model |
      | 1  | 2001 | Volkswagen | Golf  |
    Then the request response status is 'UNAUTHORIZED'
    And the following cars will exist:
      | id | yearManufactured | make       | model |
      | 1  | 2001             | Volkswagen | Jetta |
    And the application will log the following messages:
      | level | message |
