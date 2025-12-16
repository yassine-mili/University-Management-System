# Billing Service - .NET Core SOAP Service

## Overview

The Billing Service is a SOAP-based web service built with .NET Core 8.0 and SoapCore. It manages student invoices, payments, and fee structures for the University Management System.

## Technology Stack

- **.NET Core 8.0**: Runtime and framework
- **SoapCore 1.1.0**: SOAP/WSDL support for ASP.NET Core
- **Entity Framework Core 8.0**: ORM for database operations
- **PostgreSQL**: Relational database
- **Npgsql**: PostgreSQL provider for EF Core
- **Serilog**: Structured logging

## Features

- ✅ Invoice creation and management
- ✅ Payment processing and tracking
- ✅ Fee structure management
- ✅ Outstanding balance calculation
- ✅ Financial summaries and reports
- ✅ Automatic overdue detection
- ✅ Payment history tracking
- ✅ SOAP 1.1 protocol support
- ✅ Automatic WSDL generation

## Architecture

### Database Schema

The service uses 4 main tables:

#### 1. **invoices**

- `id` (int, PK): Invoice ID
- `invoice_number` (varchar): Unique invoice number (INV-YYYY-MM-XXXXX)
- `student_id` (varchar): Student identifier
- `total_amount` (decimal): Total invoice amount
- `amount_paid` (decimal): Amount already paid
- `status` (varchar): PENDING, PAID, OVERDUE, CANCELLED
- `due_date` (timestamp): Payment deadline
- `created_at` (timestamp): Creation timestamp
- `updated_at` (timestamp): Last update timestamp

#### 2. **invoice_items**

- `id` (int, PK): Item ID
- `invoice_id` (int, FK): Reference to invoice
- `description` (varchar): Item description
- `amount` (decimal): Item unit price
- `quantity` (int): Quantity
- `created_at` (timestamp): Creation timestamp

#### 3. **payments**

- `id` (int, PK): Payment ID
- `invoice_id` (int, FK): Reference to invoice
- `amount` (decimal): Payment amount
- `payment_date` (timestamp): Payment timestamp
- `payment_method` (varchar): CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CHECK, ONLINE
- `transaction_reference` (varchar): Payment reference number
- `status` (varchar): PENDING, COMPLETED, FAILED, REFUNDED
- `created_at` (timestamp): Creation timestamp

#### 4. **fee_structure**

- `id` (int, PK): Fee ID
- `fee_type` (varchar): TUITION, LAB, LIBRARY, SPORTS, EXAMINATION, REGISTRATION, etc.
- `amount` (decimal): Fee amount
- `academic_year` (int): Year (e.g., 2024)
- `semester` (varchar): FALL, SPRING, SUMMER
- `program` (varchar): Program/department code
- `level` (varchar): UNDERGRADUATE, GRADUATE, DOCTORAL
- `is_active` (boolean): Active status
- `created_at` (timestamp): Creation timestamp
- `updated_at` (timestamp): Last update timestamp

### Service Operations

#### 1. **CreateInvoice**

Creates a new invoice for a student.

**Input:**

- `StudentId` (string): Student identifier
- `DueDate` (DateTime): Payment deadline
- `Items` (List): Invoice items with description, amount, quantity

**Output:**

- `InvoiceDTO`: Created invoice with generated invoice number

**Business Logic:**

- Generates unique invoice number (format: INV-YYYY-MM-XXXXX)
- Calculates total amount from items
- Sets initial status to PENDING
- Validates due date (must be future date)

#### 2. **GetInvoice**

Retrieves an invoice by ID.

**Input:**

- `invoiceId` (int): Invoice ID

**Output:**

- `InvoiceDTO`: Invoice details with items

**Business Logic:**

- Updates status to OVERDUE if past due date and unpaid
- Returns invoice with all items

#### 3. **GetInvoicesByStudent**

Gets all invoices for a specific student.

**Input:**

- `studentId` (string): Student identifier

**Output:**

- `List<InvoiceDTO>`: All student invoices

**Business Logic:**

- Updates overdue statuses
- Orders by creation date (newest first)

#### 4. **RecordPayment**

Records a payment against an invoice.

**Input:**

- `InvoiceId` (int): Invoice to pay
- `Amount` (decimal): Payment amount
- `PaymentMethod` (string): Payment method
- `TransactionReference` (string): Payment reference

**Output:**

- `PaymentDTO`: Payment record

**Business Logic:**

- Validates payment amount > 0
- Updates invoice's amount_paid
- Changes invoice status to PAID when fully paid
- Creates payment record with COMPLETED status

#### 5. **GetPaymentHistory**

Gets all payments for a student.

**Input:**

- `studentId` (string): Student identifier

**Output:**

- `List<PaymentDTO>`: Payment history

**Business Logic:**

- Retrieves all payments via student's invoices
- Orders by payment date (newest first)

#### 6. **GetFeeStructure**

Retrieves active fees for an academic period.

**Input:**

- `academicYear` (int): Academic year
- `semester` (string): Semester

**Output:**

- `List<FeeStructureDTO>`: Active fees

**Business Logic:**

- Filters by year, semester, and active status
- Returns all matching fee structures

#### 7. **CalculateOutstandingBalance**

Calculates total outstanding balance for a student.

**Input:**

- `studentId` (string): Student identifier

**Output:**

- `decimal`: Total outstanding amount

