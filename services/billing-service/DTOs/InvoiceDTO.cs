using System.Runtime.Serialization;

namespace Universite.BillingService.DTOs;

[DataContract]
public class InvoiceDTO
{
    [DataMember]
    public int Id { get; set; }

    [DataMember]
    public int StudentId { get; set; }

    [DataMember]
    public string InvoiceNumber { get; set; } = string.Empty;

    [DataMember]
    public DateTime InvoiceDate { get; set; }

    [DataMember]
    public DateTime DueDate { get; set; }

    [DataMember]
    public decimal TotalAmount { get; set; }

    [DataMember]
    public string Status { get; set; } = string.Empty;

    [DataMember]
    public DateTime? PaymentDate { get; set; }

    [DataMember]
    public List<InvoiceItemDTO> Items { get; set; } = new();
}

[DataContract]
public class InvoiceItemDTO
{
    [DataMember]
    public int Id { get; set; }

    [DataMember]
    public int InvoiceId { get; set; }

    [DataMember]
    public string Description { get; set; } = string.Empty;

    [DataMember]
    public string ItemType { get; set; } = string.Empty;

    [DataMember]
    public int Quantity { get; set; }

    [DataMember]
    public decimal UnitPrice { get; set; }

    [DataMember]
    public decimal TotalPrice { get; set; }
}
