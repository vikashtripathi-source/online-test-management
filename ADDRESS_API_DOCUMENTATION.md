# Address API Documentation

## Overview
The Address API provides REST endpoints to manage addresses with two types: **HOME** and **COLLEGE**. It supports full CRUD operations (Create, Read, Update, Delete) and filtering by address type.

## Base URL
```
/api/addresses
```

## Address Model

### Address Entity
```json
{
  "id": 1,
  "streetAddress": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA",
  "addressType": "HOME",
  "phoneNumber": "123-456-7890",
  "email": "home@example.com"
}
```

### Address Type Enum
- `HOME` - Home address
- `COLLEGE` - College address

## API Endpoints

### 1. Create Address
**Endpoint:** `POST /api/addresses`

**Description:** Create a new address (Home or College)

**Request Body:**
```json
{
  "streetAddress": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA",
  "addressType": "HOME",
  "phoneNumber": "123-456-7890",
  "email": "home@example.com"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "streetAddress": "123 Main Street",
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

### 2. Get All Addresses
**Endpoint:** `GET /api/addresses`

**Description:** Retrieve all addresses from the database

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "streetAddress": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "addressType": "HOME",
    "phoneNumber": "123-456-7890",
    "email": "home@example.com"
  },
  {
    "id": 2,
    "streetAddress": "456 College Avenue",
    "city": "Boston",
    "state": "MA",
    "zipCode": "02108",
    "country": "USA",
    "addressType": "COLLEGE",
    "phoneNumber": "987-654-3210",
    "email": "college@example.com"
  }
]
```

---

### 3. Get Address by ID
**Endpoint:** `GET /api/addresses/{id}`

**Description:** Get a specific address by its ID

**Path Parameters:**
- `id` (Long) - The address ID

**Response:** `200 OK` or `404 Not Found`
```json
{
  "id": 1,
  "streetAddress": "123 Main Street",
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

### 4. Update Address
**Endpoint:** `PUT /api/addresses/{id}`

**Description:** Update an existing address

**Path Parameters:**
- `id` (Long) - The address ID to update

**Request Body:**
```json
{
  "streetAddress": "456 Updated Street",
  "city": "Los Angeles",
  "state": "CA",
  "zipCode": "90001",
  "country": "USA",
  "addressType": "HOME",
  "phoneNumber": "111-222-3333",
  "email": "updated@example.com"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "streetAddress": "456 Updated Street",
  "city": "Los Angeles",
  "state": "CA",
  "zipCode": "90001",
  "country": "USA",
  "addressType": "HOME",
  "phoneNumber": "111-222-3333",
  "email": "updated@example.com"
}
```

---

### 5. Delete Address
**Endpoint:** `DELETE /api/addresses/{id}`

**Description:** Delete an address

**Path Parameters:**
- `id` (Long) - The address ID to delete

**Response:** `204 No Content`

---

### 6. Get Addresses by Type
**Endpoint:** `GET /api/addresses/type/{type}`

**Description:** Get all addresses filtered by type (HOME or COLLEGE)

**Path Parameters:**
- `type` (AddressType) - The address type (HOME or COLLEGE)

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "streetAddress": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "addressType": "HOME",
    "phoneNumber": "123-456-7890",
    "email": "home@example.com"
  }
]
```

---

## Example Usage

### cURL Examples

**Create a Home Address:**
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Content-Type: application/json" \
  -d '{
    "streetAddress": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "addressType": "HOME",
    "phoneNumber": "123-456-7890",
    "email": "home@example.com"
  }'
```

**Get All Addresses:**
```bash
curl -X GET http://localhost:8080/api/addresses
```

**Get Address by ID:**
```bash
curl -X GET http://localhost:8080/api/addresses/1
```

**Update Address:**
```bash
curl -X PUT http://localhost:8080/api/addresses/1 \
  -H "Content-Type: application/json" \
  -d '{
    "streetAddress": "456 Updated Street",
    "city": "Los Angeles",
    "state": "CA",
    "zipCode": "90001",
    "country": "USA",
    "addressType": "HOME",
    "phoneNumber": "111-222-3333",
    "email": "updated@example.com"
  }'
```

**Delete Address:**
```bash
curl -X DELETE http://localhost:8080/api/addresses/1
```

**Get All HOME Addresses:**
```bash
curl -X GET http://localhost:8080/api/addresses/type/HOME
```

**Get All COLLEGE Addresses:**
```bash
curl -X GET http://localhost:8080/api/addresses/type/COLLEGE
```

---

## Implementation Details

### Project Structure
```
src/main/java/com/tech/test/
├── entity/
│   └── Address.java (JPA Entity with AddressType enum)
├── repository/
│   └── AddressRepository.java (Spring Data JPA Repository)
├── service/
│   └── AddressService.java (Service Interface)
├── serviceImpl/
│   └── AddressServiceImpl.java (Service Implementation)
├── controller/
│   └── AddressController.java (REST Controller)
└── enums/
    └── AddressType.java (HOME, COLLEGE)
```

### Technologies Used
- **Spring Boot 3.2.5** - Framework
- **Spring Data JPA** - Database persistence
- **Lombok** - Boilerplate code reduction
- **Swagger/OpenAPI** - API documentation
- **MySQL/H2** - Database (configured in application.properties)

### Testing
- Unit tests provided in `AddressControllerTest.java`
- Test coverage includes:
  - Create address
  - Get all addresses
  - Get address by ID
  - Get address by ID (not found case)
  - Update address
  - Delete address
  - Get addresses by type (HOME)
  - Get addresses by type (COLLEGE)

All tests passed successfully (8/8 tests passed).

---

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful GET or PUT request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request
- `404 Not Found` - Address ID not found
- `400 Bad Request` - Invalid request data
- `500 Internal Server Error` - Server error

### Example Error Response
```json
{
  "error": "Address not found with id 999"
}
```

---

## Notes
- All addresses are identified by a unique `id` (Long, auto-generated)
- `addressType` is mandatory and must be either "HOME" or "COLLEGE"
- Phone number and email are optional fields
- All string fields are stored as VARCHAR in the database
- Addresses are persisted in the database using Spring Data JPA


