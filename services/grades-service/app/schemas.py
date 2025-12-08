from pydantic import BaseModel, field_validator
from datetime import date, datetime
from typing import Optional, List, Any
from enum import Enum


class GradeTypeEnum(str, Enum):
    """Grade type enumeration"""
    EXAM = "EXAM"
    HOMEWORK = "HOMEWORK"
    PROJECT = "PROJECT"
    MIDTERM = "MIDTERM"
    FINAL = "FINAL"


class GradeCreate(BaseModel):
    """Schema for creating a grade"""
    student_id: int
    course_id: int
    grade_value: float
    grade_type: GradeTypeEnum
    date: date

    @field_validator('student_id')
    @classmethod
    def validate_student_id(cls, v):
        if v <= 0:
            raise ValueError('Student ID must be positive')
        return v

    @field_validator('course_id')
    @classmethod
    def validate_course_id(cls, v):
        if v <= 0:
            raise ValueError('Course ID must be positive')
        return v

    @field_validator('grade_value')
    @classmethod
    def validate_grade_value(cls, v):
        if not (0 <= v <= 20):
            raise ValueError('Grade value must be between 0 and 20')
        return v

    @field_validator('date')
    @classmethod
    def validate_date(cls, v):
        if v > date.today():
            raise ValueError('Date cannot be in the future')
        return v


class GradeUpdate(BaseModel):
    """Schema for updating a grade"""
    grade_value: Optional[float] = None
    grade_type: Optional[GradeTypeEnum] = None
    date: Optional[date] = None

    @field_validator('grade_value')
    @classmethod
    def validate_grade_value(cls, v):
        if v is not None and not (0 <= v <= 20):
            raise ValueError('Grade value must be between 0 and 20')
        return v

    @field_validator('date')
    @classmethod
    def validate_date(cls, v):
        if v is not None and v > date.today():
            raise ValueError('Date cannot be in the future')
        return v


class Grade(BaseModel):
    """Schema for grade response"""
    id: int
    student_id: int
    course_id: int
    grade_value: float
    grade_type: str
    date: date
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


class GradeCalculationRuleCreate(BaseModel):
    """Schema for creating a grade calculation rule"""
    grade_type: str
    weight: float
    description: Optional[str] = None


class GradeCalculationRule(BaseModel):
    """Schema for grade calculation rule response"""
    id: int
    grade_type: str
    weight: float
    description: Optional[str] = None
    created_at: datetime

    class Config:
        from_attributes = True


class AcademicRecordCreate(BaseModel):
    """Schema for creating an academic record"""
    student_id: int


class AcademicRecord(BaseModel):
    """Schema for academic record response"""
    id: int
    student_id: int
    gpa: float
    total_credits: int
    passed_courses: int
    failed_courses: int
    academic_status: str
    last_updated: datetime
    created_at: datetime

    class Config:
        from_attributes = True


class StudentGPA(BaseModel):
    """Schema for student GPA response"""
    student_id: int
    gpa: float
    total_grades: int
    average_grade: float


class TranscriptEntry(BaseModel):
    """Schema for a single transcript entry"""
    course_id: int
    grade_value: float
    grade_type: str
    date: date


class StudentTranscript(BaseModel):
    """Schema for student academic transcript"""
    student_id: int
    gpa: float
    total_credits: int
    academic_status: str
    grades: List[TranscriptEntry]
    generated_at: datetime


class GradeStatistics(BaseModel):
    """Schema for grade statistics"""
    course_id: Optional[int] = None
    student_id: Optional[int] = None
    average_grade: float
    highest_grade: float
    lowest_grade: float
    total_grades: int
    grade_distribution: dict = {}


class ErrorResponse(BaseModel):
    """Schema for error response"""
    message: str
    error: str
    statusCode: int
    timestamp: datetime
