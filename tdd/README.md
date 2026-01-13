# Task Management API - TDD Workflow Demo

A comprehensive Spring Boot REST API demonstrating **Test-Driven Development (TDD)** with three types of tests:

1. **Unit Tests** (JUnit 5 + Mockito)
2. **Integration Tests** (TestContainers + PostgreSQL)
3. **Contract Tests** (Spring Cloud Contract)

## 🎯 TDD Workflow (Red-Green-Refactor)

This project demonstrates the complete TDD cycle:

### 🔴 **RED Phase**: Write Failing Tests First

**Location**: `src/test/java/com/example/taskapi/service/TaskServiceTest.java`

We start by writing tests BEFORE implementing any code:

```java
@Test
@DisplayName("Should create a new task successfully")
void shouldCreateTaskSuccessfully() {
    // Given
    when(taskRepository.save(any(Task.class))).thenReturn(testTask);

    // When
    TaskResponse response = taskService.createTask(taskRequest);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getTitle()).isEqualTo("Test Task");
    // ... more assertions
}
```

**At this point, tests FAIL** because `TaskService` doesn't exist yet!

### 🟢 **GREEN Phase**: Make Tests Pass

**Location**: `src/main/java/com/example/taskapi/service/TaskService.java`

Now we implement MINIMAL code to make tests pass:

```java
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskResponse createTask(TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }
    // ... other methods
}
```

**Now all tests PASS!** ✅

### ♻️ **REFACTOR Phase**: Improve Code Quality

After tests pass, we refactor:
- Extract common logic
- Improve naming
- Add logging
- Optimize performance
- **Tests ensure we don't break anything!**

## 📋 Project Structure

```
tdd/
├── src/
│   ├── main/
│   │   ├── java/com/example/taskapi/
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── service/         # Business Logic (written AFTER tests)
│   │   │   ├── repository/      # Data Access Layer
│   │   │   ├── model/           # JPA Entities
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── config/          # Configuration Classes
│   │   │   └── TaskApiApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/com/example/taskapi/
│       │   ├── service/         # 🧪 Unit Tests (Mockito)
│       │   ├── repository/      # 🐳 Integration Tests (TestContainers)
│       │   ├── controller/      # 🌐 API Integration Tests
│       │   └── ContractTestBase.java  # 📝 Contract Test Base
│       └── resources/
│           ├── contracts/       # 📜 Contract Definitions (Groovy DSL)
│           └── application-test.yml
└── pom.xml
```

## 🧪 Three Types of Testing

### 1️⃣ Unit Tests (Fast, Isolated)

**Technology**: JUnit 5 + Mockito + AssertJ

**Purpose**: Test business logic in isolation

**Example**: `TaskServiceTest.java`

```bash
# Run only unit tests
mvn test -Dtest=*Test
```

**Key Features**:
- Mock all dependencies
- Test single unit of code
- Very fast execution (< 1 second)
- No database or external services

**Test Coverage**:
- ✅ Create task
- ✅ Get all tasks
- ✅ Get task by ID
- ✅ Update task
- ✅ Delete task
- ✅ Find tasks by status
- ✅ Exception handling

### 2️⃣ Integration Tests (Real Database)

**Technology**: Spring Boot Test + TestContainers + PostgreSQL

**Purpose**: Test with real database in Docker container

**Example**: `TaskRepositoryIntegrationTest.java`, `TaskControllerIntegrationTest.java`

```bash
# Run integration tests (requires Docker)
mvn verify
```

**Key Features**:
- Real PostgreSQL database in Docker
- Automatic container lifecycle management
- Test database operations end-to-end
- Test complete REST API

