# Library API

Spring Boot REST API for a small library system: users can browse books, borrow/return them via loans, and manage accounts with JWT-based authentication. This repo contains only the backend.

Frontend repo: [library-frontend](https://github.com/mikolajm190/library-frontend)

## Tech Stack
- Java 17, Spring Boot 3.5.9
- Spring Web, Spring Security (JWT), Spring Data JPA (Hibernate)
- PostgreSQL, Lombok
- Maven, Docker

## Architecture Overview
- Layered structure: controller -> service -> repository -> database
- DTOs + mappers to avoid exposing entities directly (`BookMapper`, `UserMapper`, `LoanMapper`)
- Global exception handling (`GlobalExceptionHandler`) for consistent HTTP responses
- JWT filter authenticates requests and populates Spring Security context

## Data Model
- User: `id`, `username`, `password`, `role` (USER/ADMIN)
- Book: `id`, `title`, `author`, `availableCopies`, `copiesOnLoan`
- Loan: `id`, `borrowDate`, `returnDate`, `user`, `book`

## Security & Authorization
- JWT tokens issued by `/api/v1/auth/login` and `/api/v1/auth/register`
- Stateless security with `Authorization: Bearer <token>` header
- Access rules:
  - Public: `GET /api/v1/books/**`
  - Authenticated: all other endpoints
  - Admin-only: create/update/delete books, user management
  - Ownership: users can only access their own profile and loans unless admin

## API Endpoints (v1)
Auth:
- `POST /api/v1/auth/register` -> returns JWT
- `POST /api/v1/auth/login` -> returns JWT

Books:
- `GET /api/v1/books` (public, pagination + sorting)
- `GET /api/v1/books/{bookId}` (public)
- `POST /api/v1/books` (ADMIN)
- `PUT /api/v1/books/{bookId}` (ADMIN)
- `DELETE /api/v1/books/{bookId}` (ADMIN)

Users:
- `GET /api/v1/users` (ADMIN, pagination + sorting)
- `GET /api/v1/users/me` (authenticated)
- `GET /api/v1/users/{userId}` (ADMIN or owner)
- `POST /api/v1/users` (ADMIN)
- `PUT /api/v1/users/{userId}` (ADMIN or owner)
- `DELETE /api/v1/users/{userId}` (ADMIN)

Loans:
- `GET /api/v1/loans` (ADMIN sees all, USER sees own; pagination + sorting)
- `GET /api/v1/loans/{loanId}` (ADMIN or owner)
- `POST /api/v1/loans` (USER can only create for self; ADMIN can create for any user)
- `PUT /api/v1/loans/{loanId}` (ADMIN or owner, prolongs return date)
- `DELETE /api/v1/loans/{loanId}` (ADMIN or owner, returns copies)

## Request/Response Examples
Login:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Use the token:
```bash
curl http://localhost:8080/api/v1/books \
  -H "Authorization: Bearer <token>"
```

Create a loan (admin or self for user):
```bash
curl -X POST http://localhost:8080/api/v1/loans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"userId":"<uuid>","bookId":"<uuid>"}'
```

## Local Setup
Prereqs:
- Java 17
- PostgreSQL running locally (defaults below)

Environment variables (defaults from `application.properties`):
- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/library`
- `SPRING_DATASOURCE_USERNAME=library`
- `SPRING_DATASOURCE_PASSWORD=B00ks-are-great`
- `SPRING_PROFILES_ACTIVE=dev`
- `JWT_SECRET=f89a377ba1d...` (base64-encoded secret)
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
With the `dev` profile, `DevDataInitializer` seeds:
- Users:
  - `admin` / `admin123` (ADMIN)
  - `user1` / `pass1` (USER)
  - `user2` / `pass2` (USER)
- 15 books with varying availability
- 2 sample loans

Note: `spring.jpa.hibernate.ddl-auto=create-drop` resets the schema on every run.

## Container Build
```bash
docker build -f Containerfile -t library-api .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/library \
  -e SPRING_DATASOURCE_USERNAME=library \
  -e SPRING_DATASOURCE_PASSWORD=B00ks-are-great \
  -e JWT_SECRET=your-base64-secret \
  library-api
```

## Validation
- Request bodies use Jakarta Bean Validation via `@Valid` in controllers.
- Books: `title` and `author` required; `availableCopies >= 1` on create.
- Users: `username` and `password` required for create/update.
- Loans: `userId` and `bookId` required on create; `daysToProlong` must be 1..30 on update.
- Pagination params: `page >= 0`, `size >= 1`; `sortBy` is restricted per resource
  (`title` for books, `username` for users, `returnDate` for loans); `sortOrder` is `ASC`/`DESC`.
- Invalid inputs return `400 Bad Request` via `GlobalExceptionHandler`.

## Business Rules Worth Noting
- Loan creation checks:
  - Book must have `availableCopies > 0`
  - User cannot borrow the same book twice
- Returning/deleting loans increments `availableCopies` and decrements `copiesOnLoan`
- Deleting a user returns all their books and removes their loans
- Deleting a book removes its loans

