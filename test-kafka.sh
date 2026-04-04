#!/bin/bash

# Kafka Test Script - This will help verify all Kafka topics are working
# Run this script after starting your application

echo "=== Kafka Test Script ==="
echo "Make sure your application is running on port 8089"
echo "Make sure Kafka is running and UI is accessible"
echo ""

BASE_URL="http://localhost:8089/api"

echo "=== Quick Kafka Test (Using Test Controller) ==="
echo "Testing all topics at once..."
curl -X POST "${BASE_URL}/test-kafka/test-all-topics" | jq .

echo ""
echo "=== Individual Topic Tests ==="
echo "Testing submit-test-topic..."
curl -X POST "${BASE_URL}/test-kafka/test/submit-test" | jq .

echo ""
echo "Testing student-record-updated-topic..."
curl -X POST "${BASE_URL}/test-kafka/test/student-record-updated" | jq .

echo ""
echo "Testing student-record-deleted-topic..."
curl -X POST "${BASE_URL}/test-kafka/test/student-record-deleted" | jq .

echo ""
echo "Testing question-added-topic..."
curl -X POST "${BASE_URL}/test-kafka/test/question-added" | jq .

echo ""
echo "Testing question-deleted-topic..."
curl -X POST "${BASE_URL}/test-kafka/test/question-deleted" | jq .

echo ""
echo "=== Manual API Tests ==="

echo "1. Testing submit-test-topic (Test Submission)..."
curl -X POST "${BASE_URL}/exams/submit-async" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "answers": [
      {
        "questionId": 1,
        "selectedAnswer": "A"
      }
    ]
  }' | jq .

echo ""
echo "2. Testing student-record-updated-topic (Update Student Record)..."
# First create a record, then update it
curl -X POST "${BASE_URL}/exams/student-test-records" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "studentName": "Test Student",
    "testName": "Test Exam",
    "score": 85,
    "totalQuestions": 100,
    "correctAnswers": 85,
    "branch": "CSE",
    "testDate": "2024-01-01T10:00:00",
    "rollNumber": "TEST001",
    "marks": 85
  }' | jq .

echo ""
echo "Now updating the record to trigger student-record-updated-topic..."
curl -X PUT "${BASE_URL}/exams/student-test-records/1" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "studentName": "Test Student Updated",
    "testName": "Test Exam Updated",
    "score": 90,
    "totalQuestions": 100,
    "correctAnswers": 90,
    "branch": "CSE",
    "testDate": "2024-01-01T10:00:00",
    "rollNumber": "TEST001",
    "marks": 90
  }' | jq .

echo ""
echo "3. Testing student-record-deleted-topic (Delete Student Record)..."
curl -X DELETE "${BASE_URL}/exams/student-test-records/1" | jq .

echo ""
echo "4. Testing question-added-topic (Add Question)..."
curl -X POST "${BASE_URL}/exams/questions" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is Kafka?",
    "optionA": "A messaging system",
    "optionB": "A database",
    "optionC": "A web server",
    "optionD": "A file system",
    "correctAnswer": "A",
    "branch": "CSE",
    "difficulty": "Medium"
  }' | jq .

echo ""
echo "5. Testing question-deleted-topic (Delete Question)..."
curl -X DELETE "${BASE_URL}/exams/questions/1" | jq .

echo ""
echo "=== Test Complete ==="
echo "Check your Kafka UI for messages in these topics:"
echo "- submit-test-topic (should have 1 message)"
echo "- student-record-updated-topic (should have 1 message)"
echo "- student-record-deleted-topic (should have 1 message)"
echo "- question-added-topic (should have 1 message)"
echo "- question-deleted-topic (should have 1 message)"
