# 🚀 **CUSTOM EXCEPTION FRAMEWORK - QUICK REFERENCE**

## 📂 **Exception Classes Location**
```
src/main/java/com/tech/test/exception/
├── ResourceNotFoundException.java      ← 404 errors
├── InvalidDataException.java           ← 400 validation errors
├── AddressException.java               ← Address operations
├── QuestionException.java              ← Question operations
├── StudentRecordException.java         ← Student records
├── OrderException.java                 ← Order operations
├── InventoryException.java             ← Inventory operations
├── BusinessLogicException.java         ← Business rule violations (409)
├── KafkaException.java                 ← Kafka operations (500)
├── TestSubmissionException.java        ← Test submission (400)
├── KafkaProcessingException.java       ← Legacy (kept for compatibility)
└── GlobalExceptionHandler.java         ← Central handler
```

---

## 🎯 **When to Use Each Exception**

### **InvalidDataException** (400)
```java
// Null checks
if (dto == null) throw new InvalidDataException("DTO cannot be null");

// Empty strings
if (name.isEmpty()) throw new InvalidDataException("Name cannot be empty");

// Invalid values
if (quantity < 0) throw new InvalidDataException("Quantity must be positive");

// Type mismatches
if (!(obj instanceof Address)) throw new InvalidDataException("Invalid type");
```

### **ResourceNotFoundException** (404)
```java
// Not found by ID
Address address = repository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));

// Alternative usage
throw new ResourceNotFoundException("Address not found with ID: " + id);
```

### **Domain-Specific Exceptions** (404)
```java
// AddressException
throw new AddressException("Address not found with ID: " + id);

// QuestionException
throw new QuestionException("Question not found with ID: " + id);

// OrderException
throw new OrderException("Order not found with ID: " + id);

// InventoryException
throw new InventoryException("Product not found in inventory");

// StudentRecordException
throw new StudentRecordException("Student record not found with ID: " + id);
```

### **BusinessLogicException** (409)
```java
// Business rule violations
if (inventory < required) {
    throw new BusinessLogicException(
        "Insufficient stock: Available " + inventory + ", Required: " + required
    );
}

// State violations
if (!isActive) {
    throw new BusinessLogicException("Account is not active");
}
```

### **KafkaException** (500)
```java
// Kafka sending failures
try {
    kafkaTemplate.send("topic", message);
} catch (Exception e) {
    throw new KafkaException("Failed to send message: " + e.getMessage(), e);
}

// Validation failures
if (message == null) {
    throw new KafkaException("Message cannot be null");
}
```

### **TestSubmissionException** (400)
```java
// Test-specific errors
if (answers.isEmpty()) {
    throw new TestSubmissionException("Answers cannot be empty");
}

if (!question.exists()) {
    throw new TestSubmissionException("Question not found with ID: " + qid);
}
```

---

## 📊 **Exception to HTTP Status Mapping**

| Exception | HTTP Status | Use Case |
|-----------|-------------|----------|
| `InvalidDataException` | 400 | Input validation failed |
| `TestSubmissionException` | 400 | Test submission validation failed |
| `ResourceNotFoundException` | 404 | Resource not found |
| `AddressException` | 404 | Address not found |
| `QuestionException` | 404 | Question not found |
| `OrderException` | 404 | Order not found |
| `InventoryException` | 404 | Inventory item not found |
| `StudentRecordException` | 404 | Student record not found |
| `BusinessLogicException` | 409 | Business rule violated |
| `KafkaException` | 500 | Kafka operation failed |
| Generic `Exception` | 500 | Unexpected error |

---

## 💡 **Service Implementation Pattern**

### **Basic Pattern**
```java
@Service
public class OrderServiceImpl {
    
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            // Step 1: Validate input
            if (orderDTO == null) {
                throw new InvalidDataException("Order DTO cannot be null");
            }
            
            // Step 2: Business logic
            Order order = mapper.toEntity(orderDTO);
            Order saved = repository.save(order);
            
            // Step 3: Integration (wrap integration exceptions)
            try {
                producer.sendOrder(saved);
            } catch (Exception e) {
                throw new KafkaException("Failed to send order: " + e.getMessage(), e);
            }
            
            return mapper.toDTO(saved);
            
        } catch (InvalidDataException | KafkaException e) {
            throw e;  // Rethrow custom exceptions
        } catch (Exception e) {
            throw new OrderException("Failed to create order: " + e.getMessage(), e);
        }
    }
}
```

