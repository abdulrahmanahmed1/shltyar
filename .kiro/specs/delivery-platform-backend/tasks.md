# Implementation Plan: Delivery Platform Backend

## Overview

This implementation plan breaks down the delivery platform backend into discrete coding tasks following the 17-week roadmap outlined in the design document. The system is built with Spring Boot, PostgreSQL, Redis, and WebSockets, handling 100,000+ orders per day with real-time tracking, automated dispatch, and comprehensive financial management.

The tasks are organized in logical implementation order, with each task building on previous work. Property-based tests validate the 22 correctness properties defined in the design document, while unit tests cover specific scenarios and edge cases.

## Tasks

### Phase 1: Core Infrastructure Setup (Weeks 1-2)

- [x] 1. Initialize Spring Boot project and configure dependencies
  - Create Maven project with Spring Boot 3.x, Java 17
  - Add dependencies: Spring Web, Spring Security, Spring Data JPA, PostgreSQL, Flyway, Redis, WebSocket, jqwik, SpringDoc OpenAPI
  - Configure application.yml with database, Redis, JWT, and logging settings
  - Set up project package structure (controller, service, repository, domain, dto, config, exception)
  - _Requirements: 1.1, 62.1, 62.2_

- [x] 2. Create database schema with Flyway migrations
  - [x] 2.1 Create migration for user management tables (users, password_reset_tokens)
    - Write V1__create_users_table.sql with all columns, constraints, and indexes
    - Include role enum constraint, status enum constraint, email unique constraint
    - _Requirements: 1.1, 1.2, 1.3, 1.4_
  
  - [x] 2.2 Create migration for driver application tables (driver_applications, driver_documents, driver_availability)
    - Write V2__create_driver_tables.sql with foreign keys to users table
    - Include document type enum, application status enum
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [x] 2.3 Create migration for geographic tables (delivery_areas, delivery_prices)
    - Write V3__create_geographic_tables.sql with area-to-area pricing matrix
    - Include unique constraint on origin-destination pairs
    - _Requirements: 7.1, 7.2, 7.3, 8.1, 8.2_
  
  - [x] 2.4 Create migration for restaurant tables (restaurants, restaurant_branches, customers)
    - Write V4__create_restaurant_tables.sql with multi-branch support
    - Include self-referencing foreign key for brand-branch relationship
    - _Requirements: 9.1, 9.2, 9.3, 10.1, 10.2, 13.1_
  
  - [x] 2.5 Create migration for subscription tables (subscriptions, subscription_history)
    - Write V5__create_subscription_tables.sql with subscription lifecycle tracking
    - Include subscription type enum, status enum, auto-renew flag
    - _Requirements: 14.1, 14.2, 14.3, 16.1, 16.2_
  
  - [x] 2.6 Create migration for order tables (orders, order_status_history, delivery_proof)
    - Write V6__create_order_tables.sql with complete order lifecycle
    - Include order status enum, idempotency key unique constraint
    - Add indexes for restaurant_id, driver_id, status, created_at
    - _Requirements: 3.1, 4.1, 4.2, 5.1, 5.2, 6.1, 28.1_
  
  - [x] 2.7 Create migration for location tracking table (driver_locations)
    - Write V7__create_location_table.sql with GPS coordinate validation
    - Add composite index on (driver_id, created_at DESC) for efficient queries
    - Include latitude/longitude check constraints
    - _Requirements: 3.3, 3.4, 36.1, 36.2_
  
  - [x] 2.8 Create migration for financial tables (financial_transactions, driver_earnings, sales_commissions, company_commissions)
    - Write V8__create_financial_tables.sql with complete transaction tracking
    - Include transaction type enum, status enum, reference number unique constraint
    - _Requirements: 11.1, 11.2, 11.3, 20.1, 20.2, 21.1, 53.1_
  
  - [x] 2.9 Create migration for notification tables (notifications, notification_preferences, system_announcements, announcement_views)
    - Write V9__create_notification_tables.sql
    - Include notification type enum, priority enum
    - _Requirements: 29.1, 29.2, 29.3, 30.1_
  
  - [x] 2.10 Create migration for system tables (disputes, dispute_attachments, system_configurations, configuration_history, analytics_snapshots, audit_logs)
    - Write V10__create_system_tables.sql
    - Include dispute type/status enums, configuration value type enum
    - _Requirements: 31.1, 32.1, 43.1, 44.1, 45.1_


- [ ] 3. Create core domain entities and repositories
  - [x] 3.1 Create User entity with JPA annotations
    - Implement User.java with all fields, BCrypt password hashing
    - Add @Entity, @Table, audit annotations (@CreatedDate, @LastModifiedDate)
    - Create Role enum (ADMIN, SALES, RESTAURANT, DRIVER)
    - Create UserStatus enum (ACTIVE, SUSPENDED, LOCKED)
    - _Requirements: 1.1, 1.2, 1.3, 42.1_
  
  - [x] 3.2 Create UserRepository interface extending JpaRepository
    - Add custom query methods: findByEmail, findByRole, findByStatus
    - _Requirements: 1.1, 1.2_
  
  - [x] 3.3 Create DriverApplication and DriverDocument entities
    - Implement DriverApplication.java with application status enum
    - Implement DriverDocument.java with document type enum
    - Create ApplicationStatus enum (PENDING, APPROVED, REJECTED)
    - Create DocumentType enum (NATIONAL_ID, DRIVER_LICENSE, MOTORCYCLE_LICENSE, MOTORCYCLE_PHOTO)
    - _Requirements: 2.1, 2.2, 2.3_
  
  - [x] 3.4 Create driver repositories (DriverApplicationRepository, DriverDocumentRepository)
    - Add query methods: findByUserId, findByStatus, findByApplicationId
    - _Requirements: 2.1, 2.2_
  
  - [x] 3.5 Create DeliveryArea and DeliveryPrice entities
    - Implement DeliveryArea.java with JSONB boundaries field
    - Implement DeliveryPrice.java with origin/destination area references
    - _Requirements: 7.1, 7.2, 7.3, 8.1_
  
  - [x] 3.6 Create geographic repositories (DeliveryAreaRepository, DeliveryPriceRepository)
    - Add query methods: findByOriginAndDestination, findByOriginArea, findAllActive
    - _Requirements: 7.1, 8.1_
  
  - [x] 3.7 Create Restaurant, RestaurantBranch, and Customer entities
    - Implement Restaurant.java with self-referencing brand relationship
    - Implement RestaurantBranch.java with GPS coordinates
    - Implement Customer.java with order count tracking
    - Create RestaurantType enum (SINGLE_BRANCH, BRAND)
    - Create RestaurantStatus enum (ACTIVE, LOCKED, SUSPENDED)
    - _Requirements: 9.1, 9.2, 9.3, 10.1, 13.1_
  
  - [x] 3.8 Create restaurant repositories (RestaurantRepository, RestaurantBranchRepository, CustomerRepository)
    - Add query methods: findByUserId, findBySalesRepId, findByParentBrandId, findByRestaurantId
    - _Requirements: 9.1, 10.1, 13.1_
  
  - [ ] 3.9 Create Subscription and SubscriptionHistory entities
    - Implement Subscription.java with date range and auto-renew
    - Implement SubscriptionHistory.java for audit trail
    - Create SubscriptionType enum (MONTHLY, YEARLY, CUSTOM)
    - Create SubscriptionStatus enum (ACTIVE, EXPIRED, CANCELLED)
    - _Requirements: 14.1, 14.2, 14.3, 16.1_
  
  - [ ] 3.10 Create subscription repositories (SubscriptionRepository, SubscriptionHistoryRepository)
    - Add query methods: findByRestaurantId, findByEndDateAndStatus, findActiveByRestaurantId
    - _Requirements: 14.1, 16.1_
  
  - [ ] 3.11 Create Order, OrderStatusHistory, and DeliveryProof entities
    - Implement Order.java with @Version for optimistic locking
    - Implement OrderStatusHistory.java for status transition tracking
    - Implement DeliveryProof.java for delivery photo storage
    - Create OrderStatus enum (CREATED, ASSIGNED, PICKED_FROM_RESTAURANT, DELIVERED, CUSTOMER_NOT_AVAILABLE, CANCELLED)
    - _Requirements: 3.1, 4.1, 5.1, 5.2, 6.1, 28.1, 63.6_
  
  - [ ] 3.12 Create order repositories (OrderRepository, OrderStatusHistoryRepository, DeliveryProofRepository)
    - Add query methods: findByStatus, findByDriverId, findByRestaurantId, findByIdempotencyKey
    - Add custom query with JOIN FETCH to avoid N+1 queries
    - _Requirements: 3.1, 4.1, 28.1_
  
  - [ ] 3.13 Create DriverLocation and DriverAvailability entities
    - Implement DriverLocation.java with GPS validation constraints
    - Implement DriverAvailability.java for driver status tracking
    - Create AvailabilityStatus enum (AVAILABLE, UNAVAILABLE)
    - _Requirements: 3.3, 3.4, 36.1, 36.2_
  
  - [ ] 3.14 Create location repositories (DriverLocationRepository, DriverAvailabilityRepository)
    - Add query methods: findLatestByDriverId, findByDriverIdAndDateRange
    - _Requirements: 3.3, 36.1_
  
  - [ ] 3.15 Create financial entities (FinancialTransaction, DriverEarning, SalesCommission, CompanyCommission)
    - Implement all financial entities with immutability constraints
    - Create TransactionType enum, TransactionStatus enum, CommissionType enum
    - _Requirements: 11.1, 11.2, 20.1, 21.1, 53.6_
  
  - [ ] 3.16 Create financial repositories
    - Create FinancialTransactionRepository, DriverEarningRepository, SalesCommissionRepository, CompanyCommissionRepository
    - Add query methods for date range filtering and aggregations
    - _Requirements: 11.1, 20.1, 21.1_
  
  - [ ] 3.17 Create notification entities (Notification, NotificationPreference, SystemAnnouncement, AnnouncementView)
    - Implement all notification entities
    - Create NotificationType enum, Priority enum
    - _Requirements: 29.1, 29.2, 30.1_
  
  - [ ] 3.18 Create notification repositories
    - Create NotificationRepository, NotificationPreferenceRepository, SystemAnnouncementRepository, AnnouncementViewRepository
    - Add query methods: findByUserIdAndIsRead, findByTargetRole
    - _Requirements: 29.1, 30.1_
  
  - [ ] 3.19 Create system entities (Dispute, DisputeAttachment, SystemConfiguration, ConfigurationHistory, AnalyticsSnapshot, AuditLog)
    - Implement all system entities
    - Create DisputeType enum, DisputeStatus enum, ConfigValueType enum
    - _Requirements: 31.1, 32.1, 43.1, 44.1, 45.1_
  
  - [ ] 3.20 Create system repositories
    - Create repositories for all system entities
    - Add query methods for filtering and searching
    - _Requirements: 31.1, 32.1, 43.1_


