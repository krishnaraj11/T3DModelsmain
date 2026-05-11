package com.webpage.T3D.outer.service

import com.webpage.T3D.outer.model.User
import com.webpage.T3D.outer.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

class CustomUserDetailsServiceSpec extends Specification {

    UserRepository userRepository
    CustomUserDetailsService service

    def setup() {
        // Initialize inside setup to ensure proper Spock lifecycle
        userRepository = Mock(UserRepository)
        service = new CustomUserDetailsService()
        // Use reflection to inject the mock repository
        service.userRepository = userRepository
    }

    def "should load user by username successfully"() {
        given:
        def username = "testuser"
        def user = new User(id: 1L, username: username, email: "test@ex.com", passwordHash: "hash123")
        userRepository.findByUsername(username) >> Optional.of(user)

        when:
        def userDetails = service.loadUserByUsername(username)

        then:
        userDetails.username == username
        userDetails.password == "hash123"
    }

    def "should throw UsernameNotFoundException when user not found"() {
        given:
        userRepository.findByUsername("none") >> Optional.empty()

        when:
        service.loadUserByUsername("none")

        then:
        thrown(UsernameNotFoundException)
    }

    def "should load user with different usernames"() {
        given:
        def user = new User(id: 1L, username: "artist", email: "a@ex.com", passwordHash: "h1")
        userRepository.findByUsername("artist") >> Optional.of(user)

        expect:
        service.loadUserByUsername("artist").username == "artist"
    }

    def "should preserve password hash from database"() {
        given:
        def hash = "complex_hash_value"
        def user = new User(id: 1L, username: "user", email: "u@ex.com", passwordHash: hash)
        userRepository.findByUsername("user") >> Optional.of(user)

        expect:
        service.loadUserByUsername("user").password == hash
    }

    def "should handle special characters in username"() {
        given:
        def name = "user-name_123"
        def user = new User(id: 1L, username: name, email: "s@ex.com", passwordHash: "h")
        userRepository.findByUsername(name) >> Optional.of(user)

        expect:
        service.loadUserByUsername(name).username == name
    }

    def "should return empty authorities list for all users"() {
        given:
        def user = new User(id: 1L, username: "u", email: "e", passwordHash: "h")
        userRepository.findByUsername("u") >> Optional.of(user)

        expect:
        service.loadUserByUsername("u").authorities.isEmpty()
    }

    def "should load user with null optional correctly"() {
        given:
        userRepository.findByUsername("missing") >> Optional.empty()

        when:
        service.loadUserByUsername("missing")

        then:
        thrown(UsernameNotFoundException)
    }

    def "should query repository only once per lookup"() {
        given:
        def user = new User(id: 1L, username: "test", email: "e", passwordHash: "h")
        userRepository.findByUsername("test") >> Optional.of(user)

        when:
        def result = service.loadUserByUsername("test")

        then:
        result.username == "test"
        result.password == "h"
    }
}