### **With Resource Lookup**
```java
public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
    try {
        // Validate input
        if (id == null || id <= 0) {
            throw new InvalidDataException("Order ID must be positive");
        }
        if (orderDTO == null) {
            throw new InvalidDataException("Order DTO cannot be null");
        }
        
        // Lookup and throw specific exception if not found
        Order existing = repository.findById(id)
            .orElseThrow(() -> new OrderException("Order not found with ID: " + id));
        
        // Update logic
        Order updated = mapper.toEntity(orderDTO);
        updated.setId(id);
        Order saved = repository.save(updated);
        
        return mapper.toDTO(saved);
        
    } catch (InvalidDataException | OrderException e) {
        throw e;
    } catch (Exception e) {
        throw new OrderException("Failed to update order: " + e.getMessage(), e);
    }
}
```

---

## 🔍 **Controller Implementation Pattern**

### **Cleanest Approach (Recommended)**
```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @PostMapping
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO dto) {
        // No try-catch needed! GlobalExceptionHandler handles all exceptions
        OrderDTO result = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

---

## 📝 **Error Response Examples**

### **Example 1: Validation Error**
```http
POST /orders
Content-Type: application/json

{
    "productName": null,
    "quantity": -5
}
```

**Response**: 400 Bad Request
```json
{
    "message": "Product name cannot be null or empty",
    "statusCode": 400
}
```

### **Example 2: Not Found Error**
```http
GET /orders/999
```

**Response**: 404 Not Found
```json
{
    "message": "Order not found with ID: 999",
    "statusCode": 404
}
```

### **Example 3: Business Logic Error**
```http
POST /api/inventory/update
Content-Type: application/json

{
    "productName": "Widget",
    "quantity": 1000
}
```

**Response**: 409 Conflict
```json
{
    "message": "Insufficient stock for product 'Widget'. Available: 100, Requested: 1000",
    "statusCode": 409
}
```

### **Example 4: Kafka Error**
```http
POST /orders
Content-Type: application/json

{
    "productName": "Product A",
    "quantity": 5
}
```

**Response**: 500 Internal Server Error (if Kafka fails)
```json
{
    "message": "Kafka processing error: Failed to send order to Kafka topic 'order-topic': Connection refused",
    "statusCode": 500
}
```

---

## ✅ **Checklist for Adding Custom Exceptions**

- [ ] Create exception class extending `RuntimeException`
- [ ] Add JavaDoc explaining when to use it
- [ ] Add constructor with `String message`
- [ ] Add constructor with `String message, Throwable cause`
- [ ] Add handler method in `GlobalExceptionHandler`
- [ ] Map to appropriate HTTP status code
- [ ] Update this reference document
- [ ] Add unit tests for exception throwing

---

## 🧪 **Testing Custom Exceptions**

### **Unit Test Example**
```java
@Test
void testCreateOrderWithNullDTO() {
    assertThrows(InvalidDataException.class, () -> {
        orderService.createOrder(null);
    });
}

@Test
void testGetOrderNotFound() {
    assertThrows(OrderException.class, () -> {
        orderService.getOrderById(999L);
    });
}
```

### **Integration Test Example**
```java
@Test
void testCreateOrderEndpoint_InvalidData() throws Exception {
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.statusCode").value(400))
        .andExpect(jsonPath("$.message").isNotEmpty());
}
```

---

## 🎓 **Best Practices**

✅ **DO:**
- Throw specific exceptions at the service layer
- Include meaningful, actionable error messages
- Wrap checked exceptions in custom exceptions
- Preserve root cause in exception chains
- Validate input early and throw InvalidDataException
- Keep controllers clean (no try-catch)

❌ **DON'T:**
- Catch and ignore exceptions silently
- Use generic RuntimeException for everything
- Return null instead of throwing exceptions
- Include sensitive information in error messages
- Throw multiple exception types inconsistently
- Add try-catch blocks in controllers

---

## 📚 **Related Documentation**

- `CUSTOM_EXCEPTION_FRAMEWORK.md` - Detailed framework overview
- `GlobalExceptionHandler.java` - Central exception handler
- Service implementation files - Real-world examples

---

## 🎯 **Quick Links**

| Resource | Location |
|----------|----------|
| Exception Classes | `src/main/java/com/tech/test/exception/` |
| Global Handler | `src/main/java/com/tech/test/exception/GlobalExceptionHandler.java` |
| Service Examples | `src/main/java/com/tech/test/serviceImpl/` |
| Controller Examples | `src/main/java/com/tech/test/controller/` |
| Framework Docs | `CUSTOM_EXCEPTION_FRAMEWORK.md` |

---

**Last Updated**: March 25, 2026  
**Status**: Production Ready ✅

