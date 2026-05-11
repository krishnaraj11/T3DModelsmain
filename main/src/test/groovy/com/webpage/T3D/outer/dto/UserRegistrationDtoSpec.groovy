package com.webpage.T3D.outer.dto

import lombok.Data
import lombok.NoArgsConstructor
import spock.lang.Specification

@Data
@NoArgsConstructor
class UserRegistrationDtoSpec extends Specification {

    def "should create DTO with username, email, and password"() {
        when:
        def dto = new UserRegistrationDto(
            username: "newuser",
            email: "newuser@example.com",
            password: "SecurePassword123!"
        )

        then:
        dto.username == "newuser"
        dto.email == "newuser@example.com"
        dto.password == "SecurePassword123!"
    }

    def "should allow setting and getting username"() {
        given:
        def dto = new UserRegistrationDto()

        when:
        dto.username = "testuser"

        then:
        dto.username == "testuser"
    }

    def "should allow setting and getting email"() {
        given:
        def dto = new UserRegistrationDto()

        when:
        dto.email = "test@example.com"

        then:
        dto.email == "test@example.com"
    }

    def "should allow setting and getting password"() {
        given:
        def dto = new UserRegistrationDto()

        when:
        dto.password = "MyPassword123!"

        then:
        dto.password == "MyPassword123!"
    }

    def "should support various valid emails"() {
        given:
        def dto = new UserRegistrationDto()

        when:
        def emails = [
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_name@example.org"
        ]

        then:
        emails.every { email ->
            dto.email = email
            dto.email == email
        }
    }

    def "should support various usernames"() {
        given:
        def dto = new UserRegistrationDto()

        when:
        def usernames = [
            "user123",
            "user_name",
            "user-name",
            "userName",
            "artist_pro"
        ]

        then:
        usernames.every { username ->
            dto.username = username
            dto.username == username
        }
    }

    def "should support strong passwords"() {
        given:
        def dto = new UserRegistrationDto()

        when:
        def passwords = [
            "StrongPassword123!",
            "MyP@ssw0rd",
            "LongPasswordWithNumbers123AndSpecial!#",
            "SecurePass2024!"
        ]

        then:
        passwords.every { password ->
            dto.password = password
            dto.password == password
        }
    }

    def "should handle null values"() {
        given:
        def dto = new UserRegistrationDto()

        expect:
        dto.username == null
        dto.email == null
        dto.password == null
    }

    def "should allow updating values"() {
        given:
        def dto = new UserRegistrationDto(
            username: "original",
            email: "original@example.com",
            password: "OriginalPass123!"
        )

        when:
        dto.username = "updated"
        dto.email = "updated@example.com"
        dto.password = "UpdatedPass123!"

        then:
        dto.username == "updated"
        dto.email == "updated@example.com"
        dto.password == "UpdatedPass123!"
    }

    def "should support builder pattern if available"() {
        when:
        def dto = new UserRegistrationDto()
        dto.username = "user"
        dto.email = "user@example.com"
        dto.password = "pass"

        then:
        dto.username == "user"
        dto.email == "user@example.com"
        dto.password == "pass"
    }
}

