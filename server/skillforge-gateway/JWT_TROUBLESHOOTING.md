# JWT Troubleshooting Guide

## 🔍 **Common JWT Issues and Solutions**

### **1. JWT Signature Mismatch Error**

**Error Message:**
```
JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.
```

**Root Cause:**
- Different JWT secrets used for signing (user-service) vs verification (gateway)
- Environment variables not properly set
- Different encoding or key lengths

**Solution:**
1. **Ensure Same JWT Secret Across Services**
   ```bash
   # Set the same secret for all services
   export JWT_SECRET="your-super-secure-jwt-secret-key-here"
   ```

2. **Verify Environment Variables**
   ```bash
   # Check if JWT_SECRET is set
   echo $JWT_SECRET
   
   # Check in each service
   curl http://localhost:8081/actuator/env | grep jwt
   curl http://localhost:8082/actuator/env | grep jwt
   ```

3. **Use Consistent Secret Management**
   - Use environment variables for all environments
   - Avoid hardcoded secrets in application.yml
   - Use Docker secrets or Kubernetes secrets in production


### **2. Verification Steps**

**1. Check JWT Secret Consistency:**
```bash
# Gateway logs should show:
JWT Configuration Validation
JWT Secret Length: 32
Using Default Secret: false
Environment Variable JWT_SECRET: SET

# User service should use the same secret
```

**2. Test Token Generation and Validation:**
```bash
# 1. Login to get a token
curl -X POST http://localhost:8081/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# 2. Use the token for authenticated requests
curl -X GET http://localhost:8081/api/v1/users/profile \
  -H "Authorization: Bearer <token-from-step-1>"
```

**3. Decode JWT Token (for debugging):**
```bash
# Install jwt-cli or use online decoder
# Check token header and payload
echo "<your-jwt-token>" | cut -d'.' -f1 | base64 -d
echo "<your-jwt-token>" | cut -d'.' -f2 | base64 -d
```