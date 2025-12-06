using BillingService.Data;
using BillingService.Services;
using Microsoft.EntityFrameworkCore;
using SoapCore;
using System.ServiceModel;

var builder = WebApplication.CreateBuilder(args);

// Add services
builder.Services.AddDbContext<BillingDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("BillingDb")));

builder.Services.AddScoped<InvoiceService>();
builder.Services.AddSoapCore();
builder.Services.AddScoped<IInvoiceSoapService, InvoiceSoapService>();

var app = builder.Build();

app.UseRouting();

// Initialize database
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<BillingDbContext>();
    db.Database.EnsureCreated();
    Console.WriteLine("Database created/verified");
}

// REST endpoints
app.MapPost("/api/invoices", async (InvoiceService service, CreateInvoiceRequest request) =>
{
    var invoice = await service.CreateInvoice(
        request.InvoiceNumber,
        request.Amount,
        request.Currency,
        request.UniversityId,
        request.FirstName,
        request.LastName
    );
    return Results.Created($"/api/invoices/{invoice?.InvoiceNumber}", invoice);
});

app.MapGet("/api/invoices/{invoiceNumber}", async (InvoiceService service, string invoiceNumber) =>
{
    var invoice = await service.GetInvoiceByNumber(invoiceNumber);
    return invoice != null ? Results.Ok(invoice) : Results.NotFound();
});

app.MapGet("/", () => "Billing Service - REST API");

// SOAP endpoint (SoapCore)
app.UseEndpoints(endpoints =>
{
    endpoints.UseSoapEndpoint<IInvoiceSoapService>("/InvoiceService.svc", new BasicHttpBinding(), SoapSerializer.DataContractSerializer);
});

app.Run();

public class CreateInvoiceRequest
{
    public string InvoiceNumber { get; set; } = string.Empty;
    public decimal Amount { get; set; }
    public string Currency { get; set; } = "TND";
    public string UniversityId { get; set; } = string.Empty;
    public string FirstName { get; set; } = string.Empty;
    public string LastName { get; set; } = string.Empty;
}
