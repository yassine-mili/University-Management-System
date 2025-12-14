using BillingService.Data;
using BillingService.Services;
using Microsoft.EntityFrameworkCore;
using SoapCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<BillingContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"))
);

// ⚡ Injection du service SOAP
builder.Services.AddScoped<IBillingService, BillingService.Services.BillingService>();
builder.Services.AddSoapCore();

// Swagger / endpoints
builder.Services.AddEndpointsApiExplorer();

var app = builder.Build();

// Initialisation DB
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<BillingContext>();
    DbInitializer.Initialize(db);
}

// SOAP endpoint
app.UseRouting();

app.UseEndpoints(endpoints =>
{
    endpoints.UseSoapEndpoint<IBillingService>(
        "/BillingService.svc",
        new SoapEncoderOptions(),
        SoapSerializer.DataContractSerializer
    );
});

app.Run();
