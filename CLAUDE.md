# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

`commercialActivities` is a JHipster 9.2.0 monolith: Spring Boot (Java) backend + Angular 21 frontend, PostgreSQL database, JWT authentication, ehcache. Package name `com.commercial.activities`. The domain is a small business management app (companies, suppliers, products, stock arrivals, sales, debts, cash collection/disbursement).

Entities (defined in `.jhipster/*.json`, generated via JDL in `app_english_v1.jdl`): AppUser, Company, CompanySubscription, Permission, Supplier, Product, StockArrival, Sale, SaleLine, Debt, DebtPayment, CashCollection, CashDisbursement.

## Common commands

Use `./mvnw` and `./npmw` (wrappers that pin local Maven/Node/npm versions) rather than global `mvn`/`npm`.

Local development (two terminals):
```bash
./npmw run backend:start   # Spring Boot backend, port 8080
./npmw run start           # Angular dev server with HMR
```

Backend tests:
```bash
./mvnw verify                          # full backend test suite
./mvnw -Dtest=SaleResourceIT verify    # single test class
./mvnw -ntp checkstyle:check           # checkstyle (backend:nohttp:test script)
```

Frontend tests/lint:
```bash
./npmw test                                    # vitest unit tests (runs `pretest` = lint first)
./npmw test -- --watch                         # watch mode
npx vitest run src/main/webapp/app/entities/sale   # narrow to one path/file
./npmw run lint                                # eslint .
./npmw run lint:fix
```

Production build / packaging:
```bash
./mvnw -Pprod clean verify        # full prod build (backend + Angular prod bundle into the jar)
npm run java:jar:dev              # dev-profile jar without tests
npm run java:docker                # build app Docker image via Jib
```

Required infra services (Postgres, etc.) via Docker Compose:
```bash
docker compose -f src/main/docker/services.yml up -d
docker compose -f src/main/docker/postgresql.yml up --wait
```
Spring Boot Docker Compose integration is enabled by default (`spring.docker.compose.enabled`), so `./npmw run backend:start` can auto-start Postgres if Docker is running.

## Architecture

### Backend (`src/main/java/com/commercial/activities/`)
Standard JHipster layering — when adding backend logic, follow existing entity patterns rather than introducing new patterns:
- `domain/` — JPA entities (one class per entity, e.g. `Sale.java`, `Product.java`)
- `repository/` — Spring Data JPA repositories
- `service/`, `service/impl/`, `service/dto/`, `service/mapper/`, `service/criteria/` — service interfaces/impls, DTOs, MapStruct mappers, and QueryDSL/JHipster criteria classes for filterable list endpoints
- `web/rest/` — one `*Resource.java` REST controller per entity (e.g. `SaleResource.java`), plus `AccountResource`/`AuthenticateController` for auth and `web/rest/errors` for exception handling
- `security/`, `config/` — JWT auth (`SecurityJwtConfiguration`), Spring Security (`SecurityConfiguration`), caching (`CacheConfiguration`, ehcache), Liquibase (`LiquibaseConfiguration`)
- `aop/logging/` — logging aspect

Database schema changes go through Liquibase changelogs in `src/main/resources/config/liquibase/changelog/`, registered in `master.xml`. Each entity has an `added_entity_<Name>.xml` and, where there are relations, an `added_entity_constraints_<Name>.xml`. Follow the existing timestamp-prefixed naming convention for new changelogs; don't edit already-applied changelogs.

### Frontend (`src/main/webapp/app/`)
Two parallel trees exist for each entity's CRUD UI — **this is the most important structural thing to know before touching entity UI code**:

- **`entities/<entity>/`** — the original JHipster-generated CRUD UI (list, detail, update, delete, `*.model.ts`, `*.service.ts`, plus `.spec.ts` tests and `.test-samples.ts`). This is the canonical source of entity models and HTTP services, and is regenerated/extended by the JHipster entity sub-generator. It is mounted at the app root via `app/entities/entity.routes.ts` → `app.routes.ts`, and is what the navbar (`app/layouts/navbar/navbar.html`) currently links to (e.g. `/sale`, `/product`).
- **`modules/<entity>/`** — a hand-customized copy of the CRUD UI (list/detail/update/delete components only, no model/service/tests) created to rework the UI/display without fighting JHipster regeneration. Components here import the model and service directly from the corresponding `entities/<entity>/` (e.g. `modules/sale/list/sale.ts` imports `ISale` and `SaleService` from `app/entities/sale`) rather than duplicating them. This tree is mounted under the `/modules` prefix via `app/modules/modules.routes.ts` → `app.routes.ts`.

When asked to change how an entity list/detail/form looks or behaves for end users, check whether the customization should go in `modules/<entity>/` (UI/UX work) or `entities/<entity>/` (would be overwritten by JHipster regeneration, but is what's currently linked from navigation) — when in doubt, ask, since the two trees are in the middle of a migration and are not yet fully consistent with each other.

Other frontend structure:
- `core/` — auth, interceptors, request config
- `shared/` — reusable directives/pipes/services (alert, date, filter, language, pagination, sort, jhipster constants)
- `layouts/` — navbar, sidebar, footer, error pages
- `account/`, `admin/`, `login/`, `home/` — JHipster standard account management, admin console (health/metrics/logs/configuration), login, home page
- Routing is all lazy-loaded (`loadChildren`/`loadComponent`) at the top-level `app.routes.ts`

### i18n
Translation files live under `src/main/webapp/i18n/` for `fr` (native language) and `en`. `enableTranslation` is on — new UI strings need keys in both locale files, following the `commercialActivitiesApp.<entity>.*` key pattern used by existing entities.

## Notes
- `skipCommitHook: true` in `.yo-rc.json` — no Husky commit hooks are enforced automatically; `npm test`'s `pretest` script runs lint before tests.
- JDL source of truth for entity definitions is `app_english_v1.jdl`; regenerating entities from it will regenerate `entities/` and Liquibase changelogs but will not touch `modules/`.
