# Billing Service - Testing Guide

## Overview

This guide provides comprehensive instructions for testing the Billing Service SOAP API using various tools and methods.

## Prerequisites

- Service running at `http://localhost:5000`
- PostgreSQL database initialized
- SOAP client tool (curl, Postman, SoapUI, or PowerShell)

## Quick Start

### 1. Verify Service is Running

```powershell
# Check WSDL availability
curl http://localhost:5000/BillingService.asmx?wsdl

# Or in browser
Start-Process "http://localhost:5000/BillingService.asmx?wsdl"
```

### 2. Test Basic Operation

```powershell
# Get service info
$body = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <bil:GetServiceInfo/>
   </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST `
  -ContentType "text/xml; charset=utf-8" `
  -Body $body
```

## Testing Methods

## Method 1: Using PowerShell

### A. Create Invoice

```powershell
$createInvoice = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <bil:CreateInvoice>
         <bil:request>
            <bil:StudentId>STU001</bil:StudentId>
            <bil:DueDate>2024-12-31T00:00:00</bil:DueDate>
            <bil:Items>
               <bil:InvoiceItemDTO>
                  <bil:Description>Tuition Fee - Fall 2024</bil:Description>
                  <bil:Amount>5000.00</bil:Amount>
                  <bil:Quantity>1</bil:Quantity>
               </bil:InvoiceItemDTO>
               <bil:InvoiceItemDTO>
                  <bil:Description>Lab Fee</bil:Description>
                  <bil:Amount>250.00</bil:Amount>
                  <bil:Quantity>1</bil:Quantity>
               </bil:InvoiceItemDTO>
            </bil:Items>
         </bil:request>
      </bil:CreateInvoice>
   </soapenv:Body>
</soapenv:Envelope>
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST `
  -ContentType "text/xml; charset=utf-8" `
  -Body $createInvoice

$response.Content
```

### B. Get Invoice

```powershell
$getInvoice = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <bil:GetInvoice>
         <bil:invoiceId>1</bil:invoiceId>
      </bil:GetInvoice>
   </soapenv:Body>
</soapenv:Envelope>
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST `
  -ContentType "text/xml; charset=utf-8" `
  -Body $getInvoice

$response.Content
```

### C. Record Payment

```powershell
$recordPayment = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <bil:RecordPayment>
         <bil:request>
            <bil:InvoiceId>1</bil:InvoiceId>
            <bil:Amount>5250.00</bil:Amount>
            <bil:PaymentMethod>CREDIT_CARD</bil:PaymentMethod>
            <bil:TransactionReference>TXN-2024-001234</bil:TransactionReference>
         </bil:request>
      </bil:RecordPayment>
   </soapenv:Body>
</soapenv:Envelope>
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST `
  -ContentType "text/xml; charset=utf-8" `
  -Body $recordPayment

$response.Content
```

### D. Get Financial Summary

```powershell
$getSummary = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <bil:GetFinancialSummary>
         <bil:studentId>STU001</bil:studentId>
         <bil:year>2024</bil:year>
      </bil:GetFinancialSummary>
   </soapenv:Body>
</soapenv:Envelope>
"@

$response = Invoke-WebRequest `
  -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST `
  -ContentType "text/xml; charset=utf-8" `
  -Body $getSummary

$response.Content
```

## Method 2: Using curl

### Create Invoice

```bash
curl -X POST http://localhost:5000/BillingService.asmx \
  -H "Content-Type: text/xml; charset=utf-8" \
  -d @test-requests/01-create-invoice.xml
```

### Get Invoice

```bash
curl -X POST http://localhost:5000/BillingService.asmx \
  -H "Content-Type: text/xml; charset=utf-8" \
  -d @test-requests/02-get-invoice.xml
```

### Record Payment

```bash
curl -X POST http://localhost:5000/BillingService.asmx \
  -H "Content-Type: text/xml; charset=utf-8" \
  -d @test-requests/04-record-payment.xml
```

## Method 3: Using Postman

### Setup

1. Open Postman
2. Create new request
3. Set method to `POST`
4. Set URL to `http://localhost:5000/BillingService.asmx`
5. Set headers:
   - `Content-Type`: `text/xml; charset=utf-8`

### Import WSDL

1. Click "Import" button
2. Select "Link"
3. Enter: `http://localhost:5000/BillingService.asmx?wsdl`
4. Postman will generate requests for all operations

### Manual Request

1. Go to "Body" tab
2. Select "raw" and "XML"
3. Paste SOAP envelope from test-requests folder
4. Click "Send"

## Method 4: Using SoapUI

### Setup

1. Download and install SoapUI
2. Create new SOAP project
3. Enter WSDL URL: `http://localhost:5000/BillingService.asmx?wsdl`
4. SoapUI will generate sample requests

### Execute Requests

1. Expand service tree
2. Double-click operation
3. Modify request XML as needed
4. Click green play button

## Complete Test Scenario

### Scenario: Student Invoice Lifecycle

