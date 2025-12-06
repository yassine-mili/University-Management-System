import requests
import time

BASE = "http://localhost:5000"

# wait for service
for i in range(12):
    try:
        r = requests.get(BASE + "/")
        print("GET / ->", r.status_code, r.text[:120])
        break
    except Exception as e:
        print("Waiting for service...", e)
        time.sleep(2)
else:
    print("Service did not start")
    raise SystemExit(1)

# Create invoice
payload = {
    "InvoiceNumber": "TND-2025-TEST-001",
    "Amount": 1000.0,
    "Currency": "TND",
    "UniversityId": "TUNTEST001",
    "FirstName": "Test",
    "LastName": "Student"
}

r = requests.post(BASE + "/api/invoices", json=payload, timeout=10)
print("POST /api/invoices ->", r.status_code)
print(r.text)

if r.status_code in (200,201):
    # fetch
    inv_num = payload["InvoiceNumber"]
    r2 = requests.get(BASE + f"/api/invoices/{inv_num}", timeout=10)
    print("GET invoice ->", r2.status_code)
    print(r2.text)
else:
    raise SystemExit(1)
