using Universite.BillingService.Contracts;
using Universite.BillingService.DTOs;
using Universite.BillingService.Models;
using Universite.BillingService.Repositories;

namespace Universite.BillingService.Services;

public class BillingService : IBillingService
{
    private readonly IInvoiceRepository _invoiceRepository;
    private readonly IPaymentRepository _paymentRepository;
    private readonly IFeeStructureRepository _feeStructureRepository;
    private readonly IConfiguration _configuration;
    private readonly ILogger<BillingService> _logger;

    public BillingService(
        IInvoiceRepository invoiceRepository,
        IPaymentRepository paymentRepository,
        IFeeStructureRepository feeStructureRepository,
        IConfiguration configuration,
        ILogger<BillingService> logger)
    {
        _invoiceRepository = invoiceRepository;
        _paymentRepository = paymentRepository;
        _feeStructureRepository = feeStructureRepository;
        _configuration = configuration;
        _logger = logger;
    }

    public InvoiceDTO CreateInvoice(CreateInvoiceRequest request)
    {
        try
        {
            _logger.LogInformation("Creating invoice for student {StudentId}", request.StudentId);

            // Validate request
            if (request.StudentId <= 0)
                throw new ArgumentException("Invalid student ID");

            if (request.Items == null || !request.Items.Any())
                throw new ArgumentException("Invoice must have at least one item");

            // Calculate total amount
            decimal totalAmount = 0;
            var invoiceItems = new List<InvoiceItem>();

            foreach (var itemDto in request.Items)
            {
                var itemTotal = itemDto.Quantity * itemDto.UnitPrice;
                totalAmount += itemTotal;

                invoiceItems.Add(new InvoiceItem
                {
                    Description = itemDto.Description,
                    ItemType = itemDto.ItemType,
                    Quantity = itemDto.Quantity,
                    UnitPrice = itemDto.UnitPrice,
                    TotalPrice = itemTotal
                });
            }

            // Generate invoice number
            var invoiceNumber = GenerateInvoiceNumber();

            // Calculate due date
            int dueDays = request.DueDays ?? 
                _configuration.GetValue<int>("BusinessRules:DefaultDueDays", 30);

            // Create invoice
            var invoice = new Invoice
            {
                StudentId = request.StudentId,
                InvoiceNumber = invoiceNumber,
                InvoiceDate = DateTime.UtcNow,
                DueDate = DateTime.UtcNow.AddDays(dueDays),
                TotalAmount = totalAmount,
                Status = "PENDING",
                Items = invoiceItems
            };

            var createdInvoice = _invoiceRepository.CreateAsync(invoice).Result;
            _logger.LogInformation("Invoice {InvoiceNumber} created successfully", invoiceNumber);

            return MapToDTO(createdInvoice);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating invoice for student {StudentId}", request.StudentId);
            throw new InvalidOperationException($"Failed to create invoice: {ex.Message}", ex);
        }
    }

    public InvoiceDTO GetInvoice(int invoiceId)
    {
        try
        {
            _logger.LogInformation("Retrieving invoice {InvoiceId}", invoiceId);

            var invoice = _invoiceRepository.GetByIdAsync(invoiceId).Result;
            if (invoice == null)
                throw new KeyNotFoundException($"Invoice not found with ID: {invoiceId}");

            // Check and update overdue status
            UpdateOverdueStatus(invoice);

            return MapToDTO(invoice);
        }
        catch (KeyNotFoundException)
        {
            throw;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error retrieving invoice {InvoiceId}", invoiceId);
            throw new InvalidOperationException($"Failed to retrieve invoice: {ex.Message}", ex);
        }
    }

    public List<InvoiceDTO> GetInvoicesByStudent(int studentId)
    {
        try
        {
            _logger.LogInformation("Retrieving invoices for student {StudentId}", studentId);

            var invoices = _invoiceRepository.GetByStudentIdAsync(studentId).Result;
            
            // Update overdue status for all invoices
            foreach (var invoice in invoices)
            {
                UpdateOverdueStatus(invoice);
            }

            return invoices.Select(MapToDTO).ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error retrieving invoices for student {StudentId}", studentId);
            throw new InvalidOperationException($"Failed to retrieve student invoices: {ex.Message}", ex);
        }
    }

