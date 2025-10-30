/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.mcp.restful.controller;

import com.alibaba.cloud.ai.mcp.restful.utils.ZoneUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @author yingzi
 * @date 2025/4/6:12:56
 */
@RestController
@RequestMapping("/time")
public class TimeController {

    private static final Logger logger = LoggerFactory.getLogger(TimeController.class);

    /**
     * 获取指定时区的时间
     */
    @GetMapping("/city")
    public String getCiteTimeMethod(
            @RequestParam("timeZoneId") String timeZoneId,
            HttpServletRequest request) {
        // 打印请求头信息
        for (String headerName : Collections.list(request.getHeaderNames())) {
            logger.info("Header {}: {}", headerName, request.getHeader(headerName));
        }
        logger.info("The current time zone is {}", timeZoneId);
        return String.format("The current time zone is %s and the current time is " + "%s", timeZoneId,
                ZoneUtils.getTimeByZoneId(timeZoneId));
    }
}
