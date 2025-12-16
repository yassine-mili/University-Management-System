using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Universite.BillingService.Models;

[Table("invoices")]
public class Invoice
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [Column("student_id")]
    public int StudentId { get; set; }

    [Required]
    [StringLength(50)]
    [Column("invoice_number")]
    public string InvoiceNumber { get; set; } = string.Empty;

    [Required]
    [Column("invoice_date")]
    public DateTime InvoiceDate { get; set; }

    [Required]
    [Column("due_date")]
    public DateTime DueDate { get; set; }

    [Required]
    [Column("total_amount", TypeName = "decimal(10,2)")]
    public decimal TotalAmount { get; set; }

    [Required]
    [StringLength(20)]
    [Column("status")]
    public string Status { get; set; } = "PENDING";

    [Column("payment_date")]
    public DateTime? PaymentDate { get; set; }

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    [Column("updated_at")]
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navigation property
    public virtual ICollection<InvoiceItem> Items { get; set; } = new List<InvoiceItem>();
    public virtual ICollection<Payment> Payments { get; set; } = new List<Payment>();
}
