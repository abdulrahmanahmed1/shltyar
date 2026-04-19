# Sales Account Creation Endpoint ✅

## 🎉 **SALES REGISTRATION ENDPOINT IMPLEMENTED**

A new endpoint has been created to allow sales representatives to register their accounts.

---

## 📋 **Endpoint Details**

### **POST /api/sales/register**

**Description:** Register a new sales account

**Access:** Public (no authentication required)

**Request Body:**
```json
{
  "email": "sales@example.com",
  "password": "password123",
  "fullName": "Sales Representative",
  "phone": "+1234567890",
  "address": "123 Main St, City, Country",
  "birthDate": "1990-01-01"
}
```

**Required Fields:**
- ✅ `email` - Valid email address
- ✅ `password` - Minimum 6 characters
- ✅ `fullName` - Full name of the sales representative
- ✅ `phone` - Contact phone number

**Optional Fields:**
- `address` - Physical address
- `birthDate` - Date of birth (format: YYYY-MM-DD)

**Response (Success - 200 OK):**
```json
{
  "id": 4,
  "email": "sales@example.com",
  "fullName": "Sales Representative",
  "phone": "+1234567890",
  "role": "SALES",
  "status": "ACTIVE",
  "createdAt": "2026-04-19T13:21:38.149",
  "message": "Sales account created successfully. You can now login with your credentials."
}
```

**Response (Error - 400 Bad Request):**
```json
{
  "status": 400,
  "message": "User already exists with email: sales@example.com",
  "timestamp": "2026-04-19T13:21:38.149"
}
```

---

## 🔐 **Authentication Flow**

### **1. Register Sales Account**
```bash
POST http://localhost:8081/api/sales/register
Content-Type: application/json

{
  "email": "sales@example.com",
  "password": "password123",
  "fullName": "Sales Representative",
  "phone": "+1234567890"
}
```

### **2. Login with Sales Account**
```bash
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "email": "sales@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "refreshToken": "eyJhbGciOiJIUzM4NCJ9...",
  "email": "sales@example.com",
  "fullName": "Sales Representative",
  "role": "SALES",
  "expiresIn": 86400000
}
```

---

## 🛡️ **Security Configuration**

### **Public Endpoints:**
- ✅ `POST /api/sales/register` - Sales account registration (no auth required)
- ✅ `POST /api/auth/login` - Login for all user types
- ✅ `POST /api/driver/application` - Driver application submission

### **Protected Endpoints:**
- ✅ `/api/admin/**` - Admin only
- ✅ `/api/driver/**` - Driver only (except application submission)
- ✅ `/api/sales/**` - Sales only (future endpoints)

---

## 💻 **Implementation Details**

### **Created Files:**

1. **SalesRegistrationRequest.java** - Request DTO
   - Validation for email, password, fullName, phone
   - Optional fields for address and birthDate

2. **SalesRegistrationResponse.java** - Response DTO
   - Returns user details and success message
   - Includes role and status information

3. **SalesService.java** - Business logic
   - Checks for existing users
   - Creates new user with SALES role
   - Encrypts password with BCrypt
   - Sets user status to ACTIVE

4. **SalesController.java** - REST endpoint
   - Handles POST /api/sales/register
   - Validates request data
   - Returns appropriate responses

### **Security Updates:**

**SecurityConfig.java** - Added public access:
```java
.requestMatchers("/api/sales/register").permitAll()
```

---

## 🧪 **Testing**

### **Test 1: Register New Sales Account**
```bash
curl -X POST http://localhost:8081/api/sales/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "sales@example.com",
    "password": "password123",
    "fullName": "Sales Representative",
    "phone": "+1234567890"
  }'
```

**Expected Result:** ✅ Account created successfully

### **Test 2: Login with Sales Account**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "sales@example.com",
    "password": "password123"
  }'
```

**Expected Result:** ✅ JWT token returned with role: SALES

### **Test 3: Duplicate Registration**
```bash
curl -X POST http://localhost:8081/api/sales/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "sales@example.com",
    "password": "password123",
    "fullName": "Another Sales Rep",
    "phone": "+9876543210"
  }'
```

**Expected Result:** ❌ Error: "User already exists with email: sales@example.com"

---

## 📊 **User Roles in System**

| Role | Registration Method | Access Level |
|------|-------------------|--------------|
| **DRIVER** | Driver Application Form | Driver endpoints |
| **SALES** | Sales Registration Endpoint | Sales endpoints |
| **RESTAURANT** | Manual/Admin creation | Restaurant endpoints |
| **ADMIN** | Manual/Database | All endpoints |

---

## 🎯 **Use Cases**

### **Sales Representative Onboarding:**
1. Sales rep receives invitation to join platform
2. They use the registration endpoint to create account
3. Account is created with SALES role
4. They can immediately login and access sales features
5. They can manage restaurant accounts and earn commissions

### **Integration with Frontend:**
- Create a sales registration form similar to driver application
- Form submits to `/api/sales/register`
- On success, redirect to login page
- Sales rep logs in and accesses sales dashboard

---

## ✅ **Verification**

**Test Results:**
- ✅ Sales account created: `sales@example.com`
- ✅ Role assigned: `SALES`
- ✅ Status: `ACTIVE`
- ✅ Login successful with JWT token
- ✅ Token contains correct role information

---

## 📱 **API Documentation**

The sales registration endpoint is now available in Swagger UI:

**Access Swagger:** `http://localhost:8081/swagger-ui.html`

**Endpoint Location:** Sales → POST /api/sales/register

You can test the endpoint directly from Swagger UI with the "Try it out" feature.

---

## 🎉 **Summary**

The sales account creation endpoint is now **fully implemented and tested**. Sales representatives can:

1. ✅ Register their accounts via API
2. ✅ Login with their credentials
3. ✅ Receive JWT tokens with SALES role
4. ✅ Access sales-specific features (when implemented)

**Endpoint:** `POST /api/sales/register`
**Status:** ✅ Working and ready for use!