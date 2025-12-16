using Universite.BillingService.Models;

namespace Universite.BillingService.Repositories;

public interface IInvoiceRepository
{
    Task<Invoice> CreateAsync(Invoice invoice);
    Task<Invoice?> GetByIdAsync(int id);
    Task<Invoice?> GetByInvoiceNumberAsync(string invoiceNumber);
    Task<List<Invoice>> GetByStudentIdAsync(int studentId);
    Task<List<Invoice>> GetAllAsync();
    Task<Invoice> UpdateAsync(Invoice invoice);
    Task<bool> DeleteAsync(int id);
    Task<List<Invoice>> GetOverdueInvoicesAsync();
}

public interface IPaymentRepository
{
    Task<Payment> CreateAsync(Payment payment);
    Task<Payment?> GetByIdAsync(int id);
    Task<List<Payment>> GetByInvoiceIdAsync(int invoiceId);
    Task<List<Payment>> GetAllAsync();
}

public interface IFeeStructureRepository
{
    Task<FeeStructure?> GetByIdAsync(int id);
    Task<List<FeeStructure>> GetAllAsync();
    Task<List<FeeStructure>> GetBySemesterAndYearAsync(string? semester, string? academicYear);
    Task<List<FeeStructure>> GetActiveFeesAsync();
}