- [ ] 4. Implement JWT authentication and authorization
  - [ ] 4.1 Create JWT token service
    - Implement JwtTokenService.java with token generation and validation
    - Use HS512 algorithm with configurable secret and expiration
    - Include user ID, email, and role in token payload
    - _Requirements: 1.2, 1.3, 42.2_
  
  - [ ] 4.2 Write property test for JWT token service
    - **Property 1: Authentication Token Generation**
    - **Validates: Requirements 1.2, 1.3**
    - For any valid user credentials, verify JWT contains correct user ID, role, and expiration
    - Test with jqwik generating random user data
  
  - [ ] 4.3 Create JWT authentication filter
    - Implement JwtAuthenticationFilter.java extending OncePerRequestFilter
    - Extract and validate JWT from Authorization header
    - Populate SecurityContext with authenticated user
    - Handle expired and invalid tokens with proper error responses
    - _Requirements: 1.2, 1.3, 42.3_
  
  - [ ] 4.4 Configure Spring Security
    - Implement SecurityConfig.java with JWT filter integration
    - Configure stateless session management
    - Set up role-based endpoint protection
    - Configure BCryptPasswordEncoder with strength 12
    - Disable CSRF for stateless API
    - _Requirements: 1.2, 1.4, 1.5, 42.1_
  
  - [ ] 4.5 Write property test for authorization enforcement
    - **Property 2: Authorization Enforcement**
    - **Validates: Requirements 1.4, 1.5**
    - For any authenticated user and protected endpoint requiring different role, verify 403 response
    - Test all role combinations with jqwik
  
  - [ ] 4.6 Create authentication DTOs
    - Implement LoginRequest, LoginResponse, RegisterRequest DTOs
    - Add validation annotations (@NotBlank, @Email, @Size)
    - _Requirements: 1.2, 33.1_


- [ ] 5. Create global exception handling and error responses
  - [ ] 5.1 Create custom exception classes
    - Implement ValidationException, UnauthorizedException, ForbiddenException, ResourceNotFoundException
    - Implement BusinessRuleException, ConcurrencyException
    - _Requirements: 33.1, 34.1, 34.2, 34.3_
  
  - [ ] 5.2 Implement global exception handler
    - Create GlobalExceptionHandler.java with @RestControllerAdvice
    - Handle all exception types with appropriate HTTP status codes
    - Return consistent ErrorResponse format with timestamp, status, message, details
    - Include correlation ID for request tracing
    - _Requirements: 34.1, 34.2, 34.3, 34.4, 34.5_
  
  - [ ] 5.3 Create ErrorResponse DTO
    - Implement ErrorResponse.java with all error fields
    - Include field-level validation error details
    - _Requirements: 34.1_
  
  - [ ] 5.4 Write unit tests for exception handling
    - Test each exception type returns correct HTTP status
    - Test error response format consistency
    - Test field-level validation error details
    - _Requirements: 34.1, 34.2_

- [ ] 6. Set up API documentation with Swagger/OpenAPI
  - [ ] 6.1 Configure SpringDoc OpenAPI
    - Add SpringDoc dependency and configuration
    - Configure API info, servers, security schemes
    - Set up JWT bearer authentication in Swagger UI
    - _Requirements: 62.3_
  
  - [ ] 6.2 Add OpenAPI annotations to DTOs
    - Add @Schema annotations to all DTO classes
    - Document field descriptions, examples, constraints
    - _Requirements: 62.3_

- [ ] 7. Checkpoint - Core infrastructure complete
  - Ensure all tests pass, verify database migrations work correctly
  - Verify JWT authentication and authorization work end-to-end
  - Ask the user if questions arise


### Phase 2: User Management (Week 3)

- [ ] 8. Implement authentication service and endpoints
  - [ ] 8.1 Create AuthenticationService
    - Implement login method with credential validation and JWT generation
    - Implement register method with password hashing
    - Implement password change and reset functionality
    - Add audit logging for authentication events
    - _Requirements: 1.2, 1.3, 1.6, 42.1, 43.1_
  
  - [ ] 8.2 Create AuthenticationController
    - Implement POST /api/v1/auth/login endpoint
    - Implement POST /api/v1/auth/register endpoint
    - Implement POST /api/v1/auth/change-password endpoint
    - Implement POST /api/v1/auth/reset-password and confirm endpoints
    - Implement POST /api/v1/auth/refresh endpoint
    - Add input validation and error handling
    - _Requirements: 1.2, 1.3, 1.6_
  
  - [ ] 8.3 Write unit tests for authentication service
    - Test successful login returns JWT
    - Test invalid credentials return error
    - Test password hashing never stores plain text
    - Test password reset token generation and validation
    - _Requirements: 1.2, 1.3, 42.1_
  
  - [ ] 8.4 Write property test for password security
    - **Property 19: Password Security**
    - **Validates: Requirements 42.1, 42.4**
    - For any password input, verify it's never stored in plain text or appears in logs
    - Verify all stored passwords are BCrypt hashes
    - _Requirements: 42.1, 42.4_


- [ ] 9. Implement driver application workflow
  - [ ] 9.1 Create DriverApplicationService
    - Implement submitApplication method for drivers
    - Implement approveApplication and rejectApplication methods for admins
    - Add validation for required documents and expiry dates
    - Create audit trail in application history
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [ ] 9.2 Create DocumentService for file uploads
    - Implement uploadDocument method with file validation
    - Validate file type (images only), size (max 10MB)
    - Store files in configured storage path
    - Generate unique file names to prevent collisions
    - _Requirements: 2.3, 33.7, 48.1, 48.2_
  
  - [ ] 9.3 Create DriverApplicationController
    - Implement POST /api/v1/driver-applications (DRIVER role)
    - Implement GET /api/v1/driver-applications/me (DRIVER role)
    - Implement POST /api/v1/driver-applications/{id}/documents (DRIVER role)
    - Implement GET /api/v1/driver-applications (ADMIN role)
    - Implement GET /api/v1/driver-applications/{id} (ADMIN role)
    - Implement PUT /api/v1/driver-applications/{id}/approve (ADMIN role)
    - Implement PUT /api/v1/driver-applications/{id}/reject (ADMIN role)
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [ ] 9.4 Write unit tests for driver application workflow
    - Test application submission creates pending application
    - Test approval changes status and activates driver
    - Test rejection with reason
    - Test document upload validation
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [ ] 10. Checkpoint - User management complete
  - Ensure all authentication and driver application tests pass
  - Verify file upload works correctly
  - Ask the user if questions arise


### Phase 3: Geographic and Pricing (Week 4)

