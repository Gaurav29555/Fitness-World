# How To Run With Docker

## Prerequisites
- Docker Desktop
- Java 17 and Maven 3.9+ if you want to run services outside containers
- Node 22+ if you want to run the frontend locally

## Start the full stack
```bash
docker compose up --build
```

## Useful endpoints
- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`
- Nginx entrypoint: `http://localhost`
- Frontend direct: containerized behind Nginx

## Optional AI configuration
Set environment variables before `docker compose up`:
- `AI_BASE_URL`
- `AI_API_KEY`
- `AI_MODEL`

Groq-compatible defaults are already wired in the AI service.

## Suggested next production steps
- Replace placeholder secrets with a secret manager.
- Add provider webhooks for Stripe and Razorpay.
- Add Prometheus/Grafana containers and scrape configs.
- Add object storage for generated PDF reports.
- Add richer trainer assignment workflows and audit logs.

