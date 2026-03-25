# 🎉 **CUSTOM EXCEPTION FRAMEWORK - COMPLETE**

## ✅ **Overview**

A comprehensive custom exception framework has been implemented for the Online Test Management System with:
- 9 custom exception classes
- Centralized exception handling via GlobalExceptionHandler
- Descriptive error messages at every layer
- Proper HTTP status code mapping
- Clean exception propagation

---

## 📦 **Custom Exception Classes Created**

### **1. ResourceNotFoundException**
- **HTTP Status**: 404 Not Found
- **Usage**: When requested resource doesn't exist
- **Constructor**: `ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue)`
- **Example**: `new ResourceNotFoundException("Address", "id", 1)`

```java
try {
    Address address = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
} catch (ResourceNotFoundException e) {
    // Response: 404 Not Found
    // Message: "Address not found with id : '1'"
}
```

### **2. InvalidDataException**
- **HTTP Status**: 400 Bad Request
- **Usage**: When invalid/null data is provided
- **Example**: `new InvalidDataException("Product name cannot be null or empty")`

```java
if (orderDTO.getProductName() == null || orderDTO.getProductName().isEmpty()) {
    throw new InvalidDataException("Product name cannot be null or empty");
}
```

### **3. AddressException**
- **HTTP Status**: 404 Not Found
- **Usage**: Address-specific operations failures
- **Thrown by**: AddressService, AddressServiceImpl
- **Example**: `new AddressException("Address not found with ID: " + id)`

### **4. QuestionException**
- **HTTP Status**: 404 Not Found
- **Usage**: Question-specific operations failures
- **Thrown by**: ExamService, ExamServiceImpl
- **Example**: `new QuestionException("Question not found with ID: " + id)`

### **5. StudentRecordException**
- **HTTP Status**: 404 Not Found
- **Usage**: Student test record operation failures
- **Thrown by**: ExamService, ExamServiceImpl
- **Example**: `new StudentRecordException("Student test record not found with ID: " + id)`

### **6. OrderException**
- **HTTP Status**: 404 Not Found
- **Usage**: Order-specific operation failures
- **Thrown by**: OrderService, OrderServiceImpl
- **Example**: `new OrderException("Order not found with ID: " + id)`

### **7. InventoryException**
- **HTTP Status**: 404 Not Found
- **Usage**: Inventory-specific operation failures
- **Thrown by**: InventoryService, InventoryServiceImpl
- **Example**: `new InventoryException("Product not found in inventory: " + productName)`

### **8. BusinessLogicException**
- **HTTP Status**: 409 Conflict
- **Usage**: Business logic violations (insufficient stock, invalid state)
- **Thrown by**: InventoryService, EmailService
- **Example**: `new BusinessLogicException("Insufficient stock for product")`

### **9. KafkaException**
- **HTTP Status**: 500 Internal Server Error
- **Usage**: Kafka messaging failures
- **Thrown by**: KafkaProducerService, KafkaConsumerService
- **Example**: `new KafkaException("Failed to send order to Kafka topic")`

---

## 🌐 **GlobalExceptionHandler - Central Exception Management**

The `GlobalExceptionHandler` class handles all exceptions with:
- Type-specific handlers for each custom exception
- Validation exception handling
- Generic exception fallback
- Consistent error response format

### **Exception Mapping**

| Exception | HTTP Status | Message Pattern |
|-----------|-------------|-----------------|
| ResourceNotFoundException | 404 | Resource not found with specified criteria |
| InvalidDataException | 400 | Invalid/null data provided |
| AddressException | 404 | Address-specific error message |
| QuestionException | 404 | Question-specific error message |
| StudentRecordException | 404 | Student record-specific error message |
| OrderException | 404 | Order-specific error message |
| InventoryException | 404 | Inventory-specific error message |
| BusinessLogicException | 409 | Business rule violation message |
| KafkaException | 500 | Kafka processing error |
| TestSubmissionException | 400 | Test submission error |
| MethodArgumentNotValidException | 400 | Validation errors with field details |
| Generic Exception | 500 | Unexpected error occurred |

### **ErrorResponse Format**

```java
{
    "message": "Descriptive error message",
    "statusCode": 404
}
```

---

## 🛠️ **Exception Handling Implementation**

### **Service Layer Pattern**

Each service follows this pattern:

```java
public OrderDTO createOrder(OrderDTO orderDTO) {
    try {
        // Input validation
        if (orderDTO == null) {
            throw new InvalidDataException("Order DTO cannot be null");
        }
        if (orderDTO.getProductName() == null || orderDTO.getProductName().isEmpty()) {
            throw new InvalidDataException("Product name cannot be null or empty");
        }
        if (orderDTO.getQuantity() <= 0) {
            throw new InvalidDataException("Quantity must be a positive number");
        }
        
        // Business logic
        Order order = orderMapper.toEntity(orderDTO);
        Order savedOrder = repository.save(order);
        
        // Integration logic with exception wrapping
        try {
            producer.sendOrder(savedOrder);
        } catch (Exception e) {
            throw new KafkaException("Failed to send order event to Kafka: " + e.getMessage(), e);
        }
        
        return orderMapper.toDTO(savedOrder);
    } catch (InvalidDataException | KafkaException e) {
        throw e;  // Re-throw custom exceptions
    } catch (Exception e) {
        throw new OrderException("Failed to create order: " + e.getMessage(), e);
    }
}
```

### **Controller Layer Pattern**

Controllers are now clean and focused on routing:

```java
@PostMapping
@Operation(summary = "Create Order")
public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO orderDTO) {
    OrderDTO saved = orderService.createOrder(orderDTO);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}
```