- [ ] 11. Implement delivery area management
  - [ ] 11.1 Create DeliveryAreaService
    - Implement CRUD operations for delivery areas
    - Add validation for area boundaries (JSONB format)
    - Implement area activation/deactivation
    - _Requirements: 7.1, 7.2_
  
  - [ ] 11.2 Create DeliveryAreaController
    - Implement POST /api/v1/delivery-areas (ADMIN role)
    - Implement GET /api/v1/delivery-areas (all authenticated users)
    - Implement GET /api/v1/delivery-areas/{id}
    - Implement PUT /api/v1/delivery-areas/{id} (ADMIN role)
    - Implement DELETE /api/v1/delivery-areas/{id} (ADMIN role, soft delete)
    - _Requirements: 7.1, 7.2_
  
  - [ ] 11.3 Write unit tests for delivery area service
    - Test area creation with valid boundaries
    - Test area soft delete
    - Test listing only active areas
    - _Requirements: 7.1, 7.2_

- [ ] 12. Implement delivery pricing management
  - [ ] 12.1 Create PricingService
    - Implement setDeliveryPrice for origin-destination pairs
    - Implement calculateDeliveryPrice method
    - Add caching for pricing data (Redis)
    - Implement bulk price update
    - Handle missing price scenarios with clear error
    - _Requirements: 7.3, 8.1, 8.2, 8.3, 8.4_
  
  - [ ] 12.2 Create DistanceCalculator utility
    - Implement Haversine formula for distance calculation
    - Use Earth radius constant (6371 km)
    - Return distance in kilometers
    - _Requirements: 3.1, 36.6_
  
  - [ ] 12.3 Write property test for Haversine distance calculation
    - **Property 3: Haversine Distance Calculation Accuracy**
    - **Validates: Requirements 3.1, 36.6**
    - For any two valid GPS coordinate pairs, verify calculated distance is within 1% of actual great-circle distance
    - Test with jqwik generating random valid coordinates
  
  - [ ] 12.4 Create DeliveryPriceController
    - Implement POST /api/v1/delivery-prices (ADMIN role)
    - Implement GET /api/v1/delivery-prices (all authenticated users)
    - Implement PUT /api/v1/delivery-prices/{id} (ADMIN role)
    - Implement POST /api/v1/delivery-prices/bulk (ADMIN role)
    - Implement GET /api/v1/delivery-prices/calculate with origin/destination params
    - _Requirements: 7.3, 8.1, 8.2, 8.3_
  
  - [ ] 12.5 Write property test for delivery price calculation
    - **Property 8: Delivery Price Calculation**
    - **Validates: Requirements 7.3, 8.3**
    - For any restaurant and delivery area with defined price, verify calculated price matches configured price
    - Test with jqwik generating random area pairs
  
  - [ ] 12.6 Write property test for missing price error handling
    - **Property 9: Missing Price Error Handling**
    - **Validates: Requirements 8.4**
    - For any area pair without defined price, verify error is returned
    - Test with jqwik generating random undefined area pairs
  
  - [ ] 12.7 Write unit tests for pricing service
    - Test price caching and cache invalidation
    - Test bulk price updates
    - Test price retrieval for valid area pairs
    - _Requirements: 8.1, 8.2, 8.3_

- [ ] 13. Configure Redis caching
  - [ ] 13.1 Create CacheConfig
    - Configure RedisCacheManager with different TTLs per cache
    - Set up deliveryPrices cache (TTL: 24 hours)
    - Set up subscriptionStatus cache (TTL: 5 minutes)
    - Set up userAuth cache (TTL: 1 hour)
    - _Requirements: 52.1, 52.2_
  
  - [ ] 13.2 Add caching annotations to services
    - Add @Cacheable to getDeliveryPrice method
    - Add @CacheEvict to updateDeliveryPrice method
    - Implement graceful degradation when Redis unavailable
    - _Requirements: 52.1, 52.2, 52.5_

- [ ] 14. Checkpoint - Geographic and pricing complete
  - Ensure all pricing tests pass
  - Verify Haversine calculation accuracy
  - Verify Redis caching works correctly
  - Ask the user if questions arise


### Phase 4: Order Management (Weeks 5-6)

- [ ] 15. Implement order creation and validation
  - [ ] 15.1 Create OrderService
    - Implement createOrder method with validation
    - Validate restaurant subscription status (not expired)
    - Validate delivery area and calculate delivery price
    - Generate unique order number
    - Support idempotency with idempotency_key
    - Create initial order status history record
    - _Requirements: 3.1, 4.1, 4.2, 7.6, 16.3, 47.2_
  
  - [ ] 15.2 Write property test for expired subscription order prevention
    - **Property 10: Expired Subscription Order Prevention**
    - **Validates: Requirements 7.6, 16.3, 16.4**
    - For any restaurant with expired subscription or LOCKED status, verify order creation fails
    - Test with jqwik generating various subscription states
  
  - [ ] 15.3 Write property test for idempotency guarantee
    - **Property 20: Idempotency Guarantee**
    - **Validates: Requirements 47.2**
    - For any order creation with same idempotency key, verify no duplicate orders created
    - Test concurrent requests with same key
  
  - [ ] 15.4 Create order DTOs
    - Implement OrderRequest, OrderResponse, OrderSummaryDTO
    - Add validation annotations for required fields, GPS coordinates, positive prices
    - _Requirements: 3.1, 33.1, 33.5, 33.6_
  
  - [ ] 15.5 Write property test for input validation
    - **Property 15: Input Validation - Required Fields**
    - **Validates: Requirements 33.1**
    - For any API request with missing required fields, verify 400 response with validation errors
    - **Property 16: Input Validation - GPS Coordinates**
    - **Validates: Requirements 33.5**
    - For any GPS coordinate outside valid range, verify validation error
    - **Property 17: Input Validation - Positive Prices**
    - **Validates: Requirements 33.6**
    - For any negative or zero price, verify validation error


- [ ] 16. Implement order lifecycle management
  - [ ] 16.1 Create OrderLifecycleService
    - Implement acceptOrder method with optimistic locking
    - Implement updateOrderStatus method with state machine validation
    - Implement cancelOrder method with cancellation reason
    - Validate status transitions (CREATED → ASSIGNED → PICKED_FROM_RESTAURANT → DELIVERED/CUSTOMER_NOT_AVAILABLE/CANCELLED)
    - Record all status changes in order_status_history
    - _Requirements: 4.4, 4.5, 5.2, 5.3, 5.4, 5.5, 5.7, 28.1_
  
  - [ ] 16.2 Write property test for order status transition validity
    - **Property 5: Order Status Transition Validity**
    - **Validates: Requirements 4.1, 5.2, 5.3, 5.4, 5.7**
    - For any order, verify status transitions only occur along valid paths
    - Test invalid transitions are rejected
  
  - [ ] 16.3 Write property test for order status history recording
    - **Property 13: Order Status History Recording**
    - **Validates: Requirements 28.1**
    - For any order status change, verify corresponding history record is created
    - Verify history includes previous status, new status, timestamp, user
  
  - [ ] 16.4 Implement DeliveryProofService
    - Implement uploadDeliveryProof method
    - Validate proof is required for DELIVERED status
    - Store GPS coordinates with proof
    - _Requirements: 6.1, 6.2_
  
  - [ ] 16.5 Write property test for delivery proof requirement
    - **Property 7: Delivery Proof Requirement**
    - **Validates: Requirements 6.1**
    - For any order transitioning to DELIVERED, verify delivery proof is required
    - Test transition fails without proof
  
  - [ ] 16.6 Create OrderController
    - Implement POST /api/v1/orders (RESTAURANT role)
    - Implement GET /api/v1/orders (filtered by role)
    - Implement GET /api/v1/orders/{id}
    - Implement PUT /api/v1/orders/{id}/accept (DRIVER role)
    - Implement PUT /api/v1/orders/{id}/status
    - Implement POST /api/v1/orders/{id}/delivery-proof (DRIVER role)
    - Implement PUT /api/v1/orders/{id}/cancel (RESTAURANT, ADMIN roles)
    - Implement GET /api/v1/orders/{id}/history
    - Implement POST /api/v1/orders/{id}/rate (RESTAURANT role)
    - _Requirements: 3.1, 4.1, 4.4, 5.2, 5.5, 6.1, 28.2_
  
  - [ ] 16.7 Write property test for transaction atomicity
    - **Property 18: Transaction Atomicity**
    - **Validates: Requirements 37.6, 52.4**
    - For any operation modifying multiple entities, verify all-or-nothing behavior
    - Test partial failures result in no changes persisted
  
  - [ ] 16.8 Write unit tests for order lifecycle
    - Test order creation with valid data
    - Test order acceptance updates status and driver
    - Test status transitions follow state machine
    - Test cancellation with reason
    - Test delivery proof upload
    - _Requirements: 4.1, 4.4, 5.2, 6.1_

- [ ] 17. Checkpoint - Order management complete
  - Ensure all order lifecycle tests pass
  - Verify optimistic locking prevents concurrent acceptance
  - Verify status history tracking works correctly
  - Ask the user if questions arise


### Phase 5: Dispatch System (Week 7)

