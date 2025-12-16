using System.Runtime.Serialization;

namespace Universite.BillingService.DTOs;

[DataContract]
public class PaymentDTO
{
    [DataMember]
    public int Id { get; set; }

    [DataMember]
    public int InvoiceId { get; set; }

    [DataMember]
    public decimal Amount { get; set; }

    [DataMember]
    public DateTime PaymentDate { get; set; }

    [DataMember]
    public string PaymentMethod { get; set; } = string.Empty;

    [DataMember]
    public string? TransactionReference { get; set; }

    [DataMember]
    public string Status { get; set; } = string.Empty;
}
