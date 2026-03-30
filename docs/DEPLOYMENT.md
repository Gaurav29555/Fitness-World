# Render and Cloudflare Deployment Guide

## Architecture
- Render Postgres: one shared PostgreSQL instance for all domain services.
- Render private services: discovery, auth, profile, planning, progress, billing, notifications, AI.
- Render web service: API gateway only.
- Cloudflare Pages: React frontend.

## Important deployment notes
- The microservice architecture is deployment-ready, but it is not low-cost on Render because private services do not run on the free plan.
- The included `render.yaml` deploys the requested architecture as separate services.
- The gateway is the only public backend endpoint. All other services stay private behind Eureka and the Render private network.
- `JWT_SECRET` is generated once in the gateway service and reused by auth through Render service environment references.
- Set `CORS_ALLOWED_ORIGINS` on `fitness-world-api` to your Cloudflare Pages domain, for example `https://fitness-world.pages.dev`.

## Render deployment
1. Push this repository to GitHub.
2. In Render, create a new Blueprint and point it to the repository.
3. Render will detect `render.yaml` and propose the database plus services.
4. Before deploy, provide values for:
   - `AI_API_KEY`
   - `CORS_ALLOWED_ORIGINS`
5. Deploy the Blueprint.
6. After deployment, note the public URL of `fitness-world-api`.

## Cloudflare Pages deployment
1. Create a new Pages project from the same repository.
2. Use these settings:
   - Root directory: `frontend`
   - Build command: `npm install && npm run build`
   - Build output directory: `dist`
3. Add environment variable:
   - `VITE_API_URL=https://your-render-api-domain/api`
4. Deploy the site.
5. Copy the final Cloudflare Pages URL and place it in Render as `CORS_ALLOWED_ORIGINS`.
6. Redeploy `fitness-world-api`.

## Local development after these production changes
For local PostgreSQL 18 environments where Flyway is problematic, use these environment variables before starting the database-backed services:
- `FLYWAY_ENABLED=false`
- `HIBERNATE_DDL_AUTO=update`

## Health checks
Use `/actuator/health` for Render health checks.
