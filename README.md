# Lost & Found Portal with QR Code Verification

An end-to-end academic project for managing lost and found item reporting, matching, ownership claiming, QR-code tagging, camera scanning, verification, and complete recovery audit trail.

---

## 1. Project Title
**Lost & Found Portal with QR Code Verification**

---

## 2. Project Objective
To simplify, automate, and secure the process of reporting, tracking, searching, matching, verifying, claiming, and recovering lost and found items within a campus or organization. The integration of ZXing QR code verification ensures physical item verification, preventing fraudulent claims and streamlining administrative handover.

---

## 3. Features
* **User & Admin Authentication**: Role-based access control (`USER` and `ADMIN`) with secure password hashing (SHA-256 with salt / PasswordEncoder).
* **Lost Item Reporting**: Detailed item reporting system generating unique `LOST-XXXX` IDs.
* **Found Item Registration**: Instant found item registration generating unique `FOUND-XXXX` IDs.
* **Automatic QR Code Tagging**: Every registered found item automatically receives a unique ZXing QR Code image (Base64 PNG).
* **QR Camera Scanning & Verification**: Real-time browser camera QR scanning, image file upload decoding, and manual QR entry fallback with backend verification against MySQL database.
* **Multi-Criteria Search & Filtering**: Centralized search page filtering by keyword, category, location, date, status, and item type (`LOST` / `FOUND`).
* **Smart Item Correlation / Matching**: Algorithm calculating similarity score (%) between reported lost items and registered found items based on category, title keywords, location, date proximity, brand, and color.
* **Claim Lifecycle Management**: Ownership claim workflow with status progression: `PENDING` → `UNDER_REVIEW` → `APPROVED` / `REJECTED` → `RETURNED`.
* **Complete Status Workflow**: Status tracking (`REPORTED` → `MATCHED` → `CLAIMED` → `RETURNED`).
* **Audit Transaction History**: Immutable logging of all item actions, user actions, status shifts, QR verification outcomes, and decisions.
* **Admin Dashboard**: Analytics cards, user account management, item status overrides, claim approval workspace, and QR verification tools.
* **User Dashboard**: Track personal lost reports, found registrations, submitted claims, potential matches, and activity logs.
* **Interactive OpenAPI / Swagger Documentation**: Available at `/swagger-ui.html`.

---

## 4. Technology Stack
* **Language & Framework**: Java 17+, Spring Boot 3.2.5
* **Build System**: Apache Maven
* **Database**: MySQL 8.0 (with H2 test support)
* **ORM & JPA**: Spring Data JPA & Hibernate
* **QR Code Engine**: Google ZXing Java Library (`core` & `javase` 3.5.3)
* **API Documentation**: SpringDoc OpenAPI Starter WebMVC UI
* **Frontend UI**: Responsive Vanilla HTML5, CSS3 Glassmorphism UI, JavaScript (Fetch API, HTML5 Camera MediaStream API)

---

## 5. System Architecture
```text
                  +-----------------------------------+
                  |   Browser / HTML5 + CSS + JS      |
                  |  (User & Admin Dashboards / QR)   |
                  +-----------------+-----------------+
                                    |
                             RESTful APIs / JSON
                                    |
                  +-----------------v-----------------+
                  |      Spring Boot Controllers      |
                  +-----------------+-----------------+
                                    |
                  +-----------------v-----------------+
                  |         Service Layer             |
                  |  (Auth, Lost, Found, QR, Claim,   |
                  |   Matching, Search, Audit Txn)    |
                  +-----------------+-----------------+
                                    |
                  +-----------------v-----------------+
                  |   Spring Data JPA Repositories    |
                  +-----------------+-----------------+
                                    |
                  +-----------------v-----------------+
                  |           MySQL Database          |
                  |  (users, lost_items, found_items, |
                  |   qr_codes, claims, transactions) |
                  +-----------------------------------+
```

---

## 6. Database Setup
1. Ensure MySQL Server is installed and running on port `3306`.
2. Create the database schema manually or let Spring Boot automatically create it:
   ```sql
   CREATE DATABASE IF NOT EXISTS lost_found_db;
   ```

---

## 7. MySQL Configuration
Configure `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lost_found_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 8. Maven Setup & Build
Build the Maven package:
```bash
mvn clean package
```
*(Or use `mvnw` wrapper if installed).*

---

## 9. How to Run Backend
Run the Spring Boot application locally:
```bash
mvn spring-boot:run
```
The server starts at `http://localhost:8080`. Sample demo data is automatically populated on first run!

---

## 10. How to Access Frontend
Open any modern browser and navigate to:
* **Landing Page**: `http://localhost:8080/index.html` or `http://localhost:8080/`
* **Login**: `http://localhost:8080/login.html`
* **Register**: `http://localhost:8080/register.html`
* **User Dashboard**: `http://localhost:8080/user-dashboard.html`
* **Admin Dashboard**: `http://localhost:8080/admin-dashboard.html`
* **QR Scanner**: `http://localhost:8080/qr-verification.html`
* **Search Listings**: `http://localhost:8080/search.html`

---

## 11. Default Credentials
| Role | Email | Password |
|---|---|---|
| **ADMIN** | `admin@campus.edu` | `admin123` |
| **USER** | `john@student.edu` | `user123` |
| **USER** | `alice@employee.edu` | `user123` |

---

## 12. API Documentation (Swagger)
Access interactive OpenAPI / Swagger UI:
* `http://localhost:8080/swagger-ui.html`
* OpenAPI JSON Spec: `http://localhost:8080/api-docs`

---

## 13. QR Code Functionality
* **Generation**: Handled by `QrGeneratorUtil.java` using ZXing. Automatically triggered whenever a found item is registered via `POST /api/found-items`.
* **Storage**: Stored in `qr_codes` MySQL table with Base64 PNG image stream & unique `QR-XXXX` verification payload.
* **Scanning**: Browser camera stream via HTML5 `getUserMedia`, file upload image reader, and manual entry fallback.
* **Verification**: `POST /api/qr/verify` validates QR string or image payload against database, returns found item info, cross-checks claimant details, and logs verification outcome.

---

## 14. Complete End-to-End Workflow
1. User registers (`POST /api/auth/register`) and logs in (`POST /api/auth/login`).
2. User submits a lost item report (`POST /api/lost-items`).
3. Finder/Staff registers a found item (`POST /api/found-items`).
4. System automatically generates a unique ZXing QR code tag for the found item.
5. Search & matching engine correlates the lost item with the found item.
6. Claimant identifies the found item and submits an ownership claim (`POST /api/claims`).
7. Admin opens QR Verification page (`/qr-verification.html`).
8. Admin scans the item's physical QR code with camera or file upload.
9. System verifies QR code authenticity and displays claimant details cross-check.
10. Admin approves the claim (`PUT /api/claims/{id}/review?status=APPROVED`).
11. Item status shifts: `REPORTED` → `MATCHED` → `CLAIMED` → `RETURNED`.
12. Action is recorded in audit transaction log (`GET /api/transactions`).

---

## 15. Testing Instructions
Execute test suite:
```bash
mvn test
```
The included `@SpringBootTest` suite (`LostFoundApplicationTests.java`) verifies the end-to-end user workflow, QR code generation, QR verification, claim approval, status transitions, and transaction log auditing.

---

## 16. Common Errors & Solutions
* **MySQL Access Denied**: Ensure `spring.datasource.username` and `password` match your local MySQL configuration.
* **Port 8080 Busy**: Change `server.port=8081` in `application.properties`.
* **Camera Access Denied**: Use the image file upload feature or manual QR string verification fallback.
