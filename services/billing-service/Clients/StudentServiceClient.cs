using Serilog;

namespace Universite.BillingService.Clients;

/// <summary>
/// Student DTO for inter-service communication
/// </summary>
public class StudentDto
{
    public string Id { get; set; } = "";
    public string FirstName { get; set; } = "";
    public string LastName { get; set; } = "";
    public string Email { get; set; } = "";
    public string Status { get; set; } = "";
}

/// <summary>
/// Client for Student Service operations
/// </summary>
public class StudentServiceClient
{
    private readonly HttpServiceClient _httpClient;
    private readonly string _baseUrl;

    public StudentServiceClient(IHttpClientFactory httpClientFactory, IConfiguration configuration)
    {
        _baseUrl = configuration["STUDENT_SERVICE_URL"] 
            ?? Environment.GetEnvironmentVariable("STUDENT_SERVICE_URL") 
            ?? "http://localhost:3001";
        
        _httpClient = new HttpServiceClient(httpClientFactory, "StudentService", _baseUrl);
    }

    /// <summary>
    /// Get student by ID
    /// </summary>
    public async Task<StudentDto?> GetStudentAsync(string studentId, string? traceId = null)
    {
        Log.Information("[{TraceId}] Fetching student {StudentId} from Student Service", traceId, studentId);

        try
        {
            return await _httpClient.GetAsync<StudentDto>(
                $"/api/v1/students/{studentId}",
                traceId
            );
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            Log.Warning("[{TraceId}] Student {StudentId} not found", traceId, studentId);
            return null;
        }
        catch (Exception ex)
        {
            Log.Error(ex, "[{TraceId}] Failed to fetch student {StudentId}", traceId, studentId);
            throw;
        }
    }

    /// <summary>
    /// Validate that student exists
    /// </summary>
    public async Task<bool> ValidateStudentAsync(string studentId, string? traceId = null)
    {
        var student = await GetStudentAsync(studentId, traceId);
        
        if (student == null)
        {
            throw new InvalidOperationException($"Student with ID {studentId} does not exist");
        }

        return true;
    }

    /// <summary>
    /// Validate multiple students exist
    /// </summary>
    public async Task<bool> ValidateStudentsAsync(IEnumerable<string> studentIds, string? traceId = null)
    {
        foreach (var studentId in studentIds)
        {
            await ValidateStudentAsync(studentId, traceId);
        }

        return true;
    }
}
