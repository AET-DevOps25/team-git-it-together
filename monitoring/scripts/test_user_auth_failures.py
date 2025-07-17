import requests
import time

GATEWAY_URL = "http://server.localhost:8081/api/v1/users/login"
USERNAME = "max123"
PASSWORD = "wrong_password"
TOTAL_FAILURES = 60   # >50 in 2m to trigger alert

def main():
    print(f"Sending {TOTAL_FAILURES} failed logins to trigger alert...")
    session = requests.Session()
    payload = {"username": USERNAME, "password": PASSWORD}
    headers = {"Content-Type": "application/json"}

    interval = 0.2  # 5 requests per second (under gateway limit)
    for i in range(1, TOTAL_FAILURES + 1):
        r = session.post(GATEWAY_URL, json=payload, headers=headers)
        print(f"[{i}] Status: {r.status_code}", flush=True)
        time.sleep(interval)

    print("Done. Check Grafana/Prometheus for alert firing.")

if __name__ == "__main__":
    main()
