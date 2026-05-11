# Groovy Test Suite - Quick Reference Guide

## 📋 Complete List of Test Files (14 Total)

### Controllers (2)
1. ✅ `UserControllerSpec.groovy` - 11 test cases
2. ✅ `AssetModelControllerSpec.groovy` - 10 test cases

### Services (2)
3. ✅ `FileStorageServiceSpec.groovy` - 9 test cases
4. ✅ `CustomUserDetailsServiceSpec.groovy` - 8 test cases

### Configuration (2)
5. ✅ `SecurityConfigSpec.groovy` - 11 test cases
6. ✅ `WebConfigSpec.groovy` - 7 test cases

### Repositories (2)
7. ✅ `UserRepositorySpec.groovy` - 12 test cases
8. ✅ `AssetModelRepositorySpec.groovy` - 11 test cases

### Models (2)
9. ✅ `UserSpec.groovy` - 9 test cases
10. ✅ `AssetModelSpec.groovy` - 17 test cases

### DTOs (2)
11. ✅ `UserRegistrationDtoSpec.groovy` - 10 test cases
12. ✅ `AssetUploadDtoSpec.groovy` - 17 test cases

### Application & Integration (2)
13. ✅ `MainApplicationSpec.groovy` - 15 test cases
14. ✅ `AssetModelIntegrationSpec.groovy` - 6 integration scenarios

**Total: 154+ Test Cases**

---

## 🚀 Quick Start

### Prerequisites
```bash
# Navigate to project
cd D:\Website1.0\T3Dmain\main

# Sync gradle (if needed)
./gradlew build --refresh-dependencies
```

### Run Tests
```bash
# Run all tests
./gradlew test

# Run tests with detailed output
./gradlew test --info

# Run tests with scan
./gradlew test --scan
```

### View Results
```bash
# After running tests, open in browser:
build/reports/tests/test/index.html
```

---

## 🎯 Common Test Commands

### Run Specific Test File
```bash
# Run UserControllerSpec only
./gradlew test --tests UserControllerSpec

# Run all controller tests
./gradlew test --tests *ControllerSpec

# Run all service tests
./gradlew test --tests *ServiceSpec

# Run all repository tests
./gradlew test --tests *RepositorySpec
```

### Run Specific Test Method
```bash
# Run specific test
./gradlew test --tests UserControllerSpec.should*successfully*register*

# Run tests matching pattern
./gradlew test --tests *Repository*
```

### Watch Mode (continuous testing)
```bash
# Run tests continuously on file changes
./gradlew test --continuous
```

### Multiple Test Runs
```bash
# Run tests multiple times
./gradlew test --rerun-tasks

# Run tests in parallel
./gradlew test --parallel
```

---

## 📊 Test Coverage by Component

### User Management (35 tests)
- UserControllerSpec (User registration, login, profile) - 11
- CustomUserDetailsServiceSpec (User details loading) - 8
- UserRepositorySpec (User persistence) - 12
- UserSpec (User model) - 9
- Total: **40 tests**

### Asset Management (35 tests)
- AssetModelControllerSpec (Asset upload, retrieval) - 10
- AssetModelRepositorySpec (Asset persistence) - 11
- AssetModelSpec (Asset model) - 17
- Total: **38 tests**

### File Handling (9 tests)
- FileStorageServiceSpec (File storage operations) - 9
- Total: **9 tests**

### Configuration (18 tests)
- SecurityConfigSpec (Security setup) - 11
- WebConfigSpec (Web configuration) - 7
- Total: **18 tests**

### DTOs (27 tests)
- UserRegistrationDtoSpec (User DTO) - 10
- AssetUploadDtoSpec (Asset DTO) - 17
- Total: **27 tests**

### Application (21 tests)
- MainApplicationSpec (Context and beans) - 15
- AssetModelIntegrationSpec (Full workflows) - 6
- Total: **21 tests**

---

## 🧪 Test Patterns Used

### Given-When-Then Pattern
```groovy
def "should allow user registration with valid credentials"() {
    given: "Valid user data"
    def dto = new UserRegistrationDto(...)

    when: "User registers"
    def response = userController.registerUser(dto)

    then: "Registration succeeds"
    response.statusCode == HttpStatus.OK
}
```

### Setup and Cleanup
```groovy
def setup() {
    // Initialize test fixtures
    userRepository = Mock(UserRepository)
}

def cleanup() {
    // Clean up after test
    assetModelRepository.deleteAll()
}
```

