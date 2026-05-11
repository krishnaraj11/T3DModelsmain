package com.webpage.T3D.outer.controller

import com.webpage.T3D.outer.dto.UserRegistrationDto
import com.webpage.T3D.outer.model.User
import com.webpage.T3D.outer.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

import java.time.Instant

class UserControllerSpec extends Specification {

    UserController userController
    UserRepository userRepository
    PasswordEncoder passwordEncoder

    def setup() {
        userRepository = Mock(UserRepository)
        passwordEncoder = Mock(PasswordEncoder)
        userController = new UserController()
        userController.userRepository = userRepository
        userController.passwordEncoder = passwordEncoder
    }

    def "should successfully register a new user with valid credentials"() {
        given:
        def dto = new UserRegistrationDto(username: "testuser", email: "test@example.com", password: "password123")
        userRepository.findByEmail(dto.email) >> Optional.empty()
        userRepository.findByUsername(dto.username) >> Optional.empty()
        passwordEncoder.encode(dto.password) >> "hashed_password"

        when:
        def response = userController.registerUser(dto)

        then:
        response.statusCode == HttpStatus.OK
        response.body == "User registered successfully!"
        1 * userRepository.save(_)
    }

    def "should reject registration with short password"() {
        given:
        def dto = new UserRegistrationDto(username: "testuser", email: "test@example.com", password: "short")

        when:
        def response = userController.registerUser(dto)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.toString().contains("at least 8 characters")
    }

    def "should reject registration when email already exists"() {
        given:
        def dto = new UserRegistrationDto(username: "newuser", email: "taken@example.com", password: "password123")
        def existingUser = new User(username: "existinguser", email: "taken@example.com", passwordHash: "hash")
        userRepository.findByEmail(dto.email) >> Optional.of(existingUser)

        when:
        def response = userController.registerUser(dto)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body == "Email is already in use."
    }

    def "should reject registration when username already taken"() {
        given:
        def dto = new UserRegistrationDto(username: "existinguser", email: "new@example.com", password: "password123")
        def existingUser = new User(username: "existinguser", email: "existing@example.com", passwordHash: "hash")
        userRepository.findByEmail(dto.email) >> Optional.empty()
        userRepository.findByUsername(dto.username) >> Optional.of(existingUser)

        when:
        def response = userController.registerUser(dto)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body == "Username is taken."
    }

    def "should successfully login with correct username and password"() {
        given:
        def user = new User(username: "testuser", email: "test@example.com", passwordHash: "hashed_password")
        def credentials = ["username": "testuser", "password": "password123"]
        userRepository.findByUsername("testuser") >> Optional.of(user)
        passwordEncoder.matches("password123", "hashed_password") >> true

        when:
        def response = userController.loginUser(credentials)

        then:
        response.statusCode == HttpStatus.OK
        response.body.username == "testuser"
        response.body.token != null
    }

    def "should successfully login with correct email and password"() {
        given:
        def user = new User(username: "testuser", email: "test@example.com", passwordHash: "hashed_password")
        def credentials = ["username": "test@example.com", "password": "password123"]
        userRepository.findByEmail("test@example.com") >> Optional.of(user)
        passwordEncoder.matches("password123", "hashed_password") >> true

        when:
        def response = userController.loginUser(credentials)

        then:
        response.statusCode == HttpStatus.OK
        response.body.username == "testuser"
    }

    def "should reject login with non-existent user"() {
        given:
        def credentials = ["username": "nonexistent", "password": "password123"]
        userRepository.findByUsername("nonexistent") >> Optional.empty()

        when:
        def response = userController.loginUser(credentials)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
        response.body == "User not found"
    }

    def "should reject login with incorrect password"() {
        given:
        def user = new User(username: "testuser", email: "test@example.com", passwordHash: "hashed_password")
        def credentials = ["username": "testuser", "password": "wrongpassword"]
        userRepository.findByUsername("testuser") >> Optional.of(user)
        passwordEncoder.matches("wrongpassword", "hashed_password") >> false

        when:
        def response = userController.loginUser(credentials)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
        response.body == "Invalid password"
    }

    def "should retrieve user profile for authenticated user"() {
        given:
        def userId = 1L
        def user = new User(
            id: userId,
            username: "testuser",
            email: "test@example.com",
            passwordHash: "hash",
            bio: "Test bio",
            profileImageUrl: "http://example.com/image.jpg",
            createdAt: Instant.now()
        )
        def principal = Mock(java.security.Principal)
        principal.getName() >> "testuser"
        userRepository.findById(userId) >> Optional.of(user)

        when:
        def response = userController.getUserProfile(userId, principal)

        then:
        response.statusCode == HttpStatus.OK
        response.body.username == "testuser"
        response.body.email == "test@example.com"
    }

    def "should deny access when user tries to view someone else's profile"() {
        given:
        def userId = 1L
        def user = new User(
            id: userId,
            username: "otheruser",
            email: "other@example.com",
            passwordHash: "hash"
        )
        def principal = Mock(java.security.Principal)
        principal.getName() >> "currentuser"
        userRepository.findById(userId) >> Optional.of(user)

        when:
        def response = userController.getUserProfile(userId, principal)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
        response.body.toString().contains("Access Denied")
    }

    def "should return 404 when user profile not found"() {
        given:
        def userId = 999L
        def principal = Mock(java.security.Principal)
        userRepository.findById(userId) >> Optional.empty()

        when:
        def response = userController.getUserProfile(userId, principal)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }
}

