using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Universite.BillingService.Models;

[Table("payments")]
public class Payment
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [Column("invoice_id")]
    public int InvoiceId { get; set; }

    [Required]
    [Column("amount", TypeName = "decimal(10,2)")]
    public decimal Amount { get; set; }

    [Required]
    [Column("payment_date")]
    public DateTime PaymentDate { get; set; }

    [Required]
    [StringLength(50)]
    [Column("payment_method")]
    public string PaymentMethod { get; set; } = string.Empty;

    [StringLength(100)]
    [Column("transaction_reference")]
    public string? TransactionReference { get; set; }

    [Required]
    [StringLength(20)]
    [Column("status")]
    public string Status { get; set; } = "COMPLETED";

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    // Navigation property
    [ForeignKey("InvoiceId")]
    public virtual Invoice? Invoice { get; set; }
}