### Mocking
```groovy
def "should mock repository"() {
    given:
    def repository = Mock(UserRepository)
    repository.findByUsername("test") >> Optional.of(user)
    
    when:
    def result = repository.findByUsername("test")
    
    then:
    result.isPresent()
}
```

### Parameterized Tests
```groovy
@Unroll
def "should support #format format"() {
    expect:
    formats.contains(format)
    
    where:
    format | _
    "GLB"  | _
    "FBX"  | _
    "OBJ"  | _
}
```

---

## 🔍 Understanding Test Results

### Success Output
```
UserControllerSpec > should successfully register a new user with valid credentials PASSED
UserControllerSpec > should reject registration with short password PASSED
```

### Failure Output
```
UserControllerSpec > should reject invalid email FAILED
    AssertionError: Expected 400 but got 200
```

### Report Sections
- **Summary**: Total tests, passed, failed
- **Package Tree**: Tests organized by package
- **Timeline**: Test execution duration
- **Failed Tests**: Detailed failure information

---

## 💡 Tips & Tricks

### Skip Tests During Build
```bash
./gradlew build -x test
```

### Enable Debug Logging
```bash
./gradlew test --debug
```

### Generate HTML Report
```bash
# Automatically generated at:
./build/reports/tests/test/index.html
```

### Run Tests in Isolation
```bash
./gradlew test --no-parallel
```

### Check Test Dependencies
```bash
./gradlew dependencies --configuration testRuntimeClasspath
```

---

## 🐛 Troubleshooting

### Issue: "Cannot find groovy"
**Solution**: 
```bash
./gradlew clean build --refresh-dependencies
```

### Issue: "Spock not found"
**Solution**: Verify gradle dependencies in build.gradle

### Issue: "H2 database errors"
**Solution**: Check application-test.properties exists and is configured

### Issue: "Test hangs/timeout"
**Solution**: 
```bash
# Increase timeout
./gradlew test --max-workers=1
```

---

## 📚 Test Documentation

Each test file contains:
- ✅ Descriptive test names in BDD format
- ✅ Setup and cleanup methods
- ✅ Multiple test scenarios
- ✅ Edge case coverage
- ✅ Error condition handling

### Example Test Structure
```groovy
class UserRepositorySpec extends Specification {
    
    @Autowired
    UserRepository userRepository
    
    def setup() {
        // Initialize test data
    }
    
    def "should save and find user by username"() {
        given: "A new user"
        // ...
        
        when: "User is saved"
        // ...
        
        then: "User can be retrieved"
        // ...
    }
}
```

---

## 🔗 Integration with CI/CD

### GitHub Actions Example
```yaml
- name: Run Tests
  run: ./gradlew test --info

- name: Upload Test Results
  uses: actions/upload-artifact@v2
  with:
    name: test-results
    path: build/reports/tests/test/
```

---

## 📖 Learning Resources

### Spock Framework
- [Spock Documentation](http://spockframework.org/)
- [Spock Features](http://spockframework.org/spock/docs/)

### Spring Boot Testing
- [Spring Boot Test](https://spring.io/guides/gs/testing-web/)
- [Spring Security Testing](https://spring.io/guides/gs/securing-web/)

### Groovy
- [Groovy Documentation](http://docs.groovy-lang.org/)
- [Groovy Syntax](http://docs.groovy-lang.org/latest/html/documentation/)

---

## ✅ Checklist Before Committing

- [ ] All tests pass: `./gradlew test`
- [ ] No test warnings
- [ ] Test coverage is adequate
- [ ] New tests added for new features
- [ ] Documentation updated
- [ ] No debug code left in tests

---

## 🎓 Project Structure After Tests

```
main/
├── src/
│   ├── main/
│   │   └── java/com/webpage/T3D/outer/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── dto/
│   │       ├── configuration/
│   │       └── MainApplication.java
│   └── test/
│       └── groovy/com/webpage/T3D/outer/
│           ├── controller/*Spec.groovy
│           ├── model/*Spec.groovy
│           ├── repository/*Spec.groovy
│           ├── service/*Spec.groovy
│           ├── configuration/*Spec.groovy
│           ├── dto/*Spec.groovy
│           ├── *IntegrationSpec.groovy
│           └── MainApplicationSpec.groovy
├── build.gradle (updated with Spock)
├── TEST_SUITE_README.md
└── TEST_QUICK_REFERENCE.md (this file)
```

---

**Last Updated**: May 8, 2026
**Total Test Coverage**: 154+ Test Cases
**Framework**: Spock 2.3 + Groovy 4.0.21

