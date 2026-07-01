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
package com.alibaba.cloud.ai.toolcall.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampusScheduleToolsTest {

    private final CampusScheduleTools campusScheduleTools = new CampusScheduleTools();

    @Test
    void shouldCreateCampusSchedule() {
        String result = campusScheduleTools.createCampusSchedule("run", "14:00", 60);

        assertAll(
                () -> assertTrue(result.contains("activity=run")),
                () -> assertTrue(result.contains("startTime=14:00")),
                () -> assertTrue(result.contains("durationMinutes=60")),
                () -> assertTrue(result.contains("preparation=10 minutes")),
                () -> assertTrue(result.contains("mainActivity=43 minutes")),
                () -> assertTrue(result.contains("wrapUp=7 minutes"))
        );
    }

    @Test
    void shouldKeepShortScheduleWithinRequestedDuration() {
        String result = campusScheduleTools.createCampusSchedule("stretch", "14:00", 5);

        assertAll(
                () -> assertTrue(result.contains("durationMinutes=5")),
                () -> assertTrue(result.contains("preparation=2 minutes")),
                () -> assertTrue(result.contains("mainActivity=1 minutes")),
                () -> assertTrue(result.contains("wrapUp=2 minutes"))
        );
    }

    @Test
    void shouldRejectZeroDuration() {
        assertThrows(IllegalArgumentException.class,
                () -> campusScheduleTools.createCampusSchedule("run", "14:00", 0));
    }

    @Test
    void shouldRejectNegativeDuration() {
        assertThrows(IllegalArgumentException.class,
                () -> campusScheduleTools.createCampusSchedule("run", "14:00", -1));
    }

    @Test
    void shouldRejectBlankActivity() {
        assertThrows(IllegalArgumentException.class,
                () -> campusScheduleTools.createCampusSchedule(" ", "14:00", 60));
    }

    @Test
    void shouldEstimateLowRiskForClearWeather() {
        assertEquals("Campus activity risk: activity=run, durationMinutes=60, riskLevel=LOW, "
                        + "suggestion=Activity can continue as planned.",
                campusScheduleTools.estimateCampusActivityRisk("clear and mild", "run", 60));
    }

    @Test
    void shouldEstimateMediumRiskForRain() {
        String result = campusScheduleTools.estimateCampusActivityRisk("light rain", "run", 60);

        assertAll(
                () -> assertTrue(result.contains("riskLevel=MEDIUM")),
                () -> assertTrue(result.contains("Shorten the activity"))
        );
    }

    @Test
    void shouldEstimateHighRiskForStorm() {
        String result = campusScheduleTools.estimateCampusActivityRisk("thunder storm", "run", 60);

        assertAll(
                () -> assertTrue(result.contains("riskLevel=HIGH")),
                () -> assertTrue(result.contains("Move the activity indoors"))
        );
    }

    @Test
    void shouldRaiseRiskForLongActivity() {
        String result = campusScheduleTools.estimateCampusActivityRisk("clear", "club activity", 180);

        assertAll(
                () -> assertTrue(result.contains("riskLevel=MEDIUM")),
                () -> assertTrue(result.contains("Long activity duration detected"))
        );
    }

    @Test
    void shouldPreserveWeatherAdviceForLongActivity() {
        String result = campusScheduleTools.estimateCampusActivityRisk("light rain", "run", 180);

        assertAll(
                () -> assertTrue(result.contains("riskLevel=MEDIUM")),
                () -> assertTrue(result.contains("prepare protection")),
                () -> assertTrue(result.contains("indoor backup place")),
                () -> assertTrue(result.contains("Long activity duration detected"))
        );
    }

    @Test
    void shouldRejectBlankWeatherSummary() {
        assertThrows(IllegalArgumentException.class,
                () -> campusScheduleTools.estimateCampusActivityRisk(" ", "run", 60));
    }

}
