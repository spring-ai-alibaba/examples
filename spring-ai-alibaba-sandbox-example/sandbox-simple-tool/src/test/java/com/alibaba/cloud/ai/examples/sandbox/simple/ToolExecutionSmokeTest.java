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
package com.alibaba.cloud.ai.examples.sandbox.simple;

import com.alibaba.cloud.ai.examples.sandbox.simple.tools.CalculatorTool;
import com.alibaba.cloud.ai.examples.sandbox.simple.tools.WeatherTool;
import io.agentscope.runtime.sandbox.box.BaseSandbox;
import io.agentscope.runtime.sandbox.box.BrowserSandbox;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A smoke test that exercises all tools without requiring a valid LLM API key.
 *
 * It proves "sandbox is really used" by asserting a container is created and
 * sandbox tool calls return expected outputs.
 */
@SpringBootTest
class ToolExecutionSmokeTest {

    @Autowired
    private CalculatorTool calculatorTool;

    @Autowired
    private WeatherTool weatherTool;

    @Autowired
    private SandboxService sandboxService;

    @Test
    void toolsShouldWorkEndToEnd() {
        // --- Calculator Tool ---
        assertEquals(5.0, parseDouble(call(calculatorTool.addTool(), "{\"a\":2,\"b\":3}")));
        assertEquals(6.0, parseDouble(call(calculatorTool.subtractTool(), "{\"a\":10,\"b\":4}")));
        assertEquals(42.0, parseDouble(call(calculatorTool.multiplyTool(), "{\"a\":6,\"b\":7}")));
        assertEquals(4.0, parseDouble(call(calculatorTool.divideTool(), "{\"a\":8,\"b\":2}")));

        // --- Weather Tool (mock data; assert format only) ---
        String city = "Hangzhou";
        String weather = call(weatherTool.getWeatherTool(), "{\"city\":\"" + city + "\"}");
        assertTrue(weather.contains("Weather in " + city + ":"), "weather output should include city: " + weather);
        assertTrue(weather.contains("Temperature:"), "weather output should include Temperature: " + weather);
        assertTrue(weather.contains("Humidity:"), "weather output should include Humidity: " + weather);

        // --- Sandbox: Python Runner + Shell Runner ---
        BaseSandbox baseSandbox = new BaseSandbox(
                sandboxService,
                "smoke-user",
                "session-" + System.currentTimeMillis()
        );

        String pythonOut = baseSandbox.runIpythonCell("print('PY_OK')");
        assertTrue(pythonOut.contains("PY_OK"), "python output should contain marker: " + pythonOut);

        String shellOut = baseSandbox.runShellCommand("echo SHELL_OK");
        assertTrue(shellOut.contains("SHELL_OK"), "shell output should contain marker: " + shellOut);

        // Prove sandbox actually created at least one container.
        assertFalse(sandboxService.getAllSandboxes().isEmpty(), "expected at least one sandbox container");

        // --- Sandbox: Browser Navigator ---
        BrowserSandbox browserSandbox = new BrowserSandbox(
                sandboxService,
                "smoke-user",
                "session-" + System.currentTimeMillis()
        );

        // Navigate + snapshot to make a robust assertion.
        browserSandbox.navigate("https://example.com");
        String snapshot = browserSandbox.snapshot();
        assertFalse(snapshot.isBlank(), "browser snapshot should not be blank");
        assertTrue(snapshot.contains("Example Domain") || snapshot.contains("example.com"),
                "snapshot should contain expected page content: " + snapshot);

        // Keep the environment clean for repeated runs.
        sandboxService.cleanupAllSandboxes();
    }

    private static String call(ToolCallback tool, String json) {
        String out = tool.call(json);
        assertNotNull(out);
        return out;
    }

    private static double parseDouble(String s) {
        return Double.parseDouble(s.trim());
    }
}

