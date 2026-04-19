# Test script for the authentication API

# Test registration
Write-Host "Testing Registration..." -ForegroundColor Green
try {
    $registerBody = @{
        email = "test@example.com"
        password = "password123"
        fullName = "Test Driver"
        phone = "+1234567890"
    } | ConvertTo-Json

    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/register" -Method POST -ContentType "application/json" -Body $registerBody
    Write-Host "Registration successful!" -ForegroundColor Green
    Write-Host "Token: $($registerResponse.token.Substring(0, 20))..." -ForegroundColor Yellow
    $token = $registerResponse.token
} catch {
    Write-Host "Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorResponse = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorResponse)
        $errorContent = $reader.ReadToEnd()
        Write-Host "Error details: $errorContent" -ForegroundColor Red
    }
}

# Test login
Write-Host "`nTesting Login..." -ForegroundColor Green
try {
    $loginBody = @{
        email = "test@example.com"
        password = "password123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "Login successful!" -ForegroundColor Green
    Write-Host "Token: $($loginResponse.token.Substring(0, 20))..." -ForegroundColor Yellow
    $token = $loginResponse.token
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorResponse = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorResponse)
        $errorContent = $reader.ReadToEnd()
        Write-Host "Error details: $errorContent" -ForegroundColor Red
    }
}

Write-Host "`nAPI testing completed!" -ForegroundColor Cyan
Write-Host "You can now access the driver application form at: http://localhost:8081/driver-application.html" -ForegroundColor Cyan