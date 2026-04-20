# Test Swagger Bearer Authentication Configuration
Write-Host "Testing Swagger UI Bearer Authentication Setup" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# 1. Login as admin to get token
Write-Host "`n1. Logging in as admin..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body '{"email":"newadmin@example.com","password":"Admin123!"}'

$token = $loginResponse.accessToken
Write-Host "✓ Login successful! Token obtained." -ForegroundColor Green

# 2. Test protected endpoint WITHOUT token (should fail)
Write-Host "`n2. Testing protected endpoint WITHOUT token..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/admin/applications" -Method GET
    Write-Host "✗ FAILED: Endpoint should require authentication!" -ForegroundColor Red
} catch {
    Write-Host "✓ Correctly rejected (401 Unauthorized)" -ForegroundColor Green
}

# 3. Test protected endpoint WITH token (should succeed)
Write-Host "`n3. Testing protected endpoint WITH Bearer token..." -ForegroundColor Yellow
$applications = Invoke-RestMethod -Uri "http://localhost:8081/api/admin/applications" `
    -Method GET `
    -Headers @{Authorization="Bearer $token"}
Write-Host "✓ Successfully accessed protected endpoint!" -ForegroundColor Green
Write-Host "   Found $($applications.Count) applications" -ForegroundColor Gray

# 4. Check Swagger UI configuration
Write-Host "`n4. Checking Swagger OpenAPI configuration..." -ForegroundColor Yellow
$apiDocs = Invoke-RestMethod -Uri "http://localhost:8081/v3/api-docs" -Method GET

if ($apiDocs.components.securitySchemes.'Bearer Authentication') {
    Write-Host "✓ Bearer Authentication security scheme is configured!" -ForegroundColor Green
    Write-Host "   Type: $($apiDocs.components.securitySchemes.'Bearer Authentication'.type)" -ForegroundColor Gray
    Write-Host "   Scheme: $($apiDocs.components.securitySchemes.'Bearer Authentication'.scheme)" -ForegroundColor Gray
} else {
    Write-Host "✗ Bearer Authentication security scheme NOT found!" -ForegroundColor Red
}

# 5. Check if endpoints have security requirements
Write-Host "`n5. Checking endpoint security annotations..." -ForegroundColor Yellow
$protectedEndpoints = 0
$publicEndpoints = 0

foreach ($path in $apiDocs.paths.PSObject.Properties) {
    foreach ($method in $path.Value.PSObject.Properties) {
        if ($method.Value.security) {
            $protectedEndpoints++
        } else {
            $publicEndpoints++
        }
    }
}

Write-Host "✓ Protected endpoints: $protectedEndpoints" -ForegroundColor Green
Write-Host "✓ Public endpoints: $publicEndpoints" -ForegroundColor Green

Write-Host "`n=============================================" -ForegroundColor Cyan
Write-Host "Swagger UI is ready at: http://localhost:8081/swagger-ui.html" -ForegroundColor Cyan
Write-Host "Click the 'Authorize' button and paste this token:" -ForegroundColor Yellow
Write-Host $token -ForegroundColor White
Write-Host "=============================================" -ForegroundColor Cyan
