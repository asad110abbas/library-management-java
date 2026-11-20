# 📚 BRIHA · Library Management API (Java / Spring Boot)

![BRIHA](https://img.shields.io/badge/Part_of-BRIHA-8B5CF6?style=for-the-badge&labelColor=0B1024) ![Module](https://img.shields.io/badge/Briha-Engineering-00F0FF?style=for-the-badge&labelColor=0B1024)
![Java](https://img.shields.io/badge/Java_21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?logo=springboot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA_/_Hibernate-59666C?logo=hibernate&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit_5-25A162?logo=junit5&logoColor=white)
![CI](https://github.com/asad110abbas/library-management-java/actions/workflows/ci.yml/badge.svg)

> ⚡ **Part of [BRIHA](https://github.com/asad110abbas)**, built by Syed Asad Abbas.

A layered **Spring Boot REST API** for running a library: books, members, loans, returns and late fines,
with real business rules enforced in a transactional service layer.

## Business rules
- A member can hold at most **3 active loans** (configurable)
- Members with **overdue books** can't borrow more
- A member can't borrow two copies of the same title at once
- Loan period is **14 days**. Late returns are fined **per day** (configurable)
- Copies can never go below zero or above the stock count (domain invariants on the entity)
- ISBN and member email are unique

## API
| Method | Path | Description |
|---|---|---|
| `POST` | `/api/books` | Add a book (validated ISBN-10/13) |
| `GET` | `/api/books?q=` | List / search by title or author |
| `POST` | `/api/members` | Register a member |
| `GET` | `/api/members/{id}/loans` | Member loan history |
| `POST` | `/api/loans` | Borrow `{memberId, bookId}` |
| `POST` | `/api/loans/{id}/return` | Return and calculate fine |
| `GET` | `/api/loans/overdue` | All overdue loans |

Errors use **RFC 7807 `application/problem+json`**: `400` validation (with field errors), `404` not found, `409` rule violation.

## Architecture
```
web/         controllers, request DTOs (Bean Validation), problem+json error handler
service/     LibraryService: transactional business rules
model/       JPA entities with domain invariants (Book, Member, Loan)
repository/  Spring Data JPA repositories (derived + JPQL queries)
```
An injectable `Clock` keeps date-dependent logic (due dates, fines) deterministic in tests.

## Run
```bash
mvn spring-boot:run          # http://localhost:8080  (H2 console at /h2-console)
mvn test                     # unit + MockMvc integration tests
```
```bash
curl -X POST localhost:8080/api/books -H "Content-Type: application/json" \
  -d '{"isbn":"9780134685991","title":"Effective Java","author":"Joshua Bloch","publishedYear":2018,"copies":2}'
```
