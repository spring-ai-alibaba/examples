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

# 运行阶段
FROM registry.cn-hangzhou.aliyuncs.com/jast-docker/openjdk:17-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y curl gnupg wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*spring

COPY target/app.jar ./app.jar
COPY mcp-libs ./mcp-libs

RUN mkdir -p /app/logs

VOLUME ["/app/logs"]

ENV BAIDU_TRANSLATE_APP_ID=${BAIDU_TRANSLATE_APP_ID} \
    BAIDU_TRANSLATE_SECRET_KEY=${BAIDU_TRANSLATE_SECRET_KEY} \
    BAIDU_MAP_API_KEY=${BAIDU_MAP_API_KEY} \
    AI_DASHSCOPE_API_KEY=${AI_DASHSCOPE_API_KEY} \
    TZ=Asia/Shanghai

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q -O /dev/null http://localhost:8080/actuator/health || exit 1
