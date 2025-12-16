using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using Serilog;

namespace Universite.BillingService.Middleware;

/// <summary>
/// JWT Authentication Middleware for Billing Service
/// Validates JWT tokens and extracts user information
/// </summary>
public class JwtAuthenticationMiddleware
{
    private readonly RequestDelegate _next;
    private readonly string _jwtSecret;
    private readonly JwtSecurityTokenHandler _tokenHandler;

    public JwtAuthenticationMiddleware(RequestDelegate next, IConfiguration configuration)
    {
        _next = next;
        _jwtSecret = configuration["JWT_SECRET"] ?? Environment.GetEnvironmentVariable("JWT_SECRET") 
            ?? "your-secret-key-change-this-in-production";
        _tokenHandler = new JwtSecurityTokenHandler();
    }

    public async Task InvokeAsync(HttpContext context)
    {
        var path = context.Request.Path.Value ?? "";
        
        // Skip authentication for OPTIONS requests (CORS preflight)
        if (context.Request.Method.Equals("OPTIONS", StringComparison.OrdinalIgnoreCase))
        {
            await _next(context);
            return;
        }
        
        // Skip authentication for WSDL and health check endpoints
        if (path.EndsWith("?wsdl", StringComparison.OrdinalIgnoreCase) ||
            path.Contains("/health", StringComparison.OrdinalIgnoreCase) ||
            path.Contains("/swagger", StringComparison.OrdinalIgnoreCase))
        {
            await _next(context);
            return;
        }

        // Extract Authorization header
        var authHeader = context.Request.Headers["Authorization"].FirstOrDefault();
        
        if (string.IsNullOrEmpty(authHeader))
        {
            Log.Warning("Missing Authorization header");
            await WriteErrorResponse(context, "AUTH_REQUIRED", "Authorization header is required", 401);
            return;
        }

        // Extract token from Bearer scheme
        if (!authHeader.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase))
        {
            Log.Warning("Invalid Authorization header format");
            await WriteErrorResponse(context, "AUTH_INVALID_FORMAT", "Authorization header must use Bearer scheme", 401);
            return;
        }

        var token = authHeader.Substring("Bearer ".Length).Trim();

        try
        {
            // Validate token
            var validationParameters = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtSecret)),
                ValidateIssuer = false,
                ValidateAudience = false,
                ValidateLifetime = true,
                ClockSkew = TimeSpan.Zero
            };

            var principal = _tokenHandler.ValidateToken(token, validationParameters, out var validatedToken);
            
            // Extract user information from claims
            var userId = principal.FindFirst("userId")?.Value 
                ?? principal.FindFirst("user_id")?.Value 
                ?? principal.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            
            var username = principal.FindFirst("username")?.Value 
                ?? principal.FindFirst(ClaimTypes.Name)?.Value;
            
            var email = principal.FindFirst("email")?.Value 
                ?? principal.FindFirst(ClaimTypes.Email)?.Value;
            
            var roles = principal.FindAll("roles")
                .Select(c => c.Value)
                .ToList();
            
            if (roles.Count == 0)
            {
                var rolesClaim = principal.FindFirst(ClaimTypes.Role);
                if (rolesClaim != null)
                {
                    roles.Add(rolesClaim.Value);
                }
            }

            if (string.IsNullOrEmpty(userId))
            {
                Log.Warning("Token missing userId claim");
                await WriteErrorResponse(context, "AUTH_TOKEN_INVALID", "Token missing required user information", 401);
                return;
            }

            // Store user info in HttpContext.Items for access in services
            context.Items["UserId"] = userId;
            context.Items["Username"] = username ?? "";
            context.Items["Email"] = email ?? "";
            context.Items["Roles"] = roles;

            // Add trace ID if not present
            var traceId = context.Request.Headers["X-Trace-Id"].FirstOrDefault() ?? Guid.NewGuid().ToString();
            context.Items["TraceId"] = traceId;
            context.Response.Headers["X-Trace-Id"] = traceId;

            Log.Information("[{TraceId}] User authenticated: {UserId}", traceId, userId);

            await _next(context);
        }
        catch (SecurityTokenExpiredException)
        {
            Log.Warning("Token expired");
            await WriteErrorResponse(context, "AUTH_TOKEN_EXPIRED", "Authentication token has expired", 401);
        }
        catch (SecurityTokenInvalidSignatureException)
        {
            Log.Warning("Invalid token signature");
            await WriteErrorResponse(context, "AUTH_TOKEN_INVALID", "Invalid token signature", 401);
        }
        catch (SecurityTokenException ex)
        {
            Log.Warning(ex, "Token validation failed");
            await WriteErrorResponse(context, "AUTH_TOKEN_INVALID", "Invalid authentication token", 401);
        }
        catch (Exception ex)
        {
            Log.Error(ex, "Error validating JWT token");
            await WriteErrorResponse(context, "AUTH_TOKEN_MALFORMED", "Malformed authentication token", 401);
        }
    }

    private static async Task WriteErrorResponse(HttpContext context, string code, string message, int statusCode)
    {
        var traceId = context.Items["TraceId"]?.ToString() ?? Guid.NewGuid().ToString();
        
        var errorResponse = new
        {
            code,
            message,
            timestamp = DateTime.UtcNow.ToString("o"),
            path = context.Request.Path.Value,
            service = "billing-service",
            traceId
        };

        context.Response.StatusCode = statusCode;
        context.Response.ContentType = "application/json";
        await context.Response.WriteAsJsonAsync(errorResponse);
    }
}

/// <summary>
/// Extension methods for JWT middleware registration
/// </summary>
public static class JwtAuthenticationMiddlewareExtensions
{
    public static IApplicationBuilder UseJwtAuthentication(this IApplicationBuilder builder)
    {
        return builder.UseMiddleware<JwtAuthenticationMiddleware>();
    }
}
