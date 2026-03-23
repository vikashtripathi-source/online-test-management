# Online Test Management System

## 🚀 Overview
This is a Spring Boot-based backend application that allows students to take tests and processes submissions asynchronously using Kafka.

---

## 🛠 Tech Stack
- Java 17
- Spring Boot
- Apache Kafka
- MySQL
- Spring Data JPA

---

## 🔥 Features
- Add and fetch questions
- Submit test answers
- Score calculation
- Kafka-based async processing
- Order processing simulation
- Global exception handling

---

## 🔄 System Flow

1. User submits test via API
2. Data is saved in MySQL
3. Event is published to Kafka topic
4. Kafka consumer processes submission asynchronously

---

## 📦 APIs

### Submit Test
POST `/submit`

```json
{
  "studentId": 1,
  "answers": [
    {
      "questionId": 1,
      "selectedAnswer": "A"
    }
  ]
}
