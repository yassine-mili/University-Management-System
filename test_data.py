#!/usr/bin/env python3
"""
Test script to view Grades and Billing database data
Run: python test_data.py
"""

import psycopg2
from datetime import datetime

def print_section(title):
    """Print a formatted section header"""
    print("\n" + "=" * 80)
    print(f"  {title}")
    print("=" * 80)

def view_grades_data():
    """View data from Grades database"""
    try:
        conn = psycopg2.connect(
            host="localhost",
            port=5432,
            database="grades_db",
            user="user",
            password="password"
        )
        cursor = conn.cursor()
        
        print_section("GRADES DATABASE")
        
        # Check if table exists
        cursor.execute("""
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_name = 'grades'
            );
        """)
        
        if not cursor.fetchone()[0]:
            print("\n⚠️  Grades table not found. Service may not have started yet.")
            print("   Start the Grades Service first:")
            print("   cd services/grades-service")
            print("   python -m uvicorn app.main:app --port 8084")
            cursor.close()
            conn.close()
            return
        
        # View grades
        cursor.execute("SELECT id, student_id, course_id, grade, weight, exam_date FROM grades ORDER BY id")
        grades = cursor.fetchall()
        
        if grades:
            print("\n📊 GRADES TABLE:")
            print(f"{'ID':<5} {'Student ID':<12} {'Course':<12} {'Grade':<8} {'Weight':<8} {'Exam Date':<12}")
            print("-" * 80)
            for row in grades:
                print(f"{row[0]:<5} {row[1]:<12} {row[2]:<12} {row[3]:<8.2f} {row[4]:<8.2f} {str(row[5]):<12}")
            print(f"\nTotal grades: {len(grades)}")
        else:
            print("\n📋 Grades table is empty. Add some grades to test!")
            print("\nExample curl command:")
            print("""curl -X POST http://localhost:8084/grades/ \\
  -H "Content-Type: application/json" \\
  -d '{
    "student_id": "STU001",
    "course_id": "MATH101",
    "grade": 15.5,
    "weight": 1.0,
    "exam_date": "2024-12-09"
  }'""")
        
        # View statistics by student
        cursor.execute("""
            SELECT 
                student_id,
                COUNT(*) as total_grades,
                ROUND(AVG(grade)::numeric, 2) as avg_grade,
                MAX(grade) as highest,
                MIN(grade) as lowest
            FROM grades
            GROUP BY student_id
            ORDER BY student_id
        """)
        stats = cursor.fetchall()
        
        if stats:
            print("\n📈 GRADE STATISTICS BY STUDENT:")
            print(f"{'Student ID':<12} {'Total':<8} {'Average':<10} {'Highest':<10} {'Lowest':<10}")
            print("-" * 80)
            for row in stats:
                print(f"{row[0]:<12} {row[1]:<8} {row[2]:<10} {row[3]:<10.2f} {row[4]:<10.2f}")
        
        cursor.close()
        conn.close()
        
    except psycopg2.OperationalError as e:
        print(f"\n❌ Cannot connect to Grades database: {e}")
        print("\nMake sure:")
        print("1. PostgreSQL is running: Get-Service postgresql-x64-14")
        print("2. Database exists: python setup_databases.py")
        print("3. Credentials are correct (user/password)")

def view_billing_data():
    """View data from Billing database"""
    try:
        conn = psycopg2.connect(
            host="localhost",
            port=5432,
            database="billing_db",
            user="user",
            password="password"
        )
        cursor = conn.cursor()
        
        print_section("BILLING DATABASE")
        
        # Check if tables exist
        cursor.execute("""
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_name = 'students'
            );
        """)
        
        if not cursor.fetchone()[0]:
            print("\n⚠️  Students table not found. Service may not have started yet.")
            print("   Start the Billing Service first:")
            print("   cd services/billing-service")
            print("   dotnet run")
            cursor.close()
            conn.close()
            return
        
        # View students
        cursor.execute("SELECT id, university_id, first_name, last_name FROM students ORDER BY id")
        students = cursor.fetchall()
        
        if students:
            print("\n👥 STUDENTS TABLE:")
            print(f"{'ID':<5} {'University ID':<15} {'First Name':<15} {'Last Name':<15}")
            print("-" * 80)
            for row in students:
                print(f"{row[0]:<5} {row[1]:<15} {row[2]:<15} {row[3]:<15}")
            print(f"\nTotal students: {len(students)}")
        else:
            print("\n📋 Students table is empty.")
        
        # View invoices
        cursor.execute("""
            SELECT id, invoice_number, amount, currency, student_id, created_at 
            FROM invoices 
            ORDER BY id
        """)
        invoices = cursor.fetchall()
        
        if invoices:
            print("\n💰 INVOICES TABLE:")
            print(f"{'ID':<5} {'Invoice #':<15} {'Amount':<12} {'Currency':<10} {'Student ID':<12} {'Created':<20}")
            print("-" * 80)
            for row in invoices:
                created = str(row[5])[:19] if row[5] else "N/A"
                print(f"{row[0]:<5} {row[1]:<15} {row[2]:<12.2f} {row[3]:<10} {row[4]:<12} {created:<20}")
            print(f"\nTotal invoices: {len(invoices)}")
            
            # Calculate total
            cursor.execute("SELECT SUM(amount) FROM invoices")
            total = cursor.fetchone()[0]
            if total:
                print(f"Total amount: {total:.2f} TND")
        else:
            print("\n📋 Invoices table is empty. Add some invoices to test!")
            print("\nExample curl command:")
            print("""curl -X POST http://localhost:5000/api/invoices \\
  -H "Content-Type: application/json" \\
  -d '{
    "invoiceNumber": "INV-001",
    "amount": 500.00,
    "currency": "TND",
    "universityId": "UNI001",
    "firstName": "John",
    "lastName": "Doe"
  }'""")
        
        cursor.close()
        conn.close()
        
    except psycopg2.OperationalError as e:
        print(f"\n❌ Cannot connect to Billing database: {e}")
        print("\nMake sure:")
        print("1. PostgreSQL is running: Get-Service postgresql-x64-14")
        print("2. Database exists: python setup_databases.py")
        print("3. Credentials are correct (user/password)")

def main():
    """Main function"""
    print("\n" + "🗄️  DATABASE VIEWER - University Management System".center(80))
    
    # View Grades
    view_grades_data()
    
    # View Billing
    view_billing_data()
    
    # Summary
    print_section("NEXT STEPS")
    print("""
1. ✅ Start Grades Service:
   cd services/grades-service
   python -m uvicorn app.main:app --port 8084

2. ✅ Start Billing Service:
   cd services/billing-service
   dotnet run

3. ✅ Add test data using curl or Postman

4. ✅ Run this script again to see the data:
   python test_data.py

5. ✅ View in pgAdmin (GUI):
   http://localhost:5050

6. ✅ View in Swagger UI:
   http://localhost:8084/docs
    """)
    
    print("=" * 80 + "\n")

if __name__ == "__main__":
    main()