```powershell
# Step 1: Create invoice for student
Write-Host "Creating invoice for STU001..." -ForegroundColor Cyan
$invoice = Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST -ContentType "text/xml; charset=utf-8" `
  -Body (Get-Content "test-requests/01-create-invoice.xml" -Raw)
Write-Host $invoice.Content

Start-Sleep -Seconds 2

# Step 2: Get invoice details
Write-Host "`nRetrieving invoice..." -ForegroundColor Cyan
$details = Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST -ContentType "text/xml; charset=utf-8" `
  -Body (Get-Content "test-requests/02-get-invoice.xml" -Raw)
Write-Host $details.Content

Start-Sleep -Seconds 2

# Step 3: Check outstanding balance
Write-Host "`nChecking outstanding balance..." -ForegroundColor Cyan
$balance = Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST -ContentType "text/xml; charset=utf-8" `
  -Body (Get-Content "test-requests/07-calculate-outstanding-balance.xml" -Raw)
Write-Host $balance.Content

Start-Sleep -Seconds 2

# Step 4: Record payment
Write-Host "`nRecording payment..." -ForegroundColor Cyan
$payment = Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST -ContentType "text/xml; charset=utf-8" `
  -Body (Get-Content "test-requests/04-record-payment.xml" -Raw)
Write-Host $payment.Content

Start-Sleep -Seconds 2

# Step 5: Verify payment
Write-Host "`nVerifying payment history..." -ForegroundColor Cyan
$history = Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST -ContentType "text/xml; charset=utf-8" `
  -Body (Get-Content "test-requests/05-get-payment-history.xml" -Raw)
Write-Host $history.Content

Start-Sleep -Seconds 2

# Step 6: Get financial summary
Write-Host "`nGetting financial summary..." -ForegroundColor Cyan
$summary = Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx" `
  -Method POST -ContentType "text/xml; charset=utf-8" `
  -Body (Get-Content "test-requests/08-get-financial-summary.xml" -Raw)
Write-Host $summary.Content

Write-Host "`nTest scenario completed!" -ForegroundColor Green
```

Save this as `test-scenario.ps1` and run with:

```powershell
./test-scenario.ps1
```

## Testing All Operations

### Using Test Request Files

```powershell
# Test all operations
$testFiles = Get-ChildItem -Path "test-requests" -Filter "*.xml" | Sort-Object Name

foreach ($file in $testFiles) {
    Write-Host "`nTesting: $($file.BaseName)" -ForegroundColor Yellow
    Write-Host "=" * 50

    $body = Get-Content $file.FullName -Raw

    try {
        $response = Invoke-WebRequest `
          -Uri "http://localhost:5000/BillingService.asmx" `
          -Method POST `
          -ContentType "text/xml; charset=utf-8" `
          -Body $body

        Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host $response.Content
    }
    catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }

    Start-Sleep -Seconds 1
}
```

## Expected Responses

### CreateInvoice Response

```xml
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
  <s:Body>
    <CreateInvoiceResponse xmlns="http://tempuri.org/">
      <CreateInvoiceResult>
        <Id>1</Id>
        <InvoiceNumber>INV-2024-01-00001</InvoiceNumber>
        <StudentId>STU001</StudentId>
        <TotalAmount>5250.00</TotalAmount>
        <AmountPaid>0</AmountPaid>
        <Status>PENDING</Status>
        <DueDate>2024-12-31T00:00:00</DueDate>
        <Items>
          <InvoiceItemDTO>
            <Description>Tuition Fee - Fall 2024</Description>
            <Amount>5000.00</Amount>
            <Quantity>1</Quantity>
          </InvoiceItemDTO>
          <InvoiceItemDTO>
            <Description>Lab Fee</Description>
            <Amount>250.00</Amount>
            <Quantity>1</Quantity>
          </InvoiceItemDTO>
        </Items>
      </CreateInvoiceResult>
    </CreateInvoiceResponse>
  </s:Body>
</s:Envelope>
```

### RecordPayment Response

```xml
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
  <s:Body>
    <RecordPaymentResponse xmlns="http://tempuri.org/">
      <RecordPaymentResult>
        <Id>1</Id>
        <InvoiceId>1</InvoiceId>
        <Amount>5250.00</Amount>
        <PaymentDate>2024-01-15T10:30:00</PaymentDate>
        <PaymentMethod>CREDIT_CARD</PaymentMethod>
        <TransactionReference>TXN-2024-001234</TransactionReference>
        <Status>COMPLETED</Status>
      </RecordPaymentResult>
    </RecordPaymentResponse>
  </s:Body>
</s:Envelope>
```

## Error Scenarios

### Invalid Invoice ID

```powershell
$invalidInvoice = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Body>
      <bil:GetInvoice>
         <bil:invoiceId>99999</bil:invoiceId>
      </bil:GetInvoice>
   </soapenv:Body>
</soapenv:Envelope>
"@

