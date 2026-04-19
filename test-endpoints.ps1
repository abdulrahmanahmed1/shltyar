# Simple test to verify API endpoints are working

Write-Host "Testing API endpoints..." -ForegroundColor Green

# Test if the API docs endpoint is accessible
try {
    $apiDocsResponse = Invoke-WebRequest -Uri "http://localhost:8081/api-docs" -UseBasicParsing
    Write-Host "✓ API docs endpoint is accessible" -ForegroundColor Green
} catch {
    Write-Host "✗ API docs endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test registration endpoint
try {
    $registerBody = '{"email":"testuser@example.com","password":"password123","fullName":"Test User","phone":"+1234567890"}'
    $registerResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/auth/register" -Method POST -ContentType "application/json" -Body $registerBody -UseBasicParsing
    
    if ($registerResponse.StatusCode -eq 200) {
        Write-Host "✓ Registration endpoint is working" -ForegroundColor Green
        $responseContent = $registerResponse.Content | ConvertFrom-Json
        Write-Host "  Token received: $($responseContent.token.Substring(0, 20))..." -ForegroundColor Yellow
    }
} catch {
    if ($_.Exception.Response.StatusCode -eq 400) {
        Write-Host "✓ Registration endpoint is working (user already exists)" -ForegroundColor Green
    } else {
        Write-Host "✗ Registration endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test login endpoint
try {
    $loginBody = '{"email":"testuser@example.com","password":"password123"}'
    $loginResponse = Invoke-WebRequest -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -UseBasicParsing
    
    if ($loginResponse.StatusCode -eq 200) {
        Write-Host "✓ Login endpoint is working" -ForegroundColor Green
        $responseContent = $loginResponse.Content | ConvertFrom-Json
        Write-Host "  Token received: $($responseContent.token.Substring(0, 20))..." -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Login endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nAll tests completed!" -ForegroundColor Cyan
Write-Host "Access the application at:" -ForegroundColor Cyan
Write-Host "  - Driver Application Form: http://localhost:8081/driver-application.html" -ForegroundColor White
Write-Host "  - Swagger UI: http://localhost:8081/swagger-ui.html" -ForegroundColor White
Write-Host "  - API Documentation: http://localhost:8081/api-docs" -ForegroundColor White