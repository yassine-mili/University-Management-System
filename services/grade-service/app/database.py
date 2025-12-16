from datetime import date
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session
from pydantic_settings import BaseSettings
import os


class Settings(BaseSettings):
    """Application settings"""
    database_url: str = os.getenv(
        "DATABASE_URL",
        "postgresql://user:password@localhost:5435/grades_db"
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


def _sample_data_enabled() -> bool:
    flag = os.getenv("ENABLE_SAMPLE_DATA", "true")
    return flag.lower() != "false"


def seed_sample_grades(db: Session):
    """Seed demo grades for showcase dashboards"""
    if not _sample_data_enabled():
        return

    from app.models import Grade  # Imported lazily to avoid circular import
    from app.business_logic import GradeCalculationService

    existing = (
        db.query(Grade)
        .filter(Grade.student_id == 1)
        .first()
    )

    if existing:
        return

    sample_grades = [
        Grade(
            student_id=1,
            course_id=1,
            grade_value=16.5,
            grade_type="MIDTERM",
            date=date(2024, 10, 15),
        ),
        Grade(
            student_id=1,
            course_id=1,
            grade_value=18.0,
            grade_type="FINAL",
            date=date(2024, 12, 2),
        ),
        Grade(
            student_id=1,
            course_id=2,
            grade_value=14.0,
            grade_type="PROJECT",
            date=date(2024, 11, 12),
        ),
        Grade(
            student_id=1,
            course_id=2,
            grade_value=15.5,
            grade_type="EXAM",
            date=date(2024, 12, 5),
        ),
    ]

    db.add_all(sample_grades)
    db.commit()

    # Refresh academic record with the new data
    try:
        GradeCalculationService.update_academic_record(db, 1)
    except Exception as exc:  # pragma: no cover - safeguard for demo seeding
        print("⚠️ Unable to update academic record during sample seeding:", exc)
