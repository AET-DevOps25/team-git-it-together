# Monitoring Stack

Concise reference for the local Prometheus + Grafana stack that monitors SkillForge micro-services.

## Stack Components

| Layer | Container | Host Port(s) | Purpose |
| --- | --- | --- | --- |
| Prometheus | `prometheus` | **9090** | Scrapes metrics & evaluates rules (`prometheus.yml`) |
| Alertmanager | `alertmanager` | **9093** | Sends alerts → MailHog (`alertmanager.yml`) |
| Grafana | `grafana` | **3001** | Dashboards & system-wide alert list |
| Loki | `loki` | **3100** | Central log store |
| Promtail | `promtail` | – | Ships container logs → Loki |
| Mongo Exporter | `mongo-exporter` | **9216** | MongoDB metrics |
| MailHog | `mailhog` | **8025 UI** / 1025 SMTP | Captures alert e-mails |
| Traefik | `reverse-proxy` | 80 / **8085** | Public entry & metrics |

### Starting the Stack

```bash
# Start all services
docker compose up -d
```

All containers use `skillforge-network`; Prometheus scrapes via container names (e.g. `skillforge-user-service:8082`).

## Alerts

Alert rules are defined in `monitoring/prometheus/alert.rules.yml`, covering:

* Service availability (exporters, GenAI, Spring Boot)
* JVM heap thresholds
* Security issues (e.g. high auth failures)

See [ALERTS.md](ALERTS.md) for full alert list, testing instructions, and UI links.

## Dashboards

Dashboards are auto-loaded from `monitoring/grafana/dashboards/`:

| File | Focus | Data-sources |
| --- | --- | --- |
| `genai.json` | GenAI FastAPI metrics | Prometheus |
| `mongo.json` | MongoDB internals | Prometheus |
| `server.json` | Spring Boot JVM + HTTP | Prometheus |
| `user-custom-metrics.json` | Auth & signups | Prometheus |
| `logs.json` | Logs + active alerts | Loki + Prometheus |

Each dashboard includes test steps to simulate real traffic and failures.

See `grafana/README.md` for full details.

## Exporter Endpoints

| Source | Endpoint | Scrape job |
| --- | --- | --- |
| Spring Boot | `/actuator/prometheus` | `spring-boot` |
| GenAI | `/metrics` | `genai` |
| Traefik | `:8085/metrics` | `traefik` |
| Mongo Exporter | `:9216/metrics` | `mongo_exporter` |

Happy monitoring! Extend dashboards, adjust alert thresholds, or plug in more exporters as needed.