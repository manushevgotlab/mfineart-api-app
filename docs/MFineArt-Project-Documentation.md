# MFineArt — Project Documentation

**Version:** 0.0.1-SNAPSHOT  
**Last updated:** June 2026  
**Component:** REST API backend (`mfineart-api-app`)

---

## 1. Introduction

### 1.1 Purpose

MFineArt is a backend application for a personal online fine art gallery. It exposes a REST API consumed by an **Angular** single-page application that serves two audiences:

1. **Public visitors** — browse collections, view paintings, read news and events.
2. **Gallery staff** — manage content through an `/admin` area (create, schedule, publish, archive).

The API owns business rules, authentication, content lifecycle, relational data, and integration with **AWS S3** for image storage.

### 1.2 Technology stack

| Concern | Choice |
|---------|--------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.2 |
| Web | Spring Web (REST) |
| Persistence | Spring Data JPA, Hibernate 6 |
| Database (dev) | H2 in-memory |
| Security | Spring Security, JWT (jjwt 0.12) |
| DTO mapping | MapStruct 1.6 |
| Object storage | AWS SDK for Java v2 (S3) |
| Build | Maven |

### 1.3 Repository layout

```
mfineart-api-app/
├── src/main/java/com/gallery/fineart/mfineart/
│   ├── controller/          REST layer
│   ├── service/             Business services
│   │   ├── collection/
│   │   ├── painting/
│   │   ├── event/
│   │   ├── image/
│   │   ├── auth/
│   │   ├── content/         Lifecycle & scheduling
│   │   └── s3/
│   ├── repository/          JPA repositories
│   ├── model/               Entities
│   ├── dto/                 Request/response objects
│   ├── mapper/              MapStruct mappers
│   ├── security/            JWT filter, SecurityConfig, CORS
│   ├── enumeration/         Domain enums
│   ├── exception/           Errors and @ControllerAdvice
│   └── config/              Bootstrap (default admin)
├── src/main/resources/
│   ├── application.properties
│   └── dbscripts/create.sql
├── docs/                    Extended documentation
└── pom.xml
```

---

## 2. System architecture

### 2.1 High-level diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Angular Frontend                          │
│  ┌──────────────────┐    ┌──────────────────────────────┐ │
│  │ Public routes    │    │ /admin routes                 │ │
│  │ (no auth)        │    │ JWT + role-based UI           │ │
│  └────────┬─────────┘    └──────────────┬───────────────┘ │
└───────────┼─────────────────────────────┼───────────────────┘
            │         HTTP/JSON           │
            ▼                             ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot API (mfineart)                      │
│  Controllers → Services → Repositories → Database            │
│              ↘ S3Service → AWS S3                            │
│              ↘ ScheduledPublishingService (cron)           │
└─────────────────────────────────────────────────────────────┘
            │                             │
            ▼                             ▼
     ┌─────────────┐              ┌─────────────┐
     │  H2 / RDBMS │              │  AWS S3     │
     └─────────────┘              └─────────────┘
```

### 2.2 Design principles

- **Thin controllers** — validate input, delegate to services, return DTOs.
- **DTO boundary** — API contracts are DTOs; entities are not exposed directly (with minor legacy exceptions for nested `Image` in some DTOs).
- **Stateless security** — JWT tokens; no server-side sessions.
- **Single API surface** — public and admin use the same endpoints; visibility differs by authentication and content status.
- **Lifecycle separation** — metadata edits and status transitions use different endpoints.

---

## 3. Domain model

### 3.1 Entity hierarchy

```
BaseGalleryEntity
  ├── id, name, date
  ├── Image (attachments only — no content status)
  └── PublishableEntity
        ├── contentStatus (default DRAFT)
        ├── publishAt (nullable LocalDateTime)
        ├── ArtCollection
        ├── Event
        └── Artwork
              └── Painting
