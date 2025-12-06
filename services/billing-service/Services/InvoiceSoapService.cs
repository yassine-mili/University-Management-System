using System.Threading.Tasks;

namespace BillingService.Services;

public class InvoiceSoapService : IInvoiceSoapService
{
    private readonly InvoiceService _inner;

    public InvoiceSoapService(InvoiceService inner)
    {
        _inner = inner;
    }

    public async Task<InvoiceDto?> CreateInvoice(string invoiceNumber, decimal amount, string currency, string universityId, string firstName, string lastName)
    {
        var invoice = await _inner.CreateInvoice(invoiceNumber, amount, currency, universityId, firstName, lastName);
        if (invoice == null) return null;
        return new InvoiceDto
        {
            InvoiceNumber = invoice.InvoiceNumber,
            Amount = invoice.Amount,
            Currency = invoice.Currency,
            UniversityId = invoice.Student?.UniversityId ?? string.Empty,
            FirstName = invoice.Student?.FirstName ?? string.Empty,
            LastName = invoice.Student?.LastName ?? string.Empty,
            CreatedAt = invoice.CreatedAt.ToString("o")
        };
    }

    public async Task<InvoiceDto?> GetInvoiceByNumber(string invoiceNumber)
    {
        var invoice = await _inner.GetInvoiceByNumber(invoiceNumber);
        if (invoice == null) return null;
        return new InvoiceDto
        {
            InvoiceNumber = invoice.InvoiceNumber,
            Amount = invoice.Amount,
            Currency = invoice.Currency,
            UniversityId = invoice.Student?.UniversityId ?? string.Empty,
            FirstName = invoice.Student?.FirstName ?? string.Empty,
            LastName = invoice.Student?.LastName ?? string.Empty,
            CreatedAt = invoice.CreatedAt.ToString("o")
        };
    }
}
