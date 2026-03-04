# Banking System API

Simple banking backend built with Spring Boot, Spring Data JPA, and PostgreSQL.

It supports:
- Account creation
- Balance query
- Deposit and withdrawal
- Transfer between accounts
- Transaction history per account

## Tech Stack
- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA (Hibernate)
- PostgreSQL 16 (Docker)
- Maven
- springdoc OpenAPI (Swagger UI)

## Prerequisites
- JDK 21
- Maven 3.9+
- Docker (or Docker Desktop)

## Configuration
Current defaults are in `src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:postgresql://localhost:5433/bankdb`
- `spring.datasource.username=bankuser`
- `spring.datasource.password=bankpass`
- `spring.jpa.hibernate.ddl-auto=update`
- `springdoc.swagger-ui.path=/swagger`
- `springdoc.api-docs.path=/api-docs`

Docker DB config (`docker-compose.yml`):
- Host port `5433` -> container port `5432`
- Database: `bankdb`
- User: `bankuser`
- Password: `bankpass`

## Quick Start
1. Start PostgreSQL with Docker:

```bash
docker compose up -d
```

2. Run the API:

```bash
mvn spring-boot:run
```

3. Open docs:
- Swagger UI: http://localhost:8080/swagger
- OpenAPI JSON: http://localhost:8080/api-docs

## API Endpoints
Base path: `/api`

| Method | Endpoint | Description | Success |
|---|---|---|---|
| POST | `/accounts` | Create account | `200 OK` |
| GET | `/accounts/{accountNumber}` | Get account | `200 OK` |
| POST | `/accounts/{accountNumber}/deposit` | Deposit money | `200 OK` |
| POST | `/accounts/{accountNumber}/withdraw` | Withdraw money | `200 OK` |
| POST | `/transfers` | Transfer between accounts | `204 No Content` |
| GET | `/accounts/{accountNumber}/transactions` | Transaction history | `200 OK` |

## Example Requests
Create account:

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "001",
    "ownerName": "Mauricio",
    "initialBalance": 1000
  }'
```

Get account:

```bash
curl http://localhost:8080/api/accounts/001
```

Deposit:

```bash
curl -X POST http://localhost:8080/api/accounts/001/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount": 250.50}'
```

Withdraw:

```bash
curl -X POST http://localhost:8080/api/accounts/001/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount": 100}'
```

Transfer:

```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "001",
    "toAccountNumber": "002",
    "amount": 50
  }'
```

Get transactions:

```bash
curl http://localhost:8080/api/accounts/001/transactions
```

## Error Handling
The API returns JSON errors in this format:

```json
{
  "error": "message here"
}
```

Mapped exceptions:
- `400 Bad Request`: `IllegalArgumentException` (invalid input, account not found, duplicate account, etc.)
- `422 Unprocessable Entity`: `InsufficientFundsException`

## Database Notes
- Tables are created/updated automatically by Hibernate (`ddl-auto=update`) when the app starts.
- Main tables:
  - `accounts`
  - `transactions`

Connect manually:

```bash
PGPASSWORD=bankpass psql -h localhost -p 5433 -U bankuser -d bankdb
```

## Run Tests
```bash
mvn test
```

## Troubleshooting
If `Port 8080 was already in use`:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

If you see `relation "accounts" does not exist`:
- Make sure Docker DB is running (`docker compose up -d`)
- Restart the API so Hibernate can create/update schema

If you see authentication errors for `bankuser`:
- Verify you are using `localhost:5433` (Docker DB), not another local PostgreSQL instance on `5432`

## Optional Console App
There is also an in-memory console implementation (`Main` + `ConsoleApp`) used for domain logic/testing outside the REST API.
