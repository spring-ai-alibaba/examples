/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.examples.sandbox.simple.config;

import io.agentscope.runtime.sandbox.manager.ManagerConfig;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import io.agentscope.runtime.sandbox.manager.client.container.docker.DockerClientStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

/**
 * Sandbox Configuration
 *
 * Configures the SandboxService which manages the lifecycle of sandbox containers.
 * Sandbox containers provide isolated environments for executing Python code, shell commands,
 * and browser automation tasks.
 */
@Configuration
public class SandboxConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SandboxConfiguration.class);

    private SandboxService sandboxService;

    @Value("${sandbox.docker.host:unix:///var/run/docker.sock}")
    private String dockerHost;

    @Value("${sandbox.pool.size:5}")
    private Integer poolSize;

    /**
     * Create and configure SandboxService
     *
     * The SandboxService manages sandbox containers using Docker.
     * Configure the Docker client and manager settings here.
     *
     * @return Configured SandboxService instance
     */
    @Bean
    public SandboxService sandboxService() {
        logger.info("Initializing SandboxService...");
        try {
            String normalizedDockerHost = normalizeDockerHost(dockerHost);
            if (!normalizedDockerHost.equals(dockerHost)) {
                logger.warn("Normalized Docker host from '{}' to '{}'", dockerHost, normalizedDockerHost);
            }
            DockerClientStarter dockerStarter = DockerClientStarter.builder()
                    .host(normalizedDockerHost)
                    .build();

            ManagerConfig managerConfig = ManagerConfig.builder()
                    .clientStarter(dockerStarter)
                    .build();

            this.sandboxService = new SandboxService(managerConfig);
            this.sandboxService.start();

            logger.info("SandboxService initialized successfully");
            logger.info("Docker Host: {}", normalizedDockerHost);
            logger.info("Pool Size: {}", poolSize);

            return this.sandboxService;
        } catch (Exception e) {
            logger.error("Failed to initialize SandboxService", e);
            throw new RuntimeException("Failed to initialize SandboxService. " +
                    "Please ensure Docker is running and accessible.", e);
        }
    }

    private String normalizeDockerHost(String host) {
        if (host == null || host.isBlank()) {
            return host;
        }
        if (host.startsWith("unix://") && host.matches("unix://.+:\\d+$")) {
            return host.replaceAll(":\\d+$", "");
        }
        return host;
    }

    /**
     * Cleanup sandbox resources on application shutdown
     */
    @PreDestroy
    public void cleanup() {
        if (sandboxService != null) {
            logger.info("Cleaning up SandboxService...");
            try {
                sandboxService.cleanupAllSandboxes();
                logger.info("SandboxService cleanup completed");
            } catch (Exception e) {
                logger.error("Error during SandboxService cleanup", e);
            }
        }
    }

}
