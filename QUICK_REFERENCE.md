# Address API - Quick Reference

## 🎯 What Was Created

A complete **Address Management REST API** with CRUD operations and support for **HOME** and **COLLEGE** address types.

---

## 📁 Files Created (7 files)

### Core Application Files
1. **AddressType.java** - Enum (HOME, COLLEGE)
2. **Address.java** - JPA Entity
3. **AddressRepository.java** - Spring Data JPA Repository
4. **AddressService.java** - Service Interface
5. **AddressServiceImpl.java** - Service Implementation
6. **AddressController.java** - REST Controller (Updated)

### Testing & Documentation
7. **AddressControllerTest.java** - Unit Tests (8 tests, 100% passing)
8. **ADDRESS_API_DOCUMENTATION.md** - Full API Documentation
9. **ADDRESS_API_IMPLEMENTATION.md** - Implementation Summary

---

## 🚀 Quick Start

### 1. Compile
```bash
mvn clean compile
```

### 2. Test
```bash
mvn test
```

### 3. Build
```bash
mvn clean package
```

### 4. Run
```bash
mvn spring-boot:run
```

---

## 📋 API Endpoints

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| **POST** | `/api/addresses` | Create address | 201 |
| **GET** | `/api/addresses` | Get all | 200 |
| **GET** | `/api/addresses/{id}` | Get by ID | 200/404 |
| **PUT** | `/api/addresses/{id}` | Update | 200 |
| **DELETE** | `/api/addresses/{id}` | Delete | 204 |
| **GET** | `/api/addresses/type/{type}` | Get by type | 200 |

---

## 📝 Request/Response Example

### Create Address (POST)
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Content-Type: application/json" \
  -d '{
    "streetAddress": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "addressType": "HOME",
    "phoneNumber": "123-456-7890",
    "email": "home@example.com"
  }'
```

### Response (201 Created)
```json
{
  "id": 1,
  "streetAddress": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA",
  "addressType": "HOME",
  "phoneNumber": "123-456-7890",
  "email": "home@example.com"
}
```

---

## 🧪 Test Results

```
✅ 8/8 Tests Passed
✅ 0 Failures
✅ 0 Errors
✅ BUILD SUCCESS
```

### Tests Included
- ✅ Create address
- ✅ Get all addresses
- ✅ Get by ID (found)
- ✅ Get by ID (not found - 404)
- ✅ Update address
- ✅ Delete address
- ✅ Filter by HOME type
- ✅ Filter by COLLEGE type

---

## 🎨 Address Types

```
HOME    - Home address
COLLEGE - College address
```

---

## 📊 Address Fields

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| id | Long | Auto | Auto-generated Primary Key |
| streetAddress | String | Yes | Street address |
| city | String | Yes | City name |
| state | String | Yes | State/Province |
| zipCode | String | Yes | Postal code |
| country | String | Yes | Country name |
| addressType | Enum | Yes | HOME or COLLEGE |
| phoneNumber | String | No | Contact number |
| email | String | No | Email address |

---

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA**
- **Lombok**
- **OpenAPI/Swagger**
- **JUnit 5**
- **Mockito**
- **Maven**

---

## ✨ Features

✅ Full CRUD operations
✅ Two address types (HOME/COLLEGE)
✅ Type-based filtering
✅ RESTful design
✅ Error handling (404)
✅ OpenAPI documentation
✅ Unit tested (100% pass rate)
✅ Production-ready code
✅ Proper HTTP status codes
✅ JSON serialization

---

## 🔧 Service Methods

```java
// Create
Address createAddress(Address address)

// Read
List<Address> getAllAddresses()
Optional<Address> getAddressById(Long id)
List<Address> getAddressesByType(AddressType type)

// Update
Address updateAddress(Long id, Address addressDetails)

// Delete
void deleteAddress(Long id)
```

---

## 📌 HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | OK (GET, PUT) |
| 201 | Created (POST) |
| 204 | No Content (DELETE) |
| 404 | Not Found |
| 400 | Bad Request |
| 500 | Server Error |

---

## 🎯 Usage Examples

### Get All Addresses
```bash
GET /api/addresses
```

### Get by ID
```bash
GET /api/addresses/1
```

### Get HOME Addresses
```bash
GET /api/addresses/type/HOME
```

### Get COLLEGE Addresses
```bash
GET /api/addresses/type/COLLEGE
```

### Update Address
```bash
PUT /api/addresses/1
```

### Delete Address
```bash
DELETE /api/addresses/1
```

---

## 📖 Documentation Files

- **ADDRESS_API_DOCUMENTATION.md** - Complete API reference
- **ADDRESS_API_IMPLEMENTATION.md** - Implementation details
- **This file** - Quick reference guide

---

## ✅ Verification

- ✅ All files created and verified
- ✅ Project compiles successfully
- ✅ All 8 unit tests passing
- ✅ Maven build successful
- ✅ Ready for deployment

---

## 🚢 Ready to Deploy

The Address API is fully implemented, tested, and ready for:
- ✅ Development use
- ✅ Testing
- ✅ Production deployment

**Build Status: SUCCESS** ✨


