package com.webpage.T3D.outer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.webpage.T3D.outer.repository")
@ComponentScan(basePackages = {"com.webpage.T3D.outer", "com.webpage.T3D.outer.controller", "com.webpage.T3D.outer.repository"})
public class MainApplication {

	public static void main(String[] args) {SpringApplication.run(MainApplication.class, args);}
}
