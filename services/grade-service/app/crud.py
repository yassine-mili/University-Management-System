from sqlalchemy.orm import Session
from sqlalchemy import func, and_
from app.models import Grade, GradeCalculationRule, AcademicRecord
from app.schemas import GradeCreate, GradeUpdate, AcademicRecordCreate
from datetime import date
from typing import List, Optional


# ==================== Grade CRUD Operations ====================

def create_grade(db: Session, grade: GradeCreate) -> Grade:
    """Create a new grade"""
    db_grade = Grade(
        student_id=grade.student_id,
        course_id=grade.course_id,
        grade_value=grade.grade_value,
        grade_type=grade.grade_type.value,
        date=grade.date,
    )
    db.add(db_grade)
    db.commit()
    db.refresh(db_grade)
    return db_grade


def get_grade(db: Session, grade_id: int) -> Optional[Grade]:
    """Get a grade by ID"""
    return db.query(Grade).filter(Grade.id == grade_id).first()


def get_all_grades(
    db: Session,
    skip: int = 0,
    limit: int = 100,
    student_id: Optional[int] = None,
    course_id: Optional[int] = None,
    grade_type: Optional[str] = None,
) -> tuple[List[Grade], int]:
    """Get all grades with optional filtering"""
    query = db.query(Grade)

    if student_id:
        query = query.filter(Grade.student_id == student_id)
    if course_id:
        query = query.filter(Grade.course_id == course_id)
    if grade_type:
        query = query.filter(Grade.grade_type == grade_type)

    total = query.count()
    grades = query.offset(skip).limit(limit).all()
    return grades, total


def get_student_grades(db: Session, student_id: int) -> List[Grade]:
    """Get all grades for a specific student"""
    return db.query(Grade).filter(Grade.student_id == student_id).all()


def get_course_grades(db: Session, course_id: int) -> List[Grade]:
    """Get all grades for a specific course"""
    return db.query(Grade).filter(Grade.course_id == course_id).all()


def update_grade(db: Session, grade_id: int, grade_update: GradeUpdate) -> Optional[Grade]:
    """Update a grade"""
    db_grade = get_grade(db, grade_id)
    if not db_grade:
        return None

    update_data = grade_update.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        if key == "grade_type" and value:
            setattr(db_grade, key, value.value)
        elif value is not None:
            setattr(db_grade, key, value)

    db.add(db_grade)
    db.commit()
    db.refresh(db_grade)
    return db_grade


def delete_grade(db: Session, grade_id: int) -> bool:
    """Delete a grade"""
    db_grade = get_grade(db, grade_id)
    if not db_grade:
        return False

    db.delete(db_grade)
    db.commit()
    return True


def check_duplicate_grade(
    db: Session,
    student_id: int,
    course_id: int,
    grade_type: str,
    exclude_id: Optional[int] = None,
) -> bool:
    """Check if a grade already exists for student-course-type combination"""
    query = db.query(Grade).filter(
        and_(
            Grade.student_id == student_id,
            Grade.course_id == course_id,
            Grade.grade_type == grade_type,
        )
    )

    if exclude_id:
        query = query.filter(Grade.id != exclude_id)

    return query.first() is not None


# ==================== Grade Calculation Rule CRUD ====================

def get_grade_calculation_rule(db: Session, grade_type: str) -> Optional[GradeCalculationRule]:
    """Get grade calculation rule by type"""
    return db.query(GradeCalculationRule).filter(
        GradeCalculationRule.grade_type == grade_type
    ).first()


def get_all_grade_calculation_rules(db: Session) -> List[GradeCalculationRule]:
    """Get all grade calculation rules"""
    return db.query(GradeCalculationRule).all()


# ==================== Academic Record CRUD ====================

def create_academic_record(db: Session, record: AcademicRecordCreate) -> AcademicRecord:
    """Create a new academic record"""
    db_record = AcademicRecord(student_id=record.student_id)
    db.add(db_record)
    db.commit()
    db.refresh(db_record)
    return db_record


def get_academic_record(db: Session, student_id: int) -> Optional[AcademicRecord]:
    """Get academic record for a student"""
    return db.query(AcademicRecord).filter(
        AcademicRecord.student_id == student_id
    ).first()


def update_academic_record(
    db: Session,
    student_id: int,
    gpa: float,
    total_credits: int,
    passed_courses: int,
    failed_courses: int,
    academic_status: str,
) -> Optional[AcademicRecord]:
    """Update academic record"""
    record = get_academic_record(db, student_id)
    if not record:
        record = create_academic_record(db, AcademicRecordCreate(student_id=student_id))

    record.gpa = gpa
    record.total_credits = total_credits
    record.passed_courses = passed_courses
    record.failed_courses = failed_courses
    record.academic_status = academic_status

    db.add(record)
    db.commit()
    db.refresh(record)
    return record


# ==================== Statistics and Analytics ====================

def get_grade_statistics(
    db: Session,
    student_id: Optional[int] = None,
    course_id: Optional[int] = None,
) -> dict:
    """Get grade statistics"""
    query = db.query(Grade)

    if student_id:
        query = query.filter(Grade.student_id == student_id)
    if course_id:
        query = query.filter(Grade.course_id == course_id)

    grades = query.all()

    if not grades:
        return {
            "average_grade": 0,
            "highest_grade": 0,
            "lowest_grade": 0,
            "total_grades": 0,
            "grade_distribution": {},
        }

    grade_values = [g.grade_value for g in grades]
    average = sum(grade_values) / len(grade_values)

    # Grade distribution by type
    distribution = {}
    for grade in grades:
        if grade.grade_type not in distribution:
            distribution[grade.grade_type] = []
        distribution[grade.grade_type].append(grade.grade_value)

    grade_distribution = {
        grade_type: {
            "count": len(values),
            "average": sum(values) / len(values),
        }
        for grade_type, values in distribution.items()
    }

    return {
        "average_grade": round(average, 2),
        "highest_grade": max(grade_values),
        "lowest_grade": min(grade_values),
        "total_grades": len(grades),
        "grade_distribution": grade_distribution,
    }


def get_semester_average(db: Session, student_id: int, semester: str) -> float:
    """Calculate semester average for a student"""
    # This assumes grades have a date field that can be used to determine semester
    # Semester format: "2024-1" for Fall 2024, "2024-2" for Spring 2024
    year, sem = semester.split("-")
    year = int(year)
    sem = int(sem)

    # Determine date range for semester
    if sem == 1:  # Fall semester (Sep-Dec)
        start_date = date(year, 9, 1)
        end_date = date(year, 12, 31)
    else:  # Spring semester (Jan-May)
        start_date = date(year, 1, 1)
        end_date = date(year, 5, 31)

    grades = db.query(Grade).filter(
        and_(
            Grade.student_id == student_id,
            Grade.date >= start_date,
            Grade.date <= end_date,
        )
    ).all()

    if not grades:
        return 0.0

    average = sum(g.grade_value for g in grades) / len(grades)
    return round(average, 2)
