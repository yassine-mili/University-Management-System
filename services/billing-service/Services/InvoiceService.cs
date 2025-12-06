using BillingService.Models;
using BillingService.Data;
using Microsoft.EntityFrameworkCore;

namespace BillingService.Services;

public class InvoiceService
{
    private readonly BillingDbContext _db;

    public InvoiceService(BillingDbContext db)
    {
        _db = db;
    }

    public async Task<Invoice?> CreateInvoice(string invoiceNumber, decimal amount, string currency, string universityId, string firstName, string lastName)
    {
        var student = await _db.Students.FirstOrDefaultAsync(s => s.UniversityId == universityId);
        if (student == null)
        {
            student = new Student { UniversityId = universityId, FirstName = firstName, LastName = lastName };
            _db.Students.Add(student);
            await _db.SaveChangesAsync();
        }

        var invoice = new Invoice
        {
            InvoiceNumber = invoiceNumber,
            Amount = amount,
            Currency = currency,
            StudentId = student.Id,
            CreatedAt = DateTime.UtcNow
        };

        _db.Invoices.Add(invoice);
        await _db.SaveChangesAsync();
        return invoice;
    }

    public async Task<Invoice?> GetInvoiceByNumber(string invoiceNumber)
    {
        return await _db.Invoices
            .Include(i => i.Student)
            .FirstOrDefaultAsync(i => i.InvoiceNumber == invoiceNumber);
    }
}
