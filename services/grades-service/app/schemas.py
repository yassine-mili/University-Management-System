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

    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "student_id": 1001,
                    "course_id": 101,
                    "grade_value": 15.5,
                    "grade_type": "FINAL",
                    "date": "2024-12-08"
                }
            ]
        }
    }

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


class CourseStatistics(BaseModel):
    """Schema for course statistics"""
    course_id: int
    total_students: int
    average_grade: float
    highest_grade: float
    lowest_grade: float
    passed_count: int
    failed_count: int
    grade_type_distribution: dict = {}

    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "course_id": 101,
                    "total_students": 45,
                    "average_grade": 14.5,
                    "highest_grade": 19.5,
                    "lowest_grade": 8.0,
                    "passed_count": 40,
                    "failed_count": 5,
                    "grade_type_distribution": {
                        "FINAL": {"count": 45, "average": 15.2},
                        "MIDTERM": {"count": 45, "average": 14.0}
                    }
                }
            ]
        }
    }


class SemesterAverage(BaseModel):
    """Schema for semester average response"""
    student_id: int
    semester: str
    average_grade: float
    total_grades: int
    courses_count: int

    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "student_id": 1001,
                    "semester": "2024-1",
                    "average_grade": 15.5,
                    "total_grades": 12,
                    "courses_count": 5
                }
            ]
        }
    }


class ApiResponse(BaseModel):
    """Generic API response wrapper"""
    message: str
    data: Any
    timestamp: str


class ErrorResponse(BaseModel):
    """Schema for error response"""
    message: str
    error: str
    statusCode: int
    timestamp: datetime
