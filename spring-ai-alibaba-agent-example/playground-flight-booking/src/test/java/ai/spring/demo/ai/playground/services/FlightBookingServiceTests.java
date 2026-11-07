/*
 * Copyright 2026 the original author or authors.
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
package ai.spring.demo.ai.playground.services;

import java.util.Random;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class FlightBookingServiceTests {

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 12 })
	void demoFlightsHaveDifferentOriginAndDestination(int preferredIndex) {
		try (MockedConstruction<Random> random = mockConstruction(Random.class, (mock, context) -> {
			when(mock.nextInt(anyInt())).thenAnswer(call -> Math.min(preferredIndex, (int) call.getArgument(0) - 1));
		})) {
			FlightBookingService service = new FlightBookingService();

			assertThat(random.constructed()).hasSize(1);
			assertThat(service.getBookings()).hasSize(5).allSatisfy(booking -> {
				assertThat(booking.from()).isNotBlank();
				assertThat(booking.to()).isNotBlank().isNotEqualTo(booking.from());
			});
		}
	}
}
