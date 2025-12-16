using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using Universite.BillingService.Models;

namespace Universite.BillingService.Seed;

public static class SampleDataSeeder
{
    private static bool IsSampleDataEnabled()
    {
        var flag = Environment.GetEnvironmentVariable("ENABLE_SAMPLE_DATA") ?? "true";
        return !flag.Equals("false", StringComparison.OrdinalIgnoreCase);
    }

    public static async Task SeedAsync(BillingDbContext context, ILogger logger)
    {
        if (!IsSampleDataEnabled())
        {
            return;
        }

        if (await context.Invoices.AnyAsync().ConfigureAwait(false))
        {
            return;
        }

        var utcNow = DateTime.UtcNow.Date;
        var pendingInvoice = new Invoice
        {
            StudentId = 1,
            InvoiceNumber = $"INV-{utcNow:yyyyMM}-0001",
            InvoiceDate = utcNow.AddDays(-30),
            DueDate = utcNow.AddDays(15),
            TotalAmount = 5450m,
            Status = "PENDING",
            Items = new List<InvoiceItem>
            {
                new()
                {
                    Description = "Tuition Fee - Fall 2024",
                    ItemType = "TUITION",
                    Quantity = 1,
                    UnitPrice = 5000m,
                    TotalPrice = 5000m,
                },
                new()
                {
                    Description = "Library Fee",
                    ItemType = "LIBRARY",
                    Quantity = 1,
                    UnitPrice = 100m,
                    TotalPrice = 100m,
                },
                new()
                {
                    Description = "Laboratory Fee",
                    ItemType = "LABORATORY",
                    Quantity = 1,
                    UnitPrice = 350m,
                    TotalPrice = 350m,
                },
            }
        };

        var settledInvoice = new Invoice
        {
            StudentId = 1,
            InvoiceNumber = $"INV-{utcNow:yyyyMM}-0002",
            InvoiceDate = utcNow.AddDays(-95),
            DueDate = utcNow.AddDays(-65),
            TotalAmount = 5200m,
            Status = "PAID",
            PaymentDate = utcNow.AddDays(-60),
            Items = new List<InvoiceItem>
            {
                new()
                {
                    Description = "Spring Tuition Fee",
                    ItemType = "TUITION",
                    Quantity = 1,
                    UnitPrice = 5000m,
                    TotalPrice = 5000m,
                },
                new()
                {
                    Description = "Sports & Recreation Fee",
                    ItemType = "SPORTS",
                    Quantity = 1,
                    UnitPrice = 200m,
                    TotalPrice = 200m,
                },
            },
            Payments = new List<Payment>
            {
                new()
                {
                    Amount = 5200m,
                    PaymentDate = utcNow.AddDays(-60),
                    PaymentMethod = "CREDIT_CARD",
                    TransactionReference = "PAY-2024-0001",
                    Status = "COMPLETED",
                }
            }
        };

        await context.Invoices.AddAsync(pendingInvoice).ConfigureAwait(false);
        await context.Invoices.AddAsync(settledInvoice).ConfigureAwait(false);
        await context.SaveChangesAsync().ConfigureAwait(false);

        logger.LogInformation("Sample billing invoices seeded for demo student 1.");
    }
}