- [ ] 18. Implement dispatch algorithm
  - [ ] 18.1 Create DispatchService
    - Implement getAvailableOrders method for drivers
    - Query orders with CREATED status
    - Calculate distance from driver to pickup location using Haversine
    - Filter orders within configured radius (default 10 km)
    - Sort by distance (nearest first)
    - _Requirements: 3.1, 3.2, 3.5, 36.6_
  
  - [ ] 18.2 Write property test for available orders filtering
    - **Property 4: Available Orders Filtering**
    - **Validates: Requirements 3.2, 3.5**
    - For any driver location and radius, verify all returned orders have CREATED status, are within radius, and not assigned
    - Test with jqwik generating random driver locations and orders
  
  - [ ] 18.3 Implement order acceptance with concurrency control
    - Implement acceptOrder method with @Transactional and optimistic locking
    - Use @Version field on Order entity
    - Handle OptimisticLockException and return 409 Conflict
    - Ensure only one driver can accept each order
    - _Requirements: 4.4, 4.5, 63.2, 63.6_
  
  - [ ] 18.4 Write property test for concurrent order acceptance exclusivity
    - **Property 6: Concurrent Order Acceptance Exclusivity**
    - **Validates: Requirements 4.4, 4.5, 63.2, 63.6**
    - For any order in CREATED status with multiple concurrent acceptance attempts, verify exactly one succeeds
    - Test with jqwik simulating concurrent requests
  
  - [ ] 18.5 Create DispatchController
    - Implement GET /api/v1/orders/available (DRIVER role)
    - Return orders within configured radius from driver's location
    - Include distance in response
    - _Requirements: 3.2, 3.5_
  
  - [ ] 18.6 Write unit tests for dispatch service
    - Test available orders query returns only CREATED orders
    - Test distance filtering works correctly
    - Test orders sorted by distance
    - Test concurrent acceptance with optimistic locking
    - _Requirements: 3.2, 3.5, 4.4, 4.5_


- [ ] 19. Implement order timeout mechanism
  - [ ] 19.1 Create OrderTimeoutService
    - Implement checkOrderTimeouts scheduled job (@Scheduled, every 5 minutes)
    - Query orders with ASSIGNED status and assigned_at < (now - timeout_period)
    - Change status back to CREATED
    - Clear driver_id assignment
    - Create order_status_history record
    - Send notification to driver
    - _Requirements: 51.1_
  
  - [ ] 19.2 Write property test for order assignment timeout
    - **Property 21: Order Assignment Timeout**
    - **Validates: Requirements 51.1**
    - For any order in ASSIGNED status exceeding timeout, verify status changes to CREATED and driver cleared
    - Test with jqwik generating various timeout scenarios
  
  - [ ] 19.3 Write unit tests for timeout service
    - Test timeout job identifies timed-out orders
    - Test status change and driver clearing
    - Test notification sent to driver
    - _Requirements: 51.1_

- [ ] 20. Implement driver availability management
  - [ ] 20.1 Create DriverAvailabilityService
    - Implement setAvailability method
    - Track availability changes in driver_availability table
    - _Requirements: 3.6_
  
  - [ ] 20.2 Create DriverController
    - Implement GET /api/v1/drivers/me (DRIVER role)
    - Implement PUT /api/v1/drivers/me (DRIVER role)
    - Implement PUT /api/v1/drivers/me/availability (DRIVER role)
    - Implement GET /api/v1/drivers/me/earnings (DRIVER role)
    - Implement GET /api/v1/drivers/me/orders (DRIVER role)
    - Implement GET /api/v1/drivers/me/statistics (DRIVER role)
    - Implement GET /api/v1/drivers (ADMIN role)
    - Implement GET /api/v1/drivers/{id} (ADMIN role)
    - Implement GET /api/v1/drivers/{id}/performance (ADMIN role)
    - Implement PUT /api/v1/drivers/{id}/suspend (ADMIN role)
    - Implement PUT /api/v1/drivers/{id}/activate (ADMIN role)
    - _Requirements: 3.6, 12.1, 12.2, 12.3, 25.1, 25.2_
  
  - [ ] 20.3 Write unit tests for driver availability
    - Test availability toggle
    - Test availability history tracking
    - _Requirements: 3.6_

- [ ] 21. Checkpoint - Dispatch system complete
  - Ensure all dispatch tests pass
  - Verify concurrent order acceptance works correctly
  - Verify order timeout mechanism works
  - Ask the user if questions arise


### Phase 6: Location Tracking (Week 8)

- [ ] 22. Implement location tracking service
  - [ ] 22.1 Create LocationService
    - Implement updateLocation method for drivers
    - Validate GPS coordinates (latitude [-90, 90], longitude [-180, 180])
    - Store location with timestamp, accuracy, speed, heading
    - Implement getDriverLocation method
    - Implement getLocationHistory method with date range filtering
    - _Requirements: 3.3, 3.4, 36.1, 36.2, 36.3_
  
  - [ ] 22.2 Create LocationController
    - Implement POST /api/v1/locations (DRIVER role)
    - Implement GET /api/v1/locations/drivers (ADMIN role)
    - Implement GET /api/v1/locations/drivers/{id} (ADMIN role)
    - Implement GET /api/v1/locations/drivers/{id}/history (ADMIN role)
    - Add pagination for location history
    - _Requirements: 3.3, 3.4, 36.1, 36.2_
  
  - [ ] 22.3 Write unit tests for location service
    - Test location update stores correct data
    - Test GPS coordinate validation
    - Test location history retrieval with date range
    - Test pagination for large location history
    - _Requirements: 3.3, 3.4, 36.1, 36.2_

- [ ] 23. Optimize location queries
  - [ ] 23.1 Add database indexes for location queries
    - Verify composite index on (driver_id, created_at DESC) exists
    - Consider table partitioning for large datasets
    - _Requirements: 36.4, 63.3_
  
  - [ ] 23.2 Implement location query optimization
    - Use native queries for complex location aggregations
    - Implement pagination for all location list endpoints
    - Add caching for frequently accessed driver locations
    - _Requirements: 36.4, 63.3_

- [ ] 24. Checkpoint - Location tracking complete
  - Ensure all location tests pass
  - Verify GPS coordinate validation works
  - Verify location history queries are performant
  - Ask the user if questions arise


### Phase 7: Real-Time Communication (Week 9)

- [ ] 25. Configure WebSocket server
  - [ ] 25.1 Create WebSocketConfig
    - Implement WebSocketMessageBrokerConfigurer
    - Configure STOMP endpoints (/ws/connect)
    - Enable simple broker for /topic and /queue
    - Set application destination prefix to /app
    - Configure SockJS fallback
    - _Requirements: 37.1, 37.2_
  
  - [ ] 25.2 Create JWT WebSocket interceptor
    - Implement JwtChannelInterceptor for WebSocket authentication
    - Extract and validate JWT from connection handshake
    - Populate user context for WebSocket session
    - _Requirements: 37.3, 42.3_
  
  - [ ] 25.3 Configure WebSocket security
    - Add JWT interceptor to client inbound channel
    - Configure message security
    - _Requirements: 37.3_

- [ ] 26. Implement real-time notification service
  - [ ] 26.1 Create WebSocketService
    - Implement sendToUser method for personal notifications
    - Implement broadcastToTopic method for public updates
    - Implement sendOrderStatusUpdate method
    - Implement sendLocationUpdate method
    - Implement sendNewOrderNotification method
    - _Requirements: 37.1, 37.2, 37.4_
  
  - [ ] 26.2 Create NotificationService
    - Implement sendNotification method
    - Store notification in database
    - Send via WebSocket if user connected
    - Support notification preferences
    - _Requirements: 29.1, 29.2, 29.3_
  
  - [ ] 26.3 Integrate WebSocket with order lifecycle
    - Send WebSocket notification on order status change
    - Notify restaurant when order assigned
    - Notify driver when new order available
    - _Requirements: 37.4_
  
  - [ ] 26.4 Integrate WebSocket with location updates
    - Broadcast driver location updates to /topic/drivers/locations
    - Filter location updates by admin role
    - _Requirements: 37.4, 37.5_


