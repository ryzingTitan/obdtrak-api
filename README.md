# OBDTRAK API

A Spring Boot RESTful API for storing and retrieving automotive telemetry data parsed from CSV log files created by the [Torque](https://torque-bhp.com/) mobile application.

## Features

- Parse and store telemetry data from Torque CSV log files
- Manage tracks, cars, and recording sessions
- Retrieve detailed vehicle performance metrics (RPM, speed, temperatures, pressures, etc.)
- OAuth2 JWT authentication via Auth0
- Reactive programming with Spring WebFlux and R2DBC
- PostgreSQL database with Liquibase migrations
- OpenAPI/Swagger documentation
- Comprehensive test coverage with unit and BDD integration tests

## Technologies

- **Language**: Kotlin 2.0.21
- **Framework**: Spring Boot 3.5.7 (WebFlux for reactive web)
- **Database**: PostgreSQL 16+ with R2DBC (reactive), Liquibase (migrations)
- **Security**: Spring Security OAuth2 Resource Server with Auth0 JWT
- **Testing**: JUnit 5, Mockito-Kotlin, Cucumber (BDD), JaCoCo (coverage)
- **Code Quality**: ktlint (linting), detekt (static analysis)
- **Build**: Gradle 9.2+ with Kotlin DSL
- **Runtime**: Java 21

## Prerequisites

- Java 21
- Docker (for running PostgreSQL locally)
- Auth0 account (for JWT authentication)

## Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/ryzingTitan/obdtrak-api.git
   cd obdtrak-api
   ```

2. Start a local PostgreSQL database:
   ```bash
   docker run --env=POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:16.3-alpine
   ```

3. Set environment variables:
   ```bash
   export DB_HOST=localhost
   export DB_USER=postgres
   export DB_PASSWORD=password
   ```

4. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The API will be available at `http://localhost:8080` and the health check endpoint at `http://localhost:8081/actuator/health`.

## API Documentation

Once the application is running, access the interactive API documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

## Development

### Build the Project

```bash
./gradlew build
```

### Run Tests

```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.ryzingtitan.obdtrakapi.domain.sessions.services.SessionServiceTest"

# Run a specific test method
./gradlew test --tests "com.ryzingtitan.obdtrakapi.domain.sessions.services.SessionServiceTest.create"
```

### Code Quality

```bash
# Run ktlint checks
./gradlew ktlintCheck

# Auto-fix ktlint issues
./gradlew ktlintFormat

# Run detekt static analysis
./gradlew detekt
```

### Test Coverage

```bash
# Generate coverage report
./gradlew jacocoTestReport

# Verify coverage meets minimum threshold (90%)
./gradlew jacocoTestCoverageVerification
```

Reports are generated at `obdtrak-api/jacocoHtmlReport/index.html`.

### Build Docker Image

```bash
./gradlew bootBuildImage
```

This creates a Docker image using Spring Boot's buildpack integration.

## Project Structure

```
src/main/kotlin/com/ryzingtitan/obdtrakapi/
├── presentation/        # REST controllers and configuration
│   ├── controllers/     # API endpoints
│   └── configuration/   # Security, OpenAPI config
├── domain/             # Business logic and DTOs
│   ├── cars/
│   ├── sessions/       # File parsing service
│   ├── records/
│   └── tracks/
└── data/               # Database entities and repositories
    ├── cars/
    ├── sessions/
    ├── records/
    └── tracks/

src/main/resources/
├── db/changelog/       # Liquibase database migrations
└── application*.yml    # Spring configuration files

src/test/
├── kotlin/             # Unit and integration tests
│   └── cucumber/       # BDD step definitions
└── resources/features/ # Cucumber feature files
```

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

All the hard work done by the developers of the [Torque](https://torque-bhp.com/) application has enabled me to create this project.
