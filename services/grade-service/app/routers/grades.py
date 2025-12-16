from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from datetime import datetime
from typing import List, Optional, Any

from app.database import get_db
from app.schemas import (
    GradeCreate, GradeUpdate, Grade, StudentGPA, StudentTranscript,
    GradeStatistics, CourseStatistics, ApiResponse, ErrorResponse, SemesterAverage
)
from app import crud
from app.business_logic import GradeCalculationService

router = APIRouter(prefix="/api/v1/grades", tags=["grades"])


def _parse_student_identifier(identifier: str) -> int | None:
    """Convert identifiers like 'STU000123' or '123' into integer IDs."""
    if identifier is None:
        return None

    identifier = identifier.strip()
    if not identifier:
        return None

    # Allow purely numeric identifiers directly
    if identifier.isdigit():
        return int(identifier)

    # Extract digits from formatted identifiers (e.g., STU000123 -> 123)
    digits = "".join(ch for ch in identifier if ch.isdigit())
    if digits:
        try:
            return int(digits)
        except ValueError:
            return None

    return None


def model_to_dict(obj) -> dict:
    """Convert SQLAlchemy model to dict, excluding internal attributes"""
    if obj is None:
        return None
    result = {}
    for column in obj.__table__.columns:
        value = getattr(obj, column.name)
        if isinstance(value, datetime):
            result[column.name] = value.isoformat()
        else:
            result[column.name] = value
    return result


# ==================== Grade CRUD Endpoints ====================

