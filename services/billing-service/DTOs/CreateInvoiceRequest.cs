using System.Runtime.Serialization;

namespace Universite.BillingService.DTOs;

[DataContract]
public class CreateInvoiceRequest
{
    [DataMember]
    public int StudentId { get; set; }

    [DataMember]
    public List<InvoiceItemDTO> Items { get; set; } = new();

    [DataMember]
    public int? DueDays { get; set; }
}