**Note**: No try-catch blocks needed - GlobalExceptionHandler manages all exceptions!

---

## 📋 **Services Updated with Exception Handling**

### **1. AddressServiceImpl**
- ✅ Throws `AddressException` for address-specific errors
- ✅ Throws `InvalidDataException` for invalid inputs
- ✅ Wraps repository exceptions

### **2. ExamServiceImpl**
- ✅ Throws `QuestionException` for question operations
- ✅ Throws `StudentRecordException` for student record operations
- ✅ Throws `TestSubmissionException` for test submission errors
- ✅ Validates all input data

### **3. OrderServiceImpl**
- ✅ Throws `OrderException` for order operations
- ✅ Throws `InvalidDataException` for invalid inputs
- ✅ Throws `KafkaException` for Kafka failures
- ✅ Validates quantity > 0

### **4. InventoryServiceImpl**
- ✅ Throws `InventoryException` for inventory lookups
- ✅ Throws `BusinessLogicException` for insufficient stock
- ✅ Comprehensive inventory validation

### **5. KafkaConsumerServiceImpl**
- ✅ Throws `KafkaException` for processing failures
- ✅ Throws `TestSubmissionException` for invalid test data
- ✅ Validates all message content
- ✅ Wraps dependency exceptions

### **6. KafkaProducerServiceImpl**
- ✅ Throws `KafkaException` for all Kafka operations
- ✅ Validates all objects before sending
- ✅ Descriptive error messages per topic

### **7. EmailServiceImpl**
- ✅ Throws `BusinessLogicException` for email failures
- ✅ Throws `InvalidDataException` for invalid input
- ✅ Validates mail sender configuration

---

## 🚀 **Exception Handling Features**

### **Input Validation**
```java
// Null checks
if (orderDTO == null) throw new InvalidDataException("DTO cannot be null");

// String validation
if (productName == null || productName.trim().isEmpty()) 
    throw new InvalidDataException("Product name cannot be null or empty");

// Numeric validation
if (quantity <= 0) 
    throw new InvalidDataException("Quantity must be positive");

// Resource existence
if (!repository.existsById(id)) 
    throw new OrderException("Order not found with ID: " + id);
```

### **Hierarchical Exception Handling**
```java
try {
    // Business logic
} catch (InvalidDataException | OrderException e) {
    throw e;  // Rethrow custom exceptions immediately
} catch (Exception e) {
    throw new OrderException("Failed to create order: " + e.getMessage(), e);
}
```

### **Root Cause Preservation**
```java
new OrderException("Failed to create order: " + e.getMessage(), e)
//                                           ↑ custom message + original message
//                                                              ↑ original exception
```

---

## ✨ **Benefits of Custom Exception Framework**

1. **Clear Error Messages**: Every exception has a descriptive message
2. **Proper HTTP Status Codes**: Correct response status for each error type
3. **Centralized Handling**: All exceptions handled in one place
4. **Clean Controllers**: No try-catch blocks in controllers
5. **Maintainability**: Easy to add new exception types
6. **Debugging**: Root causes preserved in exception chains
7. **API Consistency**: All error responses follow same format
8. **Type Safety**: Specific exceptions for specific scenarios

---

## 📊 **Exception Types by Layer**

### **Controller Layer**
- Only routing - no exception handling
- Input validation via `@Valid` annotation

### **Service Layer**
- Input validation (InvalidDataException)
- Business logic exceptions (BusinessLogicException, OrderException, etc.)
- Integration exceptions (KafkaException)
- Resource not found exceptions (AddressException, QuestionException, etc.)

### **Repository Layer**
- Standard JPA exceptions automatically converted to custom exceptions

### **Integration Layer (Kafka)**
- KafkaException for all Kafka operations

---

## 🔄 **Flow Diagram**

```
User Request
    ↓
Controller (@Valid)
    ↓
Service Layer (throws custom exceptions)
    ↓
Repository/External Services
    ↓
GlobalExceptionHandler
    ↓
ErrorResponse (JSON)
    ↓
HTTP Status Code
    ↓
User Response
```

---

## ✅ **Testing the Exception Framework**

### **Quick Test Examples**

#### **Test 1: Invalid Data**
```bash
POST /orders
{
    "productName": null,
    "quantity": 5
}
```
**Response**: 
```json
{
    "message": "Product name cannot be null or empty",
    "statusCode": 400
}
```

#### **Test 2: Resource Not Found**
```bash
GET /orders/999
```
**Response**:
```json
{
    "message": "Order not found with ID: 999",
    "statusCode": 404
}
```

#### **Test 3: Business Logic Violation**
```bash
POST /inventory/update
{
    "productName": "Product A",
    "quantity": 1000  // More than available stock
}
```
**Response**:
```json
{
    "message": "Insufficient stock for product 'Product A'. Available: 50, Requested: 1000",
    "statusCode": 409
}
```

---

## 📝 **Summary**

✅ **9 Custom Exception Classes** - Specific to different domains  
✅ **Global Exception Handler** - Centralized exception management  
✅ **Descriptive Error Messages** - Clear, actionable error information  
✅ **Proper HTTP Status Codes** - RESTful compliance  
✅ **Clean Code** - No exception handling clutter in controllers  
✅ **Type Safety** - Specific exceptions for specific scenarios  
✅ **Maintainability** - Easy to extend and modify  
✅ **Root Cause Preservation** - Original exceptions chained  

The exception framework is **production-ready** and provides a solid foundation for robust error handling! 🎯

