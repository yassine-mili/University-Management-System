using System.ServiceModel;
using System.Runtime.Serialization;
using System.Threading.Tasks;

namespace BillingService.Services;

[ServiceContract]
public interface IInvoiceSoapService
{
    [OperationContract]
    Task<InvoiceDto?> CreateInvoice(string invoiceNumber, decimal amount, string currency, string universityId, string firstName, string lastName);

    [OperationContract]
    Task<InvoiceDto?> GetInvoiceByNumber(string invoiceNumber);
}