**TestContainers Setup**:
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
```

**Test Coverage**:
- ✅ Database CRUD operations
- ✅ JPA entity relationships
- ✅ Custom repository queries
- ✅ Full HTTP request/response cycle
- ✅ Validation and error handling

### 3️⃣ Contract Tests (API Contracts)

**Technology**: Spring Cloud Contract

**Purpose**: Verify API contract between producer (this API) and consumers

**Example**: `src/test/resources/contracts/*.groovy`

```bash
# Generate and run contract tests
mvn clean test
```

**Contract Definition Example**:
```groovy
Contract.make {
    description "Should create a new task"

    request {
        method POST()
        url("/api/tasks")
        body([
            title: "New Task",
            description: "New Description"
        ])
    }

    response {
        status 201
        body([
            id: $(consumer(1), producer(regex(number()))),
            title: "New Task",
            status: "TODO"
        ])
    }
}
```

**Test Coverage**:
- ✅ GET /api/tasks - Get all tasks
- ✅ POST /api/tasks - Create task
- ✅ GET /api/tasks/{id} - Get task by ID
- ✅ 404 error response contract

## 🚀 Running the Application

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker (for TestContainers)

### Build and Run Tests

```bash
# Run all tests
cd tdd
mvn clean verify

# Run only unit tests (fast)
mvn test

# Run with specific test
mvn test -Dtest=TaskServiceTest

# Run integration tests
mvn verify -Dtest=*IntegrationTest

# Skip tests
mvn clean install -DskipTests
```

### Run Application

```bash
# Start the application
mvn spring-boot:run

# Application will be available at:
# http://localhost:8080
```

## 📚 API Endpoints

### Tasks API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create new task |
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks?status=TODO` | Get tasks by status |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

### Example Requests

**Create Task**:
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn TDD",
    "description": "Master Test-Driven Development",
    "status": "TODO"
  }'
```

**Get All Tasks**:
```bash
curl http://localhost:8080/api/tasks
```

**Get Tasks by Status**:
```bash
curl http://localhost:8080/api/tasks?status=TODO
```

**Update Task**:
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn TDD - Updated",
    "description": "Master Test-Driven Development with Spring Boot",
    "status": "IN_PROGRESS"
  }'
```

**Delete Task**:
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

## 🎓 TDD Best Practices Demonstrated

### ✅ **Write Tests First**
- All features start with failing tests
- Tests define the expected behavior
- Code is written to satisfy tests

### ✅ **Small Steps**
- One test at a time
- Minimal implementation
- Frequent test runs

### ✅ **Three Laws of TDD** (Robert C. Martin)

1. **Don't write production code** until you have a failing test
2. **Don't write more of a test** than is sufficient to fail
3. **Don't write more production code** than is sufficient to pass the test

### ✅ **Test Coverage**
- Unit tests for business logic
- Integration tests for data layer
- API tests for controllers
- Contract tests for API contracts

### ✅ **Fast Feedback Loop**
```
Write Test → Run Test (FAIL) → Write Code → Run Test (PASS) → Refactor → Repeat
   🔴            RED              🟢          GREEN            ♻️
```

## 📊 Test Execution Time

- **Unit Tests**: ~2 seconds (no external dependencies)
- **Integration Tests**: ~15 seconds (includes Docker container startup)
- **Contract Tests**: ~5 seconds (mock-based)
- **Total**: ~22 seconds for full test suite

## 🛠️ Technologies Used

| Category | Technology |
|----------|------------|
| Framework | Spring Boot 3.2.0 |
| Language | Java 17 |
| Build Tool | Maven |
| Database (Dev) | H2 (in-memory) |
| Database (Test) | PostgreSQL (TestContainers) |
| Unit Testing | JUnit 5, Mockito, AssertJ |
| Integration Testing | TestContainers |
| Contract Testing | Spring Cloud Contract |
| REST Testing | MockMvc, REST Assured |
| Validation | Jakarta Validation |
| Utilities | Lombok |

## 📖 Learning Outcomes

By studying this project, you will learn:

1. ✅ **TDD Fundamentals**: Red-Green-Refactor cycle
2. ✅ **Unit Testing**: Mocking with Mockito, assertions with AssertJ
3. ✅ **Integration Testing**: TestContainers for real database tests
4. ✅ **Contract Testing**: API contract verification with Spring Cloud Contract
5. ✅ **Spring Boot Testing**: @SpringBootTest, @DataJpaTest, @WebMvcTest
6. ✅ **REST API Testing**: MockMvc and REST Assured
7. ✅ **Test Organization**: Naming, structure, and best practices
8. ✅ **CI/CD Ready**: All tests automated and fast

## 🎯 Key Takeaways

### Why TDD?

1. **Better Design**: Tests force you to think about API design first
2. **Confidence**: Refactor without fear of breaking things
3. **Documentation**: Tests serve as living documentation
4. **Fewer Bugs**: Catch issues early in development
5. **Faster Development**: Less debugging time in the long run

### Test Pyramid

```
        /\
       /  \      Few, slow, expensive
      /____\     Contract Tests
     /      \
    /        \   Medium, integration
   /__________\  Integration Tests
  /            \
 /              \ Many, fast, cheap
/________________\ Unit Tests
```

This project follows the test pyramid:
- **Many unit tests** (fast, isolated)
- **Some integration tests** (medium speed, real database)
- **Few contract tests** (verify API contracts)

## 🔍 Code Quality

- ✅ Clean code principles
- ✅ SOLID principles
- ✅ Proper layering (Controller → Service → Repository)
- ✅ DTO pattern for API contracts
- ✅ Global exception handling
- ✅ Validation at API boundary
- ✅ Logging for observability

## 📝 License

This is a demonstration project for learning TDD with Spring Boot.

---

**Happy Testing! 🚀**

Remember: *"The code that is not tested is broken by design."* - TDD Philosophy
