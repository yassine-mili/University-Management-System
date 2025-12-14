using BillingService.Models;
using Microsoft.EntityFrameworkCore;

namespace BillingService.Data
{
    public static class DbInitializer
    {
        public static void Initialize(BillingContext context)
        {
            context.Database.Migrate();

            if (context.Students.Any())
                return;

            var students = new[]
            {
                new Student { UniversityId = 1001, FirstName = "Ali", LastName = "Ben Salah" },
                new Student { UniversityId = 1002, FirstName = "Sara", LastName = "Trabelsi" }
            };

            context.Students.AddRange(students);
            context.SaveChanges();

            var invoices = new[]
            {
                new Invoice { InvoiceNumber = "INV001", Amount = 500, Currency = "TND", CreatedAt = DateTime.UtcNow, StudentId = students[0].Id, IsPaid = false },
                new Invoice { InvoiceNumber = "INV002", Amount = 1200, Currency = "TND", CreatedAt = DateTime.UtcNow, StudentId = students[1].Id, IsPaid = false }
            };

            context.Invoices.AddRange(invoices);
            context.SaveChanges();
        }
    }
}
