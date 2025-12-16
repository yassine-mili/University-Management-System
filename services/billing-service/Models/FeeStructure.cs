using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Universite.BillingService.Models;

[Table("fee_structure")]
public class FeeStructure
{
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Required]
    [StringLength(50)]
    [Column("fee_type")]
    public string FeeType { get; set; } = string.Empty;

    [Required]
    [StringLength(100)]
    [Column("fee_name")]
    public string FeeName { get; set; } = string.Empty;

    [Required]
    [Column("amount", TypeName = "decimal(10,2)")]
    public decimal Amount { get; set; }

    [Required]
    [StringLength(20)]
    [Column("semester")]
    public string Semester { get; set; } = string.Empty;

    [Required]
    [StringLength(20)]
    [Column("academic_year")]
    public string AcademicYear { get; set; } = string.Empty;

    [Required]
    [Column("is_active")]
    public bool IsActive { get; set; } = true;

    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    [Column("updated_at")]
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
}
