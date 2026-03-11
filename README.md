# NDC‑RxCUI Utility

NDC‑RxCUI Utility is a Spring Boot–based batch application for processing drug information using NDC codes, RxCUI identifiers, and RxClass hierarchies.

On startup, the application:

- Fetches drug class (RxClass) information from external APIs.
- Processes NDC ↔ RxCUI mappings and stores RxCUI metadata.
- Builds and persists a drug hierarchy view for downstream use (analytics, clinical rules, etc.).

The application is designed to run as a one‑shot utility: it executes the full pipeline and then shuts down.

---

## Features

- **RxClass ingestion**: Fetches and stores drug class metadata (`RxClassInfoBean`) via `RxClassService` and `IRxClassInfoDAO`.
- **NDC ↔ RxCUI mapping**: Reads NDC to RxCUI mappings, processes them in batches (`INDCRxCUIMappingDAO`), and stores detailed RxCUI info (`RxCUIInfoBean`) via `IRxCUIInfoDAO`.
- **Drug hierarchy load**: Inserts hierarchical drug information via `HierarchyDrugInfoService` and `IHierarchyDrugInfoDAO`.
- **Configurable REST endpoints (optional)**:
  - `GET /api/ndcutility` – run the full utility via HTTP.
  - `GET /api/delete?rxcui={rxcui}` – delete data associated with a specific RxCUI.
  - `GET /api/check` – simple health check endpoint.

> Note: By default, the main batch flow is triggered from `NDCCodesUtilityApplication` as a `CommandLineRunner`. The REST controller is provided as an alternative integration path and may be disabled in production.

---

## Tech stack

- **Language**: Java (Spring Boot)
- **Framework**: Spring Boot (`@SpringBootApplication`, `CommandLineRunner`)
- **HTTP client**: `RestTemplate`
- **JSON**: Jackson `ObjectMapper` (configured to ignore nulls)
- **Logging**: SLF4J with Lombok `@Slf4j`
- **Build tool**: Maven or Gradle (depending on your project setup)

---

## Getting started

### Prerequisites

- Java 17+ (adjust if your project uses a different version)
- Maven or Gradle installed
- Access to:
  - Database configured in `application.properties` / `application.yml`
  - External RxNorm / RxClass APIs (e.g., RxClass GET_CLASS_TREE, RxCUI lookup endpoints)

### Clone and build

```bash
git clone <your-repo-url>
cd NDC-RxCUI-Utility

# If using Maven
mvn clean package

# Or, if using Gradle
./gradlew build
