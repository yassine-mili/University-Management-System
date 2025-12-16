#!/bin/bash

# Grades Service API Test Script
# This script tests all endpoints of the Grades Service

BASE_URL="http://localhost:8084"
API_URL="$BASE_URL/api/v1/grades"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Function to print test headers
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_code=$4
    local description=$5

    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    echo -e "${YELLOW}Test $TOTAL_TESTS: $description${NC}"
    echo "Request: $method $endpoint"

    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$API_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$API_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data")
    fi

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)

    echo "Response Code: $http_code"
    echo "Response Body: $body"

    if [ "$http_code" = "$expected_code" ]; then
        echo -e "${GREEN}✓ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗ FAILED (Expected: $expected_code)${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
}

# Function to extract ID from response
extract_id() {
    local response=$1
    echo "$response" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*'
}

# ==================== START TESTS ====================

echo -e "${BLUE}"
echo "╔════════════════════════════════════════╗"
echo "║   Grades Service API Test Suite        ║"
echo "║   Base URL: $BASE_URL"
echo "╚════════════════════════════════════════╝"
echo -e "${NC}"

# Test 1: Health Check
print_header "1. Health Check Endpoints"
test_endpoint "GET" "/health" "" "200" "Service health check"
test_endpoint "GET" "" "" "200" "Root endpoint"

# Test 2: Create Grades
print_header "2. Grade Creation Tests"

GRADE_1=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{
        "student_id": 1,
        "course_id": 101,
        "grade_value": 18.5,
        "grade_type": "EXAM",
        "date": "2024-12-07"
    }')

GRADE_1_ID=$(echo "$GRADE_1" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ ! -z "$GRADE_1_ID" ]; then
    echo -e "${GREEN}✓ Grade 1 created with ID: $GRADE_1_ID${NC}\n"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ Failed to create Grade 1${NC}\n"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

# Create second grade
GRADE_2=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{
        "student_id": 1,
        "course_id": 102,
        "grade_value": 17.0,
        "grade_type": "MIDTERM",
        "date": "2024-12-05"
    }')

GRADE_2_ID=$(echo "$GRADE_2" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ ! -z "$GRADE_2_ID" ]; then
    echo -e "${GREEN}✓ Grade 2 created with ID: $GRADE_2_ID${NC}\n"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ Failed to create Grade 2${NC}\n"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

# Create third grade for different student
GRADE_3=$(curl -s -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d '{
        "student_id": 2,
        "course_id": 101,
        "grade_value": 15.5,
        "grade_type": "EXAM",
        "date": "2024-12-07"
    }')

GRADE_3_ID=$(echo "$GRADE_3" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ ! -z "$GRADE_3_ID" ]; then
    echo -e "${GREEN}✓ Grade 3 created with ID: $GRADE_3_ID${NC}\n"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}✗ Failed to create Grade 3${NC}\n"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi
TOTAL_TESTS=$((TOTAL_TESTS + 1))

# Test 3: Retrieve Grades
print_header "3. Grade Retrieval Tests"

if [ ! -z "$GRADE_1_ID" ]; then
    test_endpoint "GET" "/$GRADE_1_ID" "" "200" "Get single grade by ID"
fi

test_endpoint "GET" "" "" "200" "List all grades"
test_endpoint "GET" "?student_id=1" "" "200" "List grades for student 1"
test_endpoint "GET" "?course_id=101" "" "200" "List grades for course 101"
test_endpoint "GET" "?grade_type=EXAM" "" "200" "List EXAM grades"
test_endpoint "GET" "?skip=0&limit=10" "" "200" "List with pagination"

# Test 4: Update Grade
print_header "4. Grade Update Tests"

if [ ! -z "$GRADE_1_ID" ]; then
    test_endpoint "PUT" "/$GRADE_1_ID" '{
        "grade_value": 19.0
    }' "200" "Update grade value"

    test_endpoint "PUT" "/$GRADE_1_ID" '{
        "grade_value": 18.5,
        "date": "2024-12-06"
    }' "200" "Update multiple fields"
fi

# Test 5: Student-Specific Endpoints
print_header "5. Student-Specific Endpoints"

test_endpoint "GET" "/student/1" "" "200" "Get all grades for student 1"
test_endpoint "GET" "/student/1/gpa" "" "200" "Calculate GPA for student 1"
test_endpoint "GET" "/student/1/transcript" "" "200" "Generate transcript for student 1"
test_endpoint "GET" "/student/2/gpa" "" "200" "Calculate GPA for student 2"

# Test 6: Course-Specific Endpoints
print_header "6. Course-Specific Endpoints"

test_endpoint "GET" "/course/101" "" "200" "Get all grades for course 101"
test_endpoint "GET" "/course/101/statistics" "" "200" "Get statistics for course 101"
test_endpoint "GET" "/course/102/statistics" "" "200" "Get statistics for course 102"

# Test 7: Statistics Endpoints
print_header "7. Statistics Endpoints"

test_endpoint "GET" "/statistics/student/1" "" "200" "Get student 1 statistics"
test_endpoint "GET" "/statistics/distribution/1" "" "200" "Get grade distribution for student 1"
test_endpoint "GET" "/statistics/student/2" "" "200" "Get student 2 statistics"

# Test 8: Error Cases
print_header "8. Error Handling Tests"

test_endpoint "GET" "/999" "" "404" "Get non-existent grade"
test_endpoint "POST" "" '{
    "student_id": -1,
    "course_id": 101,
    "grade_value": 18.5,
    "grade_type": "EXAM",
    "date": "2024-12-07"
}' "422" "Create grade with invalid student ID"

test_endpoint "POST" "" '{
    "student_id": 1,
    "course_id": 101,
    "grade_value": 25,
    "grade_type": "EXAM",
    "date": "2024-12-07"
}' "422" "Create grade with invalid value (> 20)"

test_endpoint "POST" "" '{
    "student_id": 1,
    "course_id": 101,
    "grade_value": 18.5,
    "grade_type": "EXAM",
    "date": "2025-12-07"
}' "422" "Create grade with future date"

# Test 9: Delete Grade
print_header "9. Grade Deletion Tests"

if [ ! -z "$GRADE_1_ID" ]; then
    test_endpoint "DELETE" "/$GRADE_1_ID" "" "200" "Delete grade"
    test_endpoint "GET" "/$GRADE_1_ID" "" "404" "Verify deleted grade"
fi

# ==================== TEST SUMMARY ====================

print_header "Test Summary"

echo -e "Total Tests:  ${BLUE}$TOTAL_TESTS${NC}"
echo -e "Passed:       ${GREEN}$PASSED_TESTS${NC}"
echo -e "Failed:       ${RED}$FAILED_TESTS${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "\n${GREEN}✓ All tests passed!${NC}\n"
    exit 0
else
    echo -e "\n${RED}✗ Some tests failed!${NC}\n"
    exit 1
fi
