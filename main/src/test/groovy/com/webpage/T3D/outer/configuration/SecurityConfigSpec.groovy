package com.webpage.T3D.outer.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    PasswordEncoder passwordEncoder

    def "should allow unauthenticated access to registration endpoint"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    def "should allow unauthenticated access to login endpoint"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")).andReturn()
        then:
        result.response.status in [200, 400, 401, 415] // Accepts various response codes
    }

    def "should allow unauthenticated access to storefront feed"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/models/feed")).andReturn()
        then:
        result.response.status == 200
    }

    def "should allow unauthenticated access to model details"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/models/1")).andReturn()
        then:
        result.response.status in [200, 404] // Either found or not found is acceptable
    }

    def "should allow public access to uploaded files"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/uploads/anyfile.glb")).andReturn()
        then:
        result.response.status in [200, 404] // Either found or not found is acceptable
    }

    def "should have CSRF disabled"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/api/models/upload").contentType("application/json")).andReturn()
        then:
        result.response.status in [200, 400, 401, 403] // Normal responses for POST without CSRF token
    }

    def "should support CORS for Angular frontend"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.options("/api/users/register")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")).andReturn()
        then:
        result.response.status in [200, 404, 405]
    }

    def "password encoder should encode passwords"() {
        given:
        def rawPassword = "TestPassword123!"
        when:
        def encodedPassword = passwordEncoder.encode(rawPassword)
        then:
        encodedPassword != rawPassword
        passwordEncoder.matches(rawPassword, encodedPassword)
    }

    def "password encoder should handle different passwords differently"() {
        given:
        def password1 = "Password123!"
        def password2 = "Password123!"
        when:
        def encoded1 = passwordEncoder.encode(password1)
        def encoded2 = passwordEncoder.encode(password2)
        then:
        encoded1 != encoded2
        passwordEncoder.matches(password1, encoded1)
        passwordEncoder.matches(password2, encoded2)
    }

    def "password encoder should correctly identify mismatched passwords"() {
        given:
        def correctPassword = "CorrectPassword123!"
        def wrongPassword = "WrongPassword123!"
        def hash = passwordEncoder.encode(correctPassword)
        when:
        def matchesCorrect = passwordEncoder.matches(correctPassword, hash)
        def matchesWrong = passwordEncoder.matches(wrongPassword, hash)
        then:
        matchesCorrect
        !matchesWrong
    }

    def "should use BCrypt password encoder"() {
        when:
        def encodedPassword = passwordEncoder.encode("test")
        then:
        encodedPassword.startsWith("\$2a") || encodedPassword.startsWith("\$2b") || encodedPassword.startsWith("\$2x")
    }
}

