from sqlalchemy import Column, Integer, String, Float, DateTime, Date, Enum, ForeignKey, CheckConstraint, UniqueConstraint
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from datetime import datetime
import enum

Base = declarative_base()


class GradeTypeEnum(str, enum.Enum):
    """Grade type enumeration"""
    EXAM = "EXAM"
    HOMEWORK = "HOMEWORK"
    PROJECT = "PROJECT"
    MIDTERM = "MIDTERM"
    FINAL = "FINAL"


class Grade(Base):
    """Grade model representing a student's grade for a course"""
    __tablename__ = "grades"

    id = Column(Integer, primary_key=True, index=True)
    student_id = Column(Integer, nullable=False, index=True)
    course_id = Column(Integer, nullable=False, index=True)
    grade_value = Column(Float, nullable=False)
    grade_type = Column(String(50), nullable=False)
    date = Column(Date, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    __table_args__ = (
        CheckConstraint("grade_value >= 0 AND grade_value <= 20", name="check_grade_range"),
        UniqueConstraint("student_id", "course_id", "grade_type", name="unique_student_course_grade_type"),
    )

    def __repr__(self):
        return f"<Grade(id={self.id}, student_id={self.student_id}, course_id={self.course_id}, grade_value={self.grade_value})>"


class GradeCalculationRule(Base):
    """Grade calculation rules for different grade types"""
    __tablename__ = "grade_calculation_rules"

    id = Column(Integer, primary_key=True, index=True)
    grade_type = Column(String(50), nullable=False, unique=True, index=True)
    weight = Column(Float, nullable=False)  # Weight in GPA calculation (0-1)
    description = Column(String(255))
    created_at = Column(DateTime, default=datetime.utcnow)

    def __repr__(self):
        return f"<GradeCalculationRule(grade_type={self.grade_type}, weight={self.weight})>"


class AcademicRecord(Base):
    """Academic record for tracking student's academic status"""
    __tablename__ = "academic_records"

    id = Column(Integer, primary_key=True, index=True)
    student_id = Column(Integer, nullable=False, unique=True, index=True)
    gpa = Column(Float, default=0.0)
    total_credits = Column(Integer, default=0)
    passed_courses = Column(Integer, default=0)
    failed_courses = Column(Integer, default=0)
    academic_status = Column(String(50), default="ACTIVE")  # ACTIVE, PROBATION, SUSPENDED
    last_updated = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    created_at = Column(DateTime, default=datetime.utcnow)

    def __repr__(self):
        return f"<AcademicRecord(student_id={self.student_id}, gpa={self.gpa}, status={self.academic_status})>"
