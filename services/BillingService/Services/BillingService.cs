using BillingService.Data;
using BillingService.Models;
using Microsoft.EntityFrameworkCore;

namespace BillingService.Services
{
    public class BillingService : IBillingService
    {
        private readonly BillingContext _context;

        public BillingService(BillingContext context)
        {
            _context = context;
        }

        public Invoice CreateInvoice(int studentId, decimal amount, string currency)
        {
            var student = _context.Students.Find(studentId)
                ?? throw new ArgumentException($"Étudiant {studentId} inexistant");

            if (amount <= 0)
                throw new ArgumentException("Le montant doit être > 0");

            var invoice = new Invoice
            {
                InvoiceNumber = $"INV{DateTime.UtcNow.Ticks}",
                Amount = amount,
                Currency = currency,
                CreatedAt = DateTime.UtcNow,
                StudentId = studentId,
                IsPaid = false
            };

            _context.Invoices.Add(invoice);
            _context.SaveChanges();
            return invoice;
        }

        public List<Invoice> GetInvoicesByStudent(int studentId)
        {
            var student = _context.Students.Include(s => s.Invoices).FirstOrDefault(s => s.Id == studentId)
                ?? throw new ArgumentException($"Étudiant {studentId} inexistant");

            return student.Invoices ?? new List<Invoice>();
        }

        public string PayInvoice(string invoiceNumber)
        {
            var invoice = _context.Invoices.FirstOrDefault(i => i.InvoiceNumber == invoiceNumber)
                ?? throw new ArgumentException($"Facture {invoiceNumber} non trouvée");

            if (invoice.IsPaid)
                return "Facture déjà payée";

            invoice.IsPaid = true;
            _context.SaveChanges();
            return "Paiement effectué";
        }

        public Invoice GetInvoiceByNumber(string invoiceNumber)
        {
            var invoice = _context.Invoices.Include(i => i.Student)
                .FirstOrDefault(i => i.InvoiceNumber == invoiceNumber)
                ?? throw new ArgumentException($"Facture {invoiceNumber} non trouvée");

            return invoice;
        }
    }
}
