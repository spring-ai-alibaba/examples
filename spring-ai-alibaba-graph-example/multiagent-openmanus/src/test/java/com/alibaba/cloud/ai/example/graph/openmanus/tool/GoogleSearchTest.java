/*
 * Copyright 2025 the original author or authors.
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
package com.alibaba.cloud.ai.example.graph.openmanus.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.alibaba.cloud.ai.example.graph.openmanus.tool.support.serpapi.SerpApiService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoogleSearchTest {

	private static final String NO_RESULTS = "No good search result found";

	@ParameterizedTest(name = "{0}")
	@MethodSource("responses")
	void handlesEmptyAndShortResponses(String scenario, Map<String, Object> response, String expected) {
		try (MockedConstruction<SerpApiService> services = mockConstruction(SerpApiService.class,
				(service, context) -> when(service.apply(any())).thenReturn(
						response == null ? null : new HashMap<>(response)))) {
			String result = new GoogleSearch().run("{\"query\":\"offline fixture\"}").getOutput();
			assertThat(result).isEqualTo(expected);
			assertThat(services.constructed()).hasSize(1);
			verify(services.constructed().get(0)).apply(new SerpApiService.Request("offline fixture"));
		}
	}

	static Stream<Arguments> responses() {
		List<Map<String, String>> shopping = IntStream.range(0, 5)
			.mapToObj(i -> Map.of("title", "item-" + i)).toList();
		List<Map<String, String>> images = IntStream.range(0, 12)
			.mapToObj(i -> Map.of("thumbnail", "image-" + i)).toList();
		Map<String, Object> nullSections = new HashMap<>();
		nullSections.put("answer_box", null);
		nullSections.put("sports_results", null);
		nullSections.put("knowledge_graph", null);
		return Stream.of(
			Arguments.of("null optional sections", nullSections, NO_RESULTS),
			Arguments.of("answer list", Map.of("answer_box", List.of(Map.of("answer", "list answer"))), "list answer"),
			Arguments.of("highlighted words", Map.of("answer_box", Map.of("snippet_highlighted_words", List.of("word"))), "word"),
			Arguments.of("sports result", Map.of("sports_results", Map.of("game_spotlight", "game")), "game"),
			Arguments.of("knowledge result", Map.of("knowledge_graph", Map.of("description", "knowledge")), "knowledge"),
			Arguments.of("null service response", null, NO_RESULTS),
			Arguments.of("empty response", Map.of(), NO_RESULTS),
			Arguments.of("empty organic results", Map.of("organic_results", List.of()), NO_RESULTS),
			Arguments.of("empty answer box falls through", Map.of("answer_box", List.of(),
				"organic_results", List.of(Map.of("snippet", "organic"))), "organic"),
			Arguments.of("empty highlighted words fall through", Map.of("answer_box",
				Map.of("snippet_highlighted_words", List.of()), "organic_results",
				List.of(Map.of("snippet", "organic"))), "organic"),
			Arguments.of("short shopping results", Map.of("shopping_results", List.of(Map.of("title", "item"))),
				"[{title=item}]"),
			Arguments.of("empty shopping falls through", Map.of("shopping_results", List.of(),
				"organic_results", List.of(Map.of("link", "https://example.com"))), "https://example.com"),
			Arguments.of("short image-only results", Map.of("images_results",
				List.of(Map.of("thumbnail", "image"))), "[image]"),
			Arguments.of("empty images", Map.of("images_results", List.of()), NO_RESULTS),
			Arguments.of("answer priority preserved", Map.of("answer_box", Map.of("answer", "direct"),
				"organic_results", List.of(Map.of("snippet", "organic"))), "direct"),
			Arguments.of("shopping cap preserved", Map.of("shopping_results", shopping), shopping.subList(0, 3).toString()),
			Arguments.of("image cap preserved", Map.of("organic_results", List.of(Map.of()),
				"images_results", images), images.subList(0, 10).stream().map(item -> item.get("thumbnail")).toList().toString())
		);
	}

}