**Business Logic:**

- Sums (total_amount - amount_paid) for all unpaid invoices
- Only includes PENDING and OVERDUE invoices

#### 8. **GetFinancialSummary**

Gets comprehensive financial summary for a student and year.

**Input:**

- `studentId` (string): Student identifier
- `year` (int): Academic year

**Output:**

- `FinancialSummaryDTO`: Financial summary

**Returns:**

- Total invoiced amount
- Total paid amount
- Outstanding balance
- Number of pending invoices
- Number of overdue invoices

#### 9. **GetServiceInfo**

Returns service metadata and health status.

**Output:**

- `string`: Service name, version, status

## Configuration

### appsettings.json

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Host=localhost;Port=5432;Database=billing_db;Username=postgres;Password=postgres"
  },
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  }
}
```

### Environment Variables (Docker)

- `ASPNETCORE_ENVIRONMENT`: docker
- `ConnectionStrings__DefaultConnection`: PostgreSQL connection string

## API Endpoints

### SOAP Endpoint

- **URL**: `http://localhost:5000/BillingService.asmx`
- **Protocol**: SOAP 1.1
- **Content-Type**: text/xml; charset=utf-8

### WSDL

- **URL**: `http://localhost:5000/BillingService.asmx?wsdl`

## Running the Service

### Local Development

```powershell
# Restore dependencies
dotnet restore

# Run database migrations
dotnet ef database update

# Run the service
dotnet run
```

Service will be available at `http://localhost:5000`

### Docker

```powershell
# Build image
docker build -t billing-service .

# Run container
docker run -p 5000:5000 `
  -e ConnectionStrings__DefaultConnection="Host=billing_db;Port=5432;Database=billing_db;Username=postgres;Password=postgres" `
  billing-service
```

### Docker Compose

```powershell
# From project root
cd docker
docker-compose up billing_service billing_db
```

## Testing

See [TESTING.md](TESTING.md) for detailed testing instructions and examples.

## Project Structure

```
billing-service/
├── BillingService.csproj          # Project file with dependencies
├── Program.cs                      # SoapCore configuration and startup
├── Dockerfile                      # Docker image definition
├── .dockerignore                   # Docker ignore patterns
├── appsettings.json               # Configuration (local)
├── appsettings.Development.json   # Development settings
├── Models/                         # Database entities
│   ├── Invoice.cs
│   ├── InvoiceItem.cs
│   ├── Payment.cs
│   ├── FeeStructure.cs
│   └── BillingDbContext.cs
├── DTOs/                          # Data transfer objects
│   ├── InvoiceDTO.cs
│   ├── PaymentDTO.cs
│   ├── FeeStructureDTO.cs
│   ├── FinancialSummaryDTO.cs
│   ├── CreateInvoiceRequest.cs
│   └── RecordPaymentRequest.cs
├── Contracts/                     # SOAP service contracts
│   └── IBillingService.cs
├── Repositories/                  # Data access layer
│   ├── IRepositories.cs
│   ├── InvoiceRepository.cs
│   ├── PaymentRepository.cs
│   └── FeeStructureRepository.cs
├── Services/                      # Business logic
│   └── BillingService.cs
└── test-requests/                 # SOAP test requests
    ├── 01-create-invoice.xml
    ├── 02-get-invoice.xml
    ├── 03-get-invoices-by-student.xml
    ├── 04-record-payment.xml
    ├── 05-get-payment-history.xml
    ├── 06-get-fee-structure.xml
    ├── 07-calculate-outstanding-balance.xml
    ├── 08-get-financial-summary.xml
    └── 09-get-service-info.xml
```

## Dependencies

- **SoapCore** (1.1.0): SOAP protocol implementation
- **Microsoft.EntityFrameworkCore** (8.0.0): ORM framework
- **Microsoft.EntityFrameworkCore.Design** (8.0.0): EF Core tools
- **Npgsql.EntityFrameworkCore.PostgreSQL** (8.0.0): PostgreSQL provider
- **Serilog.AspNetCore** (8.0.0): Logging framework
- **Serilog.Sinks.Console** (5.0.0): Console logging
- **Serilog.Sinks.File** (5.0.0): File logging

## Error Handling

All operations include comprehensive error handling:

- Invalid input validation
- Database operation failures
- Business rule violations
- Detailed error messages in SOAP faults

## Logging

The service uses Serilog for structured logging:

- Console output for development
- File output at `logs/billing-service-.txt` (rolling daily)
- Log levels: Information, Warning, Error

## Security Considerations

- Input validation on all operations
- SQL injection prevention via EF Core parameterization
- Transaction support for payment operations
- Audit trail via created_at/updated_at timestamps

## Performance

- Connection pooling via Npgsql
- Async/await pattern throughout
- Indexed database queries
- Efficient LINQ queries

## Future Enhancements

- [ ] Email notifications for invoices and payments
- [ ] PDF invoice generation
- [ ] Payment gateway integration
- [ ] Installment plan support
- [ ] Late fee calculation
- [ ] Discount and scholarship management
- [ ] Multi-currency support
- [ ] Payment reconciliation reports

## Support

For issues or questions, please refer to:

- TESTING.md for testing guidance
- SOAP WSDL at http://localhost:5000/BillingService.asmx?wsdl
- Project documentation in /documentation folder

## Version

**1.0.0** - Initial release with core billing functionality