    public PaymentDTO RecordPayment(RecordPaymentRequest request)
    {
        try
        {
            _logger.LogInformation("Recording payment for invoice {InvoiceId}", request.InvoiceId);

            // Validate request
            if (request.InvoiceId <= 0)
                throw new ArgumentException("Invalid invoice ID");

            if (request.Amount <= 0)
                throw new ArgumentException("Payment amount must be greater than zero");

            if (string.IsNullOrEmpty(request.PaymentMethod))
                throw new ArgumentException("Payment method is required");

            // Get invoice
            var invoice = _invoiceRepository.GetByIdAsync(request.InvoiceId).Result;
            if (invoice == null)
                throw new KeyNotFoundException($"Invoice not found with ID: {request.InvoiceId}");

            // Check if invoice is already paid
            if (invoice.Status == "PAID")
                throw new InvalidOperationException("Invoice is already fully paid");

            if (invoice.Status == "CANCELLED")
                throw new InvalidOperationException("Cannot pay a cancelled invoice");

            // Calculate total paid so far
            var totalPaid = invoice.Payments.Sum(p => p.Amount);
            var remainingAmount = invoice.TotalAmount - totalPaid;

            if (request.Amount > remainingAmount)
                throw new InvalidOperationException(
                    $"Payment amount ({request.Amount}) exceeds remaining balance ({remainingAmount})");

            // Create payment record
            var payment = new Payment
            {
                InvoiceId = request.InvoiceId,
                Amount = request.Amount,
                PaymentDate = DateTime.UtcNow,
                PaymentMethod = request.PaymentMethod,
                TransactionReference = request.TransactionReference,
                Status = "COMPLETED"
            };

            var createdPayment = _paymentRepository.CreateAsync(payment).Result;

            // Update invoice status if fully paid
            totalPaid += request.Amount;
            if (totalPaid >= invoice.TotalAmount)
            {
                invoice.Status = "PAID";
                invoice.PaymentDate = DateTime.UtcNow;
                _invoiceRepository.UpdateAsync(invoice).Wait();
                _logger.LogInformation("Invoice {InvoiceNumber} marked as PAID", invoice.InvoiceNumber);
            }

            _logger.LogInformation("Payment recorded successfully for invoice {InvoiceNumber}", invoice.InvoiceNumber);

            return new PaymentDTO
            {
                Id = createdPayment.Id,
                InvoiceId = createdPayment.InvoiceId,
                Amount = createdPayment.Amount,
                PaymentDate = createdPayment.PaymentDate,
                PaymentMethod = createdPayment.PaymentMethod,
                TransactionReference = createdPayment.TransactionReference,
                Status = createdPayment.Status
            };
        }
        catch (Exception ex) when (ex is ArgumentException || ex is KeyNotFoundException || ex is InvalidOperationException)
        {
            throw;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error recording payment for invoice {InvoiceId}", request.InvoiceId);
            throw new InvalidOperationException($"Failed to record payment: {ex.Message}", ex);
        }
    }

    public List<PaymentDTO> GetPaymentHistory(int invoiceId)
    {
        try
        {
            _logger.LogInformation("Retrieving payment history for invoice {InvoiceId}", invoiceId);

            var payments = _paymentRepository.GetByInvoiceIdAsync(invoiceId).Result;

            return payments.Select(p => new PaymentDTO
            {
                Id = p.Id,
                InvoiceId = p.InvoiceId,
                Amount = p.Amount,
                PaymentDate = p.PaymentDate,
                PaymentMethod = p.PaymentMethod,
                TransactionReference = p.TransactionReference,
                Status = p.Status
            }).ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error retrieving payment history for invoice {InvoiceId}", invoiceId);
            throw new InvalidOperationException($"Failed to retrieve payment history: {ex.Message}", ex);
        }
    }

    public List<FeeStructureDTO> GetFeeStructure(string? semester, string? academicYear)
    {
        try
        {
            _logger.LogInformation("Retrieving fee structure for semester {Semester}, year {Year}", 
                semester, academicYear);

            List<FeeStructure> fees;

            if (string.IsNullOrEmpty(semester) && string.IsNullOrEmpty(academicYear))
            {
                fees = _feeStructureRepository.GetActiveFeesAsync().Result;
            }
            else
            {
                fees = _feeStructureRepository.GetBySemesterAndYearAsync(semester, academicYear).Result;
            }

            return fees.Select(f => new FeeStructureDTO
            {
                Id = f.Id,
                FeeType = f.FeeType,
                FeeName = f.FeeName,
                Amount = f.Amount,
                Semester = f.Semester,
                AcademicYear = f.AcademicYear,
                IsActive = f.IsActive
            }).ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error retrieving fee structure");
            throw new InvalidOperationException($"Failed to retrieve fee structure: {ex.Message}", ex);
        }
    }

