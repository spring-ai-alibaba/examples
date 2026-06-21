package com.alibaba.cloud.ai.mcp.server.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class SecurityWebmvcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityWebmvcServerApplication.class, args);
    }

}
