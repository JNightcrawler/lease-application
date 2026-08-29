# Deployment Guide — Free Hosting (Neon + Render)

This guide walks through deploying the Lease Application backend for free,
using **Neon** for the database and **Render** for the app itself. This
combination is recommended over using Render for both, because Render's
free Postgres now **expires 30 days after creation** and is deleted after
a 14-day grace period — fine for a demo, not for anything you want to keep.
Neon's free tier has no such expiry.

Last checked: August 2026. Free tier terms change — re-check each
provider's pricing page before you commit to an architecture around them.

---

## 1. Why Neon + Render (and not other combinations)

| Need | Recommended | Why |
|---|---|---|
| Database | **Neon** (neon.tech) | Permanent free tier, serverless Postgres, scales to zero when idle, no expiry |
| App hosting | **Render** (render.com) | 750 free instance-hours/month, deploys straight from a `Dockerfile`, no credit card required |

**Alternatives considered, and why they're not the default pick:**
- **Render Postgres (free)** — expires 30 days after creation, then gets
  permanently deleted after a 14-day grace period. Use only for a
  throwaway demo.
- **Supabase** — also a solid permanent free tier, a fine alternative to
  Neon. Its free projects pause after about a week of inactivity, but they
  restore automatically on the next request.
- **Railway** — no longer has an ongoing free Postgres tier, just a
  one-time trial credit, then it's a paid usage-based plan.
- **Google Cloud Run / Fly.io** (as app hosts) — both have real free
  compute, but Spring Boot's JVM footprint needs manual heap tuning to fit
  their default memory limits, and cold starts run 10–20s+. Render's
  Docker-based deploy is the least fiddly starting point.

---

## 2. Step-by-step: Neon (database)

1. Go to [neon.tech](https://neon.tech) and sign up (GitHub login works).
2. Create a new project. Pick any region close to where Render will run
   your app (Render's free tier defaults to Oregon, US West).
3. Once created, open the project's **Connection Details** panel and copy
   the connection string. It looks like:
   ```
   postgresql://<user>:<password>@<host>.neon.tech/<database>?sslmode=require
   ```
4. You'll split this into three env vars for the app:
   - `DB_URL` → `jdbc:postgresql://<host>.neon.tech/<database>?sslmode=require`
     (note the `jdbc:` prefix gets added in front of the plain Postgres URL)
   - `DB_USERNAME` → `<user>`
   - `DB_PASSWORD` → `<password>`

Nothing else to do here — you do **not** need to run the Flyway migration
manually. The app runs it automatically on first startup.

---

## 3. Step-by-step: Render (app hosting)

1. Push this project to a GitHub repository (Render deploys from Git).
2. Go to [render.com](https://render.com) and sign up.
3. Click **New → Web Service**.
4. Connect the GitHub repo containing this project.
5. Render will detect the `Dockerfile` at the project root — choose
   **Docker** as the environment if it isn't auto-selected.
6. Under **Environment Variables**, add:

   | Key | Value |
   |---|---|
   | `DB_URL` | `jdbc:postgresql://<host>.neon.tech/<database>?sslmode=require` |
   | `DB_USERNAME` | from Neon |
   | `DB_PASSWORD` | from Neon |
   | `PORT` | `8080` (Render sets this automatically, but the app already reads it either way) |

7. Click **Create Web Service**. Render builds the Docker image and
   deploys it — the first build takes a few minutes.
8. Once live, check `https://<your-service>.onrender.com/health` — you
   should see `{"status":"ok","service":"lease-application"}`.

Flyway applies `V1__init.sql` against your Neon database automatically the
first time the app boots, so the schema is ready with no manual SQL step.

---

## 4. What to expect on the free tier

- **Cold starts**: Render's free web services spin down after 15 minutes
  of inactivity. The next request wakes it back up, which typically takes
  under a minute but can run longer for a JVM app. Fine for a demo or low
  traffic side project; not something to build a latency-sensitive
  production app on.
- **Instance hours**: Render grants 750 free instance-hours per workspace
  per month. A spun-down service doesn't consume hours, so a low-traffic
  app comfortably stays within the limit.
- **No backups on either free tier**: If the data in Neon ever matters,
  set up a periodic `pg_dump` (a weekly cron job or a step in CI) rather
  than relying on the provider:
  ```bash
  pg_dump "postgresql://<user>:<password>@<host>.neon.tech/<database>?sslmode=require" \
    | gzip > backup-$(date +%F).sql.gz
  ```

---

## 5. Moving off free tiers later

If this outgrows the free tier (real users, latency requirements,
uptime guarantees), the cheapest realistic next step is usually:
- Render's paid web service (~$7/month) removes the cold-start/sleep
  behavior.
- Neon and Supabase both have inexpensive paid tiers that add real
  backups and point-in-time recovery.

Both are drop-in upgrades — you're not re-architecting anything, just
changing a plan and possibly the env vars if the connection string changes.