    public decimal CalculateOutstandingBalance(int studentId)
    {
        try
        {
            _logger.LogInformation("Calculating outstanding balance for student {StudentId}", studentId);

            var invoices = _invoiceRepository.GetByStudentIdAsync(studentId).Result;

            decimal outstandingBalance = 0;

            foreach (var invoice in invoices)
            {
                if (invoice.Status != "PAID" && invoice.Status != "CANCELLED")
                {
                    var totalPaid = invoice.Payments.Sum(p => p.Amount);
                    outstandingBalance += (invoice.TotalAmount - totalPaid);
                }
            }

            return outstandingBalance;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error calculating outstanding balance for student {StudentId}", studentId);
            throw new InvalidOperationException($"Failed to calculate outstanding balance: {ex.Message}", ex);
        }
    }

    public FinancialSummaryDTO GetFinancialSummary(int studentId)
    {
        try
        {
            _logger.LogInformation("Generating financial summary for student {StudentId}", studentId);

            var invoices = _invoiceRepository.GetByStudentIdAsync(studentId).Result;

            decimal totalInvoiced = 0;
            decimal totalPaid = 0;
            int pendingCount = 0;
            int overdueCount = 0;

            foreach (var invoice in invoices)
            {
                UpdateOverdueStatus(invoice);

                totalInvoiced += invoice.TotalAmount;
                totalPaid += invoice.Payments.Sum(p => p.Amount);

                if (invoice.Status == "PENDING")
                    pendingCount++;
                if (invoice.Status == "OVERDUE")
                    overdueCount++;
            }

            var recentInvoices = invoices
                .OrderByDescending(i => i.InvoiceDate)
                .Take(5)
                .Select(MapToDTO)
                .ToList();

            return new FinancialSummaryDTO
            {
                StudentId = studentId,
                TotalInvoiced = totalInvoiced,
                TotalPaid = totalPaid,
                OutstandingBalance = totalInvoiced - totalPaid,
                TotalInvoices = invoices.Count,
                PendingInvoices = pendingCount,
                OverdueInvoices = overdueCount,
                RecentInvoices = recentInvoices
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error generating financial summary for student {StudentId}", studentId);
            throw new InvalidOperationException($"Failed to generate financial summary: {ex.Message}", ex);
        }
    }

    public string GetServiceInfo()
    {
        return "University Billing SOAP Service v1.0.0 - Invoice and payment management system";
    }

    // Helper methods
    private string GenerateInvoiceNumber()
    {
        var prefix = _configuration.GetValue<string>("BusinessRules:InvoiceNumberPrefix", "INV");
        var now = DateTime.UtcNow;
        var sequence = now.Ticks % 100000; // Last 5 digits of ticks
        return $"{prefix}-{now:yyyy}-{now:MM}-{sequence:D5}";
    }

    private void UpdateOverdueStatus(Invoice invoice)
    {
        var overdueCheckEnabled = _configuration.GetValue<bool>("BusinessRules:OverdueCheckEnabled", true);

        if (overdueCheckEnabled && 
            invoice.Status == "PENDING" && 
            invoice.DueDate < DateTime.UtcNow)
        {
            invoice.Status = "OVERDUE";
            _invoiceRepository.UpdateAsync(invoice).Wait();
        }
    }

    private InvoiceDTO MapToDTO(Invoice invoice)
    {
        return new InvoiceDTO
        {
            Id = invoice.Id,
            StudentId = invoice.StudentId,
            InvoiceNumber = invoice.InvoiceNumber,
            InvoiceDate = invoice.InvoiceDate,
            DueDate = invoice.DueDate,
            TotalAmount = invoice.TotalAmount,
            Status = invoice.Status,
            PaymentDate = invoice.PaymentDate,
            Items = invoice.Items.Select(i => new InvoiceItemDTO
            {
                Id = i.Id,
                InvoiceId = i.InvoiceId,
                Description = i.Description,
                ItemType = i.ItemType,
                Quantity = i.Quantity,
                UnitPrice = i.UnitPrice,
                TotalPrice = i.TotalPrice
            }).ToList()
        };
    }
}
