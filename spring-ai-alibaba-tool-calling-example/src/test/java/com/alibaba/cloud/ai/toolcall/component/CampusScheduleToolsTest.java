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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CampusScheduleToolsTest {

    private final CampusScheduleTools campusScheduleTools = new CampusScheduleTools();

    @Test
    void shouldCreateCampusSchedule() {
        assertEquals("Campus schedule: activity=run, startTime=14:00, durationMinutes=60.",
                campusScheduleTools.createCampusSchedule("run", "14:00", 60));
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

}
