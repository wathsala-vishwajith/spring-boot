# 🎯 TDD Workflow: Step-by-Step Guide

This document walks through the **exact TDD process** used to build the Task API.

## Table of Contents
1. [TDD Fundamentals](#tdd-fundamentals)
2. [The Red-Green-Refactor Cycle](#the-red-green-refactor-cycle)
3. [Step-by-Step Workflow](#step-by-step-workflow)
4. [Test Types Explained](#test-types-explained)
5. [Running Tests](#running-tests)

---

## 🎓 TDD Fundamentals

### What is TDD?

**Test-Driven Development (TDD)** is a software development approach where you:
1. Write a **failing test** first (RED)
2. Write **minimal code** to make it pass (GREEN)
3. **Refactor** the code while keeping tests passing (REFACTOR)

### Why TDD?

✅ **Better Design**: Forces you to think about interfaces before implementation
✅ **Living Documentation**: Tests document how code should behave
✅ **Confidence**: Refactor without fear
✅ **Fewer Bugs**: Catch issues early
✅ **Faster Development**: Less debugging time

### The Three Laws of TDD (Robert C. Martin)

1. **Don't write production code** until you have a failing test
2. **Don't write more of a test** than is sufficient to fail
3. **Don't write more production code** than is sufficient to pass the test

---

## 🔴🟢♻️ The Red-Green-Refactor Cycle

```
┌─────────────────────────────────────────┐
│                                         │
│  1. RED: Write a Failing Test          │
│     ↓                                   │
│  2. GREEN: Make It Pass (Minimal Code) │
│     ↓                                   │
│  3. REFACTOR: Improve Code             │
│     ↓                                   │
└─────┘ (Repeat)                          │
```

### 🔴 RED Phase
- Write a test for the next bit of functionality
- Run the test and watch it **FAIL** (no implementation yet)
- This proves the test can fail!

### 🟢 GREEN Phase
- Write the **simplest code** to make the test pass
- Don't worry about perfection
- Just make it work!

### ♻️ REFACTOR Phase
- Clean up the code
- Remove duplication
- Improve naming
- Optimize performance
- **Tests ensure nothing breaks!**

---

## 📝 Step-by-Step Workflow

### Example: Implementing "Create Task" Feature

Let's walk through creating the "Create Task" functionality using TDD.

---

### STEP 1: 🔴 RED - Write Failing Test First

**File**: `src/test/java/com/example/taskapi/service/TaskServiceTest.java`

```java
@Test
@DisplayName("Should create a new task successfully")
void shouldCreateTaskSuccessfully() {
    // Given
    TaskRequest request = TaskRequest.builder()
            .title("Test Task")
            .description("Test Description")
            .build();

    Task savedTask = Task.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .status(TaskStatus.TODO)
            .build();

    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    // When
    TaskResponse response = taskService.createTask(request);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTitle()).isEqualTo("Test Task");
    assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);

    verify(taskRepository, times(1)).save(any(Task.class));
}
```

**Run Test**:
```bash
mvn test -Dtest=TaskServiceTest#shouldCreateTaskSuccessfully
```

**Result**: ❌ **TEST FAILS** - `TaskService` class doesn't exist!

---

### STEP 2: 🟢 GREEN - Write Minimal Implementation

Now we write **just enough code** to make the test pass.

**File**: `src/main/java/com/example/taskapi/service/TaskService.java`

```java
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponse createTask(TaskRequest request) {
        // Minimal implementation to pass the test
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .build();

        Task savedTask = taskRepository.save(task);

        return TaskResponse.builder()
                .id(savedTask.getId())
                .title(savedTask.getTitle())
                .description(savedTask.getDescription())
                .status(savedTask.getStatus())
                .createdAt(savedTask.getCreatedAt())
                .updatedAt(savedTask.getUpdatedAt())
                .build();
    }
}
```

**Run Test Again**:
```bash
mvn test -Dtest=TaskServiceTest#shouldCreateTaskSuccessfully
```

**Result**: ✅ **TEST PASSES!**

---

### STEP 3: ♻️ REFACTOR - Improve Code

Now that tests pass, we can refactor safely.

**Refactoring**:
1. Extract mapping logic to a separate method
2. Add logging
3. Handle null status

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponse createTask(TaskRequest request) {
        log.debug("Creating new task with title: {}", request.getTitle());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());

        return mapToResponse(savedTask);  // Extracted method
    }

    // Extracted mapping logic (DRY principle)
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
```

**Run Test Again**:
```bash
mvn test -Dtest=TaskServiceTest#shouldCreateTaskSuccessfully
```

**Result**: ✅ **TEST STILL PASSES!**

---

### STEP 4: 🔁 Repeat for Next Feature

Now repeat the cycle for the next feature (e.g., "Get Task by ID"):

1. **RED**: Write test for `getTaskById()`
2. **GREEN**: Implement minimal code
3. **REFACTOR**: Improve code quality

---

## 🧪 Test Types Explained

### 1️⃣ Unit Tests (Fastest)

**Purpose**: Test business logic in isolation
**Speed**: < 1 second
**Dependencies**: All mocked (Mockito)

**Example**:
```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;  // Mocked dependency

    @InjectMocks
    private TaskService taskService;  // Class under test

    @Test
    void shouldCreateTask() {
        // Arrange: Mock repository behavior
        when(taskRepository.save(any())).thenReturn(mockTask);

        // Act: Call service method
        TaskResponse result = taskService.createTask(request);

        // Assert: Verify behavior
        assertThat(result).isNotNull();
        verify(taskRepository).save(any());
    }
}
```

**Run**:
```bash
mvn test -Dtest=*Test
```

---

### 2️⃣ Integration Tests (Medium)

**Purpose**: Test with real database
**Speed**: ~15 seconds (Docker container startup)
**Dependencies**: Real PostgreSQL via TestContainers

**Example**:
```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveTask() {
        // Act: Save to REAL database
        Task saved = taskRepository.save(task);

        // Assert: Verify saved to database
        assertThat(saved.getId()).isNotNull();
    }
}
```

**Run**:
```bash
mvn verify -Dtest=*IntegrationTest
```

---

### 3️⃣ Contract Tests (API Contracts)

**Purpose**: Verify API contracts between services
**Speed**: ~5 seconds
**Technology**: Spring Cloud Contract

**Example Contract** (`contracts/shouldCreateTask.groovy`):
```groovy
Contract.make {
    request {
        method POST()
        url("/api/tasks")
        body([
            title: "New Task",
            description: "Description"
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

**Run**:
```bash
mvn clean test
```

---

## 🚀 Running Tests

### Run All Tests
```bash
cd tdd
mvn clean verify
```

### Run Only Unit Tests (Fast)
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=TaskServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=TaskServiceTest#shouldCreateTaskSuccessfully
```

### Run Integration Tests Only
```bash
mvn verify -Dtest=*IntegrationTest
```

### Run with Coverage
```bash
mvn clean verify jacoco:report
# Report: target/site/jacoco/index.html
```

### Skip Tests (Not Recommended!)
```bash
mvn clean install -DskipTests
```

---

## 📊 TDD Workflow Timeline

Here's the actual order of development in this project:

### Phase 1: Setup (5 minutes)
1. Create project structure
2. Add dependencies to `pom.xml`
3. Configure `application.yml`

### Phase 2: Unit Tests - RED Phase (10 minutes)
1. ✏️ Write `TaskServiceTest.java` (tests FAIL ❌)
2. Create model classes: `Task`, `TaskStatus`
3. Create DTOs: `TaskRequest`, `TaskResponse`
4. Create `TaskRepository` interface

### Phase 3: Unit Tests - GREEN Phase (10 minutes)
1. ✏️ Implement `TaskService.java`
2. Run tests → All PASS ✅

### Phase 4: Integration Tests - RED Phase (15 minutes)
1. ✏️ Write `TaskRepositoryIntegrationTest.java`
2. ✏️ Write `TaskControllerIntegrationTest.java`
3. Create `TaskController.java`
4. Create `GlobalExceptionHandler.java`

### Phase 5: Integration Tests - GREEN Phase (5 minutes)
1. Verify all integration tests PASS ✅

### Phase 6: Contract Tests (10 minutes)
1. ✏️ Write contract definitions (Groovy DSL)
2. Create `ContractTestBase.java`
3. Verify contract tests PASS ✅

### Phase 7: Refactor (10 minutes)
1. Add logging
2. Extract common methods
3. Improve error messages
4. Add documentation
5. All tests still PASS ✅

**Total Time**: ~65 minutes

---

## 🎯 TDD Best Practices

### ✅ DO

- Write tests first, always
- Keep tests simple and readable
- Test one thing at a time
- Use descriptive test names
- Run tests frequently
- Refactor with confidence
- Mock external dependencies in unit tests
- Use real dependencies in integration tests

### ❌ DON'T

- Write code before tests
- Test multiple things in one test
- Skip the refactor phase
- Ignore failing tests
- Write tests after implementation
- Over-engineer solutions
- Test implementation details

---

## 📈 Test Pyramid

```
        /\           Contract Tests
       /  \          (Few, slow, verify API contracts)
      /____\
     /      \        Integration Tests
    /        \       (Some, medium speed, real DB)
   /__________\
  /            \     Unit Tests
 /              \    (Many, fast, isolated)
/________________\
```

**This project follows the pyramid**:
- 8 Unit Tests (fast, isolated)
- 10 Integration Tests (medium, real DB)
- 4 Contract Tests (API contracts)

---

## 🎓 Key Learnings

### 1. Tests Drive Design
When you write tests first, you naturally create better APIs because you think from the consumer's perspective.

### 2. Refactoring is Safe
With comprehensive tests, you can refactor confidently. If tests pass, behavior is preserved.

### 3. Documentation for Free
Tests serve as executable documentation showing how to use your code.

### 4. Fast Feedback
TDD provides immediate feedback. You know within seconds if your code works.

### 5. Less Debugging
Catching bugs during development is faster than debugging in production.

---

## 🔍 Example TDD Session

```bash
# 1. RED: Write failing test
vim TaskServiceTest.java  # Add test
mvn test                  # ❌ FAILS

# 2. GREEN: Make it pass
vim TaskService.java      # Implement
mvn test                  # ✅ PASSES

# 3. REFACTOR: Improve code
vim TaskService.java      # Refactor
mvn test                  # ✅ STILL PASSES

# 4. Repeat for next feature
```

---

## 📚 Further Reading

- **Kent Beck**: "Test Driven Development: By Example"
- **Robert C. Martin**: "Clean Code" (Chapter on Unit Tests)
- **Martin Fowler**: "Refactoring" and "Is TDD Dead?" series
- **Spring Boot Testing Documentation**: https://spring.io/guides/gs/testing-web/

---

## 🎉 Conclusion

TDD is not just about testing - it's about:
- **Design**: Better APIs and architecture
- **Confidence**: Refactor without fear
- **Speed**: Faster development in the long run
- **Quality**: Fewer bugs and regressions

**Remember**: The goal is not 100% coverage, but thoughtful tests that give you confidence to make changes.

---

**Happy TDD! 🚀**

*"Test-first code is code that's designed to be testable, which usually means it's also designed to be usable."* - Kent Beck