```

### 3.2 ArtCollection

Represents a curated group of paintings (e.g. a series or exhibition grouping).

| Field | Type | Notes |
|-------|------|-------|
| id | Long | Primary key |
| name | String | Unique |
| description | String | Optional |
| date | LocalDate | Display/metadata date |
| thumbnailUrl | String | Single S3 URL (not an Image row) |
| contentStatus | ContentStatus | Lifecycle |
| publishAt | LocalDateTime | Required when SCHEDULED |
| paintings | Set\<Painting\> | One-to-many |

### 3.3 Painting

Individual artwork with commercial and physical attributes.

| Field | Type | Notes |
|-------|------|-------|
| material | Material | OIL, ACRYLIC, PENCIL, MIXED_TECHNIQUE |
| description | String | |
| price | Double | Required when availability is AVAILABLE |
| availability | Availability | AVAILABLE, RESERVED, SOLD, PART_OF_COLLECTION |
| inCollection | Boolean | Redundant with collection FK (legacy) |
| artCollection | ArtCollection | Optional parent collection |
| width, height | Double | Dimensions |
| images | Set\<Image\> | One must be thumbnail |
| contentStatus, publishAt | | From PublishableEntity |

### 3.4 Event

News and gallery activity (exhibitions, interviews, articles, etc.).

| Field | Type | Notes |
|-------|------|-------|
| eventType | EventType | EXHIBITION, INTERVIEW, CHARITY, … |
| description | String | |
| images | Set\<Image\> | One thumbnail required on multi-image create |
| contentStatus, publishAt | | From PublishableEntity |

### 3.5 Image

Binary files stored in S3; metadata in the database.

| Field | Type | Notes |
|-------|------|-------|
| url | String | Public S3 URL |
| isThumbnail | Boolean | One per painting/event image set |
| painting | Painting | XOR parent |
| event | Event | XOR parent |

Database constraint: exactly one of `painting_id` or `event_id` must be set.

**Note:** Collection thumbnails are stored as `thumbnailUrl` on `ArtCollection`, not as `Image` rows.

### 3.6 AppUser

Back-office accounts.

| Field | Type | Notes |
|-------|------|-------|
| username | String | Unique |
| password | String | BCrypt hash |
| role | Role | ADMIN or EDITOR |
| enabled | boolean | |

---

## 4. Content lifecycle

### 4.1 Status values

| Status | Public visibility | Description |
|--------|-------------------|-------------|
| DRAFT | No | Default for new content; staff only |
| SCHEDULED | Yes, when `publishAt <= now` | Queued for future (or past) publication |
| PUBLISHED | Yes | Live on the public site |
| ARCHIVED | No | Retired content; staff only |

### 4.2 Transition matrix

| From \\ To | DRAFT | SCHEDULED | PUBLISHED | ARCHIVED |
|------------|-------|-----------|-----------|----------|
| DRAFT | — | ✓ | ✓ | ✓ |
| SCHEDULED | ✓ | — | ✓ | ✗ |
| PUBLISHED | ✗ | ✗ | — | ✓ |
| ARCHIVED | ✓ | ✗ | ✗ | — |

Implemented in `ContentLifecycleService`.

### 4.3 Rules on transition

- **→ SCHEDULED:** `publishAt` is required.
- **→ PUBLISHED:** if `publishAt` is null, it is set to the current time.
- **→ DRAFT:** `publishAt` is cleared.

### 4.4 Scheduled publishing job

`ScheduledPublishingService` runs on a cron expression (default: every minute). It finds all entities with `contentStatus = SCHEDULED` and `publishAt <= now`, and sets them to `PUBLISHED`.

Configuration:

```properties
app.content.scheduled-publish-cron=0 * * * * *
```

### 4.5 Public visibility logic

`PublicContentAccessService.isPubliclyVisible()` returns true when:

- `contentStatus == PUBLISHED`, or
- `contentStatus == SCHEDULED` and `publishAt` is not in the future.

Anonymous callers receive **404** (not 403) for non-visible content to avoid leaking existence of drafts.

Staff (`ADMIN` or `EDITOR` with valid JWT) bypass filtering on read endpoints.

### 4.6 Status API

Status cannot be changed through regular `PUT` edit endpoints (mappers ignore `contentStatus` on create/update). Use:

```
PUT /collections/content-status
PUT /paintings/painting/content-status
PUT /events/event/content-status
```

Request body:

```json
{
  "id": 1,
  "contentStatus": "SCHEDULED",
  "publishAt": "2026-06-15T10:00:00"
}
```

---

## 5. Security and authentication

### 5.1 Authentication flow

1. Client sends `POST /auth/login` with username and password.
2. Server validates credentials (BCrypt), returns JWT with embedded role claim.
3. Client sends `Authorization: Bearer <token>` on subsequent requests.
4. `JwtAuthenticationFilter` validates token and sets Spring Security context.

Token lifetime default: 24 hours (`jwt.expiration-ms=86400000`).

### 5.2 Roles

| Role | Capabilities |
|------|--------------|
| ADMIN | Full CRUD including DELETE |
| EDITOR | Create and update (POST, PUT); no DELETE |

Unauthenticated users are treated as public visitors.

### 5.3 Authorization rules (SecurityConfig)

| Pattern | Access |
|---------|--------|
| `OPTIONS /**` | Permit all |
| `POST /auth/login` | Permit all |
| `GET /collections/**`, `/paintings/**`, `/events/**`, `/images/**` | Permit all (filtering in services) |
| `DELETE` on gallery paths | ADMIN only |
| `/auth/**` (except login) | Authenticated |
| All other requests | ADMIN or EDITOR |

### 5.4 CORS

Configured for Angular dev server by default:

```properties
app.cors.allowed-origins=http://localhost:4200
```

Allowed headers: `Authorization`, `Content-Type`.

### 5.5 Default bootstrap user

On first startup, if the user table is empty:

```properties
app.security.default-admin.username=admin
app.security.default-admin.password=changeme
```

Disable with `app.security.default-admin.enabled=false`.

---

## 6. REST API reference

Base URL: `http://localhost:8080` (default Spring Boot port).

### 6.1 Authentication

#### POST /auth/login

**Auth:** none

**Request:**
```json
{"username":"admin","password":"changeme"}
```

**Response (200):**
```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresInMs": 86400000,
  "username": "admin",
  "role": "ADMIN"
}
```

#### GET /auth/me

**Auth:** Bearer token

**Response (200):**
```json
{"username":"admin","role":"ADMIN"}
```

### 6.2 Collections

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | /collections | — | List all visible collections |
| GET | /collections/{id} | — | Collection detail |
| POST | /collections/collection | Staff | Create (metadata) |
| POST | /collections/collection-images | Staff | Create with thumbnail upload |
| PUT | /collections | Staff | Update metadata |
| PUT | /collections/content-status | Staff | Update lifecycle status |
| DELETE | /collections?collectionId={id} | Admin | Delete |

**CollectionDto fields:** id, name, description, date, thumbnailUrl, contentStatus, publishAt, paintingIds

### 6.3 Paintings

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | /paintings | — | List paintings |
| GET | /paintings/{id} | — | Painting detail |
| GET | /paintings/collection/{collectionId} | — | Paintings in collection |
| POST | /paintings/painting | Staff | Create |
| POST | /paintings/painting-images | Staff | Create with images |
| PUT | /paintings/painting | Staff | Update metadata |
| PUT | /paintings/painting/content-status | Staff | Lifecycle status |
| PUT | /paintings/painting/status?id=&status=&price= | Staff | Sale availability |
| PUT | /paintings/painting-in-collection?paintingId=&collectionId= | Staff | Assign to collection |
| PUT | /paintings/painting-out-collection?paintingId= | Staff | Remove from collection |
| DELETE | /paintings?paintingId={id} | Admin | Delete |

**PaintingDto fields:** id, name, material, description, price, availability, inCollection, collectionId, images, width, height, date, contentStatus, publishAt

### 6.4 Events

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | /events | — | List events |
| GET | /events/{id} | — | Event detail |
| POST | /events/event | Staff | Create |
| POST | /events/event-images | Staff | Create with images |
| PUT | /events/event | Staff | Update |
| PUT | /events/event/content-status | Staff | Lifecycle status |
| DELETE | /events?eventId={id} | Admin | Delete |

### 6.5 Images

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | /images | — | All images |
| GET | /images/{id} | — | Image by id |
| GET | /images/by-prefix?prefix= | — | Search by name prefix |
| GET | /images/painting/{paintingId} | — | Images for painting |
| GET | /images/event/{eventId} | — | Images for event |
| POST | /images | Staff | Upload image |
| PUT | /images/{imageId}/thumbnail?isThumbnail= | Staff | Toggle thumbnail |

**Image upload:** bind via `paintingId` or `eventId` in `ImageUploadDto`. Filename prefix matching is **not** required.

### 6.6 Error responses

Standard format (from `GenerateExceptionResponseBody`):

```json
{
  "timestamp": "2026-06-12T12:00:00",
  "message": "Human-readable error"
}
```

| HTTP | Typical cause |
|------|----------------|
| 400 | Validation, invalid status transition |
| 401 | Missing/invalid JWT, bad login |
| 403 | Insufficient role (e.g. EDITOR deletes) |
| 404 | Entity not found or not public |

---

## 7. AWS S3 integration

Images are uploaded via `S3Service`:

1. Multipart file received by API.
2. File uploaded to configured bucket (key = original filename basename).
3. Public URL stored in database: `https://{bucket}.s3.amazonaws.com/{key}`.

Configuration:

```properties
aws.s3.bucketName=your-bucket-name
aws.s3.region=us-east-1
aws.accessKeyId=your-access-key
aws.secretKey=your-secret-key
```

**Known limitation:** deleting a painting/event removes DB image rows (CASCADE) but does not yet delete S3 objects.

---

## 8. Angular frontend integration guide

### 8.1 Public gallery

- Call `GET` endpoints without `Authorization`.
- Render images using `url` / `thumbnailUrl` from JSON (browser loads directly from S3).
- Only published (or due scheduled) content is returned.

### 8.2 Admin area (`/admin`)

1. **Login page** — `POST /auth/login`, store `accessToken` (e.g. sessionStorage).
2. **HTTP interceptor** — add `Authorization: Bearer <token>` to mutating requests and optionally to `GET /auth/me`.
3. **Auth guard** — protect `/admin/**`; redirect to login if no token.
4. **Role guard** — hide delete buttons unless `role === 'ADMIN'`.
5. **Content workflow UI** — show `contentStatus` badge; actions for schedule, publish, archive via content-status endpoints.

### 8.3 Suggested Angular services

| Service | Responsibility |
|---------|----------------|
| AuthService | login, logout, token storage, getCurrentUser |
| CollectionService | CRUD + content-status |
| PaintingService | CRUD + availability + content-status |
| EventService | CRUD + content-status |
| ImageService | upload, list by parent |

---

## 9. Database schema

See `src/main/resources/dbscripts/create.sql` for DDL.

Main tables: `COLLECTION`, `PAINTING`, `EVENT`, `IMAGE`, `APP_USER`.

Publishable tables include `CONTENT_STATUS` and `PUBLISH_AT`.

Development uses H2 with `spring.jpa.hibernate.ddl-auto=update`.

---

## 10. Configuration reference

| Property | Default | Description |
|----------|---------|-------------|
| spring.datasource.url | jdbc:h2:mem:mfineart | Database JDBC URL |
| spring.jpa.hibernate.ddl-auto | update | Schema management |
| jwt.secret | (change me) | JWT HMAC key |
| jwt.expiration-ms | 86400000 | Token TTL (ms) |
| app.cors.allowed-origins | http://localhost:4200 | CORS origins |
| app.content.scheduled-publish-cron | 0 * * * * * | Publish job schedule |
| app.security.default-admin.enabled | true | Bootstrap admin |
| app.security.default-admin.username | admin | |
| app.security.default-admin.password | changeme | |

---

## 11. Build, run, and test

```bash
# Run application
mvn spring-boot:run

# Run tests
mvn clean test

# Build JAR
mvn clean package
java -jar target/mfineart-0.0.1-SNAPSHOT.jar
```

---

## 12. Future improvements

| Area | Suggestion |
|------|------------|
| S3 | Store `s3Key` separately; delete objects on entity delete; presigned uploads |
| API | OpenAPI 3 spec; `/api/v1` versioning |
| Media | Server-generated S3 keys; image variants (thumbnail, medium) |
| Users | Admin UI to create EDITOR accounts |
| Publishing | Use `publishedAt` distinct from `publishAt` for audit |
| DTOs | Replace embedded `Image` entities with `ImageDto` everywhere |
| Production DB | PostgreSQL/MySQL profile instead of H2 |

---

## 13. Glossary

| Term | Definition |
|------|------------|
| Collection | Curated set of paintings with a single thumbnail |
| Content status | Lifecycle state: DRAFT, SCHEDULED, PUBLISHED, ARCHIVED |
| Staff | Authenticated user with ADMIN or EDITOR role |
| Thumbnail | Primary image for a painting, event, or collection cover |
| Availability | Commercial state of a painting (for sale, sold, etc.) |

---

*End of document*
