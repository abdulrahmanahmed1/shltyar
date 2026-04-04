# Shl Tyar - Delivery Platform Backend

A production-grade delivery platform backend built with Spring Boot 3.x and Java 17.

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Java Version**: Java 17
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Real-time**: WebSocket (Spring WebSocket + STOMP)
- **Caching**: Redis
- **Database Migration**: Flyway
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Testing**: JUnit 5, Mockito, jqwik (Property-Based Testing)
- **Build Tool**: Maven

## Project Structure

```
src/main/java/com/twintech/shl_tyar/
├── config/          # Spring configurations
├── controller/      # REST endpoints
├── domain/          # Entity models
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions and handlers
├── repository/      # Data access layer
└── service/         # Business logic
```

## Prerequisites

- Java 17 or higher
- MySQL 8.0+
- Redis (optional, for caching)
- Maven 3.6+

## Configuration

The application configuration is located in `src/main/resources/application.properties`. Key configurations include:

- **Database**: MySQL connection settings
- **Redis**: Cache configuration
- **JWT**: Token expiration and secret key
- **File Upload**: Maximum file size and allowed extensions
- **Logging**: Log levels and file output

### Environment Variables

Set the following environment variable for production:

```bash
JWT_SECRET=your-secret-key-minimum-256-bits
```

## Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE shl_tyar_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Update database credentials in `application.properties` if needed

3. Flyway will automatically run migrations on application startup

## Running the Application

### Using Maven Wrapper

```bash
./mvnw spring-boot:run
```

### Using Java

```bash
./mvnw clean package
java -jar target/shl-tyar-0.0.1-SNAPSHOT.jar
```

## API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

API documentation JSON is available at:

```
http://localhost:8080/api-docs
```

## Building for Production

```bash
./mvnw clean package -DskipTests
```

The executable JAR will be created in the `target/` directory.

## Testing

Run all tests:

```bash
./mvnw test
```

Run with coverage:

```bash
./mvnw test jacoco:report
```

## Features

- JWT-based authentication with 4 user roles (ADMIN, SALES, RESTAURANT, DRIVER)
- Driver application and approval workflow
- Real-time order dispatch and tracking
- Live driver location tracking
- Geographic delivery areas with pricing
- Financial management and commission calculation
- Restaurant subscription management
- Multi-branch restaurant support
- Comprehensive analytics and reporting

## License

Proprietary - Twin Tech
