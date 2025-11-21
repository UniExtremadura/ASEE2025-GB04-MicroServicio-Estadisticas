package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// Si DemoApplication está en com.example.demo, esta anotación NO es necesaria.
// @ComponentScan(basePackages = {"com.example.demo.controller", "com.example.demo.repository", "com.example.demo.model", "com.example.demo.service"}) 
public class DemoApplication {

    @Bean // 🚩 Cambio 1: Es crucial para inyectar RestTemplate
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}