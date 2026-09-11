# HamaraShops API Gateway (`api-gateway`)

## 1. Overview
The **API Gateway** is the single public entry point for the **HamaraShops.ai** microservices architecture. It handles request routing, centralized Cross-Origin Resource Sharing (CORS), security headers, and service discovery integration.

- **Java Version:** Java 21 (Eclipse Temurin)
- **Spring Boot Version:** 4.1.0
- **Spring Cloud Version:** 2025.1.2
- **Default Local Port:** `8080` (Cloud Run dynamically binds to `${PORT}`)

> [!NOTE]
> **Eureka is used ONLY for local development service discovery and testing.** Eureka is NOT deployed to Google Cloud Run in production.

---

## 2. Profiles & Routing Architecture

### Local Profile (`spring.profiles.active=local`)
- **Eureka Server:** `http://localhost:8761/eureka/`
- **Registration Name:** `api-gateway`
- **Routing Engine:** Spring Cloud Gateway + Spring Cloud LoadBalancer (`lb://`)
- **Routes:**
  - Content Suite (`/api/v1/products/**`, `/api/v1/solutions/**`, `/api/v1/services/**`, `/api/v1/insights/**`, `/api/v1/company/**`, `/api/v1/partners/**`, `/api/v1/search/**`) $\rightarrow$ `lb://CONTENT-SERVICE`
  - Business Suite (`/api/v1/industries/**`, `/api/v1/case-studies/**`, `/api/v1/careers/**`) $\rightarrow$ `lb://BUSINESS-SERVICE`
  - Contact Inquiries (`/api/v1/contact/**`) $\rightarrow$ `lb://CONTACT-SERVICE`

### Cloud Profile (`spring.profiles.active=cloud`)
- **Eureka Server:** Disabled (`eureka.client.enabled=false`)
- **Routing Engine:** Direct environment-driven Cloud Run URLs
- **Environment Variables:**
  - `CONTENT_SERVICE_URL`
  - `BUSINESS_SERVICE_URL`
  - `CONTACT_SERVICE_URL`

---

## 3. Local Development & Commands

### Prerequisites
1. Ensure your existing Eureka Server is running at `http://localhost:8761`.
2. Installed Java 21 and Maven 3.9+.

### Build & Package (Using Installed Maven)
```bash
mvn clean package -DskipTests
```

### Run Locally (Local Profile)
```bash
mvn spring-boot:run
```
*(Or specify profile explicitly: `mvn spring-boot:run -Dspring-boot.run.profiles=local`)*

### Verify Health & Eureka Registration
- **Actuator Health Check:** `http://localhost:8080/actuator/health` (HTTP 200 `{"status":"UP"}`)
- **Eureka Dashboard:** `http://localhost:8761` (Verify `API-GATEWAY` is registered)

---

## 4. Docker & Google Cloud Run

### Docker Multi-Stage Build
```bash
docker build -t <REGION>-docker.pkg.dev/<PROJECT_ID>/<IMAGE_REPOSITORY>/api-gateway:<IMAGE_TAG> .
```

### Google Cloud Run Deployment
Deploy using `cloud.yml` manifest template or `gcloud`:
```bash
gcloud run deploy api-gateway \
  --image <REGION>-docker.pkg.dev/<PROJECT_ID>/<IMAGE_REPOSITORY>/api-gateway:<IMAGE_TAG> \
  --set-env-vars SPRING_PROFILES_ACTIVE=cloud,CONTENT_SERVICE_URL=$CONTENT_URL,BUSINESS_SERVICE_URL=$BUSINESS_URL,CONTACT_SERVICE_URL=$CONTACT_URL
```
