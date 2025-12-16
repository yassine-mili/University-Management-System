using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Universite.BillingService.Models;

[Table("invoice_items")]
public class InvoiceItem
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [Column("invoice_id")]
    public int InvoiceId { get; set; }

    [Required]
    [StringLength(200)]
    [Column("description")]
    public string Description { get; set; } = string.Empty;

    [Required]
    [StringLength(50)]
    [Column("item_type")]
    public string ItemType { get; set; } = string.Empty;

    [Required]
    [Column("quantity")]
    public int Quantity { get; set; } = 1;

    [Required]
    [Column("unit_price", TypeName = "decimal(10,2)")]
    public decimal UnitPrice { get; set; }

    [Required]
    [Column("total_price", TypeName = "decimal(10,2)")]
    public decimal TotalPrice { get; set; }

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    // Navigation property
    [ForeignKey("InvoiceId")]
    public virtual Invoice? Invoice { get; set; }
}
