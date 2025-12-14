from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session
from pydantic_settings import BaseSettings
import os


class Settings(BaseSettings):
    """Application settings"""
    database_url: str = os.getenv(
        "DATABASE_URL",
        "postgresql://appuser:password123@localhost:5432/grades_db"
    )
    port: int = int(os.getenv("PORT", 8084))
    environment: str = os.getenv("ENVIRONMENT", "development")

    class Config:
        env_file = ".env"


settings = Settings()

# Create database engine
engine = create_engine(
    settings.database_url,
    echo=settings.environment == "development",
    pool_pre_ping=True,
    pool_size=10,
    max_overflow=20,
)

# Create session factory
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def get_db() -> Session:
    """Dependency to get database session"""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def init_db():
    """Initialize database tables"""
    from app.models import Base
    Base.metadata.create_all(bind=engine)


def seed_grade_calculation_rules(db: Session):
    """Seed default grade calculation rules"""
    from app.models import GradeCalculationRule

    rules = [
        GradeCalculationRule(grade_type="EXAM", weight=0.40, description="Exam grades"),
        GradeCalculationRule(grade_type="MIDTERM", weight=0.20, description="Midterm exam"),
        GradeCalculationRule(grade_type="FINAL", weight=0.30, description="Final exam"),
        GradeCalculationRule(grade_type="HOMEWORK", weight=0.05, description="Homework assignments"),
        GradeCalculationRule(grade_type="PROJECT", weight=0.05, description="Project work"),
    ]

    for rule in rules:
        existing = db.query(GradeCalculationRule).filter(
            GradeCalculationRule.grade_type == rule.grade_type
        ).first()
        if not existing:
            db.add(rule)

    db.commit()
