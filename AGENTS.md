# AGENTS.md — TrackStudio Development Guide

This document provides guidelines for AI agents working on the TrackStudio codebase.

## 1. Running the Application

### Development (Docker)
```bash
# Start all services (PostgreSQL, Liquibase migrator, TrackStudio/Tomcat)
docker compose up -d --build

# View application logs
docker compose logs -f trackstudio

# View migration logs
docker compose logs -f migrator

# Stop all services
docker compose down

# Stop and remove database (all data lost!)
docker compose down -v
```

### Local Development Access
- URL: http://localhost:8080
- Default credentials: `root` / `root`

## 2. Build Commands (just for reference, build docker container instead)

All commands use Gradle wrapper (`./gradlew`):

```bash
# Build project (compile + test)
./gradlew build

# Compile only
./gradlew compileJava

# Run all tests
./gradlew test

# Run single test class
./gradlew test --tests "com.trackstudio.kernel.manager.IndexManagerTest"

# Run single test method
./gradlew test --tests "com.trackstudio.kernel.manager.IndexManagerTest.whenHugeText"

# Run tests with fail-fast (stop on first failure)
./gradlew test --fail-fast

# Debug tests (listen on port 5005)
./gradlew test --debug-jvm

# Create WAR file
./gradlew war

# Clean build
./gradlew clean

# Generate Javadoc
./gradlew javadoc

# View dependencies
./gradlew dependencies

# Print version
./gradlew printVersion
```

## 3. Code Style Guidelines

### 3.1 Architecture (from docs/architecture.md)

**Layer Structure:**
- Web/UI: `com.trackstudio.action`, JSP/Tiles, Servlets
- Application: `AdapterManager`, `Secured*` adapters, `SessionContext`
- Kernel: `KernelManager` and managers (`TaskManager`, `UserManager`, etc.)
- Caches: `TaskRelatedManager`, `UserRelatedManager`, `CategoryCacheManager`
- Persistence: Hibernate models (`.hbm.xml`), Liquibase

**Technology Stack:**
- Java 21, Tomcat 9+, PostgreSQL 17
- Hibernate 5.6, Lucene 7.6
- Logging: SLF4J + Logback (migrated from Log4j 1.2)

### 3.2 Thread Safety Rules

**From architecture.md Section 8:**

- Use only Lock API (`LockManager`, `ReadWriteLock`) in kernel code
- Do NOT mix locks and `synchronized` in the same method
- Lock entire public operations or extract to separate methods
- Always pair `acquireConnection` / `releaseConnection` in `try/finally`

**State Management:**
- Isolate mutable state in dedicated classes
- Use `Atomic*` / `Concurrent*` / `CopyOnWrite*` and explicit locks
- Wrap shared state access with read/write locks

**Export Rules:**
- Return copies or read-only views externally
- Never expose internal mutable collections directly
- Use `getAcl()` / `getReadOnlyAcl()` pattern for ACL

**Deadlock Prevention:**
- Never upgrade locks (read -> write in same thread)
- Maintain consistent lock order across entities
- Document any new lock scenarios

### 3.3 Naming Conventions

- Classes: `PascalCase` (e.g., `TaskView`, `AdapterManager`)
- Methods: `camelCase` (e.g., `getTask()`, `isCategoryViewable()`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `PATH_DELIMITER`)
- Packages: lowercase (e.g., `com.trackstudio.kernel.manager`)
- Database tables: `gr_*` prefix (e.g., `gr_task`, `gr_user`)

### 3.4 Logging

- Use SLF4J API: `org.slf4j.Logger`, `org.slf4j.LoggerFactory`
- Legacy commons-logging still exists but avoid adding new usage
- Log levels: ERROR for exceptions, DEBUG for detailed flow

### 3.5 Imports

```java
// Standard order (no blank lines between groups):
import java packages first, then javax, then org.apache, then com.trackstudio

import java.util.Collection;
import java.util.Iterator;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;

import net.jcip.annotations.ThreadSafe;
```

### 3.6 Error Handling

- Custom base exception: `GranException` extends `Exception`
- User-visible errors: `UserException` extends `GranException`
- Always log errors with context: `log.error("Action failed", e)`
- Use `try/finally` for resource cleanup

### 3.7 Annotations

- Use `@ThreadSafe` from `jcip.annotations` for thread-safe classes
- Use `@Override` for overridden methods
- Use `@Deprecated` with migration notes

### 3.8 Database & Hibernate

- Mapping files: `.hbm.xml` in same package as model
- Use Liquibase for schema changes (see `liquibase/` directory)
- Never commit schema changes without proper migration

### 3.9 REST API

- Stateful REST: requires login first, then use `sessionId`
- Base path: `/rest/*`
- Endpoints: `/rest/auth`, `/rest/task`, `/rest/user`
- CORS enabled for all `/rest/*` requests

### 3.10 UI Code (JSP/CSS)

- CSS organization (per docs/UI_MODERNIZATION_PLAN.md):
  - `style_tokens.css`: CSS variables, reset, typography
  - `style_components.css`: Component styles
  - `style_legacy.css`: Legacy styles pending migration
- Use CSS custom properties for theming
- Minimize JSP changes (add class/style attributes only)

## 4. Key Files and Locations

| Purpose | Location |
|---------|----------|
| Build config | `build.gradle.kts` |
| Docker config | `docker-compose.yml`, `Dockerfile` |
| DB migrations | `liquibase/` |
| Architecture | `docs/architecture.md` |
| UI plan | `docs/UI_MODERNIZATION_PLAN.md` |
| Main Java | `src/main/java/com/trackstudio/` |
| Tests | `src/test/java/com/trackstudio/` |
| Web resources | `src/main/webapp/` |

## 5. Important Constraints

**From architecture.md Section 14:**

Changes requiring architecture review:
- Lock order modifications
- ACL model changes
- Cache structure/invalidation changes
- Startup process and configuration
- REST contract modifications

When making such changes, update this document and architecture.md accordingly.

## 6. Testing Notes

- Framework: JUnit 4
- Tests are in `src/test/java/`
- Some tests may be `@Ignore`d (e.g., resource-intensive tests)
- Tests require UTF-8: configured via `systemProperty("file.encoding", "UTF-8")`

## 7. Configuration

- Config priority: `TS_CONFIG` env > `-Dtrackstudio.Home` > `/WEB-INF`
- Default configs: `trackstudio.default.properties`
- Override configs: `trackstudio.properties`
- See architecture.md Section 3 for full config details
