using System.Runtime.Serialization;

namespace Universite.BillingService.DTOs;

[DataContract]
public class RecordPaymentRequest
{
    [DataMember]
    public int InvoiceId { get; set; }

    [DataMember]
    public decimal Amount { get; set; }

    [DataMember]
    public string PaymentMethod { get; set; } = string.Empty;

    [DataMember]
    public string? TransactionReference { get; set; }
}
