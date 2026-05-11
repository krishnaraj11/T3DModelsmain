package com.webpage.T3D.outer.repository

import com.webpage.T3D.outer.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.time.Instant

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class UserRepositorySpec extends Specification {

    @Autowired
    UserRepository userRepository

    def "should save and find user by username"() {
        given:
        def user = new User(
            username: "testuser",
            email: "test@example.com",
            passwordHash: "hashed_password",
            createdAt: Instant.now()
        )

        when:
        userRepository.save(user)
        def foundUser = userRepository.findByUsername("testuser")

        then:
        foundUser.isPresent()
        foundUser.get().username == "testuser"
        foundUser.get().email == "test@example.com"
    }

    def "should find user by email"() {
        given:
        def user = new User(
            username: "emailtest",
            email: "emailuser@example.com",
            passwordHash: "hash123",
            createdAt: Instant.now()
        )

        when:
        userRepository.save(user)
        def foundUser = userRepository.findByEmail("emailuser@example.com")

        then:
        foundUser.isPresent()
        foundUser.get().username == "emailtest"
    }

    def "should return empty optional when user not found by username"() {
        when:
        def foundUser = userRepository.findByUsername("nonexistent")

        then:
        !foundUser.isPresent()
    }

    def "should return empty optional when user not found by email"() {
        when:
        def foundUser = userRepository.findByEmail("nonexistent@example.com")

        then:
        !foundUser.isPresent()
    }

    def "should find user by id"() {
        given:
        def user = new User(
            username: "idtest",
            email: "idtest@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        )

        when:
        def savedUser = userRepository.save(user)
        def foundUser = userRepository.findById(savedUser.id)

        then:
        foundUser.isPresent()
        foundUser.get().id == savedUser.id
    }

    def "should enforce unique username constraint"() {
        given:
        def user1 = new User(
            username: "unique",
            email: "user1@example.com",
            passwordHash: "hash1",
            createdAt: Instant.now()
        )
        def user2 = new User(
            username: "unique",
            email: "user2@example.com",
            passwordHash: "hash2",
            createdAt: Instant.now()
        )

        when:
        userRepository.save(user1)
        userRepository.save(user2)

        then:
        thrown(Exception) // Database constraint violation
    }

    def "should enforce unique email constraint"() {
        given:
        def user1 = new User(
            username: "user1",
            email: "duplicate@example.com",
            passwordHash: "hash1",
            createdAt: Instant.now()
        )
        def user2 = new User(
            username: "user2",
            email: "duplicate@example.com",
            passwordHash: "hash2",
            createdAt: Instant.now()
        )

        when:
        userRepository.save(user1)
        userRepository.save(user2)

        then:
        thrown(Exception) // Database constraint violation
    }

    def "should update user information"() {
        given:
        def user = new User(
            username: "updatetest",
            email: "update@example.com",
            passwordHash: "oldhash",
            bio: "Old bio",
            createdAt: Instant.now()
        )
        userRepository.save(user)

        when:
        user.bio = "New bio"
        user.passwordHash = "newhash"
        userRepository.save(user)
        def updatedUser = userRepository.findByUsername("updatetest").get()

        then:
        updatedUser.bio == "New bio"
        updatedUser.passwordHash == "newhash"
    }

    def "should delete user by id"() {
        given:
        def user = new User(
            username: "deletetest",
            email: "delete@example.com",
            passwordHash: "hash",
            createdAt: Instant.now()
        )
        def savedUser = userRepository.save(user)

        when:
        userRepository.deleteById(savedUser.id)
        def foundUser = userRepository.findById(savedUser.id)

        then:
        !foundUser.isPresent()
    }

    def "should find multiple users"() {
        given:
        def users = [
            new User(username: "user1", email: "user1@ex.com", passwordHash: "h1", createdAt: Instant.now()),
            new User(username: "user2", email: "user2@ex.com", passwordHash: "h2", createdAt: Instant.now()),
            new User(username: "user3", email: "user3@ex.com", passwordHash: "h3", createdAt: Instant.now())
        ]

        when:
        users.each { userRepository.save(it) }
        def allUsers = userRepository.findAll()

        then:
        allUsers.size() >= 3
    }

    def "should store user profile image URL"() {
        given:
        def user = new User(
            username: "imgtest",
            email: "img@example.com",
            passwordHash: "hash",
            profileImageUrl: "http://example.com/profile.jpg",
            createdAt: Instant.now()
        )

        when:
        userRepository.save(user)
        def foundUser = userRepository.findByUsername("imgtest").get()

        then:
        foundUser.profileImageUrl == "http://example.com/profile.jpg"
    }

    def "should store PayPal email for creators"() {
        given:
        def user = new User(
            username: "creator",
            email: "creator@example.com",
            passwordHash: "hash",
            paypalEmail: "creator@paypal.com",
            createdAt: Instant.now()
        )

        when:
        userRepository.save(user)
        def foundUser = userRepository.findByUsername("creator").get()

        then:
        foundUser.paypalEmail == "creator@paypal.com"
    }
}

