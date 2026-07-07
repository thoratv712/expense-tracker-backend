# AI-Powered Expense Tracker — Backend

A production-ready REST API built with Spring Boot 4, powering an AI-driven personal finance tracker. The backend handles JWT authentication, full expense CRUD with per-user data isolation, and integrates Google Gemini AI to answer natural-language questions about spending patterns.

🔗 **Live API:** https://expense-tracker-backend-e5pz.onrender.com  
🖥️ **Frontend Repository:** https://github.com/thoratv712/expense-tracker-frontend  
🌐 **Live App:** https://venerable-peony-b416e4.netlify.app

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.1.0 |
| Language | Java 21 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | MySQL 8 (Aiven cloud) |
| ORM | JPA / Hibernate |
| AI | Google Gemini API (gemini-2.5-flash) |
| HTTP Client | Spring RestClient |
| Validation | Jakarta Bean Validation |
| Deployment | Docker (multi-stage build) on Render |
| Secrets | Environment variables via spring-dotenv |

---

### Reliability

The service includes exponential-backoff retry logic (up to 2 retries with 1s → 2s wait) to handle Gemini's free-tier `503` errors gracefully, rather than failing immediately on the first transient error.

---

## Local Development Setup

### Prerequisites

- Java 21 (Eclipse Temurin recommended)
- Maven 3.9+
- MySQL 8.0+ running locally
- A Google Gemini API key (free at https://aistudio.google.com/apikey)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/thoratv712/expense-tracker-backend.git
cd expense-tracker-backend
```

**2. Create the local database**
```sql
CREATE DATABASE expense_tracker;
```

**3. Create `.env` in the project root**
```properties
DB_URL=jdbc:mysql://localhost:3306/expense_tracker?createDatabaseIfNotExist=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_long_random_secret_at_least_32_characters
GEMINI_API_KEY=your_gemini_api_key
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

**5. Verify it started**

Visit `http://localhost:8080/api/health` — you should see `Backend is running!`

Hibernate will automatically create all tables on first startup (`spring.jpa.hibernate.ddl-auto=update`).

---

## Deployment

The backend is deployed on **Render** (free tier) using a multi-stage Docker build.

### Dockerfile

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Run (smaller image, no Maven/source code)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Environment Variables (set on Render dashboard)

| Key | Description |
|---|---|
| `DB_URL` | Full JDBC URL to Aiven MySQL with SSL parameters |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Long random string for signing tokens |
| `GEMINI_API_KEY` | Google AI Studio API key |

### Production Database

MySQL hosted on **Aiven** (free tier) — fully managed, SSL-enforced, located in Asia/Bangalore for low latency.

### Uptime

Render's free tier spins down after 15 minutes of inactivity. An **UptimeRobot** monitor pings `/api/health` every 5 minutes to keep the instance warm.

---

## Key Design Decisions

**Per-user categories** — categories are owned by individual users, not shared globally. This prevents one user's categories from appearing in another user's dropdowns, and enforces a composite unique constraint on `(user_id, name)` so the same category name can exist for different users independently.

**DTOs for all endpoints** — entities (`User`, `Category`, `Expense`) are never returned directly from controllers. Separate request and response DTOs prevent mass-assignment vulnerabilities and accidental secret leakage (e.g., `passwordHash` never appears in any API response).

**Service layer** — all business logic lives in `ExpenseService` and `AiService`, not in controllers. Controllers only handle HTTP (parse request, call service, set status code). This makes each layer independently testable and keeps controllers thin.

**Flexible AI date range** — the AI endpoint accepts optional `startDate`/`endDate` parameters, defaulting to the current month if not provided. The prompt explicitly states the actual date range used, so the AI never incorrectly describes "last 3 months" as "this month."

---

## Author

**Vaishnavi Thorat**  
GitHub: [@thoratv712](https://github.com/thoratv712)
