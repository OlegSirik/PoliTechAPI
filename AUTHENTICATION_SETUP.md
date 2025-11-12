# JWT Authentication & OAuth2 Integration

## 🎯 Features Implemented

### ✅ Complete JWT Authentication System
- **Token Generation**: Secure JWT tokens with configurable expiration
- **Token Validation**: Automatic token validation in all protected endpoints
- **Token Refresh**: Ability to refresh expired tokens
- **Role-based Authorization**: ADMIN, USER, MODERATOR roles with granular permissions

### ✅ Local Identity Provider
- **User Registration**: Create accountEntities with username/email/password
- **Authentication**: Login with credentials
- **Password Management**: Change password functionality
- **Profile Management**: Update user information

### ✅ External Identity Provider Support
- **Google OAuth2**: Full integration with Google accountEntities
- **GitHub OAuth2**: GitHub accountEntity authentication
- **Keycloak Support**: Can be easily added
- **Custom OAuth2**: Extensible for any OAuth2 provider

### ✅ Security Features
- **Password Encryption**: BCrypt password hashing
- **JWT Security**: HMAC-SHA256 signing with configurable secrets
- **Role-based Access Control**: Method-level security annotations
- **Automatic Token Extraction**: From Authorization header or URL parameters

## 🚀 Quick Start

### 1. Start the Application
```bash
docker-compose up -d
```

The authentication system initializes automatically:
- Creates default roles (ADMIN, USER, MODERATOR)
- Sets up database tables via Liquibase migration V4
- Ready for immediate use

### 2. Register Your First User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

