package com.alibaba.cloud.ai.mcp.server.security.service;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class SecureCityWeatherService {

    private static final Map<String, SecureWeather> WEATHER = Map.of(
            "beijing", new SecureWeather("Beijing", "Sunny", 27, "OAuth2 or API key call succeeded"),
            "shanghai", new SecureWeather("Shanghai", "Cloudy", 29, "OAuth2 or API key call succeeded"),
            "hangzhou", new SecureWeather("Hangzhou", "Light rain", 25, "OAuth2 or API key call succeeded"),
            "shenzhen", new SecureWeather("Shenzhen", "Showers", 30, "OAuth2 or API key call succeeded"));

    @McpTool(name = "getSecureWeather", description = "Query protected city weather")
    @PreAuthorize("hasAuthority('SCOPE_mcp:tools') or authentication.name == 'api01'")
    public String getSecureWeather(@McpToolParam(description = "City name, for example beijing", required = true)
            String city) {
        SecureWeather weather = findWeather(city);
        return "%s secure weather: %s, %d C. %s.".formatted(
                weather.city(), weather.condition(), weather.temperature(), weather.message());
    }

    private SecureWeather findWeather(String city) {
        String normalized = city == null ? "" : city.trim().toLowerCase(Locale.ROOT);
        return WEATHER.entrySet().stream()
                .filter(entry -> entry.getKey().equals(normalized)
                        || entry.getValue().city().equalsIgnoreCase(city))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown city: " + city));
    }

    private record SecureWeather(String city, String condition, int temperature, String message) {
    }

}
