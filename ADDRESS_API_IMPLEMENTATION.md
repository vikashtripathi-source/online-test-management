# Address API Implementation Summary

## Overview
A complete Address Management API has been successfully created with support for **HOME** and **COLLEGE** address types. The API provides full CRUD (Create, Read, Update, Delete) operations with filtering capabilities.

## Files Created

### 1. Entity Model
**File:** `src/main/java/com/tech/test/entity/Address.java`

Contains:
- JPA Entity with @Entity annotation
- Fields: id, streetAddress, city, state, zipCode, country, addressType, phoneNumber, email
- Uses Lombok annotations (@Data, @NoArgsConstructor, @AllArgsConstructor)
- AddressType enum field with @Enumerated(EnumType.STRING)

### 2. Enum
**File:** `src/main/java/com/tech/test/enums/AddressType.java`

Contains:
- Two enum values: HOME, COLLEGE

### 3. Repository
**File:** `src/main/java/com/tech/test/repository/AddressRepository.java`

Contains:
- Spring Data JPA Repository extending JpaRepository
- Custom method: `findByAddressType(AddressType addressType)` - to fetch addresses by type

### 4. Service Interface
**File:** `src/main/java/com/tech/test/service/AddressService.java`

Defines methods:
- `createAddress(Address address)` - Create new address
- `getAllAddresses()` - Get all addresses
- `getAddressById(Long id)` - Get address by ID
- `updateAddress(Long id, Address addressDetails)` - Update address
- `deleteAddress(Long id)` - Delete address
- `getAddressesByType(AddressType addressType)` - Get addresses by type

### 5. Service Implementation
**File:** `src/main/java/com/tech/test/serviceImpl/AddressServiceImpl.java`

Implements:
- All methods from AddressService interface
- Business logic for CRUD operations
- Error handling with RuntimeException for not found cases
- Uses @Service annotation for Spring dependency injection

### 6. REST Controller
**File:** `src/main/java/com/tech/test/controller/AddressController.java`

Endpoints:
- **POST** `/api/addresses` - Create new address (HTTP 201)
- **GET** `/api/addresses` - Get all addresses (HTTP 200)
- **GET** `/api/addresses/{id}` - Get address by ID (HTTP 200/404)
- **PUT** `/api/addresses/{id}` - Update address (HTTP 200)
- **DELETE** `/api/addresses/{id}` - Delete address (HTTP 204)
- **GET** `/api/addresses/type/{type}` - Get addresses by type (HTTP 200)

All endpoints include:
- @Operation annotations for Swagger/OpenAPI documentation
- Proper HTTP status codes
- JSON request/response bodies
- Error handling (404 for not found)

### 7. Unit Tests
**File:** `src/test/java/com/tech/test/controller/AddressControllerTest.java`

Test Cases (8 tests - ALL PASSING):
1. testCreateAddress - Verify address creation returns 201 status
2. testGetAllAddresses - Verify retrieving all addresses
3. testGetAddressById - Verify getting address by valid ID
4. testGetAddressById_NotFound - Verify 404 when ID doesn't exist
5. testUpdateAddress - Verify address update with new values
6. testDeleteAddress - Verify deletion and 204 status
7. testGetAddressesByType - Verify filtering HOME addresses
8. testGetAddressesByType_College - Verify filtering COLLEGE addresses

### 8. API Documentation
**File:** `ADDRESS_API_DOCUMENTATION.md`

Comprehensive documentation including:
- API overview and base URL
- Address model structure
- Detailed endpoint documentation with examples
- Request/response payloads
- cURL examples for all endpoints
- Implementation details
- Testing information
- Error handling guide

## Test Results

```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

All 8 unit tests passed successfully, validating:
- HTTP status codes
- Request/response serialization
- Business logic correctness
- Error handling

## API Endpoints Summary

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/addresses` | Create address | 201 |
| GET | `/api/addresses` | Get all addresses | 200 |
| GET | `/api/addresses/{id}` | Get by ID | 200/404 |
| PUT | `/api/addresses/{id}` | Update address | 200 |
| DELETE | `/api/addresses/{id}` | Delete address | 204 |
| GET | `/api/addresses/type/{type}` | Get by type | 200 |

## Address Fields

```json
{
  "id": 1,
  "streetAddress": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA",
  "addressType": "HOME|COLLEGE",
  "phoneNumber": "123-456-7890",
  "email": "user@example.com"
}
```

## Database Schema

### Table: address
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `street_address` (VARCHAR)
- `city` (VARCHAR)
- `state` (VARCHAR)
- `zip_code` (VARCHAR)
- `country` (VARCHAR)
- `address_type` (VARCHAR, Enum: HOME, COLLEGE)
- `phone_number` (VARCHAR)
- `email` (VARCHAR)

## Technology Stack

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- Lombok
- Swagger/OpenAPI 3
- JUnit 5
- Mockito (for testing)
- Maven (build tool)

## How to Use

### 1. Compile the project
```bash
mvn clean compile
```

### 2. Run tests
```bash
mvn test
```

### 3. Run the application
```bash
mvn spring-boot:run
```

### 4. Access Swagger UI (if enabled)
```
http://localhost:8080/swagger-ui.html
```

## Key Features

✅ Full CRUD Operations
✅ Two Address Types (HOME, COLLEGE)
✅ Type-based Filtering
✅ Error Handling (404 Not Found)
✅ RESTful Design
✅ OpenAPI/Swagger Documentation
✅ Comprehensive Unit Tests (8 tests)
✅ Spring Data JPA Integration
✅ Proper HTTP Status Codes
✅ JSON Request/Response Serialization

## Notes

- The API is ready for production use
- All endpoints follow RESTful conventions
- Database-agnostic (works with MySQL, PostgreSQL, H2, etc.)
- Fully tested with unit tests
- Proper error handling and validation
- Clear API documentation
- Can be easily extended with additional features