@router.post(
    "",
    status_code=201,
    summary="Create a new grade",
    description="Create a new grade entry for a student in a course",
    responses={
        201: {"description": "Grade created successfully"},
        400: {"description": "Invalid input or duplicate grade"},
        404: {"description": "Student or course not found"},
    }
)
def create_grade(
    grade: GradeCreate,
    db: Session = Depends(get_db)
):
    """Create a new grade entry"""
    # Check for duplicate grade
    if crud.check_duplicate_grade(
        db,
        grade.student_id,
        grade.course_id,
        grade.grade_type.value
    ):
        raise HTTPException(
            status_code=400,
            detail="A grade of this type already exists for this student-course combination"
        )

    db_grade = crud.create_grade(db, grade)

    # Update academic record
    GradeCalculationService.update_academic_record(db, grade.student_id)

    return {
        "message": "Grade created successfully",
        "data": {"id": db_grade.id, "grade": model_to_dict(db_grade)},
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get(
    "",
    summary="List all grades",
    description="Get all grades with optional filtering",
    responses={
        200: {"description": "Grades retrieved successfully"},
    }
)
def list_grades(
    skip: int = Query(0, ge=0, description="Number of records to skip"),
    limit: int = Query(100, ge=1, le=1000, description="Number of records to return"),
    student_id: Optional[int] = Query(None, description="Filter by student ID"),
    course_id: Optional[int] = Query(None, description="Filter by course ID"),
    grade_type: Optional[str] = Query(None, description="Filter by grade type"),
    db: Session = Depends(get_db)
):
    """Get all grades with optional filtering"""
    grades, total = crud.get_all_grades(
        db,
        skip=skip,
        limit=limit,
        student_id=student_id,
        course_id=course_id,
        grade_type=grade_type
    )

    return {
        "message": "Grades retrieved successfully",
        "data": {
            "grades": [model_to_dict(g) for g in grades],
            "total": total,
            "skip": skip,
            "limit": limit
        },
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get(
    "/{grade_id}",
    summary="Get a single grade",
    description="Retrieve a specific grade by ID",
    responses={
        200: {"description": "Grade retrieved successfully"},
        404: {"description": "Grade not found"},
    }
)
def get_grade(grade_id: int, db: Session = Depends(get_db)):
    """Get a single grade by ID"""
    db_grade = crud.get_grade(db, grade_id)
    if not db_grade:
        raise HTTPException(status_code=404, detail="Grade not found")

    return {
        "message": "Grade retrieved successfully",
        "data": {"grade": model_to_dict(db_grade)},
        "timestamp": datetime.utcnow().isoformat()
    }


@router.put(
    "/{grade_id}",
    summary="Update a grade",
    description="Update an existing grade entry",
    responses={
        200: {"description": "Grade updated successfully"},
        404: {"description": "Grade not found"},
        400: {"description": "Invalid input"},
    }
)
def update_grade(
    grade_id: int,
    grade_update: GradeUpdate,
    db: Session = Depends(get_db)
):
    """Update a grade"""
    db_grade = crud.get_grade(db, grade_id)
    if not db_grade:
        raise HTTPException(status_code=404, detail="Grade not found")

    # Check for duplicate if changing grade type
    if grade_update.grade_type:
        if crud.check_duplicate_grade(
            db,
            db_grade.student_id,
            db_grade.course_id,
            grade_update.grade_type.value,
            exclude_id=grade_id
        ):
            raise HTTPException(
                status_code=400,
                detail="A grade of this type already exists for this student-course combination"
            )

    updated_grade = crud.update_grade(db, grade_id, grade_update)

    # Update academic record
    GradeCalculationService.update_academic_record(db, db_grade.student_id)

    return {
        "message": "Grade updated successfully",
        "data": {"grade": model_to_dict(updated_grade)},
        "timestamp": datetime.utcnow().isoformat()
    }


@router.delete(
    "/{grade_id}",
    status_code=200,
    summary="Delete a grade",
    description="Remove a grade entry",
    responses={
        200: {"description": "Grade deleted successfully"},
        404: {"description": "Grade not found"},
    }
)
def delete_grade(grade_id: int, db: Session = Depends(get_db)):
    """Delete a grade"""
    db_grade = crud.get_grade(db, grade_id)
    if not db_grade:
        raise HTTPException(status_code=404, detail="Grade not found")

    student_id = db_grade.student_id
    success = crud.delete_grade(db, grade_id)

    if not success:
        raise HTTPException(status_code=404, detail="Grade not found")

    # Update academic record
    GradeCalculationService.update_academic_record(db, student_id)

    return {
        "message": "Grade deleted successfully",
        "data": {"id": grade_id},
        "timestamp": datetime.utcnow().isoformat()
    }


# ==================== Student-Specific Endpoints ====================

@router.get(
    "/student/{student_identifier}",
    summary="Get student grades",
    description="Retrieve all grades for a specific student",
    responses={
        200: {"description": "Grades retrieved successfully"},
        404: {"description": "Student not found"},
    }
)
def get_student_grades(student_identifier: str, db: Session = Depends(get_db)):
    """Get all grades for a student"""
    student_id = _parse_student_identifier(student_identifier)
    if student_id is None:
        return {
            "message": "No grades found for this student",
            "data": {"grades": []},
            "timestamp": datetime.utcnow().isoformat()
        }

    grades = crud.get_student_grades(db, student_id)

    if not grades:
        return {
            "message": "No grades found for this student",
            "data": {"grades": []},
            "timestamp": datetime.utcnow().isoformat()
        }

    return {
        "message": "Student grades retrieved successfully",
        "data": {"grades": [model_to_dict(g) for g in grades]},
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get(
    "/student/{student_identifier}/gpa",
    summary="Calculate student GPA",
    description="Calculate the GPA for a specific student",
    responses={
        200: {"description": "GPA calculated successfully"},
    }
)
def calculate_student_gpa(student_identifier: str, db: Session = Depends(get_db)):
    """Calculate GPA for a student"""
    student_id = _parse_student_identifier(student_identifier)
    if student_id is None:
        return {
            "message": "GPA calculated successfully",
            "data": {
                "student_id": student_identifier,
                "gpa": 0.0,
                "total_grades": 0,
                "average_grade": 0.0
            },
            "timestamp": datetime.utcnow().isoformat()
        }

    gpa, total_grades = GradeCalculationService.calculate_gpa(db, student_id)
    average = GradeCalculationService.calculate_average_grade(db, student_id)

    return {
        "message": "GPA calculated successfully",
        "data": {
            "student_id": student_identifier,
            "gpa": gpa,
            "total_grades": total_grades,
            "average_grade": average
        },
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get(
    "/student/{student_identifier}/average",
    summary="Calculate student average grade",
    description="""Calculate the overall average grade for a specific student.
    
    Returns simple average of all grades and weighted GPA on 4.0 scale.""",
    responses={
        200: {"description": "Average calculated successfully"},
        404: {"description": "No grades found for student"},
    }
)
def calculate_student_average(student_identifier: str, db: Session = Depends(get_db)):
    """Calculate overall average and GPA for a student"""
    student_id = _parse_student_identifier(student_identifier)
    if student_id is None:
        return {
            "message": "Average calculated successfully",
            "data": {
                "student_id": student_identifier,
                "gpa": 0.0,
                "total_grades": 0,
                "average_grade": 0.0
            },
            "timestamp": datetime.utcnow().isoformat()
        }

    gpa, total_grades = GradeCalculationService.calculate_gpa(db, student_id)
    average = GradeCalculationService.calculate_average_grade(db, student_id)

    if total_grades == 0:
        return {
            "message": "Average calculated successfully",
            "data": {
                "student_id": student_identifier,
                "gpa": 0.0,
                "total_grades": 0,
                "average_grade": 0.0
            },
            "timestamp": datetime.utcnow().isoformat()
        }

    return {
        "message": "Average calculated successfully",
        "data": {
            "student_id": student_identifier,
            "gpa": gpa,
            "total_grades": total_grades,
            "average_grade": average
        },
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get(
    "/student/{student_identifier}/semester/{semester}/average",
    summary="Calculate semester average",
    description="""Calculate average grade for a specific semester.
    
    Format: 'YYYY-S' where S is 1 (Fall) or 2 (Spring).
    Example: '2024-1' for Fall 2024.""",
    responses={
        200: {"description": "Semester average calculated"},
        400: {"description": "Invalid semester format"},
    }
)
def calculate_semester_average(
    student_identifier: str,
    semester: str,
    db: Session = Depends(get_db)
):
    """Calculate semester average for a student"""
    student_id = _parse_student_identifier(student_identifier)
    if student_id is None:
        return {
            "message": "Semester average calculated",
            "data": {
                "student_id": student_identifier,
                "semester": semester,
                "average_grade": 0.0,
                "total_grades": 0,
                "courses_count": 0
            },
            "timestamp": datetime.utcnow().isoformat()
        }

    try:
        average = crud.get_semester_average(db, student_id, semester)
        
        # Get grade count for semester
        year, sem = semester.split("-")
        year = int(year)
        sem = int(sem)
        
        from datetime import date as dt_date
        from sqlalchemy import and_
        from app.models import Grade
        
        if sem == 1:
            start_date = dt_date(year, 9, 1)
            end_date = dt_date(year, 12, 31)
        else:
            start_date = dt_date(year, 1, 1)
            end_date = dt_date(year, 5, 31)
        
        grades = db.query(Grade).filter(
            and_(
                Grade.student_id == student_id,
                Grade.date >= start_date,
                Grade.date <= end_date,
            )
        ).all()
        
        courses = len(set(g.course_id for g in grades))
        
        return {
            "message": "Semester average calculated",
            "data": {
                "student_id": student_identifier,
                "semester": semester,
                "average_grade": average,
                "total_grades": len(grades),
                "courses_count": courses
            },
            "timestamp": datetime.utcnow().isoformat()
        }
    except ValueError as e:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid semester format: {str(e)}"
        )


@router.get(
    "/student/{student_identifier}/transcript",
    summary="Generate student transcript",
    description="Generate an academic transcript for a student",
    responses={
        200: {"description": "Transcript generated successfully"},
    }
)
def generate_transcript(student_identifier: str, db: Session = Depends(get_db)):
    """Generate academic transcript for a student"""
    student_id = _parse_student_identifier(student_identifier)
    if student_id is None:
        return {
            "message": "Transcript generated successfully",
            "data": {
                "student_id": student_identifier,
                "gpa": 0.0,
                "total_credits": 0,
                "academic_status": "NO_RECORD",
                "grades": [],
                "generated_at": datetime.utcnow().isoformat()
            },
            "timestamp": datetime.utcnow().isoformat()
        }

    transcript = GradeCalculationService.generate_transcript(db, student_id)

    return {
        "message": "Transcript generated successfully",
        "data": transcript,
        "timestamp": datetime.utcnow().isoformat()
    }


# ==================== Course-Specific Endpoints ====================

@router.get(
    "/course/{course_id}",
    summary="Get course grades",
    description="Retrieve all grades for a specific course",
    responses={
        200: {"description": "Grades retrieved successfully"},
    }
)
def get_course_grades(course_id: int, db: Session = Depends(get_db)):
    """Get all grades for a course"""
    grades = crud.get_course_grades(db, course_id)

    if not grades:
        return {
            "message": "No grades found for this course",
            "data": {"grades": []},
            "timestamp": datetime.utcnow().isoformat()
        }

    return {
        "message": "Course grades retrieved successfully",
        "data": {"grades": [model_to_dict(g) for g in grades]},
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get(
    "/course/{course_id}/statistics",
    summary="Get course statistics",
    description="Get statistical analysis of grades for a course",
    responses={
        200: {"description": "Statistics retrieved successfully"},
    }
)
def get_course_statistics(course_id: int, db: Session = Depends(get_db)):
    """Get statistics for a course"""
    stats = GradeCalculationService.get_course_statistics(db, course_id)

    return {
        "message": "Course statistics retrieved successfully",
        "data": stats,
        "timestamp": datetime.utcnow().isoformat()
    }


# ==================== Statistics Endpoints ====================

@router.get(
    "/statistics/student/{student_identifier}",
    summary="Get student grade statistics",
    description="Get statistical analysis of a student's grades",
    responses={
        200: {"description": "Statistics retrieved successfully"},
    }
)
def get_student_statistics(student_identifier: str, db: Session = Depends(get_db)):
    """Get grade statistics for a student"""
    student_id = _parse_student_identifier(student_identifier)
    if student_id is None:
        return {
            "message": "Student statistics retrieved successfully",
            "data": {
                "student_id": student_identifier,
                "average_grade": 0.0,
                "highest_grade": 0.0,
                "lowest_grade": 0.0,
                "total_grades": 0,
                "grade_distribution": {}
            },
            "timestamp": datetime.utcnow().isoformat()
        }
    stats = crud.get_grade_statistics(db, student_id=student_id)

    return {
        "message": "Student statistics retrieved successfully",
        "data": stats,
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get(
    "/statistics/distribution/{student_identifier}",
    summary="Get grade distribution",
    description="Get grade distribution by type for a student",
    responses={
        200: {"description": "Distribution retrieved successfully"},
    }
)
def get_grade_distribution(student_identifier: str, db: Session = Depends(get_db)):
    """Get grade distribution by grade type for a student"""
    student_id = _parse_student_identifier(student_identifier)
    if student_id is None:
        return {
            "message": "No grades found for this student",
            "data": {"distribution": {}},
            "timestamp": datetime.utcnow().isoformat()
        }

    distribution = GradeCalculationService.get_grade_distribution_by_type(db, student_id)

    return {
        "message": "Grade distribution retrieved successfully",
        "data": {"distribution": distribution},
        "timestamp": datetime.utcnow().isoformat()
    }


# ==================== Health Check ====================

@router.get(
    "/health",
    summary="Health check",
    description="Check if the grades service is running",
    responses={
        200: {"description": "Service is healthy"},
    }
)
def health_check():
    """Health check endpoint"""
    return {
        "message": "Grades service is healthy",
        "data": {"status": "ok"},
        "timestamp": datetime.utcnow().isoformat()
    }
