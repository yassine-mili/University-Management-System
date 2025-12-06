# Billing Service

This service implements university billing (invoices) with both REST and SOAP endpoints.

Features
- REST API: POST `/api/invoices`, GET `/api/invoices/{invoiceNumber}`
- SOAP API: WSDL at `/InvoiceService.svc?wsdl`, operations `CreateInvoice` and `GetInvoiceByNumber`
- EF Core + SQL Server persistence
- Dockerized + `docker-compose.yml` for local development
- Tests: `tests/test_rest_client.py` and `tests/test_soap_client.py`

Quickstart

1. Ensure Docker Desktop is running and `.env` contains `SA_PASSWORD`.
2. From this folder:

```powershell
cd C:\Users\Lenovo\Desktop\University-Management-System\services\billing-service
docker compose down
docker compose up --build -d
```

3. Wait for containers to start (SQL Server may take 20–40s).

4. Smoke tests

- REST:
```powershell
Invoke-RestMethod -Uri 'http://localhost:5000/' -Method Get
```
- SOAP WSDL:
```powershell
curl http://localhost:5000/InvoiceService.svc?wsdl
```
- Run automated tests:
```powershell
python .\tests\test_rest_client.py
python .\tests\test_soap_client.py
```

Notes
- The SOAP endpoint is implemented with SoapCore and maps to the same `InvoiceService` business logic used by the REST API.
- For development we use `db.Database.EnsureCreated()`; for production switch to EF Migrations (`dotnet ef migrations add` / `dotnet ef database update`).
- If Docker push fails due to authentication, run `git push` from your environment where credentials are set.

Troubleshooting
- If you see connection errors, check `docker logs billing-sql` and `docker logs billing-service-billing-service-1`.
- If the SOAP client fails with 500, inspect the WSDL and the app logs for operation names and payload shape.

Contact
- For issues, open an issue or contact the project maintainer.
