# 🚀 Quick Start Guide

Get started with the TDD Task API in 5 minutes!

## Prerequisites

- ☕ Java 17 or higher
- 📦 Maven 3.8+
- 🐳 Docker (for integration tests)

## 1️⃣ Run Tests

```bash
cd tdd

# Run all tests (requires Docker for TestContainers)
mvn clean verify

# Run only unit tests (no Docker needed)
mvn test
```

Expected output:
```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0  (Unit Tests)
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 (Integration Tests)
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0  (Contract Tests)
[INFO] BUILD SUCCESS
```

## 2️⃣ Start the Application

```bash
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

## 3️⃣ Test the API

### Create a Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn TDD",
    "description": "Master Test-Driven Development with Spring Boot"
  }'
```

### Get All Tasks
```bash
curl http://localhost:8080/api/tasks
```

### Get Task by ID
```bash
curl http://localhost:8080/api/tasks/1
```

### Update Task
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn TDD - Completed!",
    "status": "DONE"
  }'
```

### Delete Task
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

## 📁 Project Structure

```
tdd/
├── src/
│   ├── main/java/             # Production code (written AFTER tests)
│   │   ├── controller/        # REST endpoints
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Data access
│   │   └── model/             # Entities & DTOs
│   └── test/java/             # Tests (written FIRST!)
│       ├── service/           # Unit tests
│       ├── repository/        # Integration tests
│       └── controller/        # API tests
├── README.md                  # Full documentation
├── TDD_WORKFLOW_GUIDE.md     # Step-by-step TDD guide
└── pom.xml                    # Dependencies
```

## 🧪 Test Types

| Type | Files | Speed | Purpose |
|------|-------|-------|---------|
| Unit | `*Test.java` | < 1s | Test business logic |
| Integration | `*IntegrationTest.java` | ~15s | Test with real DB |
| Contract | `contracts/*.groovy` | ~5s | API contract verification |

## 📚 Next Steps

1. Read `README.md` for full documentation
2. Study `TDD_WORKFLOW_GUIDE.md` to learn TDD workflow
3. Explore test files to see TDD in action
4. Try modifying code and watch tests catch issues!

## 🎯 TDD Workflow

```
1. 🔴 RED:   Write failing test
2. 🟢 GREEN: Make it pass (minimal code)
3. ♻️  REFACTOR: Improve code
4. 🔁 REPEAT!
```

## ❓ Troubleshooting

### Tests fail with "Cannot connect to Docker"
- Start Docker Desktop
- Or run only unit tests: `mvn test` (no Docker needed)

### Build fails with "Java version"
- Ensure Java 17+ is installed: `java -version`

### Port 8080 already in use
- Stop other applications on port 8080
- Or change port in `application.yml`

## 🎓 Learning Path

**Beginner**: Start with unit tests in `TaskServiceTest.java`
**Intermediate**: Explore integration tests with TestContainers
**Advanced**: Study contract tests for microservices

---

**Happy Coding! 🎉**
