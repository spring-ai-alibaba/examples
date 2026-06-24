/*
 * Copyright 2024-2025 the original author or authors.
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
package com.alibaba.cloud.ai.toolcall.controller;

import com.alibaba.cloud.ai.toolcall.config.ToolConfiguration;
import com.alibaba.cloud.ai.toolcall.service.time.function.TimeService;
import com.alibaba.cloud.ai.toolcall.service.time.method.TimeTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolBookAlignmentTests {

    @Test
    void bookStyleControllersExposeExpectedEntrypoints() {
        assertRequestMapping(BasicTimeController.class, "/basic/tool/time");
        assertGetMapping(BasicTimeController.class, "/call");
        assertGetMapping(BasicTimeController.class, "/call/function");
        assertGetMapping(BasicTimeController.class, "/call/method");
        assertGetMapping(BasicTimeController.class, "/call/method-auto");
        assertGetMapping(BasicTimeController.class, "/call/callback");
        assertGetMapping(BasicTimeController.class, "/call/method-false");

        assertRequestMapping(SearchController.class, "/basic/tool/search");
        assertGetMapping(SearchController.class, "/call");

        assertRequestMapping(PythonController.class, "/basic/tool/python");
        assertGetMapping(PythonController.class, "/call");
    }

    @Test
    void toolControllersUseExplicitToolCallingAdvisor() {
        List.of(BasicTimeController.class, SearchController.class, PythonController.class,
                WeatherController.class, BaiduTranslateController.class, AddressController.class,
                CampusAssistantController.class, TimeController.class)
                .forEach(this::assertConstructorHasAdvisor);
    }

    @Test
    void methodToolIsDeclaredByToolAnnotation() throws NoSuchMethodException {
        Method method = TimeTools.class.getDeclaredMethod("getCityTimeMethod", String.class);

        Tool tool = method.getAnnotation(Tool.class);

        assertNotNull(tool);
        assertEquals("getCityTimeMethod", tool.name());
        assertFalse(tool.description().isBlank());
    }

    @Test
    void toolConfigurationExposesAdvisorAndCallbackBeans() {
        List<Class<?>> returnTypes = Arrays.stream(ToolConfiguration.class.getDeclaredMethods())
                .map(Method::getReturnType)
                .toList();

        assertTrue(returnTypes.contains(ToolCallingAdvisor.class));
        assertTrue(returnTypes.contains(ToolCallback.class));
    }

    @Test
    void timeServiceFormatsTimeZoneResponse() {
        TimeService.Response response = new TimeService().apply(new TimeService.Request("UTC"));

        assertTrue(response.description().contains("UTC"));
        assertTrue(response.description().contains("current time"));
    }

    @Test
    void methodFalseEndpointReturnsChatResponse() throws NoSuchMethodException {
        Method method = BasicTimeController.class.getDeclaredMethod("callToolMethodFalse", String.class);

        assertEquals(ChatResponse.class, method.getReturnType());
    }

    private void assertRequestMapping(Class<?> controllerType, String path) {
        RequestMapping mapping = controllerType.getAnnotation(RequestMapping.class);

        assertNotNull(mapping);
        assertTrue(Arrays.asList(mapping.value()).contains(path));
    }

    private void assertGetMapping(Class<?> controllerType, String path) {
        assertTrue(Arrays.stream(controllerType.getDeclaredMethods())
                .map(method -> method.getAnnotation(GetMapping.class))
                .filter(mapping -> mapping != null)
                .flatMap(mapping -> Arrays.stream(mapping.value()))
                .toList()
                .contains(path));
    }

    private void assertConstructorHasAdvisor(Class<?> controllerType) {
        assertTrue(Arrays.stream(controllerType.getDeclaredConstructors())
                .map(Constructor::getParameterTypes)
                .map(Arrays::asList)
                .anyMatch(parameterTypes -> parameterTypes.contains(ToolCallingAdvisor.class)));
    }

}
