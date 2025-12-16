# Billing Service - Quick Reference

## Service Information

- **Name**: Billing Service
- **Technology**: .NET Core 8.0 + SoapCore
- **Protocol**: SOAP 1.1
- **Port**: 5000
- **Database**: PostgreSQL (port 5436)
- **Endpoint**: http://localhost:5000/BillingService.asmx
- **WSDL**: http://localhost:5000/BillingService.asmx?wsdl

## Quick Start Commands

### Local Development

```powershell
# Build and run
./run.ps1

# Or manually
dotnet restore
dotnet build
dotnet run
```

### Docker

```powershell
# Build and start
./build-docker.ps1

# Stop
./build-docker.ps1 -Down
```

### Testing

```powershell
# Test service info
Invoke-WebRequest -Uri "http://localhost:5000/BillingService.asmx?wsdl"

# Run test scenario
./test-scenario.ps1
```

## SOAP Operations Summary

### 1. CreateInvoice

**Purpose**: Create new invoice for student  
**Input**: StudentId, DueDate, Items[]  
**Output**: InvoiceDTO  
**Example**:

```xml
<bil:CreateInvoice>
  <bil:request>
    <bil:StudentId>STU001</bil:StudentId>
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
```

### 2. GetInvoice

**Purpose**: Retrieve invoice by ID  
**Input**: invoiceId (int)  
**Output**: InvoiceDTO  
**Example**:

```xml
<bil:GetInvoice>
  <bil:invoiceId>1</bil:invoiceId>
</bil:GetInvoice>
```

### 3. GetInvoicesByStudent

**Purpose**: Get all invoices for a student  
**Input**: studentId (string)  
**Output**: List<InvoiceDTO>  
**Example**:

```xml
<bil:GetInvoicesByStudent>
  <bil:studentId>STU001</bil:studentId>
</bil:GetInvoicesByStudent>
```

### 4. RecordPayment

**Purpose**: Record payment against invoice  
**Input**: InvoiceId, Amount, PaymentMethod, TransactionReference  
**Output**: PaymentDTO  
**Example**:

```xml
<bil:RecordPayment>
  <bil:request>
    <bil:InvoiceId>1</bil:InvoiceId>
    <bil:Amount>5000.00</bil:Amount>
    <bil:PaymentMethod>CREDIT_CARD</bil:PaymentMethod>
    <bil:TransactionReference>TXN-123</bil:TransactionReference>
  </bil:request>
</bil:RecordPayment>
```

### 5. GetPaymentHistory

**Purpose**: Get all payments for student  
**Input**: studentId (string)  
**Output**: List<PaymentDTO>  
**Example**:

```xml
<bil:GetPaymentHistory>
  <bil:studentId>STU001</bil:studentId>
</bil:GetPaymentHistory>
```

### 6. GetFeeStructure

**Purpose**: Get active fees for academic period  
**Input**: academicYear (int), semester (string)  
**Output**: List<FeeStructureDTO>  
**Example**:

```xml
<bil:GetFeeStructure>
  <bil:academicYear>2024</bil:academicYear>
  <bil:semester>FALL</bil:semester>
</bil:GetFeeStructure>
```

### 7. CalculateOutstandingBalance

**Purpose**: Calculate total outstanding for student  
**Input**: studentId (string)  
**Output**: decimal  
**Example**:

```xml
<bil:CalculateOutstandingBalance>
  <bil:studentId>STU001</bil:studentId>
</bil:CalculateOutstandingBalance>
```

### 8. GetFinancialSummary

**Purpose**: Get financial summary for student/year  
**Input**: studentId (string), year (int)  
**Output**: FinancialSummaryDTO  
**Example**:

```xml
<bil:GetFinancialSummary>
  <bil:studentId>STU001</bil:studentId>
  <bil:year>2024</bil:year>
</bil:GetFinancialSummary>
```

### 9. GetServiceInfo

**Purpose**: Get service metadata  
**Input**: None  
**Output**: string  
**Example**:

```xml
<bil:GetServiceInfo/>
```

## Data Types

### Invoice Status

- `PENDING` - Not paid
- `PAID` - Fully paid
- `OVERDUE` - Past due date
- `CANCELLED` - Cancelled

### Payment Method

- `CASH`
- `CREDIT_CARD`
- `DEBIT_CARD`
- `BANK_TRANSFER`
- `CHECK`
- `ONLINE`

### Payment Status

- `PENDING` - Processing
- `COMPLETED` - Success
- `FAILED` - Failed
- `REFUNDED` - Refunded

### Fee Types

- `TUITION` - Tuition fees
- `LAB` - Laboratory fees
- `LIBRARY` - Library fees
- `SPORTS` - Sports fees
- `EXAMINATION` - Exam fees
- `REGISTRATION` - Registration fees
- `HOSTEL` - Hostel fees
- `TRANSPORTATION` - Transport fees
- `MISCELLANEOUS` - Other fees

### Semester

- `FALL`
- `SPRING`
- `SUMMER`

### Student Level

- `UNDERGRADUATE`
- `GRADUATE`
- `DOCTORAL`

