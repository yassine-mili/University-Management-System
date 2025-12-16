using System.Runtime.Serialization;

namespace Universite.BillingService.DTOs;

[DataContract]
public class FinancialSummaryDTO
{
    [DataMember]
    public int StudentId { get; set; }

    [DataMember]
    public decimal TotalInvoiced { get; set; }

    [DataMember]
    public decimal TotalPaid { get; set; }

    [DataMember]
    public decimal OutstandingBalance { get; set; }

    [DataMember]
    public int TotalInvoices { get; set; }

    [DataMember]
    public int PendingInvoices { get; set; }

    [DataMember]
    public int OverdueInvoices { get; set; }

    [DataMember]
    public List<InvoiceDTO> RecentInvoices { get; set; } = new();
}
