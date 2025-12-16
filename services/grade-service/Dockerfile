# Multi-stage build for Grades Service
# Stage 1: Builder
FROM python:3.11-slim as builder

WORKDIR /usr/src/app

# Install build dependencies
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    postgresql-client \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir --user -r requirements.txt


# Stage 2: Runtime
FROM python:3.11-slim

LABEL maintainer="university-management"
LABEL service="grades-service"
LABEL version="1.0.0"

WORKDIR /usr/src/app

# Install runtime dependencies only
RUN apt-get update && apt-get install -y \
    postgresql-client \
    libpq5 \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy Python dependencies from builder to system site-packages (not user directory)
COPY --from=builder /root/.local /usr/local

# Copy application code
COPY . .

# Make sure scripts are executable
RUN chmod +x test-api.sh 2>/dev/null || true

# Create non-root user
RUN useradd -m -u 1000 appuser && \
    chown -R appuser:appuser /usr/src/app

# Expose port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8084/health || exit 1

# Switch to non-root user
USER appuser

# Run the application using python -m uvicorn
CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8084"]
