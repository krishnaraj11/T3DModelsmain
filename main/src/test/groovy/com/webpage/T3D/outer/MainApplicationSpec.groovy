package com.webpage.T3D.outer

import com.webpage.T3D.outer.controller.AssetModelController
import com.webpage.T3D.outer.controller.UserController
import com.webpage.T3D.outer.service.CustomUserDetailsService
import com.webpage.T3D.outer.service.FileStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class MainApplicationSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    def "should load application context"() {
        expect:
        applicationContext != null
    }

    def "should have required controllers and services"() {
        expect:
        applicationContext.getBean(UserController) != null
        applicationContext.getBean(AssetModelController) != null
        applicationContext.getBean(FileStorageService) != null
        applicationContext.getBean(CustomUserDetailsService) != null
    }

    def "should have required infrastructure beans"() {
        expect:
        applicationContext.getBean("dataSource") != null
        applicationContext.getBean("entityManagerFactory") != null
        applicationContext.getBean(org.springframework.security.crypto.password.PasswordEncoder) != null
    }

    def "application should start without errors"() {
        expect:
        applicationContext.isActive()
    }
}