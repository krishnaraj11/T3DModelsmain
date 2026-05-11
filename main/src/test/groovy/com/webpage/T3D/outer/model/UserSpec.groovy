package com.webpage.T3D.outer.model

import spock.lang.Specification

import java.time.Instant

class UserSpec extends Specification {

    def "should create user with all properties"() {
        when:
        def user = new User(
            id: 1L,
            username: "testuser",
            email: "test@example.com",
            passwordHash: "hashed_password_123",
            bio: "Test bio",
            profileImageUrl: "http://example.com/profile.jpg",
            paypalEmail: "test@paypal.com",
            createdAt: Instant.now(),
            updatedAt: Instant.now()
        )

        then:
        user.id == 1L
        user.username == "testuser"
        user.email == "test@example.com"
        user.passwordHash == "hashed_password_123"
        user.bio == "Test bio"
        user.profileImageUrl == "http://example.com/profile.jpg"
        user.paypalEmail == "test@paypal.com"
        user.createdAt != null
        user.updatedAt != null
    }

    def "should have default timestamps"() {
        when:
        def user = new User(
            username: "user",
            email: "user@example.com",
            passwordHash: "hash"
        )

        then:
        user.createdAt != null
        user.updatedAt != null
    }

    def "should allow setting and getting username"() {
        given:
        def user = new User()

        when:
        user.username = "newusername"

        then:
        user.username == "newusername"
    }

    def "should allow setting and getting email"() {
        given:
        def user = new User()

        when:
        user.email = "new@example.com"

        then:
        user.email == "new@example.com"
    }

    def "should allow setting and getting passwordHash"() {
        given:
        def user = new User()

        when:
        user.passwordHash = "new_hash"

        then:
        user.passwordHash == "new_hash"
    }

    def "should allow updating bio"() {
        given:
        def user = new User(bio: "Original bio")

        when:
        user.bio = "Updated bio"

        then:
        user.bio == "Updated bio"
    }

    def "should allow updating profile image URL"() {
        given:
        def user = new User(profileImageUrl: "http://example.com/old.jpg")

        when:
        user.profileImageUrl = "http://example.com/new.jpg"

        then:
        user.profileImageUrl == "http://example.com/new.jpg"
    }

    def "should allow setting PayPal email"() {
        given:
        def user = new User()

        when:
        user.paypalEmail = "creator@paypal.com"

        then:
        user.paypalEmail == "creator@paypal.com"
    }

    def "should support null optional fields"() {
        given:
        def user = new User(
            username: "test",
            email: "test@example.com",
            passwordHash: "hash"
        )

        expect:
        user.bio == null
        user.profileImageUrl == null
        user.paypalEmail == null
    }
}

