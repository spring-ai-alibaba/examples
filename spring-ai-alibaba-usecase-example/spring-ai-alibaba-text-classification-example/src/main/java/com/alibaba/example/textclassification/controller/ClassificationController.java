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

package com.alibaba.example.textclassification.controller;

import java.util.List;

import com.alibaba.example.textclassification.ClassificationType;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClassificationController {

	private final ChatClient chatClient;

	ClassificationController(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder
				.defaultOptions(ChatOptions.builder()
						.temperature(0.0)
						.build())
				.build();
	}

	@PostMapping("/classify/class-names")
	String classifyClassNames(@RequestBody String text) {
		return chatClient
				.prompt()
				.system(
						"""
								 	Classify the provided text into one of these classes:
									BUSINESS, SPORT, TECHNOLOGY, OTHER.
								"""
				).user(text)
				.call()
				.content();
	}

	@PostMapping("/classify/class-descriptions")
	String classifyClassDescriptions(@RequestBody String text) {
		return chatClient
				.prompt()
				.system(
						"""
									Classify the provided text into one of these classes.
									
									BUSINESS: Commerce, finance, markets, entrepreneurship, corporate developments.
									SPORT: Athletic events, tournament outcomes, performances of athletes and teams.
									TECHNOLOGY: innovations and trends in software, artificial intelligence, cybersecurity.
									OTHER: Anything that doesn't fit into the other categories.
								"""
				).user(text)
				.call()
				.content();
	}

	@PostMapping("/classify/few-shots-prompt")
	String classifyFewShotsPrompt(@RequestBody String text) {
		return chatClient
				.prompt()
				.system(
						"""
								Classify the provided text into one of these classes.
															
								BUSINESS: Commerce, finance, markets, entrepreneurship, corporate developments.
								SPORT: Athletic events, tournament outcomes, performances of athletes and teams.
								TECHNOLOGY: innovations and trends in software, artificial intelligence, cybersecurity.
								OTHER: Anything that doesn't fit into the other categories.
															
								---
															
								Text: Clean Energy Startups Make Waves in 2024, Fueling a Sustainable Future.
								Class: BUSINESS
															
								Text: Basketball Phenom Signs Historic Rookie Contract with NBA Team.
								Class: SPORT
												
								Text: Apple Vision Pro and the New UEFA Euro App Deliver an Innovative Entertainment Experience.
								Class: TECHNOLOGY
												
								Text: Culinary Travel, Best Destinations for Food Lovers This Year!
								Class: OTHER
								"""
				).user(text)
				.call()
				.content();
	}

	@PostMapping("/classify/few-shots-history")
	String classifyFewShotsHistory(@RequestBody String text) {
		return chatClient
				.prompt()
				.messages(getPromptWithFewShotsHistory())
				.user(text)
				.call()
				.content();
	}

	@PostMapping("/classify/structured-output")
	ClassificationType classifyStructured(@RequestBody String text) {
		return chatClient
				.prompt()
				.messages(getPromptWithFewShotsHistory())
				.user(text)
				.call()
				.entity(ClassificationType.class);
	}

	@PostMapping("/classify")
	ClassificationType classify(@RequestBody String text) {
		return classifyStructured(text);
	}

	private List<Message> getPromptWithFewShotsHistory() {
		return List.of(
				new SystemMessage("""
						Classify the provided text into one of these classes.
						                    
						BUSINESS: Commerce, finance, markets, entrepreneurship, corporate developments.
						SPORT: Athletic events, tournament outcomes, performances of athletes and teams.
						TECHNOLOGY: innovations and trends in software, artificial intelligence, cybersecurity.
						OTHER: Anything that doesn't fit into the other categories.
						"""),

				new UserMessage("Apple Vision Pro and the New UEFA Euro App Deliver an Innovative Entertainment Experience."),
				new AssistantMessage("TECHNOLOGY"),
				new UserMessage("Wall Street, Trading Volumes Reach All-Time Highs Amid Market Optimism."),
				new AssistantMessage("BUSINESS"),
				new UserMessage("Sony PlayStation 6 Launch, Next-Gen Gaming Experience Redefines Console Performance."),
				new AssistantMessage("TECHNOLOGY"),
				new UserMessage("Water Polo Star Secures Landmark Contract with Major League Team."),
				new AssistantMessage("SPORT"),
				new UserMessage("Culinary Travel, Best Destinations for Food Lovers This Year!"),
				new AssistantMessage("OTHER"),
				new UserMessage("UEFA Euro 2024, Memorable Matches and Record-Breaking Goals Define Tournament Highlights."),
				new AssistantMessage("SPORT"),
				new UserMessage("Rock Band Resurgence, Legendary Groups Return to the Stage with Iconic Performances."),
				new AssistantMessage("OTHER")
		);
	}

}
