# MFineArt API

REST API backend for a personal online fine art gallery. It powers a public browsing experience (collections, paintings, events) and a staff **admin** area for content management. Designed to work with a separate **Angular** frontend.

## System overview

```
Angular (public + /admin)  --HTTP/JSON-->  Spring Boot API  -->  H2 / RDBMS
                                              |
                                              +--> AWS S3 (images)
```

| Layer | Technology |
|-------|------------|
| Runtime | Java 17, Spring Boot 3.3 |
| Persistence | Spring Data JPA, H2 (dev) |
| Security | Spring Security, JWT (stateless) |
| Media | AWS S3 SDK v2 |
| Mapping | MapStruct |

## Features

- **Collections** — group paintings; one thumbnail URL per collection
- **Paintings** — material, dimensions, price, availability, multiple images
- **Events** — exhibitions, news, interviews, etc., with multiple images
- **Content lifecycle** — `DRAFT` → `SCHEDULED` → `PUBLISHED` → `ARCHIVED`
- **JWT authentication** — roles `ADMIN` and `EDITOR` for back-office
- **Public API** — read-only access to published (or due scheduled) content
- **Scheduled publishing** — background job promotes `SCHEDULED` content when `publishAt` is reached

## Quick start

### Prerequisites

- JDK 17+
- Maven 3.9+
- AWS S3 bucket and credentials (for image uploads)

### Run locally

```bash
mvn spring-boot:run
```

Default URL: `http://localhost:8080`

H2 console (dev): `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:mfineart`

### Default admin account

Created on first startup when no users exist:

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `changeme` |

Change these in `application.properties` before any real deployment.

### Login

```http
POST /auth/login
Content-Type: application/json

{"username":"admin","password":"changeme"}
```

Use the returned `accessToken` as:

```http
Authorization: Bearer <accessToken>
```

## Security model

Rules are defined in `SecurityConfig.java` (evaluated top to bottom).

| Access | Who |
|--------|-----|
| `GET` on `/collections`, `/paintings`, `/events`, `/images` | Public (published content only in service layer) |
| `POST /auth/login` | Public |
| `POST`, `PUT` on gallery resources | `ADMIN` or `EDITOR` (JWT required) |
| `DELETE` on gallery resources | `ADMIN` only |
| `GET /auth/me` | Authenticated staff |

**Staff vs public on the same endpoints:** authenticated `ADMIN`/`EDITOR` users see all content statuses; anonymous users only see `PUBLISHED` or `SCHEDULED` items whose `publishAt` has passed.

## Content lifecycle

| Status | Meaning |
|--------|---------|
| `DRAFT` | Work in progress; staff only |
| `SCHEDULED` | Will publish at `publishAt` |
| `PUBLISHED` | Visible on the public site |
| `ARCHIVED` | Retired; staff only |

**Allowed transitions**

```
DRAFT     → SCHEDULED, PUBLISHED, ARCHIVED
SCHEDULED → DRAFT, PUBLISHED
PUBLISHED → ARCHIVED
ARCHIVED  → DRAFT
```

New content starts as `DRAFT`. Status changes use dedicated endpoints (not the regular edit APIs):

```http
PUT /collections/content-status
PUT /paintings/painting/content-status
PUT /events/event/content-status

{"id":1,"contentStatus":"SCHEDULED","publishAt":"2026-06-15T10:00:00"}
```

## API reference (summary)

### Auth — `/auth`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/auth/login` | — | Obtain JWT |
| GET | `/auth/me` | Staff | Current user and role |

### Collections — `/collections`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/collections` | — | List (public: published only) |
| GET | `/collections/{id}` | — | Detail |
| POST | `/collections/collection` | Staff | Create metadata |
| POST | `/collections/collection-images` | Staff | Create with thumbnail |
| PUT | `/collections` | Staff | Update metadata |
| PUT | `/collections/content-status` | Staff | Change lifecycle status |
| DELETE | `/collections?collectionId=` | Admin | Delete |

