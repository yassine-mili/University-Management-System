using BillingService.Models;
using System.Collections.Generic;
using System.ServiceModel;

namespace BillingService.Services
{
    [ServiceContract]
    public interface IBillingService
    {
        [OperationContract]
        Invoice CreateInvoice(int studentId, decimal amount, string currency);

        [OperationContract]
        List<Invoice> GetInvoicesByStudent(int studentId);

        [OperationContract]
        string PayInvoice(string invoiceNumber);

        [OperationContract]
        Invoice GetInvoiceByNumber(string invoiceNumber);
    }
}
