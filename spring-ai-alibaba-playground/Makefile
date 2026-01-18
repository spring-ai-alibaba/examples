# Copyright 2024-2026 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

## Docker images
docker-build: backend-build
	@docker build -t spring-ai-alibaba-playground:latest -f deploy/docker/Dockerfile .

## Docker build remove
docker-build-remove:
	@docker rmi spring-ai-alibaba-playground:latest

## Docker run by default image from ghcr.io
docker-run:
	@echo "Running playground with default image from ghcr.io, only has dashscope api key env to set."
	@echo "If has more function, please refer README#Docker docs."
	@docker run -d -p 8080:8080 \
	-e AI_DASHSCOPE_API_KEY=your_api_key \
	--name spring-ai-alibaba-playground \
	ghrcr.io/spring-ai-alibaba/spring-ai-alibaba-examples/spring-ai-alibaba-playground:latest

## Docker build and run
docker-build-run:
	@docker run -d -p 8080:8080 \
	-e AI_DASHSCOPE_API_KEY=your_api_key \
	--name spring-ai-alibaba-playground \
	spring-ai-alibaba-playground:latest

## Docker Compose
docker-compose: frontend-build
	@docker-compose -f deploy/docker/docker-compose.yml up --build -d

## Kubernetes

## Backend build
backend-build:
	@mvn clean package -DskipTests

## frontend build
frontend-build:
	@cd frontend && pnpm install && pnpm run build

## frontend dev
frontend-dev:
	@cd frontend && pnpm install && pnpm dev
