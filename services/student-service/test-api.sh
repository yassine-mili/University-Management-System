#!/bin/bash

# Student Service API Test Suite
# Demonstrates all CRUD operations and features

echo "🧪 Student Service API Test Suite"
echo "=================================="
echo ""

BASE_URL="http://localhost:3000/api/v1/students"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Helper function
test_endpoint() {
  local name=$1
  local method=$2
  local endpoint=$3
  local data=$4
  local expected_status=$5
  
  echo -e "${BLUE}Test: ${name}${NC}"
  
  if [ -z "$data" ]; then
    response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint")
  else
    response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
      -H "Content-Type: application/json" \
      -d "$data")
  fi
  
  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | head -n-1)
  
  if [ "$http_code" = "$expected_status" ]; then
    echo -e "${GREEN}✓ PASSED${NC} (Status: $http_code)"
    ((TESTS_PASSED++))
  else
    echo -e "${RED}✗ FAILED${NC} (Expected: $expected_status, Got: $http_code)"
    ((TESTS_FAILED++))
  fi
  
  echo "$body" | jq '.' 2>/dev/null || echo "$body"
  echo ""
}

# 1. CREATE Student 1
test_endpoint \
  "CREATE Student 1" \
  "POST" \
  "" \
  '{
    "numero_etudiant": "STU2001",
    "email": "marie.dupont@university.edu",
    "nom": "Dupont",
    "prenom": "Marie",
    "niveau": "L1",
    "filiere": "Informatique"
  }' \
  "201"

# 2. CREATE Student 2
test_endpoint \
  "CREATE Student 2" \
  "POST" \
  "" \
  '{
    "numero_etudiant": "STU2002",
    "email": "jean.bernard@university.edu",
    "nom": "Bernard",
    "prenom": "Jean",
    "niveau": "L2",
    "filiere": "Mathématiques"
  }' \
  "201"

# 3. CREATE Student 3
test_endpoint \
  "CREATE Student 3" \
  "POST" \
  "" \
  '{
    "numero_etudiant": "STU2003",
    "email": "sophie.martin@university.edu",
    "nom": "Martin",
    "prenom": "Sophie",
    "niveau": "M1",
    "filiere": "Informatique"
  }' \
  "201"

# 4. READ All Students
test_endpoint \
  "READ All Students (Pagination)" \
  "GET" \
  "?page=1&limit=10" \
  "" \
  "200"

# 5. READ All with Level Filter
test_endpoint \
  "READ Students filtered by Level (L1)" \
  "GET" \
  "?niveau=L1" \
  "" \
  "200"

# 6. READ Single Student by ID
test_endpoint \
  "READ Single Student by ID (1)" \
  "GET" \
  "/1" \
  "" \
  "200"

# 7. READ Single Student by numero_etudiant
test_endpoint \
  "READ Single Student by numero_etudiant (STU2002)" \
  "GET" \
  "/STU2002" \
  "" \
  "200"

# 8. READ Single Student by email
test_endpoint \
  "READ Single Student by email" \
  "GET" \
  "/sophie.martin@university.edu" \
  "" \
  "200"

# 9. UPDATE Student
test_endpoint \
  "UPDATE Student (Change level and filiere)" \
  "PUT" \
  "/1" \
  '{
    "niveau": "L2",
    "filiere": "Génie Informatique"
  }' \
  "200"

# 10. UPDATE - Deactivate Student
test_endpoint \
  "UPDATE Student (Deactivate)" \
  "PUT" \
  "/2" \
  '{"est_actif": false}' \
  "200"

# 11. Test Validation Error
test_endpoint \
  "CREATE with Invalid Email (Validation Error)" \
  "POST" \
  "" \
  '{
    "numero_etudiant": "STU2004",
    "email": "invalid-email",
    "nom": "Test",
    "prenom": "User",
    "niveau": "L1"
  }' \
  "400"

# 12. Test Missing Required Fields
test_endpoint \
  "CREATE with Missing Fields (Validation Error)" \
  "POST" \
  "" \
  '{"nom": "Test"}' \
  "400"

# 13. Test Duplicate Email
test_endpoint \
  "CREATE with Duplicate Email (Conflict)" \
  "POST" \
  "" \
  '{
    "numero_etudiant": "STU2005",
    "email": "marie.dupont@university.edu",
    "nom": "Autre",
    "prenom": "Personne",
    "niveau": "L1"
  }' \
  "409"

# 14. Test Not Found
test_endpoint \
  "READ Non-existent Student (Not Found)" \
  "GET" \
  "/99999" \
  "" \
  "404"

# 15. DELETE Student
test_endpoint \
  "DELETE Student" \
  "DELETE" \
  "/3" \
  "" \
  "200"

# 16. Verify Deletion
test_endpoint \
  "READ Deleted Student (Verification)" \
  "GET" \
  "/3" \
  "" \
  "404"

# 17. READ All Students (Final Count)
test_endpoint \
  "READ All Students (Final)" \
  "GET" \
  "?limit=10" \
  "" \
  "200"

# Summary
echo "=================================="
echo "Test Summary"
echo "=================================="
echo -e "${GREEN}Passed: $TESTS_PASSED${NC}"
echo -e "${RED}Failed: $TESTS_FAILED${NC}"
TOTAL=$((TESTS_PASSED + TESTS_FAILED))
echo "Total: $TOTAL"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
  echo -e "${GREEN}✓ All tests passed!${NC}"
  exit 0
else
  echo -e "${RED}✗ Some tests failed${NC}"
  exit 1
fi
