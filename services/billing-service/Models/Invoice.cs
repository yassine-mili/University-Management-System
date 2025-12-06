namespace BillingService.Models;

public class Invoice
{
    public int Id { get; set; }
    public string InvoiceNumber { get; set; } = string.Empty;
    public decimal Amount { get; set; }
    public string Currency { get; set; } = "TND";
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public int StudentId { get; set; }
    public Student? Student { get; set; }
}
