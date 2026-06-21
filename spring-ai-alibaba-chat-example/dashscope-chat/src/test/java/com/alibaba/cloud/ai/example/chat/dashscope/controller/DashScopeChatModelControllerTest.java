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

package com.alibaba.cloud.ai.example.chat.dashscope.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatApiSpec.ChatCompletionRequest.Parameters.SearchOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashScopeChatModelControllerTest {

	private CapturingChatModel chatModel;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.chatModel = new CapturingChatModel();
		this.mockMvc = MockMvcBuilders.standaloneSetup(new DashScopeChatModelController(this.chatModel)).build();
	}

	@Test
	void textEndpointBuildsPromptAndReturnsResponse() throws Exception {
		this.mockMvc.perform(post("/model/dashscope/text")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "model": "qwen-max",
						  "system": "You are concise.",
						  "prompt": "你是谁？",
						  "temperature": 0.7,
						  "topP": 0.8,
						  "topK": 50
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("call content"))
			.andExpect(jsonPath("$.requestId").value("request-1"))
			.andExpect(jsonPath("$.usage.inputTokens").value(10))
			.andExpect(jsonPath("$.usage.outputTokens").value(20))
			.andExpect(jsonPath("$.usage.totalTokens").value(30))
			.andExpect(jsonPath("$.searchInfo.source").value("dashscope"));

		DashScopeChatOptions options = options(this.chatModel.callPrompt);
		assertThat(options.getModel()).isEqualTo("qwen-max");
		assertThat(options.getTemperature()).isEqualTo(0.7);
		assertThat(options.getTopP()).isEqualTo(0.8);
		assertThat(options.getTopK()).isEqualTo(50);
		assertThat(options.getMultiModel()).isNull();

		assertThat(this.chatModel.callPrompt.getInstructions()).hasSize(2);
		assertThat(this.chatModel.callPrompt.getInstructions().get(0))
			.isInstanceOfSatisfying(SystemMessage.class, message -> assertThat(message.getText()).isEqualTo("You are concise."));
		assertThat(this.chatModel.callPrompt.getInstructions().get(1))
			.isInstanceOfSatisfying(UserMessage.class, message -> assertThat(message.getText()).isEqualTo("你是谁？"));
	}

	@Test
	void textStreamEndpointUsesStreamAndIncrementalOutput() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/model/dashscope/text/stream")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.content("""
						{
						  "prompt": "你是谁？"
						}
						"""))
			.andExpect(request().asyncStarted())
			.andReturn();

		this.mockMvc.perform(asyncDispatch(result))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("stream content")));

		DashScopeChatOptions options = options(this.chatModel.streamPrompt);
		assertThat(options.getModel()).isEqualTo("qwen-plus");
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getMultiModel()).isNull();
		assertThat(this.chatModel.callPrompt).isNull();
	}

	@Test
	void searchEndpointEnablesSearchOptionsWithoutForcedSearch() throws Exception {
		this.mockMvc.perform(post("/model/dashscope/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "prompt": "明天杭州天气如何？",
						  "searchOptions": {
						    "enableSource": true,
						    "enableCitation": true,
						    "citationFormat": "[<number>]",
						    "searchStrategy": "turbo",
						    "enableSearchExtension": true,
						    "prependSearchResult": true
						  }
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("call content"));

		DashScopeChatOptions options = options(this.chatModel.callPrompt);
		assertThat(options.getModel()).isEqualTo("qwen-plus");
		assertThat(options.getEnableSearch()).isTrue();

		SearchOptions searchOptions = options.getSearchOptions();
		assertThat(searchOptions.enableSource()).isTrue();
		assertThat(searchOptions.enableCitation()).isTrue();
		assertThat(searchOptions.citationFormat()).isEqualTo("[<number>]");
		assertThat(searchOptions.searchStrategy()).isEqualTo("turbo");
		assertThat(searchOptions.enableSearchExtension()).isTrue();
		assertThat(searchOptions.prependSearchResult()).isTrue();
	}

	@Test
	void documentEndpointUsesFileIdSystemMessage() throws Exception {
		this.mockMvc.perform(post("/model/dashscope/document")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "fileId": "file-xxx",
						  "prompt": "这篇文章讲了什么？"
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("call content"));

		DashScopeChatOptions options = options(this.chatModel.callPrompt);
		assertThat(options.getModel()).isEqualTo("qwen-long");
		assertThat(options.getMultiModel()).isNull();

		assertThat(this.chatModel.callPrompt.getInstructions()).hasSize(3);
		assertThat(this.chatModel.callPrompt.getInstructions().get(0).getText()).isEqualTo("You are a helpful assistant.");
		assertThat(this.chatModel.callPrompt.getInstructions().get(1).getText()).isEqualTo("fileid://file-xxx");
		assertThat(this.chatModel.callPrompt.getInstructions().get(2).getText()).isEqualTo("这篇文章讲了什么？");
	}

	@Test
	void pptStreamEndpointUsesDocTurboSkill() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/model/dashscope/ppt/stream")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.content("""
						{
						  "document": "您的文档内容",
						  "prompt": "生成一个10到20页的ppt",
						  "mode": "general",
						  "templateId": "news_01"
						}
						"""))
			.andExpect(request().asyncStarted())
			.andReturn();

		this.mockMvc.perform(asyncDispatch(result))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("stream content")));

		DashScopeChatOptions options = options(this.chatModel.streamPrompt);
		assertThat(options.getModel()).isEqualTo("qwen-doc-turbo");
		assertThat(options.getIncrementalOutput()).isTrue();
		assertThat(options.getMultiModel()).isNull();
		assertThat(options.getSkill()).singleElement().satisfies(skill -> {
			assertThat(skill.type()).isEqualTo("ppt");
			assertThat(skill.mode()).isEqualTo("general");
			assertThat(skill.templateId()).isEqualTo("news_01");
		});
		assertThat(this.chatModel.streamPrompt.getInstructions()).extracting(Object::toString).anySatisfy(text ->
				assertThat(text).contains("您的文档内容"));
	}

	@Test
	void multimodalImageEndpointUsesMultiModelImageMessage() throws Exception {
		this.mockMvc.perform(post("/model/dashscope/multimodal/image")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "prompt": "这些是什么？",
						  "imageUrls": [
						    "https://example.com/dog.jpg",
						    "https://example.com/cat.png"
						  ]
						}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("call content"));

		assertMultimodalPrompt("qwen-vl-plus", MessageFormat.IMAGE, 2, this.chatModel.callPrompt);
		assertThat(options(this.chatModel.callPrompt).getVlHighResolutionImages()).isTrue();
	}

	@Test
	void multimodalImageStreamEndpointUsesStreamOptions() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/model/dashscope/multimodal/image/stream")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.content("""
						{
						  "prompt": "图中描绘的是什么景象？",
						  "imageUrls": [
						    "https://example.com/dog.jpg"
						  ]
						}
						"""))
			.andExpect(request().asyncStarted())
			.andReturn();

		this.mockMvc.perform(asyncDispatch(result))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("stream content")));

		assertMultimodalPrompt("qwen3-vl-plus", MessageFormat.IMAGE, 1, this.chatModel.streamPrompt);
		assertThat(options(this.chatModel.streamPrompt).getIncrementalOutput()).isTrue();
	}

	@Test
	void multimodalVideoEndpointUsesVideoMessageFormat() throws Exception {
		this.mockMvc.perform(post("/model/dashscope/multimodal/video")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "prompt": "描述这个视频的具体过程",
						  "frameUrls": [
						    "https://example.com/frame-1.jpg",
						    "https://example.com/frame-2.jpg"
						  ]
						}
						"""))
			.andExpect(status().isOk());

		assertMultimodalPrompt("qwen-vl-max", MessageFormat.VIDEO, 2, this.chatModel.callPrompt);
		assertThat(options(this.chatModel.callPrompt).getIncrementalOutput()).isFalse();
	}

	@Test
	void multimodalAudioEndpointAddsSystemMessageAndAudioFormat() throws Exception {
		this.mockMvc.perform(post("/model/dashscope/multimodal/audio")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "prompt": "这段音频在说什么？",
						  "audioUrl": "https://example.com/welcome.mp3"
						}
						"""))
			.andExpect(status().isOk());

		DashScopeChatOptions options = options(this.chatModel.callPrompt);
		assertThat(options.getModel()).isEqualTo("qwen-audio-turbo-latest");
		assertThat(options.getMultiModel()).isTrue();

		assertThat(this.chatModel.callPrompt.getInstructions()).hasSize(2);
		assertThat(this.chatModel.callPrompt.getInstructions().get(0)).isInstanceOf(SystemMessage.class);
		UserMessage message = (UserMessage) this.chatModel.callPrompt.getInstructions().get(1);
		assertThat(message.getMetadata()).containsEntry(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.AUDIO);
		assertThat(message.getMedia()).singleElement().satisfies(media -> {
			assertThat(media.getMimeType().toString()).isEqualTo("audio/mpeg");
			assertThat(media.getData()).isEqualTo("https://example.com/welcome.mp3");
		});
	}

	private static DashScopeChatOptions options(Prompt prompt) {
		assertThat(prompt).isNotNull();
		assertThat(prompt.getOptions()).isInstanceOf(DashScopeChatOptions.class);
		return (DashScopeChatOptions) prompt.getOptions();
	}

	private static void assertMultimodalPrompt(String model, MessageFormat format, int mediaCount, Prompt prompt) {
		DashScopeChatOptions options = options(prompt);
		assertThat(options.getModel()).isEqualTo(model);
		assertThat(options.getMultiModel()).isTrue();
		assertThat(prompt.getInstructions()).singleElement().isInstanceOf(UserMessage.class);

		UserMessage message = (UserMessage) prompt.getInstructions().get(0);
		assertThat(message.getMetadata()).containsEntry(DashScopeApiConstants.MESSAGE_FORMAT, format);
		assertThat(message.getMedia()).hasSize(mediaCount).extracting(Media::getData).allSatisfy(data ->
				assertThat(data).asString().startsWith("https://example.com/"));
	}

	private static ChatResponse response(String content) {
		AssistantMessage message = AssistantMessage.builder()
			.content(content)
			.properties(Map.of("search_info", Map.of("source", "dashscope")))
			.build();
		return new ChatResponse(List.of(new Generation(message)), ChatResponseMetadata.builder()
			.id("request-1")
			.model("qwen-test")
			.usage(new TestUsage(10, 20))
			.build());
	}

	private record TestUsage(Integer getPromptTokens, Integer getCompletionTokens) implements Usage {

		@Override
		public Object getNativeUsage() {
			return null;
		}
	}

	private static final class CapturingChatModel implements ChatModel {

		private Prompt callPrompt;

		private Prompt streamPrompt;

		@Override
		public ChatResponse call(Prompt prompt) {
			this.callPrompt = prompt;
			return response("call content");
		}

		@Override
		public Flux<ChatResponse> stream(Prompt prompt) {
			this.streamPrompt = prompt;
			return Flux.just(response("stream content"));
		}

		@Override
		public ChatOptions getDefaultOptions() {
			return DashScopeChatOptions.builder().model("qwen-plus").build();
		}
	}
}
