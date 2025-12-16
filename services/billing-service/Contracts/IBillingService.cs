using System.ServiceModel;
using Universite.BillingService.DTOs;

namespace Universite.BillingService.Contracts;

[ServiceContract(Namespace = "http://billing.universite.com/")]
public interface IBillingService
{
    [OperationContract]
    InvoiceDTO CreateInvoice(CreateInvoiceRequest request);

    [OperationContract]
    InvoiceDTO GetInvoice(int invoiceId);

    [OperationContract]
    List<InvoiceDTO> GetInvoicesByStudent(int studentId);

    [OperationContract]
    PaymentDTO RecordPayment(RecordPaymentRequest request);

    [OperationContract]
    List<PaymentDTO> GetPaymentHistory(int invoiceId);

    [OperationContract]
    List<FeeStructureDTO> GetFeeStructure(string? semester, string? academicYear);

    [OperationContract]
    decimal CalculateOutstandingBalance(int studentId);

    [OperationContract]
    FinancialSummaryDTO GetFinancialSummary(int studentId);

    [OperationContract]
    string GetServiceInfo();
}
