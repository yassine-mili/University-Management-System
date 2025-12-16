using Microsoft.EntityFrameworkCore;

namespace Universite.BillingService.Models;

public class BillingDbContext : DbContext
{
    public BillingDbContext(DbContextOptions<BillingDbContext> options) : base(options)
    {
    }

    public DbSet<Invoice> Invoices { get; set; }
    public DbSet<InvoiceItem> InvoiceItems { get; set; }
    public DbSet<Payment> Payments { get; set; }
    public DbSet<FeeStructure> FeeStructures { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Invoice configuration
        modelBuilder.Entity<Invoice>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.HasIndex(e => e.InvoiceNumber).IsUnique();
            entity.HasIndex(e => e.StudentId);
            entity.HasIndex(e => e.Status);
            entity.HasIndex(e => e.DueDate);

            entity.HasMany(e => e.Items)
                .WithOne(i => i.Invoice)
                .HasForeignKey(i => i.InvoiceId)
                .OnDelete(DeleteBehavior.Cascade);

            entity.HasMany(e => e.Payments)
                .WithOne(p => p.Invoice)
                .HasForeignKey(p => p.InvoiceId)
                .OnDelete(DeleteBehavior.Cascade);
        });

        // Invoice Item configuration
        modelBuilder.Entity<InvoiceItem>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.HasIndex(e => e.InvoiceId);
        });

        // Payment configuration
        modelBuilder.Entity<Payment>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.HasIndex(e => e.InvoiceId);
            entity.HasIndex(e => e.PaymentDate);
            entity.HasIndex(e => e.TransactionReference);
        });

        // Fee Structure configuration
        modelBuilder.Entity<FeeStructure>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.HasIndex(e => e.FeeType);
            entity.HasIndex(e => new { e.Semester, e.AcademicYear });
            entity.HasIndex(e => e.IsActive);
        });

        // Seed data
        SeedData(modelBuilder);
    }

    private void SeedData(ModelBuilder modelBuilder)
    {
        // Seed fee structure data
        modelBuilder.Entity<FeeStructure>().HasData(
            new FeeStructure
            {
                Id = 1,
                FeeType = "TUITION",
                FeeName = "Tuition Fee - Fall 2024",
                Amount = 5000.00m,
                Semester = "Fall 2024",
                AcademicYear = "2024-2025",
                IsActive = true,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
            new FeeStructure
            {
                Id = 2,
                FeeType = "LIBRARY",
                FeeName = "Library Fee",
                Amount = 100.00m,
                Semester = "Fall 2024",
                AcademicYear = "2024-2025",
                IsActive = true,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
            new FeeStructure
            {
                Id = 3,
                FeeType = "LABORATORY",
                FeeName = "Laboratory Fee",
                Amount = 300.00m,
                Semester = "Fall 2024",
                AcademicYear = "2024-2025",
                IsActive = true,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
            new FeeStructure
            {
                Id = 4,
                FeeType = "SPORTS",
                FeeName = "Sports & Recreation Fee",
                Amount = 150.00m,
                Semester = "Fall 2024",
                AcademicYear = "2024-2025",
                IsActive = true,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            }
        );
    }
}
