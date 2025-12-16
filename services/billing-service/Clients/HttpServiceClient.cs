using Polly;
using Polly.CircuitBreaker;
using Polly.Retry;
using Polly.Timeout;
using System.Net;
using System.Text;
using System.Text.Json;
using Serilog;

namespace Universite.BillingService.Clients;

/// <summary>
/// Error response from service calls
/// </summary>
public class ServiceErrorResponse
{
    public string Code { get; set; } = "";
    public string Message { get; set; } = "";
    public string? Details { get; set; }
    public string Timestamp { get; set; } = "";
    public string Service { get; set; } = "";
    public string? TraceId { get; set; }
}

/// <summary>
/// HTTP Service Client with Polly resilience policies
/// Provides circuit breaker, retry, and timeout functionality
/// </summary>
public class HttpServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly AsyncRetryPolicy<HttpResponseMessage> _retryPolicy;
    private readonly AsyncCircuitBreakerPolicy<HttpResponseMessage> _circuitBreakerPolicy;
    private readonly AsyncTimeoutPolicy _timeoutPolicy;
    private readonly string _serviceName;

    public HttpServiceClient(IHttpClientFactory httpClientFactory, string serviceName, string? baseUrl = null)
    {
        _serviceName = serviceName;
        _httpClient = httpClientFactory.CreateClient(serviceName);
        
        if (!string.IsNullOrEmpty(baseUrl))
        {
            _httpClient.BaseAddress = new Uri(baseUrl);
        }

        // Retry policy: 3 retries with exponential backoff
        _retryPolicy = Policy
            .HandleResult<HttpResponseMessage>(r =>
                r.StatusCode >= HttpStatusCode.InternalServerError || // 5xx errors
                r.StatusCode == HttpStatusCode.RequestTimeout)        // 408 timeout
            .WaitAndRetryAsync(
                retryCount: 3,
                sleepDurationProvider: attempt => TimeSpan.FromSeconds(Math.Pow(2, attempt)),
                onRetry: (outcome, timespan, retryCount, context) =>
                {
                    var traceId = context.ContainsKey("TraceId") ? context["TraceId"] : "N/A";
                    Log.Warning(
                        "[{TraceId}] {ServiceName} request failed (attempt {RetryCount}), retrying in {RetryDelay}s",
                        traceId, _serviceName, retryCount, timespan.TotalSeconds
                    );
                });

        // Circuit breaker policy: Open after 5 failures, stay open for 60s
        _circuitBreakerPolicy = Policy
            .HandleResult<HttpResponseMessage>(r =>
                r.StatusCode >= HttpStatusCode.InternalServerError)
            .CircuitBreakerAsync(
                handledEventsAllowedBeforeBreaking: 5,
                durationOfBreak: TimeSpan.FromSeconds(60),
                onBreak: (outcome, duration) =>
                {
                    Log.Error(
                        "{ServiceName} circuit breaker opened. Blocking requests for {Duration}s",
                        _serviceName, duration.TotalSeconds
                    );
                },
                onReset: () =>
                {
                    Log.Information("{ServiceName} circuit breaker reset to closed state", _serviceName);
                },
                onHalfOpen: () =>
                {
                    Log.Information("{ServiceName} circuit breaker half-open, testing service", _serviceName);
                });

        // Timeout policy: 10 seconds
        _timeoutPolicy = Policy.TimeoutAsync(TimeSpan.FromSeconds(10));
    }

    /// <summary>
    /// Send GET request with resilience policies
    /// </summary>
    public async Task<TResponse?> GetAsync<TResponse>(string path, string? traceId = null, string? jwtToken = null)
    {
        var request = new HttpRequestMessage(HttpMethod.Get, path);
        return await SendRequestAsync<TResponse>(request, traceId, jwtToken);
    }

    /// <summary>
    /// Send POST request with resilience policies
    /// </summary>
    public async Task<TResponse?> PostAsync<TRequest, TResponse>(
        string path,
        TRequest data,
        string? traceId = null,
        string? jwtToken = null)
    {
        var request = new HttpRequestMessage(HttpMethod.Post, path)
        {
            Content = new StringContent(
                JsonSerializer.Serialize(data),
                Encoding.UTF8,
                "application/json")
        };
        return await SendRequestAsync<TResponse>(request, traceId, jwtToken);
    }

    /// <summary>
    /// Send PUT request with resilience policies
    /// </summary>
    public async Task<TResponse?> PutAsync<TRequest, TResponse>(
        string path,
        TRequest data,
        string? traceId = null,
        string? jwtToken = null)
    {
        var request = new HttpRequestMessage(HttpMethod.Put, path)
        {
            Content = new StringContent(
                JsonSerializer.Serialize(data),
                Encoding.UTF8,
                "application/json")
        };
        return await SendRequestAsync<TResponse>(request, traceId, jwtToken);
    }

    /// <summary>
    /// Send DELETE request with resilience policies
    /// </summary>
    public async Task<bool> DeleteAsync(string path, string? traceId = null, string? jwtToken = null)
    {
        var request = new HttpRequestMessage(HttpMethod.Delete, path);
        var response = await SendRequestCoreAsync(request, traceId, jwtToken);
        return response.IsSuccessStatusCode;
    }

    /// <summary>
    /// Core request execution with deserialization
    /// </summary>
    private async Task<TResponse?> SendRequestAsync<TResponse>(
        HttpRequestMessage request,
        string? traceId,
        string? jwtToken)
    {
        var response = await SendRequestCoreAsync(request, traceId, jwtToken);

        if (!response.IsSuccessStatusCode)
        {
            await HandleErrorResponse(response, traceId);
        }

        var content = await response.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<TResponse>(content, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        });
    }

    /// <summary>
    /// Core request execution with Polly policies
    /// </summary>
    private async Task<HttpResponseMessage> SendRequestCoreAsync(
        HttpRequestMessage request,
        string? traceId,
        string? jwtToken)
    {
        // Add headers
        if (!string.IsNullOrEmpty(traceId))
        {
            request.Headers.Add("X-Trace-Id", traceId);
        }

        if (!string.IsNullOrEmpty(jwtToken))
        {
            request.Headers.Add("Authorization", $"Bearer {jwtToken}");
        }

        var context = new Context
        {
            ["TraceId"] = traceId ?? Guid.NewGuid().ToString()
        };

        // Execute with combined policies: Timeout -> Retry -> Circuit Breaker
        try
        {
            return await _timeoutPolicy.WrapAsync(_retryPolicy).WrapAsync(_circuitBreakerPolicy)
                .ExecuteAsync(ctx => _httpClient.SendAsync(request), context);
        }
        catch (BrokenCircuitException)
        {
            Log.Error("[{TraceId}] {ServiceName} circuit is open", traceId, _serviceName);
            throw new HttpRequestException($"{_serviceName} is temporarily unavailable (circuit open)");
        }
        catch (TimeoutRejectedException)
        {
            Log.Error("[{TraceId}] {ServiceName} request timed out", traceId, _serviceName);
            throw new HttpRequestException($"{_serviceName} request timed out");
        }
    }

    /// <summary>
    /// Handle error responses from services
    /// </summary>
    private async Task HandleErrorResponse(HttpResponseMessage response, string? traceId)
    {
        var content = await response.Content.ReadAsStringAsync();

        try
        {
            var error = JsonSerializer.Deserialize<ServiceErrorResponse>(content, new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            });

            if (error != null)
            {
                Log.Error(
                    "[{TraceId}] {ServiceName} error: {Code} - {Message}",
                    traceId, _serviceName, error.Code, error.Message
                );

                throw new HttpRequestException(
                    $"{_serviceName} error: {error.Message} (Code: {error.Code})",
                    null,
                    response.StatusCode
                );
            }
        }
        catch (JsonException)
        {
            // Response is not JSON
        }

        Log.Error(
            "[{TraceId}] {ServiceName} HTTP {StatusCode}: {Content}",
            traceId, _serviceName, (int)response.StatusCode, content
        );

        throw new HttpRequestException(
            $"{_serviceName} request failed with status {response.StatusCode}",
            null,
            response.StatusCode
        );
    }
}
