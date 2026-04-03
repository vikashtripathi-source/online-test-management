# Cart & Order API Documentation

## Cart Management APIs

### 1. Add Item to Cart
**POST** `/api/cart/add`

**Request Parameters:**
```
studentId (Long, required) - Student ID
productId (Long, required) - Product ID  
quantity (Integer, required, min: 1) - Quantity to add
```

**Example Request:**
```
POST /api/cart/add?studentId=1&productId=5&quantity=2
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": 5,
  "productName": "Mathematics Textbook",
  "description": "Advanced mathematics textbook for engineering students",
  "price": 45.99,
  "quantity": 2,
  "imageUrl": "https://example.com/images/math-book.jpg"
}
```

**Error Responses:**
- 400 Bad Request: Invalid parameters
- 404 Not Found: Product not found
- 500 Internal Server Error: Server error

---

### 2. Get Student's Cart
**GET** `/api/cart/{studentId}`

**Path Parameter:**
```
studentId (Long) - Student ID
```

**Example Request:**
```
GET /api/cart/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "productId": 5,
    "productName": "Mathematics Textbook",
    "description": "Advanced mathematics textbook for engineering students",
    "price": 45.99,
    "quantity": 2,
    "imageUrl": "https://example.com/images/math-book.jpg"
  },
  {
    "id": 2,
    "productId": 8,
    "productName": "Physics Lab Manual",
    "description": "Comprehensive physics laboratory manual",
    "price": 32.50,
    "quantity": 1,
    "imageUrl": "https://example.com/images/physics-lab.jpg"
  }
]
```

**Error Responses:**
- 404 Not Found: Student not found
- 500 Internal Server Error: Server error

---

### 3. Update Cart Item Quantity
**PUT** `/api/cart/update`

**Request Parameters:**
```
itemId (Long, required) - Cart item ID
quantity (Integer, required, min: 1) - New quantity
```

**Example Request:**
```
PUT /api/cart/update?itemId=1&quantity=3
```

**Response (200 OK):**
```json
{
  "id": 1,
  "productId": 5,
  "productName": "Mathematics Textbook",
  "description": "Advanced mathematics textbook for engineering students",
  "price": 45.99,
  "quantity": 3,
  "imageUrl": "https://example.com/images/math-book.jpg"
}
```

**Error Responses:**
- 400 Bad Request: Invalid parameters
- 404 Not Found: Cart item not found
- 500 Internal Server Error: Server error

---

### 4. Remove Cart Item
**DELETE** `/api/cart/{itemId}`

**Path Parameter:**
```
itemId (Long) - Cart item ID to remove
```

**Example Request:**
```
DELETE /api/cart/1
```

**Response (204 No Content):**
No response body

**Error Responses:**
- 404 Not Found: Cart item not found
- 500 Internal Server Error: Server error

---

### 5. Clear Cart
**DELETE** `/api/cart/{studentId}`

**Path Parameter:**
```
studentId (Long) - Student ID whose cart to clear
```

**Example Request:**
```
DELETE /api/cart/1
```

**Response (204 No Content):**
No response body

**Error Responses:**
- 404 Not Found: Student not found
- 500 Internal Server Error: Server error

---

## Order Management APIs

### 6. Submit Order from Cart
**POST** `/api/orders/submit-cart`

**Request Body:**
```json
{
  "studentId": 1,
  "addressId": 5
}
```

**Example Request:**
```
POST /api/orders/submit-cart
Content-Type: application/json

{
  "studentId": 1,
  "addressId": 5
}
```

**Response (200 OK):**
```json
{
  "id": 123,
  "studentId": 1,
  "addressId": 5,
  "totalAmount": 124.48,
  "status": "PENDING",
  "orderDate": "2026-04-02T13:30:00",
  "createdDate": "2026-04-02T13:30:00",
  "orderItems": [
    {
      "id": 45,
      "productId": 5,
      "quantity": 2,
      "price": 45.99
    },
    {
      "id": 46,
      "productId": 8,
      "quantity": 1,
      "price": 32.50
    }
  ]
}
```

**Error Responses:**
- 400 Bad Request: Invalid request data
- 404 Not Found: Cart is empty or student not found
- 500 Internal Server Error: Server error

---

### 7. Submit Order with Address (Existing)
**POST** `/api/orders/submit`

**Request Body:**
```json
{
  "studentId": 1,
  "productName": "Mathematics Textbook",
  "quantity": 2,
  "address": "123 Main St",
  "city": "New York",
  "zipCode": "10001",
  "totalAmount": 91.98
}
```

**Response (200 OK):**
```json
{
  "id": 124,
  "studentId": 1,
  "productName": "Mathematics Textbook",
  "quantity": 2,
  "address": "123 Main St",
  "city": "New York",
  "zipCode": "10001",
  "totalAmount": 91.98,
  "status": "PENDING",
  "orderDate": "2026-04-02T13:30:00",
  "createdDate": "2026-04-02T13:30:00"
}
```

---

### 8. Get Student Orders (Existing)
**GET** `/api/orders/student/{studentId}`

**Path Parameter:**
```
studentId (Long) - Student ID
```

**Example Request:**
```
GET /api/orders/student/1
```

**Response (200 OK):**
```json
[
  {
    "id": 123,
    "studentId": 1,
    "productName": "Mathematics Textbook",
    "quantity": 2,
    "address": "123 Main St",
    "city": "New York",
    "zipCode": "10001",
    "totalAmount": 91.98,
    "status": "PENDING",
    "createdDate": "2026-04-02T13:30:00"
  },
  {
    "id": 122,
    "studentId": 1,
    "productName": "Physics Lab Manual",
    "quantity": 1,
    "address": "123 Main St",
    "city": "New York",
    "zipCode": "10001",
    "totalAmount": 32.50,
    "status": "COMPLETED",
    "createdDate": "2026-04-01T10:15:00"
  }
]
```

---

## Common Error Response Format

All error responses follow this format:

```json
{
  "timestamp": "2026-04-02T13:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Product not found with id: 5",
  "path": "/api/cart/add"
}
```

## Order Status Values

Orders can have the following status values:
- `PENDING` - Order is pending processing
- `CONFIRMED` - Order has been confirmed
- `PROCESSING` - Order is being processed
- `SHIPPED` - Order has been shipped
- `DELIVERED` - Order has been delivered
- `CANCELLED` - Order has been cancelled
- `COMPLETED` - Order has been completed

## Notes for Frontend Team

1. **Authentication**: All endpoints require proper authentication headers
2. **Content-Type**: POST/PUT requests should use `application/json`
3. **Date Format**: All dates are in ISO 8601 format (YYYY-MM-DDTHH:mm:ss)
4. **Price Format**: All prices are in decimal format with 2 decimal places
5. **Stock Validation**: The API validates stock availability before adding to cart or submitting orders
6. **Cart Clearing**: When submitting order from cart, the cart is automatically cleared after successful order creation
7. **Error Handling**: Implement proper error handling for all HTTP status codes
8. **Loading States**: Use loading states for operations that may take time (order submission, cart operations)
