using Microsoft.EntityFrameworkCore;
using Universite.BillingService.Models;

namespace Universite.BillingService.Repositories;

public class InvoiceRepository : IInvoiceRepository
{
    private readonly BillingDbContext _context;

    public InvoiceRepository(BillingDbContext context)
    {
        _context = context;
    }

    public async Task<Invoice> CreateAsync(Invoice invoice)
    {
        _context.Invoices.Add(invoice);
        await _context.SaveChangesAsync();
        return invoice;
    }

    public async Task<Invoice?> GetByIdAsync(int id)
    {
        return await _context.Invoices
            .Include(i => i.Items)
            .Include(i => i.Payments)
            .FirstOrDefaultAsync(i => i.Id == id);
    }

    public async Task<Invoice?> GetByInvoiceNumberAsync(string invoiceNumber)
    {
        return await _context.Invoices
            .Include(i => i.Items)
            .Include(i => i.Payments)
            .FirstOrDefaultAsync(i => i.InvoiceNumber == invoiceNumber);
    }

    public async Task<List<Invoice>> GetByStudentIdAsync(int studentId)
    {
        return await _context.Invoices
            .Include(i => i.Items)
            .Include(i => i.Payments)
            .Where(i => i.StudentId == studentId)
            .OrderByDescending(i => i.InvoiceDate)
            .ToListAsync();
    }

    public async Task<List<Invoice>> GetAllAsync()
    {
        return await _context.Invoices
            .Include(i => i.Items)
            .Include(i => i.Payments)
            .OrderByDescending(i => i.InvoiceDate)
            .ToListAsync();
    }

    public async Task<Invoice> UpdateAsync(Invoice invoice)
    {
        invoice.UpdatedAt = DateTime.UtcNow;
        _context.Invoices.Update(invoice);
        await _context.SaveChangesAsync();
        return invoice;
    }

    public async Task<bool> DeleteAsync(int id)
    {
        var invoice = await _context.Invoices.FindAsync(id);
        if (invoice == null) return false;

        _context.Invoices.Remove(invoice);
        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<List<Invoice>> GetOverdueInvoicesAsync()
    {
        return await _context.Invoices
            .Include(i => i.Items)
            .Where(i => i.Status == "PENDING" && i.DueDate < DateTime.UtcNow)
            .ToListAsync();
    }
}