### Paintings — `/paintings`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/paintings` | — | List |
| GET | `/paintings/{id}` | — | Detail |
| GET | `/paintings/collection/{collectionId}` | — | By collection |
| POST | `/paintings/painting` | Staff | Create |
| POST | `/paintings/painting-images` | Staff | Create with images |
| PUT | `/paintings/painting` | Staff | Update |
| PUT | `/paintings/painting/content-status` | Staff | Lifecycle status |
| PUT | `/paintings/painting/status` | Staff | Sale availability + price |
| PUT | `/paintings/painting-in-collection` | Staff | Assign to collection |
| PUT | `/paintings/painting-out-collection` | Staff | Remove from collection |
| DELETE | `/paintings?paintingId=` | Admin | Delete |

### Events — `/events`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/events` | — | List |
| GET | `/events/{id}` | — | Detail |
| POST | `/events/event` | Staff | Create |
| POST | `/events/event-images` | Staff | Create with images |
| PUT | `/events/event` | Staff | Update |
| PUT | `/events/event/content-status` | Staff | Lifecycle status |
| DELETE | `/events?eventId=` | Admin | Delete |

### Images — `/images`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/images` | — | All images |
| GET | `/images/{id}` | — | By id |
| GET | `/images/by-prefix?prefix=` | — | Search by name prefix |
| GET | `/images/painting/{paintingId}` | — | Painting images (parent must be public) |
| GET | `/images/event/{eventId}` | — | Event images |
| POST | `/images` | Staff | Upload (bind via `paintingId` or `eventId`) |
| PUT | `/images/{imageId}/thumbnail?isThumbnail=` | Staff | Set thumbnail |

## Domain model

```
ArtCollection (PublishableEntity)
  ├── thumbnailUrl
  └── paintings[]

Painting (Artwork → PublishableEntity)
  ├── material, price, availability, dimensions
  ├── optional collection
  └── images[] (with isThumbnail)

Event (PublishableEntity)
  └── images[]

Image (BaseGalleryEntity)
  ├── url (S3)
  └── parent: Painting OR Event
```

**Enums:** `ContentStatus`, `Availability`, `Material`, `EventType`, `Role`

## Configuration

Key properties in `src/main/resources/application.properties`:

| Property | Purpose |
|----------|---------|
| `jwt.secret` | HS256 signing key (min 32 chars) |
| `jwt.expiration-ms` | Token lifetime (default 24h) |
| `app.cors.allowed-origins` | Angular origin (e.g. `http://localhost:4200`) |
| `app.content.scheduled-publish-cron` | Cron for auto-publishing |
| `app.security.default-admin.*` | Bootstrap admin user |
| `aws.s3.*` | S3 bucket and credentials |

## Angular integration

| Area | Approach |
|------|----------|
| Public site | Call `GET` endpoints without a token |
| `/admin` | Login → store JWT → `Authorization` header on mutating requests |
| Route guard | Redirect to login if no token; optional `GET /auth/me` for role |
| Delete actions | Show only when `role === 'ADMIN'` |
| Status badges | Read `contentStatus` and `publishAt` from DTOs (staff sees all) |

## Project structure

```
src/main/java/com/gallery/fineart/mfineart/
  controller/     REST endpoints
  service/        Business logic
  repository/     JPA repositories
  model/          JPA entities
  dto/            API contracts
  mapper/         MapStruct entity ↔ DTO
  security/       JWT, CORS, SecurityConfig
  service/content/  Lifecycle, scheduling, public visibility
  exception/      Error handling
```

## Build and test

```bash
mvn clean test          # unit / context tests
mvn clean package       # build JAR
```

## Documentation

- **This file** — overview and quick reference
- **[docs/MFineArt-Project-Documentation.md](docs/MFineArt-Project-Documentation.md)** — detailed technical documentation
- **[docs/MFineArt-Project-Documentation.docx](docs/MFineArt-Project-Documentation.docx)** — Word export of the detailed guide

To regenerate the Word document:

```bash
cd docs && python3 -m venv .venv && .venv/bin/pip install python-docx && .venv/bin/python generate_docx.py
```

## License

Personal project — add a license if you plan to open-source or distribute.
