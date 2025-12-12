# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

OBDTRAK API is a Spring Boot 4.0.0 application written in Kotlin that provides a RESTful API for storing and retrieving automotive telemetry data parsed from CSV log files created by the Torque mobile application. The API stores data in PostgreSQL and uses reactive programming with Spring WebFlux and R2DBC.

## Build System & Commands

This project uses Gradle with Kotlin DSL. All commands use the Gradle wrapper (`./gradlew` on Unix/Mac, `gradlew.bat` on Windows).

### Essential Commands

- **Build the project**: `./gradlew build`
- **Run tests**: `./gradlew test`
- **Run a single test class**: `./gradlew test --tests "FullyQualifiedClassName"`
- **Run a single test method**: `./gradlew test --tests "FullyQualifiedClassName.testMethodName"`
- **Run lint checks**: `./gradlew ktlintCheck`
- **Auto-fix lint issues**: `./gradlew ktlintFormat`
- **Run static analysis**: `./gradlew detekt`
- **Generate test coverage report**: `./gradlew jacocoTestReport`
- **Verify coverage meets minimum**: `./gradlew jacocoTestCoverageVerification`
- **Build Docker image**: `./gradlew bootBuildImage`
- **Check for dependency updates**: `./gradlew dependencyUpdates`

### Running Locally

1. Start PostgreSQL: `docker run --env=POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:16.3-alpine`
2. Set environment variables: `DB_HOST=localhost`, `DB_USER=postgres`, `DB_PASSWORD=password`
3. Run the application: `./gradlew bootRun` (uses Spring profile `local` by default)
4. Application runs on port 8080, actuator on port 8081

## Architecture

### Three-Layer Architecture

The codebase follows a clean three-layer architecture pattern:

1. **Presentation Layer** (`presentation/`)
   - Controllers: REST endpoints using Spring WebFlux reactive handlers
   - Configuration: Security (OAuth2/Auth0 JWT), OpenAPI/Swagger
   - All endpoints under `/api/**` require JWT authentication except health and docs

2. **Domain Layer** (`domain/`)
   - Services: Business logic (parsing CSV files, validating data, orchestrating repository calls)
   - DTOs: Domain models for API requests/responses
   - Exceptions: Domain-specific exceptions with descriptive messages
   - Four main domains: `cars`, `sessions`, `records`, `tracks`

3. **Data Layer** (`data/`)
   - Entities: Database models with R2DBC annotations
   - Repositories: R2DBC reactive repositories extending `CoroutineCrudRepository`
   - Database migrations managed by Liquibase (JDBC) in `src/main/resources/db/changelog/`

### Key Domain Concepts

- **Track**: Racing track or location with latitude/longitude coordinates
- **Car**: Vehicle with year, make, and model
- **Session**: A recording session linking a user, car, track, start time, and end time
- **Record**: Individual telemetry data point with timestamp, GPS coordinates, and vehicle metrics (RPM, speed, boost pressure, temperatures, etc.)

### CSV File Parsing

The `FileParsingService` parses Torque CSV files with Apache Commons CSV:
- Expected columns: "Device Time", "Longitude", "Latitude", "Altitude", vehicle metrics
- Timestamps parsed as "dd-MMM-yyyy HH:mm:ss.SSS" in America/New_York timezone
- Optional fields checked with `row.isMapped()` before parsing
- Invalid rows logged and skipped (not failing entire file)
- Records associated with session ID after session creation

### Security

- Auth0 OAuth2 resource server with JWT validation
- Issuer: `https://dev-7pr07becg7e5y37g.us.auth0.com/`
- All `/api/**` endpoints require authentication except OPTIONS requests
- Health endpoint `/actuator/health` and OpenAPI docs are public
- User identity extracted from JWT for filtering data by `userEmail`

### Database

- PostgreSQL 16+ with schema `obd_trak`
- Liquibase migrations in `src/main/resources/db/changelog/db.changelog-master.yaml`
- All tables use UUIDs as primary keys (auto-generated)
- R2DBC for reactive database access, JDBC only for Liquibase migrations
- H2 in-memory database for tests with R2DBC support

## Testing

### Test Structure

- **Unit tests**: Located in `src/test/kotlin/.../domain/` and `src/test/kotlin/.../presentation/`
  - Use MockK (Mockito-Kotlin) for mocking
  - Test individual services and controllers in isolation

- **Integration tests**: Cucumber BDD tests in `src/test/kotlin/.../cucumber/`
  - Feature files in `src/test/resources/features/`
  - Test entire API flows with real database (H2)
  - Mock OAuth2 server for authentication (`no.nav.security:mock-oauth2-server`)
  - Step definitions in `cucumber/controllers/`

### Coverage Requirements

- Minimum line coverage: 90% (enforced by `jacocoTestCoverageVerification`)
- Reports generated at `obdtrak-api/jacocoHtmlReport/`

## Code Quality

- **ktlint**: Kotlin style enforcement (use `./gradlew ktlintFormat` to auto-fix)
- **detekt**: Static analysis with HTML reports at `obdtrak-api/detektHtmlReport/detekt.html`
- Kotlin compiler strict mode: `-Xjsr305=strict`
- Java 21 required (language version configured in toolchain)

## Configuration Profiles

- `application.yml`: Default configuration (requires env vars: `DB_HOST`, `DB_USER`, `DB_PASSWORD`)
- `application-local.yml`: Local development overrides
- `application-test.yml`: Test profile with H2 database

## CI/CD

- **GitHub Actions CI** (`.github/workflows/ci.yml`): Runs `./gradlew build` on every push
- **Docker Build** (`.github/workflows/build.yml`): On push to `main`, builds Docker image with Spring Boot buildpacks and pushes to GitHub Container Registry (ghcr.io)
  - Version extracted from `build.gradle.kts`
  - Git tag automatically created
  - Uses `bootBuildImage` with Paketo buildpacks

## Dependencies of Note

- Spring Boot 4.0.0 with WebFlux (reactive web)
- Kotlin 2.2.20 with coroutines
- Gradle 9.2.1 with Kotlin DSL
- R2DBC for reactive database access (PostgreSQL driver)
- Liquibase for database migrations
- Apache Commons CSV 1.14.1 for parsing Torque files
- SpringDoc OpenAPI 3.0.0 for API documentation
- Spring Security OAuth2 Resource Server for Auth0 JWT validation
- Mockito-Kotlin 6.1.0 for testing
- Cucumber 7.33.0 for BDD integration tests
- Mock OAuth2 Server 3.0.1 for auth testing
