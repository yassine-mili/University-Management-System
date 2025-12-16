from sqlalchemy.orm import Session
from app.models import Grade, GradeCalculationRule, AcademicRecord
from app import crud
from typing import List, Dict, Tuple
from datetime import datetime


class GradeCalculationService:
    """Service for grade calculations and academic analytics"""

    @staticmethod
    def calculate_gpa(db: Session, student_id: int) -> Tuple[float, int]:
        """
        Calculate GPA for a student using weighted average.
        GPA is calculated on a 4.0 scale.

        Formula: GPA = SUM(grade_value * weight) / SUM(weight)
        where grade_value is normalized to 4.0 scale (grade_value / 5)
        """
        grades = crud.get_student_grades(db, student_id)

        if not grades:
            return 0.0, 0

        # Group grades by type and get the latest of each type per course
        grade_dict = {}
        for grade in grades:
            key = (grade.course_id, grade.grade_type)
            if key not in grade_dict or grade.date > grade_dict[key].date:
                grade_dict[key] = grade

        latest_grades = list(grade_dict.values())

        if not latest_grades:
            return 0.0, 0

        # Calculate weighted GPA
        total_weight = 0
        weighted_sum = 0

        for grade in latest_grades:
            rule = crud.get_grade_calculation_rule(db, grade.grade_type)
            weight = rule.weight if rule else 0.1

            # Normalize grade to 4.0 scale (0-20 -> 0-4)
            normalized_grade = (grade.grade_value / 5.0)

            weighted_sum += normalized_grade * weight
            total_weight += weight

        gpa = weighted_sum / total_weight if total_weight > 0 else 0.0
        gpa = round(min(gpa, 4.0), 2)  # Cap at 4.0

        return gpa, len(latest_grades)

    @staticmethod
    def calculate_average_grade(db: Session, student_id: int) -> float:
        """Calculate simple average of all grades"""
        grades = crud.get_student_grades(db, student_id)

        if not grades:
            return 0.0

        average = sum(g.grade_value for g in grades) / len(grades)
        return round(average, 2)

    @staticmethod
    def determine_academic_status(gpa: float, passed_courses: int, failed_courses: int) -> str:
        """
        Determine academic status based on GPA and course performance.

        ACTIVE: GPA >= 2.0 and failed_courses <= 2
        PROBATION: 1.0 <= GPA < 2.0 or failed_courses > 2
        SUSPENDED: GPA < 1.0 or failed_courses > 4
        """
        if gpa < 1.0 or failed_courses > 4:
            return "SUSPENDED"
        elif gpa < 2.0 or failed_courses > 2:
            return "PROBATION"
        else:
            return "ACTIVE"

    @staticmethod
    def calculate_course_performance(db: Session, student_id: int) -> Tuple[int, int]:
        """
        Calculate passed and failed courses for a student.
        A course is passed if the final grade >= 10, failed if < 10.
        """
        grades = crud.get_student_grades(db, student_id)

        # Group by course and get the final grade (or latest grade if no final)
        course_grades = {}
        for grade in grades:
            if grade.course_id not in course_grades:
                course_grades[grade.course_id] = []
            course_grades[grade.course_id].append(grade)

        passed = 0
        failed = 0

        for course_id, course_grade_list in course_grades.items():
            # Get the final grade if exists, otherwise use the highest grade
            final_grade = None
            for g in course_grade_list:
                if g.grade_type == "FINAL":
                    final_grade = g.grade_value
                    break

            if final_grade is None:
                final_grade = max(g.grade_value for g in course_grade_list)

            if final_grade >= 10:
                passed += 1
            else:
                failed += 1

        return passed, failed

    @staticmethod
    def update_academic_record(db: Session, student_id: int) -> AcademicRecord:
        """Update academic record with latest calculations"""
        gpa, total_grades = GradeCalculationService.calculate_gpa(db, student_id)
        passed, failed = GradeCalculationService.calculate_course_performance(db, student_id)
        academic_status = GradeCalculationService.determine_academic_status(gpa, passed, failed)

        record = crud.update_academic_record(
            db=db,
            student_id=student_id,
            gpa=gpa,
            total_credits=total_grades,
            passed_courses=passed,
            failed_courses=failed,
            academic_status=academic_status,
        )

        return record

    @staticmethod
    def generate_transcript(db: Session, student_id: int) -> Dict:
        """Generate academic transcript for a student"""
        grades = crud.get_student_grades(db, student_id)
        gpa, _ = GradeCalculationService.calculate_gpa(db, student_id)
        record = crud.get_academic_record(db, student_id)

        if not record:
            record = GradeCalculationService.update_academic_record(db, student_id)

        # Format grades for transcript
        transcript_entries = []
        for grade in sorted(grades, key=lambda g: g.date, reverse=True):
            transcript_entries.append({
                "course_id": grade.course_id,
                "grade_value": grade.grade_value,
                "grade_type": grade.grade_type,
                "date": grade.date.isoformat(),
            })

        return {
            "student_id": student_id,
            "gpa": gpa,
            "total_credits": record.total_credits,
            "academic_status": record.academic_status,
            "passed_courses": record.passed_courses,
            "failed_courses": record.failed_courses,
            "grades": transcript_entries,
            "generated_at": datetime.utcnow().isoformat(),
        }

    @staticmethod
    def get_grade_distribution_by_type(db: Session, student_id: int) -> Dict[str, List[float]]:
        """Get grade distribution grouped by grade type"""
        grades = crud.get_student_grades(db, student_id)

        distribution = {}
        for grade in grades:
            if grade.grade_type not in distribution:
                distribution[grade.grade_type] = []
            distribution[grade.grade_type].append(grade.grade_value)

        return distribution

    @staticmethod
    def get_course_statistics(db: Session, course_id: int) -> Dict:
        """Get statistics for a course"""
        grades = crud.get_course_grades(db, course_id)

        if not grades:
            return {
                "course_id": course_id,
                "total_students": 0,
                "average_grade": 0,
                "highest_grade": 0,
                "lowest_grade": 0,
                "passed_count": 0,
                "failed_count": 0,
            }

        grade_values = [g.grade_value for g in grades]
        passed = sum(1 for g in grades if g.grade_value >= 10)
        failed = len(grades) - passed

        return {
            "course_id": course_id,
            "total_students": len(set(g.student_id for g in grades)),
            "average_grade": round(sum(grade_values) / len(grade_values), 2),
            "highest_grade": max(grade_values),
            "lowest_grade": min(grade_values),
            "passed_count": passed,
            "failed_count": failed,
        }

    @staticmethod
    def get_class_statistics(db: Session, course_id: int) -> Dict:
        """Get detailed class statistics"""
        grades = crud.get_course_grades(db, course_id)

        if not grades:
            return {
                "course_id": course_id,
                "statistics": {},
            }

        # Group by grade type
        type_stats = {}
        for grade in grades:
            if grade.grade_type not in type_stats:
                type_stats[grade.grade_type] = []
            type_stats[grade.grade_type].append(grade.grade_value)

        statistics = {}
        for grade_type, values in type_stats.items():
            statistics[grade_type] = {
                "count": len(values),
                "average": round(sum(values) / len(values), 2),
                "highest": max(values),
                "lowest": min(values),
            }

        return {
            "course_id": course_id,
            "statistics": statistics,
        }