## Database Schema

### invoices

```sql
id (PK)
invoice_number (UNIQUE)
student_id
total_amount
amount_paid
status
due_date
created_at
updated_at
```

### invoice_items

```sql
id (PK)
invoice_id (FK)
description
amount
quantity
created_at
```

### payments

```sql
id (PK)
invoice_id (FK)
amount
payment_date
payment_method
transaction_reference
status
created_at
```

### fee_structure

```sql
id (PK)
fee_type
amount
academic_year
semester
program
level
is_active
created_at
updated_at
```

## Common Queries

### Get All Pending Invoices

```sql
SELECT * FROM invoices
WHERE status = 'PENDING'
ORDER BY due_date;
```

### Get Overdue Invoices

```sql
SELECT * FROM invoices
WHERE status = 'OVERDUE'
OR (status = 'PENDING' AND due_date < NOW());
```

### Student Total Paid

```sql
SELECT
  student_id,
  SUM(amount_paid) as total_paid
FROM invoices
GROUP BY student_id;
```

### Monthly Revenue

```sql
SELECT
  DATE_TRUNC('month', payment_date) as month,
  SUM(amount) as revenue
FROM payments
WHERE status = 'COMPLETED'
GROUP BY month
ORDER BY month DESC;
```

## PowerShell Test Snippet

```powershell
# Quick test function
function Test-BillingService {
    param([string]$Operation)

    $body = Get-Content "test-requests/$Operation.xml" -Raw

    $response = Invoke-WebRequest `
      -Uri "http://localhost:5000/BillingService.asmx" `
      -Method POST `
      -ContentType "text/xml; charset=utf-8" `
      -Body $body

    [xml]$xml = $response.Content
    $xml.Envelope.Body.InnerXml | Format-Xml
}

# Usage
Test-BillingService "01-create-invoice"
Test-BillingService "02-get-invoice"
```

## curl Test Snippet

```bash
# Quick test function
test_billing() {
    curl -X POST http://localhost:5000/BillingService.asmx \
      -H "Content-Type: text/xml; charset=utf-8" \
      -d @test-requests/$1.xml
}

# Usage
test_billing "01-create-invoice"
test_billing "02-get-invoice"
```

## Environment Variables

### Development

```
ASPNETCORE_ENVIRONMENT=Development
ConnectionStrings__DefaultConnection=Host=localhost;Port=5432;Database=billing_db;Username=postgres;Password=postgres
```

### Docker

```
ASPNETCORE_ENVIRONMENT=docker
ConnectionStrings__DefaultConnection=Host=billing_db;Port=5432;Database=billing_db;Username=postgres;Password=postgres
```

## Logging

### Log Locations

- Console: stdout
- File: `logs/billing-service-YYYYMMDD.txt`

### Log Levels

- Information: Normal operations
- Warning: Unexpected situations
- Error: Operation failures

### Sample Log Entry

```
2024-01-15 10:30:45.123 [Information] Invoice created: INV-2024-01-00001 for student STU001
2024-01-15 10:31:20.456 [Information] Payment recorded: $5000.00 for invoice 1
```

## Troubleshooting

### Service Won't Start

```powershell
# Check port availability
Test-NetConnection -ComputerName localhost -Port 5000

# Check database connectivity
Test-NetConnection -ComputerName localhost -Port 5436
```

### Database Issues

```powershell
# Reset database
docker-compose -f docker/docker-compose.yml down billing_db
docker volume rm docker_billing_db_data
docker-compose -f docker/docker-compose.yml up -d billing_db
```

### View Logs

```powershell
# Docker logs
docker-compose -f docker/docker-compose.yml logs -f billing_service

# Local logs
Get-Content logs/billing-service-*.txt -Tail 50 -Wait
```

## Performance Tips

1. **Connection Pooling**: Automatically handled by Npgsql
2. **Async Operations**: All database operations are async
3. **Indexing**: Database has indexes on student_id, invoice_number
4. **Batch Operations**: Use GetInvoicesByStudent for multiple invoices

## Security Notes

- Input validation on all operations
- SQL injection prevention via EF Core
- Transaction support for payments
- Audit trail with timestamps
- No sensitive data in logs

## Integration Points

### With Student Service

- Student ID validation
- Enrollment status checks

### With Courses Service

- Course fee calculation
- Registration fees

### With Auth Service

- User authentication
- Role-based access

## Next Steps

1. Review README.md for detailed documentation
2. Check TESTING.md for testing procedures
3. Run test scenario with `./test-scenario.ps1`
4. Explore WSDL at http://localhost:5000/BillingService.asmx?wsdl
5. Monitor logs during operation

## Support Resources

- **WSDL Documentation**: http://localhost:5000/BillingService.asmx?wsdl
- **Test Requests**: `test-requests/` folder
- **Database Schema**: See README.md
- **API Documentation**: See README.md

## Version Information

**Current Version**: 1.0.0  
**Last Updated**: January 2024  
**.NET Version**: 8.0  
**SoapCore Version**: 1.1.0  
**PostgreSQL Version**: 14