# Expected: SOAP Fault with "Invoice not found" message
```

### Invalid Payment Amount

```powershell
$invalidPayment = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Body>
      <bil:RecordPayment>
         <bil:request>
            <bil:InvoiceId>1</bil:InvoiceId>
            <bil:Amount>-100.00</bil:Amount>
            <bil:PaymentMethod>CASH</bil:PaymentMethod>
         </bil:request>
      </bil:RecordPayment>
   </soapenv:Body>
</soapenv:Envelope>
"@

# Expected: SOAP Fault with "Amount must be greater than 0" message
```

## Database Verification

### Check Database State

```sql
-- Connect to PostgreSQL
psql -h localhost -p 5436 -U postgres -d billing_db

-- View invoices
SELECT * FROM invoices ORDER BY created_at DESC;

-- View payments
SELECT * FROM payments ORDER BY created_at DESC;

-- View invoice items
SELECT
  i.invoice_number,
  ii.description,
  ii.amount,
  ii.quantity
FROM invoice_items ii
JOIN invoices i ON i.id = ii.invoice_id
ORDER BY i.created_at DESC;

-- Financial summary
SELECT
  student_id,
  COUNT(*) as invoice_count,
  SUM(total_amount) as total_invoiced,
  SUM(amount_paid) as total_paid,
  SUM(total_amount - amount_paid) as outstanding
FROM invoices
GROUP BY student_id;
```

## Performance Testing

### Load Test with Multiple Requests

```powershell
# Create 100 invoices
1..100 | ForEach-Object -Parallel {
    $studentId = "STU" + ($_ % 10).ToString("000")

    $body = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:bil="http://tempuri.org/">
   <soapenv:Body>
      <bil:CreateInvoice>
         <bil:request>
            <bil:StudentId>$studentId</bil:StudentId>
            <bil:DueDate>2024-12-31T00:00:00</bil:DueDate>
            <bil:Items>
               <bil:InvoiceItemDTO>
                  <bil:Description>Tuition Fee</bil:Description>
                  <bil:Amount>5000.00</bil:Amount>
                  <bil:Quantity>1</bil:Quantity>
               </bil:InvoiceItemDTO>
            </bil:Items>
         </bil:request>
      </bil:CreateInvoice>
   </soapenv:Body>
</soapenv:Envelope>
"@

    $response = Invoke-WebRequest `
      -Uri "http://localhost:5000/BillingService.asmx" `
      -Method POST `
      -ContentType "text/xml; charset=utf-8" `
      -Body $body

    Write-Output "Created invoice for $studentId"
} -ThrottleLimit 10
```

## Troubleshooting

### Common Issues

#### 1. Connection Refused

```
Error: Unable to connect to the remote server
```

**Solution:** Verify service is running on port 5000

#### 2. Database Connection Error

```
Error: Connection to database failed
```

**Solution:** Check PostgreSQL is running and credentials are correct

#### 3. SOAP Fault

```xml
<s:Fault>
  <faultcode>s:Server</faultcode>
  <faultstring>...</faultstring>
</s:Fault>
```

**Solution:** Check request format and input values

### Debug Mode

Enable detailed logging in appsettings.json:

```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Debug",
      "Microsoft.AspNetCore": "Debug"
    }
  }
}
```

## CI/CD Integration

### Automated Testing Script

```powershell
# test.ps1
$ErrorActionPreference = "Stop"

Write-Host "Starting Billing Service Tests..." -ForegroundColor Cyan

# Wait for service to be ready
$maxAttempts = 30
$attempt = 0
while ($attempt -lt $maxAttempts) {
    try {
        $response = Invoke-WebRequest "http://localhost:5000/BillingService.asmx?wsdl" -TimeoutSec 2
        if ($response.StatusCode -eq 200) {
            Write-Host "Service is ready!" -ForegroundColor Green
            break
        }
    }
    catch {
        $attempt++
        Write-Host "Waiting for service... ($attempt/$maxAttempts)"
        Start-Sleep -Seconds 2
    }
}

if ($attempt -eq $maxAttempts) {
    Write-Host "Service failed to start!" -ForegroundColor Red
    exit 1
}

# Run tests
$passed = 0
$failed = 0

$testFiles = Get-ChildItem "test-requests" -Filter "*.xml"

foreach ($file in $testFiles) {
    try {
        $body = Get-Content $file.FullName -Raw
        $response = Invoke-WebRequest `
          -Uri "http://localhost:5000/BillingService.asmx" `
          -Method POST `
          -ContentType "text/xml; charset=utf-8" `
          -Body $body

        Write-Host "✓ $($file.BaseName)" -ForegroundColor Green
        $passed++
    }
    catch {
        Write-Host "✗ $($file.BaseName): $($_.Exception.Message)" -ForegroundColor Red
        $failed++
    }
}

Write-Host "`nTest Results:" -ForegroundColor Cyan
Write-Host "Passed: $passed" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor Red

if ($failed -gt 0) {
    exit 1
}
```

## Additional Resources

- WSDL: http://localhost:5000/BillingService.asmx?wsdl
- Logs: `logs/billing-service-*.txt`
- Database: PostgreSQL on port 5436
- README.md for service documentation