### 3. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/loginEntity \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 4. Use Authenticated Endpoints
```bash
# Add the token from loginEntity response to Authorization header
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 🔐 Authentication Endpoints

### Public Endpoints (No Auth Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new local user |
| POST | `/api/auth/loginEntity` | Login with credentials |
| GET | `/oauth2/authorization/google` | Google OAuth2 loginEntity |
| GET | `/oauth2/authorization/github` | GitHub OAuth2 loginEntity |

### Protected Endpoints (Auth Required)
| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| GET | `/api/auth/profile` | Get current user profile | Authenticated |
| PUT | `/api/auth/profile` | Update user profile | Authenticated |
| POST | `/api/auth/change-password` | Change password | Authenticated |
| POST | `/api/auth/refresh` | Refresh JWT token | Authenticated |
| POST | `/api/auth/logout` | Logout (clear context) | Authenticated |

### Role-based Endpoints
| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|--------------|
| GET | `/api/admin/**` | Admin functionality | ADMIN |
| POST | `/api/test/**` | Test data creation | MODERATOR, ADMIN |
| PUT | `/api/test/**` | Test data updates | MODERATOR, ADMIN |
| DELETE | `/api/test/**` | Test data deletion | ADMIN |

## 🌐 OAuth2 External Providers

### Google OAuth2 Setup

1. **Create Google OAuth2 Application**:
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create new project or select existing
   - Enable Google+ API
   - Create OAuth2 credentials
   - Add redirect URI: `http://localhost:8080/oauth2/callback/google`

2. **Configure Environment Variables**:
   ```bash
   export GOOGLE_CLIENT_ID="your-clientEntity-id.apps.googleusercontent.com"
   export GOOGLE_CLIENT_SECRET="your-clientEntity-secret"
   ```

3. **Uncomment Google Configuration** in `application.yml`

4. **Access Google Login**:
   ```
   http://localhost:8080/oauth2/authorization/google
   ```

### GitHub OAuth2 Setup

1. **Create GitHub OAuth App**:
   - Go to [GitHub Settings > Developer settings > OAuth Apps](https://github.com/settings/developers)
   - Create new OAuth App
   - Set Authorization callback URL: `http://localhost:8080/oauth2/callback/github`

2. **Configure Environment Variables**:
   ```bash
   export GITHUB_CLIENT_ID="your-github-clientEntity-id"
   export GITHUB_CLIENT_SECRET="your-github-clientEntity-secret"
   ```

3. **Uncomment GitHub Configuration** in `application.yml`

4. **Access GitHub Login**:
   ```
   http://localhost:8080/oauth2/authorization/github
   ```

### OAuth2 Flow Example

1. User visits: `http://localhost:8080/oauth2/authorization/google`
2. Redirected to Google loginEntity page
3. User authenticates with Google
4. Google redirects back with authorization code
5. System exchanges code for access token
6. System fetches user info from Google
7. Creates or updates local user record
8. Generates JWT token for the user
9. Redirects to: `http://localhost:8080/oauth2/success?token=JWT_TOKEN`

## ⚙️ Configuration

### JWT Configuration (`application.yml`)
```yaml
jwt:
  secret: ${JWT_SECRET:mySecretKey123456789012345678901234567890}
  expiration: ${JWT_EXPIRATION:86400000} # 24 hours in milliseconds
```

**For Production**:
```bash
# Generate strong secret
openssl rand -base64 64

# Set environment variable
export JWT_SECRET="your-super-secure-secret-here"
export JWT_EXPIRATION=3600000  # 1 hour
```

### Database Configuration
Authentication system uses existing PostgreSQL database with new tables:
- `auth_roles`: Application roles
- `auth_users`: User accountEntities (local + OAuth2)
- `auth_user_roles`: User-role associations

## 🗃️ Database Schema

### Tables Created by Migration V4:

```sql
-- Roles table
CREATE TABLE auth_roles (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_seq'),
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP
);

-- Users table  
CREATE TABLE auth_users (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_seq'),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255),  -- NULL for OAuth2 users
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    account_id BIGINT REFERENCES acc_accounts(id),
    provider VARCHAR(20),   -- LOCAL, GOOGLE, GITHUB
    external_id VARCHAR(255),
    provider_data JSONB,
    is_enabled BOOLEAN DEFAULT true,
    is_account_non_expired BOOLEAN DEFAULT true,
    is_account_non_locked BOOLEAN DEFAULT true,
    is_credentials_non_expired BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Role associations
CREATE TABLE auth_user_roles (
    user_id BIGINT REFERENCES auth_users(id),
    role_id BIGINT REFERENCES auth_roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

### Default Roles Created Automatically:
- **ADMIN**: Full system access
- **USER**: Standard user role
- **MODERATOR**: Content management permissions

## 🛠️ Development & Testing

### Test Authentication Flow

1. **Start Application**:
   ```bash
   docker-compose up -d
   ```

2. **Register Test User**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"test","password":"test123","email":"test@example.com"}'
   ```

3. **Login and Test Protected Endpoint**:
   ```bash
   # Login
   TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/loginEntity \
     -H "Content-Type: application/json" \
     -d '{"username":"test","password":"test123"}' | jq -r .token)
   
   # Use token
   curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/auth/profile
   ```

### Frontend Integration

**JavaScript/Axios Example**:
```javascript
// Login and store token
const loginEntity = async (credentials) => {
  const response = await axios.post('/api/auth/loginEntity', credentials);
  localStorage.setItem('token', response.data.token);
}

// Use token in requests
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});
```

**React Example**:
```javascript
// Login component
const Login = () => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  
  const handleLogin = async (credentials) => {
    const response = await fetch('/api/auth/loginEntity', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });
    
    const data = await response.json();
    setToken(data.token);
    localStorage.setItem('token', data.token);
  };

  return token ? <Dashboard token={token} /> : <LoginForm onSubmit={handleLogin} />;
};
```

## 🔒 Security Best Practices

### JWT Security
- ✅ Uses HMAC-SHA256 signing
- ✅ Configurable secret key
- ✅ Token expiration validation
- ✅ Automatic token validation middleware

### Password Security
- ✅ BCrypt hashing (configurable rounds)
- ✅ Password change requires current password
- ✅ No plain text storage

### OAuth2 Security
- ✅ Secure state validation
- ✅ Proper callback URL validation
- ✅ User info caching
- ✅ Provider data isolation

### Additional Recommendations
- Set strong JWT secrets in production
- Use HTTPS for all authentication endpoints
- Implement rate limiting on auth endpoints
- Monitor authentication attempts
- Regular token rotation for high-security applications

## 🚨 Troubleshooting

### Common Issues

**1. Migration Fails - Tables Already Exist**
```sql
-- Drop Liquibase tracking tables to restart
DROP TABLE databasechangelog, databasechangeloglock;
```

**2. JWT Token Expired**
```bash
# Use refresh endpoint
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer OLD_TOKEN"
```

**3. OAuth2 Provider Not Working**
- Check clientEntity ID/secret configuration
- Verify redirect URIs match exactly
- Ensure provider is enabled in application.yml

**4. Permission Denied**
- Verify user has required roles
- Check endpoint security configuration
- Ensure token is valid and not expired

### Debug Mode
Enable debug logging in `application-dev.yml`:
```yaml
logging:
  level:
    liquibase: DEBUG
    ru.pt.security: DEBUG
    org.springframework.security: DEBUG
```

## 📚 Additional Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [JWT.io Debugger](https://jwt.io/)
- [OAuth2 RFC Specification](https://tools.ietf.org/html/rfc6749)
- [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [GitHub OAuth2 Documentation](https://docs.github.com/en/developers/apps/authorizing-oauth-apps)

The authentication system is now fully integrated and ready for production use! 🎉

