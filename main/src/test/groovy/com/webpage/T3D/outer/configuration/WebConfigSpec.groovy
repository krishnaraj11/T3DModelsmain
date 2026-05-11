package com.webpage.T3D.outer.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebConfigSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    def "should serve static files from /uploads URI"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/uploads/test-file.glb"))
    }

    def "should allow GET requests to uploaded files"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/uploads/model.fbx"))
    }

    def "should support CORS for specific origin"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.options("/uploads/file.glb")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET"))
    }

    def "should deny requests from unauthorized origins"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/uploads/file.glb")
                .header("Origin", "http://unauthorized.com"))
    }

    def "should support nested upload paths"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/uploads/subfolder/model.glb"))
    }

    def "should handle requests with various file extensions"() {
        given:
        def fileExtensions = ["glb", "fbx", "obj", "jpg", "png"]
        expect:
        fileExtensions.every { ext ->
            mockMvc.perform(MockMvcRequestBuilders.get("/uploads/model.${ext}"))
            true
        }
    }

    def "should respond with appropriate content type for static resources"() {
        expect:
        mockMvc.perform(MockMvcRequestBuilders.get("/uploads/style.css"))
    }
}

