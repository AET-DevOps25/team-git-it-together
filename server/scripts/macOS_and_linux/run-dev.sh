#!/usr/bin/env bash
set -euo pipefail

# ── Load environment ───────────────────────────────
ENV_FILE=".env.dev"
if [[ -f "$ENV_FILE" ]]; then
  echo "📦 Loading environment from $ENV_FILE..."
  export $(grep -vE '^\s*#' "$ENV_FILE" | xargs)
else
  echo "⚠️  $ENV_FILE not found — using defaults."
fi

# ── Defaults if not set ────────────────────────────
: "${SPRING_PROFILES_ACTIVE:=dev}"
: "${APP_NAME:=skill-forge-server}"

# ── Run Spring Boot ───────────────────────────────
echo "▶️  Running ${APP_NAME} with Spring profile '${SPRING_PROFILES_ACTIVE}'"
./gradlew bootRun -Dspring.profiles.active="$SPRING_PROFILES_ACTIVE"
