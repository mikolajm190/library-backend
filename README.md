# Library API

Spring Boot REST API for a small library system (books, loans, reservations) with JWT-based authentication. Backend only.

## Tech Stack
- Java 17, Spring Boot 3.5.9
- Spring Web, Spring Security, Spring Validation, Spring Data JPA (Hibernate)
- PostgreSQL
- Maven, Docker

## Architecture
- Layered structure: controller -> service -> repository -> database
- DTOs + mappers to keep persistence models out of the API surface
- Global exception handling via `GlobalExceptionHandler`
- Stateless JWT auth via `JwtAuthenticationFilter`

## Data Model
- User: `id`, `username`, `password`, `role` (USER/LIBRARIAN/ADMIN)
- Book: `id`, `title`, `author`, `totalCopies`, `availableCopies` (computed from loans + READY reservations)
- Loan: `id`, `borrowDate`, `returnDate`, `user`, `book`
- Reservation: `id`, `createdAt`, `expiresAt`, `status` (QUEUED/READY/EXPIRED), `user`, `book`

## Security & Authorization
- `POST /api/v1/auth/register`, `POST /api/v1/auth/login` issue JWTs
- Public: `GET /api/v1/books/**`
- Authenticated: all other endpoints
- Role rules:
  - ADMIN: manage books and users; read/write any loan or reservation
  - LIBRARIAN: manage loans and reservations; read users
  - USER: read/update own profile; read own loans/reservations; create own reservations; extend own loans

## API Endpoints (v1)
Auth:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

Books:
- `GET /api/v1/books` (public; pagination + sorting by `title|author|totalCopies`)
- `GET /api/v1/books/{bookId}` (public)
- `POST /api/v1/books` (ADMIN)
- `PUT /api/v1/books/{bookId}` (ADMIN)
- `DELETE /api/v1/books/{bookId}` (ADMIN)

Users:
- `GET /api/v1/users` (ADMIN/LIBRARIAN; pagination + sorting by `username|role`)
- `GET /api/v1/users/me` (authenticated)
- `GET /api/v1/users/{userId}` (ADMIN/LIBRARIAN/owner)
- `POST /api/v1/users` (ADMIN)
- `PUT /api/v1/users/{userId}` (ADMIN/owner)
- `DELETE /api/v1/users/{userId}` (ADMIN)

Loans:
- `GET /api/v1/loans` (ADMIN/LIBRARIAN all; USER own; pagination + sorting by `returnDate`)
- `GET /api/v1/loans/{loanId}` (ADMIN/LIBRARIAN/owner)
- `POST /api/v1/loans` (ADMIN/LIBRARIAN)
- `PUT /api/v1/loans/{loanId}` (ADMIN/LIBRARIAN/owner; prolongs return date)
- `DELETE /api/v1/loans/{loanId}` (ADMIN/LIBRARIAN)

Reservations:
- `GET /api/v1/reservations` (ADMIN/LIBRARIAN all; USER own; pagination + sorting by `createdAt|expiresAt|status|user.username|book.title`)
- `GET /api/v1/reservations/{reservationId}` (ADMIN/LIBRARIAN/owner)
- `POST /api/v1/reservations` (authenticated; USER can only create for self)
- `POST /api/v1/reservations/expire` (ADMIN/LIBRARIAN; mark expired)
- `DELETE /api/v1/reservations/{reservationId}` (ADMIN/LIBRARIAN/owner)
- `DELETE /api/v1/reservations/expired` (ADMIN/LIBRARIAN; purge expired)

## Validation & Pagination
- Books: `title` and `author` required; `totalCopies` in `1..10`
- Users: `username` and `password` required; `/api/v1/users` payloads include `role` but service ignores it (created users are always USER; updates keep the current role)
- Loans: `userId` and `bookId` required on create; `daysToProlong` in `1..30` on update
- Reservations: `userId` and `bookId` required on create
- Pagination params: `page >= 0`, `size >= 1`; `sortOrder` is `ASC|DESC`

## Business Rules Worth Noting
- Availability is derived from `totalCopies - active loans - READY, unexpired reservations`
- Loan creation:
  - User cannot borrow the same book twice
  - If a reservation exists it must be READY; otherwise the book must be available
- Reservation creation:
  - READY + 3-day expiry when copies are available
  - QUEUED + 3-month expiry when copies are not available
- Deleting a loan or reservation advances the reservation queue for that book

## Local Setup
Prereqs:
- Java 17
- PostgreSQL running locally

Environment variables (defaults from `application.properties`):
- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/library`
- `SPRING_DATASOURCE_USERNAME=library`
- `SPRING_DATASOURCE_PASSWORD=B00ks-are-great`
- `SPRING_PROFILES_ACTIVE=dev`
- `JWT_SECRET=<base64 secret>`
- `JWT_ACCESS_EXPIRATION=1440000` (ms)

Run locally:
```bash
./mvnw spring-boot:run
```

Build + run the jar:
```bash
./mvnw clean package
java -jar target/*.jar
```

## Seed Data (dev profile)
`DevDataInitializer` seeds:
- Users: `admin/admin123` (ADMIN), `lib/lib456` (LIBRARIAN), `user1/pass1`, `user2/pass2`
- 15 books
- 2 loans
- 4 reservations

Note: `spring.jpa.hibernate.ddl-auto=create-drop` resets the schema on every run.

## Container Build
```bash
docker build -f Containerfile -t library-api .
```

Run:
```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/library \
  -e SPRING_DATASOURCE_USERNAME=library \
  -e SPRING_DATASOURCE_PASSWORD=B00ks-are-great \
  -e JWT_SECRET=your-base64-secret \
  library-api
```

Or with Compose:
```bash
docker compose up --build
```
