# Lease Application — Backend (Java / Spring Boot + PostgreSQL)

A Spring Boot 3 (Java 17) backend for managing a rental/lease business:
material inventory, customer orders, and per-material lending records.
Uses **PostgreSQL** with **Flyway** migrations, so the schema is versioned
and reproducible on any Postgres host.

---

## 1. Tech stack

- Java 17, Spring Boot 3.3 (Web, Data JPA, Validation, Actuator)
- PostgreSQL (any host — free tiers listed below)
- Flyway for schema migrations (`src/main/resources/db/migration`)
- Maven

---

## 2. Running locally

### Option A — Docker Compose Postgres (recommended for dev)

```bash
docker compose up -d          # starts Postgres on localhost:5432
mvn spring-boot:run           # uses the defaults in application.yml, which match docker-compose.yml
```

### Option B — your own Postgres

Set these environment variables (see `.env.example`), then run:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/lease_application
export DB_USERNAME=lease_user
export DB_PASSWORD=lease_password
mvn spring-boot:run
```

The app starts on `http://localhost:8080` (override with `PORT`). Flyway
creates the schema automatically on first startup — no manual SQL needed.

Health check: `GET /health`

### Running tests

```bash
mvn test
```

Tests run against an in-memory H2 database in Postgres-compatibility mode
(`src/test/resources/application-test.yml`), so no real Postgres is needed
to run the test suite.

---

## 3. Deploying with a free, hostable Postgres

Any of these give you a free Postgres instance reachable over the internet —
just plug the connection string into `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`:

| Provider | Free tier notes |
|---|---|
| **Neon** (neon.tech) | Serverless Postgres, generous free tier, scales to zero |
| **Supabase** (supabase.com) | Postgres + free tier, includes a web SQL editor |
| **Railway** (railway.app) | Free trial credits, one-click Postgres + app hosting |
| **Render** (render.com) | Free Postgres (90-day limit on the free plan) + free web service hosting |

General steps (same shape for all four):

1. Create a Postgres database on the provider — it gives you a host, port,
   database name, username, and password (often as one connection string).
2. Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` as environment variables on
   wherever you run the app (see below). Most providers require
   `?sslmode=require` on the JDBC URL — check their docs.
3. Deploy the app itself. Two easy options:
   - **Same provider, as a web service**: Railway and Render can both build
     directly from this repo's `Dockerfile` and host the app for free
     alongside the database.
   - **Any container host**: build the image (`docker build -t lease-app .`)
     and run it wherever you like, pointing the env vars at your hosted DB.
4. On first startup, Flyway applies `V1__init.sql` automatically — no
   manual schema setup required.

---

## 4. Data model

### Material (`materials`)
| Field | Type | Notes |
|---|---|---|
| id | UUID (PK) | auto-generated |
| materialName | text | unique |
| noOfStocksAvailable | integer | current available stock |
| totalStocks | integer | total owned |
| costPerDay | numeric | rental cost per unit per day |

### Order (`orders`)
| Field | Type | Notes |
|---|---|---|
| orderNumber | UUID (PK) | auto-generated |
| mobileNumber | text | customer's mobile number (indexed, not unique — one customer can have many orders) |
| createTimestamp | timestamptz | auto-set on creation |
| closingTimestamp | timestamptz | set when order is closed |
| isClosed | boolean | default false |
| approximateDateToReturn | timestamptz | customer's expected return date |

### OrderDetail (`order_details`)
| Field | Type | Notes |
|---|---|---|
| id | UUID (PK) | auto-generated |
| orderNumber | UUID (FK → orders) | |
| materialId | UUID (FK → materials) | |
| materialName | text | snapshot of the name at lend time |
| noOfMaterialRequired | integer | quantity lent |
| lentTimestamp | timestamptz | auto-set on creation |
| returnTimestamp | timestamptz | set when returned |
| cost | numeric | provisional at lend time, finalized on return |

---

## 5. Design decisions & suggestions (beyond what was asked)

Since payment wasn't in scope yet, these are the improvements made to the
original spec, plus ideas for later:

1. **`mobileNumber` is not the Order primary key.** A customer places many
   orders over time, so `orderNumber` stays the PK and `mobileNumber` is
   just indexed for fast lookups (`GET /api/orders?mobileNumber=...`).
2. **`OrderDetail` references `materialId` (FK), not just a name.** This
   keeps referential integrity and lets stock be safely decremented/
   incremented. `materialName` is still stored as a denormalized snapshot so
   historical records stay readable if a material is later renamed.
3. **Stock is enforced automatically and safely under concurrency.**
   Lending checks `noOfStocksAvailable` and rejects if insufficient;
   returning restocks it. Both paths use `SELECT ... FOR UPDATE`
   (`@Lock(PESSIMISTIC_WRITE)`) inside a transaction, so two simultaneous
   requests can't oversell the same stock.
4. **Cost is calculated in two stages**: a *provisional* cost at lend time
   (based on the order's `approximateDateToReturn`, or a 1-day minimum),
   and a *final* cost recalculated from the actual `lentTimestamp` →
   `returnTimestamp` span on return. Any partial day in progress counts as
   a full day — change `CostCalculator.java` if you'd rather bill hourly or
   prorate.
5. **An order can't be closed until every material in it has been
   returned** (`PATCH /api/orders/{orderNumber}/close`).
6. **Schema is Flyway-managed**, not auto-generated by Hibernate
   (`ddl-auto: validate`), so the schema is explicit, versioned, and safe to
   run against a shared hosted database.
7. **Ideas for next steps** (not built, since payment is out of scope):
   - A `Payment` table (orderNumber FK, amount, method, paidAt, status) —
     each `OrderDetail.cost` already gives a clean line-item total to bill.
   - A `Customer` table if you want profiles beyond a bare mobile number.
   - Overdue tracking: a scheduled job comparing `approximateDateToReturn`
     against a still-null `returnTimestamp` to flag late returns.
   - Auth (Spring Security + JWT) and role-based access before production.

---

## 6. API Reference

Base URL: `http://localhost:8080/api`

