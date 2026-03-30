# Fitness World

Fitness World is a microservice-based gym management platform with Spring Boot services, a React SPA, PostgreSQL persistence, JWT-based gateway security, AI-ready multilingual coaching, and local plus cloud deployment support.

## Services
- `discovery-service`: Eureka registry.
- `api-gateway`: central routing, JWT verification, resilience, and frontend-facing CORS.
- `auth-service`: registration, login, seeded demo users.
- `profile-service`: member profile management.
- `planning-service`: versioned workout and nutrition plans.
- `progress-service`: body metrics and exportable progress reports.
- `billing-service`: payment and invoice tracking for Stripe, Razorpay, and UPI-oriented flows.
- `notification-service`: reminder and summary schedulers.
- `ai-service`: OpenAI-compatible gateway for Groq-style chat and plan generation.
- `frontend`: multilingual React client.

## Local run
1. Configure optional AI settings: `AI_BASE_URL`, `AI_API_KEY`, `AI_MODEL`.
2. For PostgreSQL 18 local setups, also set:
   - `FLYWAY_ENABLED=false`
   - `HIBERNATE_DDL_AUTO=update`
3. Start everything: `docker compose up --build`.
4. Open `http://localhost:5173` for the UI.
5. Gateway APIs are available at `http://localhost:8080/api/...`.

## Demo accounts
- `admin@fitnessworld.local`
- `trainer@fitnessworld.local`
- `member@fitnessworld.local`

The seeded password hash is intended as a placeholder and should be replaced before production rollout.

## Deployment
- Render backend/database blueprint: [render.yaml](render.yaml)
- Deployment guide: [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)
- Docker local stack: [docker-compose.yml](docker-compose.yml)

## Notes
- Billing currently uses provider abstractions and internal tracking records. Production Stripe and Razorpay webhook hardening should still be added before going live.
- AI endpoints fall back gracefully when a Groq-compatible API key is not present.
- Each persistent service owns its schema and Flyway migration history.

