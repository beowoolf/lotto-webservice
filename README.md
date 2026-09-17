# lotto-webservice

[![Java](https://img.shields.io/badge/Java-25-orange)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.16-6DB33F)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8.7-C71A36)](https://maven.apache.org/)
[![Version](https://img.shields.io/badge/version-0.0.1-blue)](pom.xml)

Lotto RESTful web service (`pl.gry:lotto:0.0.1`) — a lottery backend that accepts player tickets, fetches winning numbers from an external random-number API, evaluates draws on a schedule, and lets players look up results by ticket id.

## Table of contents

- [Project description](#project-description)
- [Tech stack](#tech-stack)
- [Getting started locally](#getting-started-locally)
- [Available scripts](#available-scripts)
- [Project scope](#project-scope)
- [Project status](#project-status)
- [License](#license)

## Project description

This service is a **modular monolith** with a **hexagonal (ports and adapters)** layout. Domain logic lives in facades under `pl.gry.lotto.domain`; HTTP, MongoDB, schedulers, and the random-number HTTP client live under `pl.gry.lotto.infrastructure`.

Typical player flow:

1. Submit six distinct integers via `POST /inputNumbers`.
2. Receive a ticket with a UUID hash and the next Saturday 12:00 draw date.
3. On draw day, a scheduler pulls winning numbers from an external HTTP service and another scheduler scores tickets.
4. Look up the outcome with `GET /results/{id}` (ticket hash). A player wins with **at least three** matching numbers.

Packaging is a **WAR** (`ServletInitializer` is included) that still runs as an executable Spring Boot application (`java -jar`).

Interactive API docs (SpringDoc OpenAPI 1.7.0):

- Swagger UI: `http://localhost:8000/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8000/v3/api-docs`

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Java 25 (`maven.compiler.source` / `target` / `java.version`) |
| Runtime / framework | Spring Boot **3.4.16** (parent POM), Spring Web, Validation, Scheduling |
| Persistence | Spring Data MongoDB |
| HTTP client | Spring `RestTemplate` against `/random/?min=&max=&count=` |
| API documentation | springdoc-openapi-ui 1.7.0 |
| Build | Maven Wrapper **3.8.7** (`./mvnw` / `mvnw.cmd`) |
| Packaging | WAR (`lotto` / `0.0.1`) |
| Utilities | Lombok 1.18.34 |
| Tests | JUnit (Spring Boot starter), AssertJ 3.23.1, Mockito (via starter), MockMvc, WireMock 2.35.1, Awaitility 4.2.0, Testcontainers 1.20.0 (MongoDB) |
| Containers | Docker multi-stage build, Docker Compose (MongoDB 4.0.10, mongo-express, app) |

Architecture modules:

| Module | Responsibility |
| --- | --- |
| `numberreceiver` | Validate and persist tickets, compute next draw date, generate ticket hashes |
| `numbergenerator` | Fetch and validate six winning numbers for the next draw |
| `resultchecker` | Compare tickets to winning numbers and persist player results |
| `resultannouncer` | Public result lookup with caching and draw-time gating |

## Getting started locally

### Prerequisites

- **JDK 25** (matches `pom.xml`; the Maven compiler will fail on older JDKs)
- **Maven 3.8.7+** or the included wrapper (`mvnw` / `mvnw.cmd`)
- **Docker** and **Docker Compose** if you use the provided MongoDB stack
- Outbound HTTP access to the random-number service configured in `application.properties`

> **Note:** `Dockerfile` still builds with `eclipse-temurin:21`. Local Maven builds follow **Java 25** from `pom.xml`. Align the image base tags if you need a Java 25 container.

### Configuration

Default settings are in [`src/main/resources/application.properties`](src/main/resources/application.properties). The HTTP server listens on **port 8000**.

| Property | Default (main profile) | Purpose |
| --- | --- | --- |
| `server.port` | `8000` | HTTP port |
| `spring.data.mongodb.uri` | `mongodb://lottoWebUser:lottoWebPassword@localhost:27017/lottoWebDataBase` | MongoDB connection (overridable via `MONGO_*` env vars) |
| `lotto.number-generator.lotteryRunOccurrence` | `0 0 12 * * 6` | Winning-number job: Saturday 12:00 |
| `lotto.result-checker.lotteryRunOccurrence` | `0 55 11 * * 6` | Result-checker job: Saturday 11:55 |
| `lotto.number-generator.facade.count` | `25` | How many integers to request from the random API (six distinct values are kept) |
| `lotto.number-generator.facade.lowerBand` / `upperBand` | `1` / `99` | Requested number range |
| `lotto.number-generator.http.client.config.uri` | `https://api.mojezapiski.pl` | Random-number host |
| `lotto.number-generator.http.client.config.port` | `443` | Random-number port |
| `lotto.number-generator.http.client.config.connectionTimeout` / `readTimeout` | `5000` | HTTP client timeouts (ms) |

[`application-local.properties`](src/main/resources/application-local.properties) speeds up schedulers (`*/5` and `*/7` seconds) and points MongoDB at `localhost` with `authSource=admin`. Activate it with `-Dspring-boot.run.profiles=local`.

CORS allows `http://localhost` with GET, POST, PUT, DELETE, and OPTIONS.

### MongoDB with Docker Compose

From the repository root (starts MongoDB, mongo-express, and optionally the app):

```bash
docker compose up -d mongo
```

[`init-mongo.js`](init-mongo.js) creates user `lottoWebUser` / `lottoWebPassword` with `readWrite` on `lottoWebDataBase`. Root credentials in Compose are `root` / `toor`. mongo-express is published on **8081**.

To run the full stack (MongoDB + app image):

```bash
docker compose up -d
```

The Compose service maps **8000:8000**. Default app environment:

- `MONGO_USER=lottoWebUser`
- `MONGO_PASSWORD=lottoWebPassword`
- `MONGO_HOST=mongo`
- `MONGO_PORT=27017`
- `MONGO_DB_NAME=lottoWebDataBase`

### Run the application (Maven)

Linux / macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

Faster local schedules:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Run from a built WAR

```bash
./mvnw clean package -DskipTests
java -jar target/lotto-0.0.1.war
```

### Docker image (standalone)

```bash
docker build -f Dockerfile -t lotto-webservice .
docker run -d -p 8000:8000 lotto-webservice
```

The image expects MongoDB to be reachable using the `MONGO_*` variables (see Compose). The container process is `java -jar app.war`.

### Smoke-check the API

Submit six numbers in **1–99**:

```bash
curl -s -X POST http://localhost:8000/inputNumbers \
  -H "Content-Type: application/json" \
  -d "{\"inputNumbers\":[1,2,3,4,5,6]}"
```

A successful body includes a ticket (`hash`, `numbers`, `drawDate`) and message `SUCCESS`. Invalid payloads (missing/empty `inputNumbers`) return **400** with validation messages.

Look up a ticket after results exist:

```bash
curl -s http://localhost:8000/results/<ticket-hash>
```

## Available scripts

There is no `package.json`; use Maven Wrapper and Docker.

| Command | Description |
| --- | --- |
| `./mvnw spring-boot:run` | Start the app on port 8000 |
| `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` | Start with frequent cron jobs and local Mongo URI |
| `./mvnw test` | Unit + integration tests (Testcontainers MongoDB, WireMock, MockMvc) |
| `./mvnw clean package` | Compile, test, and build `target/lotto-0.0.1.war` |
| `./mvnw clean package -DskipTests` | WAR only (used by the Docker build stage) |
| `docker build -f Dockerfile -t lotto-webservice .` | Multi-stage image (`./mvnw clean package -DskipTests`, then JRE) |
| `docker run -d -p 8000:8000 lotto-webservice` | Run the image (MongoDB must be reachable) |
| `docker compose up -d` | MongoDB 4.0.10, mongo-express (:8081), and the app (:8000) |
| `docker compose up -d mongo` | Database only for a local Maven process |

On Windows, replace `./mvnw` with `mvnw.cmd`.

## Project scope

**In scope**

- REST API for ticket submission and result lookup
- Ticket rules: exactly **six** distinct integers from **1** to **99**
- Next draw: **Saturday 12:00** (same Saturday if the request is before noon)
- Ticket id: UUID string from `HashGenerator`
- Winning numbers: HTTP GET `{uri}:{port}/random/?min={lower}&max={upper}&count={count}`, then six distinct values; validated in range **0–99**
- Scheduled generation of winning numbers and scheduled winner calculation
- Win rule: **≥ 3** hits
- Result announcer messages: win, lose, already checked, ticket missing, or “results are being calculated” if the draw time has not passed
- Bean Validation on `inputNumbers` (`@NotNull`, `@NotEmpty`)
- MongoDB persistence for tickets, winning numbers, players, and cached result responses
- Integration coverage of the happy path (`UserPlayedLottoAndWonIntegrationTest`) and API validation failures

**REST surface**

| Method | Path | Body / params | Success |
| --- | --- | --- | --- |
| `POST` | `/inputNumbers` | `{ "inputNumbers": [n1,…,n6] }` | `200` + ticket + `SUCCESS` |
| `GET` | `/results/{id}` | Path `id` = ticket hash | `200` + result DTO + announcer message |

**Out of scope (current codebase)**

- Authentication / authorization
- Payments, prizes, or jackpot accounting
- A first-party web UI (CORS is configured for `http://localhost` only)
- A project `LICENSE` file or GitHub Actions workflows
- Multi-service decomposition (this is a single Spring Boot WAR)

## Project status

- Maven version: **0.0.1** (early / educational lottery backend).
- Actively maintained at the dependency level (recent commits bump Spring Boot **3.4.16** and Java **21 → 25**).
- Default branch: `main` (`https://github.com/beowoolf/lotto-webservice.git`).
- No CI configuration is present under `.github/`.
- Known drift: README historically listed **Java 21** and Docker port **8080**; the POM now requires **Java 25** and the app/Compose publish **8000**.

## License

No license file is included in this repository. Usage rights are unspecified until the maintainers add a license (for example SPDX identifiers such as Apache-2.0 or MIT). The Maven Wrapper files are licensed by the Apache Software Foundation under the Apache License 2.0.
