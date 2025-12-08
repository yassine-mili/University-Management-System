from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging

from app.database import init_db, seed_grade_calculation_rules, SessionLocal, settings
from app.routers import grades

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Lifespan context manager for startup and shutdown events"""
    # Startup
    logger.info("Starting Grades Service...")
    try:
        init_db()
        logger.info("Database initialized successfully")

        # Seed default grade calculation rules
        db = SessionLocal()
        try:
            seed_grade_calculation_rules(db)
            logger.info("Grade calculation rules seeded")
        finally:
            db.close()
    except Exception as e:
        logger.error(f"Error during startup: {e}")
        raise

    yield

    # Shutdown
    logger.info("Shutting down Grades Service...")


# Create FastAPI application
app = FastAPI(
    title="University Grades Service",
    description="REST API for managing student grades, GPA calculation, and academic transcripts",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
    lifespan=lifespan,
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(grades.router)


@app.get(
    "/",
    summary="Root endpoint",
    description="Welcome to the Grades Service API",
    tags=["root"]
)
def root():
    """Root endpoint"""
    return {
        "message": "Welcome to University Grades Service",
        "version": "1.0.0",
        "docs": "/docs",
        "redoc": "/redoc",
        "health": "/api/v1/grades/health"
    }


@app.get(
    "/health",
    summary="Health check",
    description="Check if the service is running",
    tags=["health"]
)
def health_check():
    """Health check endpoint"""
    return {
        "status": "healthy",
        "service": "grades-service",
        "version": "1.0.0"
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=settings.port,
        reload=settings.environment == "development",
        log_level="info",
    )
