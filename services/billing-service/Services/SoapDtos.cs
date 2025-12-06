using System.Runtime.Serialization;

namespace BillingService.Services;

[DataContract]
public class CreateInvoiceRequestDto
{
    [DataMember]
    public string InvoiceNumber { get; set; } = string.Empty;
    [DataMember]
    public decimal Amount { get; set; }
    [DataMember]
    public string Currency { get; set; } = "TND";
    [DataMember]
    public string UniversityId { get; set; } = string.Empty;
    [DataMember]
    public string FirstName { get; set; } = string.Empty;
    [DataMember]
    public string LastName { get; set; } = string.Empty;
}

[DataContract]
public class InvoiceDto
{
    [DataMember]
    public string InvoiceNumber { get; set; } = string.Empty;
    [DataMember]
    public decimal Amount { get; set; }
    [DataMember]
    public string Currency { get; set; } = string.Empty;
    [DataMember]
    public string UniversityId { get; set; } = string.Empty;
    [DataMember]
    public string FirstName { get; set; } = string.Empty;
    [DataMember]
    public string LastName { get; set; } = string.Empty;
    [DataMember]
    public string CreatedAt { get; set; } = string.Empty;
}
