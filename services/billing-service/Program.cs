using Microsoft.EntityFrameworkCore;
using SoapCore;
using Universite.BillingService.Contracts;
using Universite.BillingService.Models;
using Universite.BillingService.Repositories;
using Universite.BillingService.Services;
using Universite.BillingService.Middleware;
using Universite.BillingService.Clients;
using Serilog;
using Universite.BillingService.Seed;
using Microsoft.Extensions.Logging;

var builder = WebApplication.CreateBuilder(args);

// Configure Serilog
Log.Logger = new LoggerConfiguration()
    .ReadFrom.Configuration(builder.Configuration)
    .WriteTo.Console()
    .WriteTo.File("logs/billing-service-.txt", rollingInterval: RollingInterval.Day)
    .CreateLogger();

builder.Host.UseSerilog();

// Add services to the container
builder.Services.AddSoapCore();

// Database configuration
var environment = Environment.GetEnvironmentVariable("ENVIRONMENT");
var connectionString = environment == "docker"
    ? builder.Configuration.GetConnectionString("BillingDatabaseDocker")
    : builder.Configuration.GetConnectionString("BillingDatabase");

builder.Services.AddDbContext<BillingDbContext>(options =>
    options.UseNpgsql(connectionString));

// Register repositories
builder.Services.AddScoped<IInvoiceRepository, InvoiceRepository>();
builder.Services.AddScoped<IPaymentRepository, PaymentRepository>();
builder.Services.AddScoped<IFeeStructureRepository, FeeStructureRepository>();

// Register SOAP service
builder.Services.AddScoped<IBillingService, BillingService>();

// Add HTTP client factory for external service calls
builder.Services.AddHttpClient();

// Register service clients
builder.Services.AddScoped<StudentServiceClient>();

// Add controllers and other services
builder.Services.AddControllers();

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}

// Add CORS
app.UseCors(policy => policy
    .AllowAnyOrigin()
    .AllowAnyMethod()
    .AllowAnyHeader());

// Add JWT authentication middleware
app.UseJwtAuthentication();

// Database migration and seeding
using (var scope = app.Services.CreateScope())
{
    try
    {
        var dbContext = scope.ServiceProvider.GetRequiredService<BillingDbContext>();
        // Use EnsureCreated for now - creates database schema based on models
        // TODO: Replace with proper migrations later
        dbContext.Database.EnsureCreated();
        Log.Information("Database schema created successfully");

        var loggerFactory = scope.ServiceProvider.GetRequiredService<ILoggerFactory>();
        var seederLogger = loggerFactory.CreateLogger("SampleDataSeeder");
        await SampleDataSeeder.SeedAsync(dbContext, seederLogger);
    }
    catch (Exception ex)
    {
        Log.Error(ex, "An error occurred while creating the database schema");
    }
}

// Configure SOAP endpoint
var soapPath = builder.Configuration.GetValue<string>("ServiceSettings:SoapPath") ?? "/BillingService.asmx";

((IApplicationBuilder)app).UseSoapEndpoint<IBillingService>(soapPath, new SoapEncoderOptions
{
    MessageVersion = System.ServiceModel.Channels.MessageVersion.Soap11,
    WriteEncoding = System.Text.Encoding.UTF8
}, SoapSerializer.DataContractSerializer);

app.UseRouting();

// Configure port
var port = builder.Configuration.GetValue<int>("ServiceSettings:Port", 5000);
app.Urls.Add($"http://0.0.0.0:{port}");

Log.Information("=".PadRight(80, '='));
Log.Information("University Billing SOAP Service Starting...");
Log.Information("Environment: {Environment}", environment ?? "local");
Log.Information("Service URL: http://0.0.0.0:{Port}{Path}", port, soapPath);
Log.Information("WSDL URL: http://0.0.0.0:{Port}{Path}?wsdl", port, soapPath);
Log.Information("Database: {Connection}", connectionString?.Split(';')[0]);
Log.Information("=".PadRight(80, '='));

app.Run();
