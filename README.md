# Spring Security Authentication Examples

A comprehensive demonstration project showcasing various Spring Security authentication mechanisms. This project serves as learning material for developers looking to understand and implement different authentication strategies in Spring Boot applications.

## 📋 Table of Contents

- [Overview](#overview)
- [Authentication Mechanisms](#authentication-mechanisms)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Demo Users](#demo-users)
- [Authentication Examples](#authentication-examples)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [References](#references)

## 🎯 Overview

This project demonstrates **8 different Spring Security authentication mechanisms** based on the official [Spring Security Documentation](https://docs.spring.io/spring-security/reference/servlet/authentication/index.html):

1. **Username and Password** - Traditional form-based authentication
2. **OAuth 2.0 Login** - Social login with GitHub and Google
3. **SAML 2.0 Login** - Enterprise SSO with SAML
4. **CAS** - Central Authentication Server integration
5. **Remember Me** - Persistent authentication across sessions
6. **JAAS** - Java Authentication and Authorization Service
7. **Pre-Authentication** - External authentication (SiteMinder, Java EE)
8. **X.509** - Client certificate authentication

Each authentication mechanism is implemented in a separate package under `com.example.security.authentication` with detailed documentation and examples.

## 🔐 Authentication Mechanisms

### 1. Username and Password Authentication

**Status:** ✅ Enabled by default

Traditional form-based authentication with credentials stored in an H2 database.

**Features:**
- BCrypt password encoding
- Database-backed user storage
- Role-based access control (RBAC)
- Custom UserDetailsService implementation

**Endpoints:**
- Login: `http://localhost:8080/basic/login`
- Home: `http://localhost:8080/basic/home`
- Admin: `http://localhost:8080/basic/admin` (ADMIN role required)

**Reference:** [Spring Security - Username/Password](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html)

### 2. OAuth 2.0 Login

**Status:** ✅ Enabled (requires configuration)

Social login integration with GitHub and Google using OAuth 2.0 and OpenID Connect.

**Supported Providers:**
- GitHub OAuth 2.0
- Google OpenID Connect

**Setup:**

1. **GitHub:**
   - Go to [GitHub Developer Settings](https://github.com/settings/developers)
   - Create a new OAuth App
   - Set Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
   - Set environment variables:
     ```bash
     export GITHUB_CLIENT_ID=your-client-id
     export GITHUB_CLIENT_SECRET=your-client-secret
     ```

2. **Google:**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create OAuth 2.0 credentials
   - Set Authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
   - Set environment variables:
     ```bash
     export GOOGLE_CLIENT_ID=your-client-id
     export GOOGLE_CLIENT_SECRET=your-client-secret
     ```

**Endpoints:**
- GitHub Login: `http://localhost:8080/oauth2/authorization/github`
- Google Login: `http://localhost:8080/oauth2/authorization/google`
- Home: `http://localhost:8080/oauth2/home`

**Reference:** [Spring Security - OAuth 2.0 Login](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/index.html)

### 3. SAML 2.0 Login

**Status:** ❌ Disabled by default (requires external IdP)

Enterprise Single Sign-On (SSO) using SAML 2.0 protocol.

**Setup:**

1. Generate Service Provider certificates:
   ```bash
   keytool -genkeypair -alias spring-saml -keyalg RSA -keysize 2048 \
           -storetype PKCS12 -keystore saml-keystore.p12 -validity 3650
   ```

2. Configure SAML IdP metadata URL in `application.yml`

3. Register SP metadata with your IdP:
   - Metadata URL: `http://localhost:8080/saml2/service-provider-metadata/example-saml`

4. Enable in `application.yml`:
   ```yaml
   auth:
     examples:
       saml:
         enabled: true
   ```

**Reference:** [Spring Security - SAML 2.0](https://docs.spring.io/spring-security/reference/servlet/saml2/login/index.html)

### 4. CAS (Central Authentication Server)

**Status:** ❌ Disabled by default (requires CAS server)

Integration with Apereo CAS for centralized authentication.

**Setup:**

1. Install and configure a CAS server: [Apereo CAS](https://apereo.github.io/cas/)

2. Set environment variables:
   ```bash
   export CAS_SERVER_URL=https://your-cas-server.com/cas
   export CAS_SERVICE_URL=http://localhost:8080
   ```

3. Enable in `application.yml`:
   ```yaml
   auth:
     examples:
       cas:
         enabled: true
   ```

**Reference:** [Spring Security - CAS](https://docs.spring.io/spring-security/reference/servlet/authentication/cas.html)

### 5. Remember Me

**Status:** ✅ Enabled by default

Persistent authentication that survives session expiration using secure tokens stored in the database.

**Features:**
- Persistent token repository (database-backed)
- Configurable token validity (default: 24 hours)
- Secure cookie management
- Automatic cleanup of expired tokens

**How it works:**
1. User logs in with "Remember Me" checkbox enabled
2. A persistent token is generated and stored in the database
3. Token is sent to browser as a secure cookie
4. On subsequent visits, the cookie authenticates the user automatically
5. Works even after browser restart or session timeout

**Endpoints:**
- Login: `http://localhost:8080/rememberme/login`
- Home: `http://localhost:8080/rememberme/home`

**Reference:** [Spring Security - Remember Me](https://docs.spring.io/spring-security/reference/servlet/authentication/rememberme.html)

### 6. JAAS Authentication

**Status:** ❌ Disabled by default (legacy)

Integration with Java Authentication and Authorization Service (JAAS).

**Note:** JAAS is a legacy authentication mechanism. Modern applications should prefer other authentication methods.

**Setup:**

1. Create JAAS configuration file (`jaas.config`)

2. Enable in `application.yml`:
   ```yaml
   auth:
     examples:
       jaas:
         enabled: true
   ```

**Reference:** [Spring Security - JAAS](https://docs.spring.io/spring-security/reference/servlet/authentication/jaas.html)

### 7. Pre-Authentication Scenarios

**Status:** ❌ Disabled by default

Integration with external authentication systems (SiteMinder, Java EE Security, reverse proxies).

**Use Cases:**
- Enterprise SSO systems (CA SiteMinder, Oracle Access Manager)
- Java EE container-managed security
- Reverse proxy authentication (Apache, Nginx)

**How it works:**
- External system authenticates the user
- External system sets authentication header (e.g., `SM_USER`, `REMOTE_USER`)
- Spring Security trusts the header and handles authorization

**Setup:**

1. Configure external authentication system to set header

2. Enable in `application.yml`:
   ```yaml
   auth:
     examples:
       preauth:
         enabled: true
   ```

**Reference:** [Spring Security - Pre-Authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/preauth.html)

### 8. X.509 Certificate Authentication

**Status:** ❌ Disabled by default (requires SSL)

Client certificate-based authentication using SSL/TLS mutual authentication.

**Setup:**

1. Generate server keystore:
   ```bash
   keytool -genkeypair -alias server -keyalg RSA -keysize 2048 \
           -storetype PKCS12 -keystore server-keystore.p12 -validity 3650
   ```

2. Generate client certificate:
   ```bash
   keytool -genkeypair -alias client -keyalg RSA -keysize 2048 \
           -storetype PKCS12 -keystore client-keystore.p12 -validity 3650
   ```

3. Export and import certificates:
   ```bash
   # Export client certificate
   keytool -exportcert -alias client -keystore client-keystore.p12 \
           -storetype PKCS12 -file client.cer

   # Import into server truststore
   keytool -importcert -alias client -file client.cer \
           -keystore server-truststore.p12 -storetype PKCS12
   ```

4. Enable SSL in `application.yml`:
   ```yaml
   server:
     ssl:
       enabled: true
       key-store: classpath:server-keystore.p12
       key-store-password: password
       key-store-type: PKCS12
       client-auth: need

   auth:
     examples:
       x509:
         enabled: true
   ```

**Reference:** [Spring Security - X.509](https://docs.spring.io/spring-security/reference/servlet/authentication/x509.html)

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+** or Gradle
- **Git** (optional)
- OAuth provider credentials (optional, for OAuth 2.0 examples)

## 🚀 Getting Started

### Clone the Repository

```bash
git clone https://github.com/wathsala-vishwajith/spring-boot.git
cd spring-boot
git checkout claude/spring-security-auth-examples-01LQY15N8SDgUWnNYA6LJuBf
cd authenticate
```

### Build the Project

```bash
./mvnw clean install
```

Or if using Windows:

```bash
mvnw.cmd clean install
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 👥 Demo Users

The application comes pre-configured with demo users stored in the H2 in-memory database:

| Username | Password | Roles |
|----------|----------|-------|
| admin | password | ROLE_ADMIN, ROLE_USER |
| user | password | ROLE_USER |
| john | password | ROLE_USER |
| jane | password | ROLE_USER, ROLE_MANAGER |
| bob | password | ROLE_USER |

**Note:** All passwords are BCrypt encoded. The plaintext password for all users is `password`.

### H2 Database Console

Access the H2 console at: `http://localhost:8080/h2-console`

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:authdb`
- Username: `sa`
- Password: `password`

## 🧪 Authentication Examples

### Example 1: Basic Username/Password Login

1. Navigate to `http://localhost:8080`
2. Click on "Username & Password"
3. Login with `admin` / `password`
4. Try accessing the admin panel (only accessible with ADMIN role)

### Example 2: OAuth 2.0 Login

1. Configure GitHub/Google OAuth credentials (see OAuth 2.0 section)
2. Navigate to `http://localhost:8080/basic/login`
3. Click "Login with GitHub" or "Login with Google"
4. Authorize the application
5. View your OAuth user information

### Example 3: Remember Me

1. Navigate to `http://localhost:8080/rememberme/login`
2. Login with `user` / `password`
3. Check the "Remember Me" checkbox
4. Close your browser completely
5. Open browser and return to `http://localhost:8080/rememberme/home`
6. You should still be logged in!

## 📁 Project Structure

```
spring-boot/
└── authenticate/                       # Main project directory
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/example/security/
        │   │   ├── SpringSecurityAuthExamplesApplication.java
        │   │   ├── model/
        │   │   │   └── User.java
        │   │   ├── repository/
        │   │   │   └── UserRepository.java
        │   │   ├── config/
        │   │   │   ├── SecurityConfig.java
        │   │   │   └── MainController.java
        │   │   └── authentication/
        │   │       ├── basic/              # Username/Password
        │   │       ├── oauth2/             # OAuth 2.0 Login
        │   │       ├── saml/               # SAML 2.0
        │   │       ├── cas/                # CAS
        │   │       ├── rememberme/         # Remember Me
        │   │       ├── jaas/               # JAAS
        │   │       ├── preauth/            # Pre-Authentication
        │   │       └── x509/               # X.509
        │   └── resources/
        │       ├── application.yml
        │       ├── data.sql
        │       └── templates/
        │           ├── index.html
        │           ├── basic/
        │           ├── oauth2/
        │           ├── rememberme/
        │           └── ...
        └── test/
```

## ⚙️ Configuration

All configuration is centralized in `authenticate/src/main/resources/application.yml`.

### Enable/Disable Authentication Methods

```yaml
auth:
  examples:
    basic:
      enabled: true          # Username/Password
    oauth2:
      enabled: true          # OAuth 2.0
    saml:
      enabled: false         # SAML 2.0
    cas:
      enabled: false         # CAS
    rememberme:
      enabled: true          # Remember Me
    jaas:
      enabled: false         # JAAS
    preauth:
      enabled: false         # Pre-Auth
    x509:
      enabled: false         # X.509
```

### Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:authdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
```

### Remember Me Configuration

```yaml
auth:
  examples:
    rememberme:
      enabled: true
      key: mySecretRememberMeKey
      token-validity-seconds: 86400  # 24 hours
```

## 🧪 Testing

### Run Unit Tests

Navigate to the authenticate directory first:

```bash
cd authenticate
./mvnw test
```

### Manual Testing Checklist

- [ ] Basic login works with demo users
- [ ] Admin panel accessible only to admin user
- [ ] OAuth 2.0 login works with GitHub
- [ ] OAuth 2.0 login works with Google
- [ ] Remember Me persists across browser sessions
- [ ] Logout clears authentication
- [ ] H2 console is accessible

## 📚 References

- [Spring Security Reference Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Security Authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/index.html)
- [Spring Boot Security Auto-Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.security)
- [OAuth 2.0 Authorization Framework](https://oauth.net/2/)
- [SAML 2.0 Specification](http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-tech-overview-2.0.html)
- [Apereo CAS](https://apereo.github.io/cas/)

## 📝 License

This project is licensed under the terms specified in the LICENSE file.

## 🤝 Contributing

This is an educational example project. Feel free to fork and modify for your own learning purposes.

## 📧 Support

For questions or issues, please refer to the [Spring Security documentation](https://docs.spring.io/spring-security/reference/index.html) or create an issue in the repository.

---

**Note:** This is a demonstration project for learning purposes. Production deployments require additional security hardening, proper secret management, and compliance with security best practices.