- [ ] 27. Implement notification management endpoints
  - [ ] 27.1 Create NotificationController
    - Implement GET /api/v1/notifications (list user's notifications)
    - Implement GET /api/v1/notifications/{id}
    - Implement PUT /api/v1/notifications/{id}/read
    - Implement PUT /api/v1/notifications/read-all
    - Implement GET /api/v1/notifications/preferences
    - Implement PUT /api/v1/notifications/preferences
    - _Requirements: 29.1, 29.2, 29.3_
  
  - [ ] 27.2 Write unit tests for notification service
    - Test notification creation and storage
    - Test WebSocket delivery
    - Test notification preferences
    - Test mark as read functionality
    - _Requirements: 29.1, 29.2, 29.3_

- [ ] 28. Implement WebSocket connection management
  - [ ] 28.1 Add connection lifecycle handlers
    - Handle connection established event
    - Handle connection closed event
    - Implement heartbeat mechanism (30 second interval)
    - Implement automatic reconnection on client side
    - _Requirements: 37.2, 37.5_
  
  - [ ] 28.2 Implement graceful degradation
    - Handle WebSocket unavailable scenario
    - Fall back to polling for critical updates
    - Log warnings when WebSocket fails
    - _Requirements: 52.5_

- [ ] 29. Checkpoint - Real-time communication complete
  - Ensure WebSocket connections work correctly
  - Verify JWT authentication for WebSocket
  - Verify real-time notifications are delivered
  - Test connection handling and reconnection
  - Ask the user if questions arise


### Phase 8: Restaurant Management (Week 10)

- [ ] 30. Implement restaurant management service
  - [ ] 30.1 Create RestaurantService
    - Implement createRestaurant method (for sales reps)
    - Implement updateRestaurant method
    - Implement getRestaurant method with ownership validation
    - Implement listRestaurants method (filtered by role)
    - Support both SINGLE_BRANCH and BRAND types
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_
  
  - [ ] 30.2 Implement branch management
    - Implement addBranch method for BRAND restaurants
    - Implement updateBranch method
    - Implement listBranches method
    - Validate branch belongs to brand
    - _Requirements: 10.1, 10.2, 10.3_
  
  - [ ] 30.3 Create RestaurantController
    - Implement POST /api/v1/restaurants (SALES role)
    - Implement GET /api/v1/restaurants (filtered by role)
    - Implement GET /api/v1/restaurants/{id}
    - Implement PUT /api/v1/restaurants/{id} (SALES, ADMIN roles)
    - Implement POST /api/v1/restaurants/{id}/branches (SALES, ADMIN roles)
    - Implement GET /api/v1/restaurants/{id}/branches
    - Implement GET /api/v1/restaurants/me/statistics (RESTAURANT role)
    - Implement GET /api/v1/restaurants/{id}/performance (SALES, ADMIN roles)
    - _Requirements: 9.1, 9.2, 10.1, 10.2, 26.1_
  
  - [ ] 30.4 Write unit tests for restaurant service
    - Test restaurant creation by sales rep
    - Test branch addition to brand
    - Test ownership validation
    - Test role-based filtering
    - _Requirements: 9.1, 9.2, 10.1_


- [ ] 31. Implement customer management
  - [ ] 31.1 Create CustomerService
    - Implement createCustomer method (for restaurants)
    - Implement updateCustomer method
    - Implement deleteCustomer method (soft delete)
    - Implement searchCustomers method
    - Validate customer belongs to restaurant
    - _Requirements: 13.1, 13.2, 13.3, 13.4_
  
  - [ ] 31.2 Create CustomerController
    - Implement POST /api/v1/customers (RESTAURANT role)
    - Implement GET /api/v1/customers (RESTAURANT role, filtered by restaurant)
    - Implement GET /api/v1/customers/{id} (RESTAURANT role)
    - Implement PUT /api/v1/customers/{id} (RESTAURANT role)
    - Implement DELETE /api/v1/customers/{id} (RESTAURANT role, soft delete)
    - Implement GET /api/v1/customers/search (RESTAURANT role)
    - _Requirements: 13.1, 13.2, 13.3, 13.4_
  
  - [ ] 31.3 Write property test for soft delete behavior
    - **Property 14: Soft Delete Behavior**
    - **Validates: Requirements 27.4, 43.2, 43.3**
    - For any entity that is soft deleted, verify deleted_at is set, record remains in database, and doesn't appear in default queries
    - Test with jqwik on various entities
  
  - [ ] 31.4 Write unit tests for customer service
    - Test customer creation
    - Test customer search
    - Test soft delete
    - Test ownership validation
    - _Requirements: 13.1, 13.2, 13.3, 13.4_

- [ ] 32. Implement restaurant statistics
  - [ ] 32.1 Create RestaurantStatisticsService
    - Implement calculateStatistics method
    - Calculate total orders, completed orders, cancelled orders
    - Calculate total revenue, average order value
    - Calculate customer count
    - _Requirements: 26.1, 26.2_
  
  - [ ] 32.2 Write unit tests for statistics calculation
    - Test statistics calculation with various order states
    - Test revenue calculation
    - _Requirements: 26.1, 26.2_

- [ ] 33. Checkpoint - Restaurant management complete
  - Ensure all restaurant and customer tests pass
  - Verify branch management works correctly
  - Verify statistics calculation is accurate
  - Ask the user if questions arise


### Phase 9: Subscription Management (Week 11)

- [ ] 34. Implement subscription service
  - [ ] 34.1 Create SubscriptionService
    - Implement createSubscription method (for sales reps and admins)
    - Implement renewSubscription method
    - Calculate end_date based on subscription type (MONTHLY, YEARLY, CUSTOM)
    - Create subscription history record for all changes
    - Support auto-renew flag
    - _Requirements: 14.1, 14.2, 14.3, 15.1, 15.2, 16.1_
  
  - [ ] 34.2 Implement subscription status checking
    - Implement getSubscriptionStatus method with caching
    - Check if subscription is active (end_date >= today)
    - Cache subscription status with 5-minute TTL
    - _Requirements: 16.2, 52.2_
  
  - [ ] 34.3 Create SubscriptionController
    - Implement POST /api/v1/subscriptions (SALES, ADMIN roles)
    - Implement GET /api/v1/subscriptions/{id}
    - Implement PUT /api/v1/subscriptions/{id}/renew (SALES, ADMIN roles)
    - Implement GET /api/v1/subscriptions/expiring (ADMIN role)
    - _Requirements: 14.1, 15.1, 16.1_
  
  - [ ] 34.4 Write unit tests for subscription service
    - Test subscription creation with different types
    - Test end_date calculation for MONTHLY, YEARLY, CUSTOM
    - Test subscription renewal
    - Test subscription history tracking
    - _Requirements: 14.1, 14.2, 14.3, 15.1_


- [ ] 35. Implement subscription expiration checking job
  - [ ] 35.1 Create SubscriptionCheckService
    - Implement checkSubscriptionExpiration scheduled job (@Scheduled, daily at midnight)
    - Query subscriptions with end_date = today and status ACTIVE
    - Set subscription status to EXPIRED
    - Set restaurant status to LOCKED
    - Create notification for restaurant
    - Query subscriptions with end_date = today + 3 days
    - Send expiration warning notifications
    - _Requirements: 16.3, 16.4, 17.1_
  
  - [ ] 35.2 Write unit tests for expiration check job
    - Test job identifies expired subscriptions
    - Test restaurant status set to LOCKED
    - Test expiration notifications sent
    - Test warning notifications sent 3 days before expiry
    - _Requirements: 16.3, 16.4, 17.1_

- [ ] 36. Implement document expiration checking job
  - [ ] 36.1 Create DocumentExpirationService
    - Implement checkDocumentExpiration scheduled job (@Scheduled, daily at midnight)
    - Query driver applications with license expiry dates
    - For licenses expiring in 30 days: Send warning notification
    - For expired licenses: Set driver status to SUSPENDED, send notification
    - _Requirements: 2.6_
  
  - [ ] 36.2 Write unit tests for document expiration job
    - Test job identifies expiring documents
    - Test warning notifications sent 30 days before expiry
    - Test driver suspension for expired documents
    - _Requirements: 2.6_

- [ ] 37. Checkpoint - Subscription management complete
  - Ensure all subscription tests pass
  - Verify expiration checking jobs work correctly
  - Verify restaurant locking for expired subscriptions
  - Ask the user if questions arise


### Phase 10: Financial Management (Week 12)

- [ ] 38. Implement financial transaction service
  - [ ] 38.1 Create FinancialService
    - Implement recordTransaction method
    - Generate unique reference numbers
    - Support transaction types: DRIVER_EARNING, SALES_COMMISSION, COMPANY_COMMISSION, SUBSCRIPTION_PAYMENT
    - Ensure financial records are immutable (no updates or deletes)
    - _Requirements: 11.1, 20.1, 21.1, 53.1, 53.6_
  
  - [ ] 38.2 Write property test for financial transaction immutability
    - **Property 22: Financial Transaction Immutability**
    - **Validates: Requirements 53.6**
    - For any financial transaction record, verify attempts to modify or delete fail
    - Test with jqwik attempting various modifications

- [ ] 39. Implement driver earnings calculation
  - [ ] 39.1 Create EarningsService
    - Implement calculateDriverEarning method
    - Calculate: driver_earning = delivery_price - company_commission
    - Support commission types: FIXED_PER_ORDER, PERCENTAGE
    - Create FinancialTransaction and DriverEarning records
    - Trigger calculation on order DELIVERED status
    - _Requirements: 11.2, 11.3, 11.4_
  
  - [ ] 39.2 Write property test for driver earnings calculation
    - **Property 11: Driver Earnings Calculation**
    - **Validates: Requirements 11.2, 11.3, 11.4**
    - For any completed order, verify driver_earning = delivery_price - company_commission
    - Test with jqwik generating various commission types and values
  
  - [ ] 39.3 Implement getDriverEarnings method
    - Calculate total earnings for date range
    - Calculate earnings by order
    - Support filtering by date range
    - _Requirements: 12.1, 12.2_
  
  - [ ] 39.4 Write unit tests for earnings service
    - Test earnings calculation with FIXED_PER_ORDER commission
    - Test earnings calculation with PERCENTAGE commission
    - Test earnings aggregation by date range
    - _Requirements: 11.2, 11.3, 12.1_


- [ ] 40. Implement sales commission calculation
  - [ ] 40.1 Create CommissionService
    - Implement calculateSalesCommission method
    - Use NEW_RESTAURANT_COMMISSION percentage for first subscriptions
    - Use OLD_RESTAURANT_COMMISSION percentage for renewals
    - Create FinancialTransaction and SalesCommission records
    - Trigger calculation on subscription creation/renewal
    - _Requirements: 20.1, 20.2, 20.3_
  
  - [ ] 40.2 Write property test for sales commission calculation
    - **Property 12: Sales Commission Calculation**
    - **Validates: Requirements 20.1, 20.2**
    - For any subscription payment, verify commission uses correct percentage (new vs renewal)
    - Test with jqwik generating various subscription scenarios
  
  - [ ] 40.3 Implement getSalesCommissions method
    - Calculate total commissions for sales rep
    - Support filtering by date range
    - Calculate commissions by restaurant
    - _Requirements: 20.4, 20.5_
  
  - [ ] 40.4 Write unit tests for commission service
    - Test commission calculation for new restaurant
    - Test commission calculation for renewal
    - Test commission aggregation by date range
    - _Requirements: 20.1, 20.2, 20.4_

- [ ] 41. Implement financial reporting
  - [ ] 41.1 Create ReportingService
    - Implement generateFinancialReport method
    - Calculate total driver earnings
    - Calculate total sales commissions
    - Calculate total company commissions
    - Calculate net profit
    - Support date range filtering
    - _Requirements: 21.1, 21.2, 21.3_
  
  - [ ] 41.2 Create FinancialController
    - Implement GET /api/v1/financials/dashboard (ADMIN role)
    - Implement GET /api/v1/financials/transactions (ADMIN role)
    - Implement GET /api/v1/financials/transactions/{id} (ADMIN role)
    - Implement GET /api/v1/financials/reports (ADMIN role)
    - Implement GET /api/v1/financials/export (ADMIN role)
    - Implement GET /api/v1/financials/reconciliation (ADMIN role)
    - _Requirements: 21.1, 21.2, 21.3, 21.4_
  
  - [ ] 41.3 Write unit tests for reporting service
    - Test financial report generation
    - Test reconciliation calculations
    - Test date range filtering
    - _Requirements: 21.1, 21.2, 21.3_

- [ ] 42. Checkpoint - Financial management complete
  - Ensure all financial calculation tests pass
  - Verify earnings and commission calculations are accurate
  - Verify financial transaction immutability
  - Ask the user if questions arise


### Phase 11: Sales Representative Management (Week 12 continued)

- [ ] 43. Implement sales representative management
  - [ ] 43.1 Create SalesService
    - Implement createSalesAccount method (for admins)
    - Implement updateSalesRep method
    - Implement deactivateSalesRep method
    - Implement transferRestaurant method (transfer restaurant to different sales rep)
    - _Requirements: 18.1, 18.2, 18.3, 19.1_
  
  - [ ] 43.2 Create SalesController
    - Implement POST /api/v1/sales (ADMIN role)
    - Implement GET /api/v1/sales (ADMIN role)
    - Implement GET /api/v1/sales/{id} (ADMIN role)
    - Implement PUT /api/v1/sales/{id} (ADMIN role)
    - Implement PUT /api/v1/sales/{id}/deactivate (ADMIN role)
    - Implement GET /api/v1/sales/me/dashboard (SALES role)
    - Implement GET /api/v1/sales/me/restaurants (SALES role)
    - Implement GET /api/v1/sales/me/commissions (SALES role)
    - Implement PUT /api/v1/sales/transfer (ADMIN role)
    - _Requirements: 18.1, 18.2, 18.3, 19.1, 20.4, 20.5_
  
  - [ ] 43.3 Write unit tests for sales service
    - Test sales account creation
    - Test restaurant transfer
    - Test sales rep deactivation
    - Test commission retrieval
    - _Requirements: 18.1, 18.2, 19.1_

- [ ] 44. Checkpoint - Sales management complete
  - Ensure all sales management tests pass
  - Verify restaurant transfer works correctly
  - Ask the user if questions arise


### Phase 12: Analytics and Reporting (Week 13)

- [ ] 45. Implement analytics calculation service
  - [ ] 45.1 Create AnalyticsService
    - Implement calculateHourlyMetrics scheduled job (@Scheduled, every hour)
    - Calculate active drivers count
    - Calculate active restaurants count
    - Calculate orders created in last hour
    - Calculate orders completed in last hour
    - Calculate average delivery time
    - Calculate order success rate
    - Store metrics in analytics_snapshots table
    - _Requirements: 22.1, 22.2, 22.3, 22.4_
  
  - [ ] 45.2 Implement generateDailyReport scheduled job
    - Run daily at 1 AM
    - Calculate total driver earnings for previous day
    - Calculate total sales commissions for previous day
    - Calculate total company commissions for previous day
    - Calculate net profit
    - Store in analytics_snapshots table
    - _Requirements: 21.1, 21.2, 21.3_
  
  - [ ] 45.3 Write unit tests for analytics service
    - Test metric calculation
    - Test daily report generation
    - Test analytics snapshot storage
    - _Requirements: 22.1, 22.2, 22.3_

- [ ] 46. Implement analytics endpoints
  - [ ] 46.1 Create AnalyticsController
    - Implement GET /api/v1/analytics/overview (ADMIN role)
    - Implement GET /api/v1/analytics/orders (ADMIN role)
    - Implement GET /api/v1/analytics/drivers (ADMIN role)
    - Implement GET /api/v1/analytics/restaurants (ADMIN role)
    - Implement GET /api/v1/analytics/trends (ADMIN role)
    - Implement GET /api/v1/analytics/export (ADMIN role)
    - Support date range filtering
    - _Requirements: 22.1, 22.2, 22.3, 22.4, 22.5_
  
  - [ ] 46.2 Write unit tests for analytics endpoints
    - Test analytics retrieval with date ranges
    - Test trend analysis
    - Test export functionality
    - _Requirements: 22.1, 22.2, 22.5_

- [ ] 47. Checkpoint - Analytics complete
  - Ensure all analytics tests pass
  - Verify scheduled jobs run correctly
  - Verify analytics data is accurate
  - Ask the user if questions arise


### Phase 13: System Features (Week 13 continued)

- [ ] 48. Implement system configuration management
  - [ ] 48.1 Create ConfigurationService
    - Implement getConfiguration method
    - Implement updateConfiguration method
    - Track configuration changes in configuration_history
    - Support different value types (STRING, INTEGER, DECIMAL, BOOLEAN, JSON)
    - _Requirements: 32.1, 32.2, 32.3_
  
  - [ ] 48.2 Create ConfigurationController
    - Implement GET /api/v1/config (ADMIN role)
    - Implement GET /api/v1/config/{key} (ADMIN role)
    - Implement PUT /api/v1/config/{key} (ADMIN role)
    - Implement GET /api/v1/config/history (ADMIN role)
    - _Requirements: 32.1, 32.2, 32.3_
  
  - [ ] 48.3 Write unit tests for configuration service
    - Test configuration retrieval
    - Test configuration update
    - Test configuration history tracking
    - _Requirements: 32.1, 32.2, 32.3_

- [ ] 49. Implement dispute management
  - [ ] 49.1 Create DisputeService
    - Implement createDispute method (for restaurants and drivers)
    - Implement updateDispute method (for admins)
    - Implement uploadDisputeAttachment method
    - Support dispute types: PAYMENT_ISSUE, DELIVERY_ISSUE, CUSTOMER_ISSUE, OTHER
    - _Requirements: 31.1, 31.2_
  
  - [ ] 49.2 Create DisputeController
    - Implement POST /api/v1/disputes (RESTAURANT, DRIVER roles)
    - Implement GET /api/v1/disputes (filtered by role)
    - Implement PUT /api/v1/disputes/{id} (ADMIN role)
    - _Requirements: 31.1, 31.2_
  
  - [ ] 49.3 Write unit tests for dispute service
    - Test dispute creation
    - Test dispute resolution
    - Test attachment upload
    - _Requirements: 31.1, 31.2_


- [ ] 50. Implement system announcements
  - [ ] 50.1 Create AnnouncementService
    - Implement createAnnouncement method (for admins)
    - Implement getAnnouncements method (filtered by role)
    - Track announcement views
    - Support urgent announcements
    - _Requirements: 30.1, 30.2_
  
  - [ ] 50.2 Create AnnouncementController
    - Implement POST /api/v1/announcements (ADMIN role)
    - Implement GET /api/v1/announcements (all authenticated users)
    - _Requirements: 30.1, 30.2_
  
  - [ ] 50.3 Write unit tests for announcement service
    - Test announcement creation
    - Test role-based filtering
    - Test view tracking
    - _Requirements: 30.1, 30.2_

- [ ] 51. Implement audit logging
  - [ ] 51.1 Create AuditService
    - Implement logAction method
    - Capture user_id, action, entity_type, entity_id
    - Store old_values and new_values as JSONB
    - Capture IP address and user agent
    - _Requirements: 43.1, 43.4_
  
  - [ ] 51.2 Add audit logging to critical operations
    - Log order status changes
    - Log financial transactions
    - Log user role changes
    - Log configuration changes
    - _Requirements: 43.1, 43.4_
  
  - [ ] 51.3 Write unit tests for audit service
    - Test audit log creation
    - Test old/new values capture
    - _Requirements: 43.1, 43.4_

- [ ] 52. Implement health check endpoints
  - [ ] 52.1 Create HealthController
    - Implement GET /api/v1/health (public)
    - Implement GET /api/v1/health/detailed (ADMIN role)
    - Check database connectivity
    - Check Redis connectivity
    - Return overall system status
    - _Requirements: 44.1, 44.2_
  
  - [ ] 52.2 Implement GET /api/v1/version endpoint
    - Return API version information
    - _Requirements: 44.1_
  
  - [ ] 52.3 Write unit tests for health endpoints
    - Test health check returns correct status
    - Test detailed health check
    - _Requirements: 44.1, 44.2_

- [ ] 53. Checkpoint - System features complete
  - Ensure all system feature tests pass
  - Verify configuration management works
  - Verify audit logging captures all critical operations
  - Ask the user if questions arise


### Phase 14: Performance Optimization and Security (Week 14)

- [ ] 54. Implement database query optimization
  - [ ] 54.1 Add composite indexes for common query patterns
    - Create index on orders(restaurant_id, status, created_at DESC)
    - Create index on orders(driver_id, status, created_at DESC)
    - Create partial index on orders(created_at DESC) WHERE status = 'CREATED'
    - Create partial index on subscriptions(end_date) WHERE status = 'ACTIVE'
    - _Requirements: 63.3, 63.4_
  
  - [ ] 54.2 Optimize repository queries
    - Add JOIN FETCH to avoid N+1 queries
    - Use native queries for complex aggregations
    - Implement pagination for all list endpoints
    - _Requirements: 63.3, 63.4_
  
  - [ ] 54.3 Configure connection pooling
    - Set HikariCP maximum-pool-size to 20
    - Set minimum-idle to 5
    - Configure connection timeout and idle timeout
    - _Requirements: 63.1_

- [ ] 55. Implement caching optimizations
  - [ ] 55.1 Add caching for frequently accessed data
    - Cache delivery areas (TTL: 1 hour)
    - Cache user authentication data (TTL: 1 hour)
    - Implement cache invalidation on updates
    - _Requirements: 52.1, 52.2_
  
  - [ ] 55.2 Implement cache-aside pattern
    - Check cache before database query
    - Update cache on write operations
    - Handle cache unavailability gracefully
    - _Requirements: 52.1, 52.5_

- [ ] 56. Implement security enhancements
  - [ ] 56.1 Add CORS configuration
    - Configure allowed origins
    - Set allowed methods and headers
    - Enable credentials support
    - _Requirements: 42.5_
  
  - [ ] 56.2 Implement rate limiting
    - Create RateLimitingFilter
    - Set different limits per role (ADMIN: 1000, SALES: 500, RESTAURANT: 300, DRIVER: 600 per minute)
    - Use Redis for distributed rate limiting
    - Return 429 status when limit exceeded
    - _Requirements: 42.6_
  
  - [ ] 56.3 Implement input sanitization
    - Create InputSanitizer utility
    - Sanitize all user inputs to prevent SQL injection
    - Sanitize inputs to prevent XSS attacks
    - HTML encode special characters
    - _Requirements: 42.7, 42.8_
  
  - [ ] 56.4 Write unit tests for security features
    - Test CORS configuration
    - Test rate limiting
    - Test input sanitization
    - _Requirements: 42.5, 42.6, 42.7_


- [ ] 57. Implement async processing
  - [ ] 57.1 Configure async executor
    - Create AsyncConfig with ThreadPoolTaskExecutor
    - Set core pool size to 5, max pool size to 10
    - Configure queue capacity
    - _Requirements: 63.5_
  
  - [ ] 57.2 Make notification sending async
    - Add @Async annotation to sendNotification method
    - Ensure notifications don't block main request thread
    - _Requirements: 63.5_
  
  - [ ] 57.3 Write unit tests for async processing
    - Test async notification sending
    - Test thread pool configuration
    - _Requirements: 63.5_

- [ ] 58. Implement response compression
  - [ ] 58.1 Configure GZIP compression
    - Enable compression in application.yml
    - Set mime types for compression
    - Set minimum response size (1024 bytes)
    - _Requirements: 63.7_

- [ ] 59. Implement batch processing
  - [ ] 59.1 Configure Hibernate batch processing
    - Set jdbc.batch_size to 20
    - Enable order_inserts and order_updates
    - _Requirements: 63.8_
  
  - [ ] 59.2 Implement batch location saves
    - Flush and clear entity manager every 20 records
    - _Requirements: 63.8_

- [ ] 60. Checkpoint - Performance and security complete
  - Ensure all optimization tests pass
  - Verify rate limiting works correctly
  - Verify caching improves performance
  - Ask the user if questions arise


### Phase 15: Comprehensive Testing (Weeks 15-16)

- [ ] 61. Complete unit test coverage
  - [ ] 61.1 Write unit tests for all service classes
    - Achieve minimum 80% line coverage
    - Test success paths and error paths
    - Test edge cases and boundary conditions
    - _Requirements: 64.1_
  
  - [ ] 61.2 Write unit tests for all controller classes
    - Test request validation
    - Test authorization checks
    - Test error responses
    - _Requirements: 64.1_
  
  - [ ] 61.3 Write unit tests for utility classes
    - Test DistanceCalculator with known coordinates
    - Test InputSanitizer with malicious inputs
    - _Requirements: 64.1_

- [ ] 62. Implement all property-based tests
  - [ ] 62.1 Set up jqwik for property-based testing
    - Add jqwik dependency
    - Configure test execution with 100+ iterations
    - _Requirements: 64.2_
  
  - [ ] 62.2 Verify all 22 property tests are implemented
    - Property 1: Authentication Token Generation (task 4.2)
    - Property 2: Authorization Enforcement (task 4.5)
    - Property 3: Haversine Distance Calculation Accuracy (task 12.3)
    - Property 4: Available Orders Filtering (task 18.2)
    - Property 5: Order Status Transition Validity (task 16.2)
    - Property 6: Concurrent Order Acceptance Exclusivity (task 18.4)
    - Property 7: Delivery Proof Requirement (task 16.5)
    - Property 8: Delivery Price Calculation (task 12.5)
    - Property 9: Missing Price Error Handling (task 12.6)
    - Property 10: Expired Subscription Order Prevention (task 15.2)
    - Property 11: Driver Earnings Calculation (task 39.2)
    - Property 12: Sales Commission Calculation (task 40.2)
    - Property 13: Order Status History Recording (task 16.3)
    - Property 14: Soft Delete Behavior (task 31.3)
    - Property 15: Input Validation - Required Fields (task 15.5)
    - Property 16: Input Validation - GPS Coordinates (task 15.5)
    - Property 17: Input Validation - Positive Prices (task 15.5)
    - Property 18: Transaction Atomicity (task 16.7)
    - Property 19: Password Security (task 8.4)
    - Property 20: Idempotency Guarantee (task 15.3)
    - Property 21: Order Assignment Timeout (task 19.2)
    - Property 22: Financial Transaction Immutability (task 38.2)
    - _Requirements: 64.2_
  
  - [ ] 62.3 Run all property tests with full iteration count
    - Execute all property tests with 100+ iterations
    - Verify all properties pass consistently
    - _Requirements: 64.2_


- [ ] 63. Write integration tests
  - [ ] 63.1 Set up integration test infrastructure
    - Configure test database (H2 or Testcontainers with PostgreSQL)
    - Configure test Redis instance
    - Set up MockMvc for API testing
    - _Requirements: 64.3_
  
  - [ ] 63.2 Write integration tests for authentication flow
    - Test complete login flow (credentials → JWT → authenticated request)
    - Test token expiration handling
    - Test invalid token rejection
    - _Requirements: 64.3_
  
  - [ ] 63.3 Write integration tests for order lifecycle
    - Test complete order flow: create → assign → pick up → deliver
    - Test order cancellation flow
    - Test concurrent order acceptance
    - _Requirements: 64.3_
  
  - [ ] 63.4 Write integration tests for subscription expiration
    - Test subscription expiration job
    - Test restaurant locking on expiration
    - Test order creation blocked for expired subscription
    - _Requirements: 64.3_
  
  - [ ] 63.5 Write integration tests for financial calculations
    - Test end-to-end earnings calculation on order delivery
    - Test commission calculation on subscription creation
    - _Requirements: 64.3_
  
  - [ ] 63.6 Write integration tests for WebSocket communication
    - Test WebSocket connection establishment
    - Test message broadcasting
    - Test channel isolation by role
    - _Requirements: 64.3_

- [ ] 64. Perform load testing
  - [ ] 64.1 Set up load testing infrastructure
    - Install JMeter or Gatling
    - Create test scenarios for critical endpoints
    - _Requirements: 64.4_
  
  - [ ] 64.2 Execute load tests
    - Test sustained load: 100,000 orders per day
    - Test peak load: 10x normal traffic
    - Measure response times under load (p95 < 500ms, p99 < 1000ms)
    - Identify bottlenecks
    - _Requirements: 64.4, 65.1_
  
  - [ ] 64.3 Optimize based on load test results
    - Address identified bottlenecks
    - Tune database queries
    - Adjust connection pool sizes
    - _Requirements: 64.4_

- [ ] 65. Checkpoint - Testing complete
  - Ensure all tests pass (unit, property, integration)
  - Verify 80%+ code coverage achieved
  - Verify all 22 property tests pass with 100+ iterations
  - Verify load testing meets performance targets
  - Ask the user if questions arise


### Phase 16: Deployment Preparation (Week 17)

- [ ] 66. Create Docker configuration
  - [ ] 66.1 Create Dockerfile
    - Use multi-stage build (build stage + runtime stage)
    - Use eclipse-temurin:17-jdk-alpine for build
    - Use eclipse-temurin:17-jre-alpine for runtime
    - Configure JVM options for container support
    - Run as non-root user
    - _Requirements: 66.1_
  
  - [ ] 66.2 Create docker-compose.yml for local development
    - Configure PostgreSQL service
    - Configure Redis service
    - Configure application service with environment variables
    - Set up volume mounts for file storage
    - _Requirements: 66.1_
  
  - [ ] 66.3 Test Docker deployment locally
    - Build Docker image
    - Run docker-compose up
    - Verify all services start correctly
    - Test API endpoints
    - _Requirements: 66.1_

- [ ] 67. Create Kubernetes deployment configuration
  - [ ] 67.1 Create deployment.yaml
    - Configure 3 replicas for high availability
    - Set resource requests and limits (memory: 512Mi-2Gi, cpu: 500m-2000m)
    - Configure environment variables from secrets
    - Set up liveness and readiness probes
    - _Requirements: 66.2_
  
  - [ ] 67.2 Create service.yaml
    - Configure LoadBalancer service
    - Expose port 80 mapping to container port 8080
    - _Requirements: 66.2_
  
  - [ ] 67.3 Create secrets configuration
    - Create secret for database credentials
    - Create secret for JWT secret
    - Create secret for Redis password
    - _Requirements: 66.2_
  
  - [ ] 67.4 Test Kubernetes deployment (if K8s cluster available)
    - Apply Kubernetes manifests
    - Verify pods are running
    - Test service connectivity
    - _Requirements: 66.2_


- [ ] 68. Set up monitoring and observability
  - [ ] 68.1 Configure Prometheus metrics
    - Enable Prometheus endpoint in application.yml
    - Expose JVM metrics, HTTP metrics, database metrics
    - Add custom business metrics (orders created, drivers active)
    - _Requirements: 67.1_
  
  - [ ] 68.2 Configure structured logging
    - Set up JSON logging format for production
    - Configure log levels per package
    - Set up log rotation (max 100MB per file, 30 days retention)
    - Ensure sensitive data is never logged
    - _Requirements: 67.2, 42.4_
  
  - [ ] 68.3 Create monitoring dashboards
    - Document key metrics to monitor
    - Create example Grafana dashboard configuration (optional)
    - _Requirements: 67.1_

- [ ] 69. Configure backup and disaster recovery
  - [ ] 69.1 Document backup strategy
    - Daily full database backups at 2 AM
    - Hourly incremental backups
    - 30-day retention for daily, 7-day for hourly
    - File storage backups with 90-day retention
    - _Requirements: 68.1_
  
  - [ ] 69.2 Document disaster recovery plan
    - RTO: 4 hours, RPO: 1 hour
    - Recovery steps documented
    - _Requirements: 68.1_

- [ ] 70. Create deployment documentation
  - [ ] 70.1 Write deployment guide
    - Document environment variables
    - Document database setup and migrations
    - Document Redis setup
    - Document file storage configuration
    - Document SSL/TLS setup
    - _Requirements: 69.1_
  
  - [ ] 70.2 Write operations manual
    - Document scheduled jobs and their schedules
    - Document monitoring and alerting
    - Document backup and restore procedures
    - Document troubleshooting common issues
    - _Requirements: 69.1_
  
  - [ ] 70.3 Complete API documentation
    - Verify all endpoints documented in Swagger
    - Add request/response examples
    - Document authentication requirements
    - Document error codes and responses
    - _Requirements: 69.2_

- [ ] 71. Final checkpoint - Deployment preparation complete
  - Ensure Docker and Kubernetes configurations work
  - Verify monitoring is configured
  - Verify all documentation is complete
  - Ask the user if questions arise


### Phase 17: Production Deployment (Week 17 continued)

- [ ] 72. Perform security audit
  - [ ] 72.1 Review security configuration
    - Verify JWT secret is strong and not hardcoded
    - Verify database credentials are in secrets
    - Verify HTTPS is enforced in production
    - Verify CORS is properly configured
    - Verify rate limiting is enabled
    - _Requirements: 42.1, 42.2, 42.5, 42.6_
  
  - [ ] 72.2 Review input validation
    - Verify all endpoints have input validation
    - Verify SQL injection prevention
    - Verify XSS prevention
    - Verify file upload validation
    - _Requirements: 42.7, 42.8, 48.1, 48.2_
  
  - [ ] 72.3 Review authentication and authorization
    - Verify all endpoints have proper role checks
    - Verify resource ownership validation
    - Verify password hashing is secure
    - _Requirements: 1.4, 1.5, 42.1_

- [ ] 73. Perform final testing
  - [ ] 73.1 Run full test suite
    - Execute all unit tests
    - Execute all property tests (100+ iterations)
    - Execute all integration tests
    - Verify 80%+ code coverage
    - _Requirements: 64.1, 64.2, 64.3_
  
  - [ ] 73.2 Perform smoke testing
    - Test all critical user flows end-to-end
    - Test authentication flow
    - Test order creation and lifecycle
    - Test financial calculations
    - Test real-time notifications
    - _Requirements: 70.1_
  
  - [ ] 73.3 Verify performance targets
    - Verify API response time p95 < 500ms
    - Verify API response time p99 < 1000ms
    - Verify system handles 100,000 orders per day
    - _Requirements: 65.1_

- [ ] 74. Deploy to production
  - [ ] 74.1 Prepare production environment
    - Set up production database
    - Set up production Redis
    - Configure file storage
    - Set up SSL/TLS certificates
    - _Requirements: 66.1, 66.2_
  
  - [ ] 74.2 Run database migrations
    - Execute Flyway migrations on production database
    - Verify all tables created correctly
    - Verify all indexes created
    - _Requirements: 62.2_
  
  - [ ] 74.3 Deploy application
    - Build production Docker image
    - Deploy to Kubernetes cluster (or chosen platform)
    - Verify all pods are running
    - Verify health checks pass
    - _Requirements: 66.1, 66.2_
  
  - [ ] 74.4 Verify production deployment
    - Test API endpoints in production
    - Verify authentication works
    - Verify database connectivity
    - Verify Redis connectivity
    - Verify WebSocket connectivity
    - Monitor logs for errors
    - _Requirements: 70.1_

- [ ] 75. Final checkpoint - Production deployment complete
  - Ensure all production services are running
  - Verify monitoring and alerting are active
  - Verify all critical flows work in production
  - System is ready for use

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP delivery
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation throughout implementation
- Property tests validate universal correctness properties using jqwik
- Unit tests validate specific scenarios and edge cases
- Integration tests validate end-to-end flows
- The implementation follows a 17-week roadmap with clear milestones
- All 22 correctness properties from the design document are covered by property-based tests
- The system is designed for horizontal scalability to support 100,000+ orders per day
