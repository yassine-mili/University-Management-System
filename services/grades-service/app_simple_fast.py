#!/usr/bin/env python3
"""
Simple Grades Service - In-memory version (no database)
Run: python app_simple_fast.py
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from datetime import datetime
from typing import List, Optional

app = FastAPI(title="Grades Service", version="1.0.0")

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# In-memory storage
grades_db = []
next_grade_id = 1

# Models
class Grade(BaseModel):
    id: Optional[int] = None
    student_id: str
    course_id: str
    grade: float
    weight: float = 1.0
    exam_date: str

class GradeResponse(BaseModel):
    id: int
    student_id: str
    course_id: str
    grade: float
    weight: float
    exam_date: str

class Transcript(BaseModel):
    student_id: str
    gpa: float
    total_credits: float
    academic_status: str
    grades: List[GradeResponse]

class Statistics(BaseModel):
    student_id: str
    total_grades: int
    passed_grades: int
    failed_grades: int
    average_grade: float
    highest_grade: float
    lowest_grade: float

# Endpoints
@app.get("/")
async def root():
    return {"message": "Grades Service - REST API", "version": "1.0.0"}

@app.get("/health")
async def health():
    return {"status": "healthy", "service": "grades-service"}

@app.post("/grades/")
async def create_grade(grade: Grade):
    global next_grade_id
    grade_dict = grade.dict()
    grade_dict["id"] = next_grade_id
    grades_db.append(grade_dict)
    next_grade_id += 1
    return grade_dict

@app.get("/grades/student/{student_id}")
async def get_student_grades(student_id: str):
    student_grades = [g for g in grades_db if g["student_id"] == student_id]
    return student_grades

@app.get("/grades/student/{student_id}/transcript")
async def get_transcript(student_id: str):
    student_grades = [g for g in grades_db if g["student_id"] == student_id]
    
    if not student_grades:
        return {
            "student_id": student_id,
            "gpa": 0.0,
            "total_credits": 0.0,
            "academic_status": "ACTIVE",
            "grades": []
        }
    
    # Calculate GPA (simple average)
    total_grade = sum(g["grade"] for g in student_grades)
    avg_grade = total_grade / len(student_grades)
    
    # Convert to 4.0 scale (assuming 20 is max)
    gpa = (avg_grade / 20) * 4.0
    
    # Determine academic status
    if gpa >= 3.0:
        status = "ACTIVE"
    elif gpa >= 2.0:
        status = "PROBATION"
    else:
        status = "SUSPENDED"
    
    return {
        "student_id": student_id,
        "gpa": round(gpa, 2),
        "total_credits": float(len(student_grades)),
        "academic_status": status,
        "grades": student_grades
    }

@app.get("/grades/student/{student_id}/statistics")
async def get_statistics(student_id: str):
    student_grades = [g for g in grades_db if g["student_id"] == student_id]
    
    if not student_grades:
        return {
            "student_id": student_id,
            "total_grades": 0,
            "passed_grades": 0,
            "failed_grades": 0,
            "average_grade": 0.0,
            "highest_grade": 0.0,
            "lowest_grade": 0.0
        }
    
    grades_list = [g["grade"] for g in student_grades]
    passed = sum(1 for g in grades_list if g >= 10)
    failed = sum(1 for g in grades_list if g < 10)
    
    return {
        "student_id": student_id,
        "total_grades": len(student_grades),
        "passed_grades": passed,
        "failed_grades": failed,
        "average_grade": round(sum(grades_list) / len(grades_list), 2),
        "highest_grade": max(grades_list),
        "lowest_grade": min(grades_list)
    }

@app.delete("/grades/{grade_id}")
async def delete_grade(grade_id: int):
    global grades_db
    grades_db = [g for g in grades_db if g["id"] != grade_id]
    return {"message": "Grade deleted successfully"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8084)