### Materials — `/materials`
| Method | Path | Body | Description |
|---|---|---|---|
| POST | `/materials` | `{ materialName, totalStocks, costPerDay, noOfStocksAvailable? }` | Create a material |
| GET | `/materials` | — | List all materials |
| GET | `/materials/{id}` | — | Get one material |
| PUT | `/materials/{id}` | any subset of fields | Update a material |
| DELETE | `/materials/{id}` | — | Delete a material |

### Orders — `/orders`
| Method | Path | Body | Description |
|---|---|---|---|
| POST | `/orders` | `{ mobileNumber, approximateDateToReturn? }` | Create an order |
| GET | `/orders` | query: `mobileNumber?`, `isClosed?` | List orders (with their details) |
| GET | `/orders/{orderNumber}` | — | Get one order (with details) |
| PUT | `/orders/{orderNumber}` | `{ mobileNumber?, approximateDateToReturn? }` | Edit an open order |
| PATCH | `/orders/{orderNumber}/close` | — | Close an order (all items must be returned) |
| DELETE | `/orders/{orderNumber}` | — | Delete an order (blocked if items still lent out) |

### Order Details — `/order-details`
| Method | Path | Body | Description |
|---|---|---|---|
| POST | `/order-details` | `{ orderNumber, materialId, noOfMaterialRequired, lentTimestamp? }` | Lend a material against an order (checks & reserves stock) |
| GET | `/order-details` | query: `orderNumber?` | List order line items |
| GET | `/order-details/{id}` | — | Get one line item |
| PUT | `/order-details/{id}` | `{ noOfMaterialRequired }` | Adjust quantity before return |
| PATCH | `/order-details/{id}/return` | `{ returnTimestamp? }` | Mark returned, finalize cost, restock |
| DELETE | `/order-details/{id}` | — | Remove a line item (releases reserved stock if not yet returned) |

All responses are wrapped as `{ success, data, count? }` on success or
`{ success: false, status, message, timestamp }` on error.

---

## 7. Example flow

```bash
# 1. Create a material
curl -X POST localhost:8080/api/materials -H "Content-Type: application/json" \
  -d '{"materialName":"Cement Mixer","totalStocks":5,"costPerDay":500}'

# 2. Create an order
curl -X POST localhost:8080/api/orders -H "Content-Type: application/json" \
  -d '{"mobileNumber":"9876543210","approximateDateToReturn":"2026-09-05T00:00:00Z"}'

# 3. Lend a material against that order
curl -X POST localhost:8080/api/order-details -H "Content-Type: application/json" \
  -d '{"orderNumber":"<orderNumber>","materialId":"<materialId>","noOfMaterialRequired":2}'

# 4. Return the material
curl -X PATCH localhost:8080/api/order-details/<orderDetailId>/return

# 5. Close the order
curl -X PATCH localhost:8080/api/orders/<orderNumber>/close
```

---

## 8. Project structure

```
lease-application-java/
├── pom.xml
├── Dockerfile
├── docker-compose.yml        # local Postgres for dev
├── .env.example
├── src/main/java/com/leaseapp/
│   ├── LeaseApplication.java
│   ├── entity/                # Material, Order, OrderDetail (JPA entities)
│   ├── repository/            # Spring Data JPA repositories
│   ├── dto/                   # request/response DTOs + ApiResponse wrapper
│   ├── service/                # business logic incl. stock & cost handling
│   ├── controller/             # REST controllers
│   ├── exception/              # ApiException + global handler
│   └── util/                   # CostCalculator, entity<->DTO Mapper
├── src/main/resources/
│   ├── application.yml         # reads DB config from env vars
│   └── db/migration/V1__init.sql
└── src/test/                   # H2-backed tests (context load + flow test)
```
