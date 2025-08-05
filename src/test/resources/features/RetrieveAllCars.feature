Feature: Retrieve all cars

  Scenario: Retrieve all cars
    Given the following cars exist:
      | yearManufactured | make       | model    |
      | 2001             | Volkswagen | Jetta    |
      | 1999             | Chevrolet  | Corvette |
    And the user has a valid authorization token
    When all cars are retrieved
    Then the request response status is 'OK'
    And the following cars are returned:
      | id | year | make       | model    |
      | 1  | 2001 | Volkswagen | Jetta    |
      | 2  | 1999 | Chevrolet  | Corvette |
    And the application will log the following messages:
      | level | message             |
      | INFO  | Retrieving all cars |

  Scenario: Cars cannot be retrieved with an invalid authorization token
    Given the following cars exist:
      | yearManufactured | make       | model    |
      | 2001             | Volkswagen | Jetta    |
      | 1999             | Chevrolet  | Corvette |
    And the user has an invalid authorization token
    When all tracks are retrieved
    Then the request response status is 'UNAUTHORIZED'
    And the following cars are returned:
      | id | year | make | model |
    And the application will log the following messages:
      | level | message |