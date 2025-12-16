using Microsoft.EntityFrameworkCore;
using Universite.BillingService.Models;

namespace Universite.BillingService.Repositories;

public class PaymentRepository : IPaymentRepository
{
    private readonly BillingDbContext _context;

    public PaymentRepository(BillingDbContext context)
    {
        _context = context;
    }

    public async Task<Payment> CreateAsync(Payment payment)
    {
        _context.Payments.Add(payment);
        await _context.SaveChangesAsync();
        return payment;
    }

    public async Task<Payment?> GetByIdAsync(int id)
    {
        return await _context.Payments
            .Include(p => p.Invoice)
            .FirstOrDefaultAsync(p => p.Id == id);
    }

    public async Task<List<Payment>> GetByInvoiceIdAsync(int invoiceId)
    {
        return await _context.Payments
            .Where(p => p.InvoiceId == invoiceId)
            .OrderByDescending(p => p.PaymentDate)
            .ToListAsync();
    }

    public async Task<List<Payment>> GetAllAsync()
    {
        return await _context.Payments
            .Include(p => p.Invoice)
            .OrderByDescending(p => p.PaymentDate)
            .ToListAsync();
    }
}
