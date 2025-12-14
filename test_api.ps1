# PowerShell script to test Grades and Billing APIs
# Run: .\test_api.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "API Testing Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Colors
$success = "Green"
$error = "Red"
$info = "Yellow"

function Test-Service {
    param(
        [string]$Name,
        [string]$Url
    )
    
    Write-Host "`n--- Testing $Name ---" -ForegroundColor $info
    
    try {
        $response = Invoke-WebRequest -Uri $Url -Method Get -ErrorAction Stop
        Write-Host "✓ $Name is running" -ForegroundColor $success
        return $true
    } catch {
        Write-Host "✗ $Name is not responding" -ForegroundColor $error
        Write-Host "  Make sure it's started on the correct port" -ForegroundColor $error
        return $false
    }
}

function Add-Grade {
    param(
        [string]$StudentId,
        [string]$CourseId,
        [float]$Grade,
        [string]$ExamDate
    )
    
    $body = @{
        student_id = $StudentId
        course_id = $CourseId
        grade = $Grade
        weight = 1.0
        exam_date = $ExamDate
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8084/grades/" `
            -Method Post `
            -ContentType "application/json" `
            -Body $body `
            -ErrorAction Stop
        
        Write-Host "✓ Grade added: $StudentId - $CourseId - $Grade" -ForegroundColor $success
        return $true
    } catch {
        Write-Host "✗ Failed to add grade: $_" -ForegroundColor $error
        return $false
    }
}

function Get-StudentGrades {
    param(
        [string]$StudentId
    )
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8084/grades/student/$StudentId" `
            -Method Get `
            -ErrorAction Stop
        
        $grades = $response.Content | ConvertFrom-Json
        Write-Host "✓ Retrieved grades for $StudentId" -ForegroundColor $success
        Write-Host "  Total grades: $($grades.Count)" -ForegroundColor $info
        return $grades
    } catch {
        Write-Host "✗ Failed to get grades: $_" -ForegroundColor $error
        return $null
    }
}

function Get-Transcript {
    param(
        [string]$StudentId
    )
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8084/grades/student/$StudentId/transcript" `
            -Method Get `
            -ErrorAction Stop
        
        $transcript = $response.Content | ConvertFrom-Json
        Write-Host "✓ Retrieved transcript for $StudentId" -ForegroundColor $success
        Write-Host "  GPA: $($transcript.gpa)" -ForegroundColor $info
        Write-Host "  Status: $($transcript.academic_status)" -ForegroundColor $info
        return $transcript
    } catch {
        Write-Host "✗ Failed to get transcript: $_" -ForegroundColor $error
        return $null
    }
}

function Get-Statistics {
    param(
        [string]$StudentId
    )
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8084/grades/student/$StudentId/statistics" `
            -Method Get `
            -ErrorAction Stop
        
        $stats = $response.Content | ConvertFrom-Json
        Write-Host "✓ Retrieved statistics for $StudentId" -ForegroundColor $success
        Write-Host "  Total grades: $($stats.total_grades)" -ForegroundColor $info
        Write-Host "  Passed: $($stats.passed_grades)" -ForegroundColor $info
        Write-Host "  Failed: $($stats.failed_grades)" -ForegroundColor $info
        Write-Host "  Average: $($stats.average_grade)" -ForegroundColor $info
        return $stats
    } catch {
        Write-Host "✗ Failed to get statistics: $_" -ForegroundColor $error
        return $null
    }
}

function Add-Invoice {
    param(
        [string]$InvoiceNumber,
        [float]$Amount,
        [string]$FirstName,
        [string]$LastName
    )
    
    $body = @{
        invoiceNumber = $InvoiceNumber
        amount = $Amount
        currency = "TND"
        universityId = "UNI001"
        firstName = $FirstName
        lastName = $LastName
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:5000/api/invoices" `
            -Method Post `
            -ContentType "application/json" `
            -Body $body `
            -ErrorAction Stop
        
        Write-Host "✓ Invoice created: $InvoiceNumber - $Amount TND" -ForegroundColor $success
        return $true
    } catch {
        Write-Host "✗ Failed to create invoice: $_" -ForegroundColor $error
        return $false
    }
}

function Get-Invoice {
    param(
        [string]$InvoiceNumber
    )
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:5000/api/invoices/$InvoiceNumber" `
            -Method Get `
            -ErrorAction Stop
        
        $invoice = $response.Content | ConvertFrom-Json
        Write-Host "✓ Retrieved invoice: $InvoiceNumber" -ForegroundColor $success
        Write-Host "  Amount: $($invoice.amount) $($invoice.currency)" -ForegroundColor $info
        return $invoice
    } catch {
        Write-Host "✗ Failed to get invoice: $_" -ForegroundColor $error
        return $null
    }
}

# Main Test Flow
Write-Host "`n" + "="*50 -ForegroundColor Cyan
Write-Host "CHECKING SERVICES" -ForegroundColor Cyan
Write-Host "="*50 -ForegroundColor Cyan

$gradesRunning = Test-Service "Grades Service" "http://localhost:8084/health"
$billingRunning = Test-Service "Billing Service" "http://localhost:5000/"

if (-not $gradesRunning -or -not $billingRunning) {
    Write-Host "`n⚠️  Some services are not running!" -ForegroundColor $error
    Write-Host "Start them first:" -ForegroundColor $info
    Write-Host "  Terminal 1: cd services/grades-service && python -m uvicorn app.main:app --port 8084" -ForegroundColor $info
    Write-Host "  Terminal 2: cd services/billing-service && dotnet run" -ForegroundColor $info
    exit
}

# Test Grades Service
Write-Host "`n" + "="*50 -ForegroundColor Cyan
Write-Host "TESTING GRADES SERVICE" -ForegroundColor Cyan
Write-Host "="*50 -ForegroundColor Cyan

Write-Host "`n1. Adding test grades..." -ForegroundColor $info
Add-Grade "STU001" "MATH101" 18 "2024-12-01"
Add-Grade "STU001" "ENG102" 16 "2024-12-02"
Add-Grade "STU001" "PHY103" 14 "2024-12-03"
Add-Grade "STU002" "MATH101" 12 "2024-12-01"

Write-Host "`n2. Retrieving grades..." -ForegroundColor $info
$grades = Get-StudentGrades "STU001"

Write-Host "`n3. Getting transcript..." -ForegroundColor $info
$transcript = Get-Transcript "STU001"

Write-Host "`n4. Getting statistics..." -ForegroundColor $info
$stats = Get-Statistics "STU001"

# Test Billing Service
Write-Host "`n" + "="*50 -ForegroundColor Cyan
Write-Host "TESTING BILLING SERVICE" -ForegroundColor Cyan
Write-Host "="*50 -ForegroundColor Cyan

Write-Host "`n1. Creating test invoices..." -ForegroundColor $info
Add-Invoice "INV-2024-001" 1500 "John" "Doe"
Add-Invoice "INV-2024-002" 2000 "Jane" "Smith"

Write-Host "`n2. Retrieving invoice..." -ForegroundColor $info
$invoice = Get-Invoice "INV-2024-001"

# Summary
Write-Host "`n" + "="*50 -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "="*50 -ForegroundColor Cyan

Write-Host "`n✓ All tests completed!" -ForegroundColor $success
Write-Host "`nNext steps:" -ForegroundColor $info
Write-Host "1. View data in database: python test_data.py" -ForegroundColor $info
Write-Host "2. View in pgAdmin: http://localhost:5050" -ForegroundColor $info
Write-Host "3. View Grades Swagger: http://localhost:8084/docs" -ForegroundColor $info
Write-Host "4. View database with psql:" -ForegroundColor $info
Write-Host "   psql -U user -h localhost -d grades_db" -ForegroundColor $info
Write-Host "   psql -U user -h localhost -d billing_db" -ForegroundColor $info

Write-Host "`n"
