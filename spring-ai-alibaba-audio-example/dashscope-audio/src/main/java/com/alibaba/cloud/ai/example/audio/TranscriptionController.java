package com.alibaba.cloud.ai.example.audio;

import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAudioTranscriptionPrompt;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeAsrTranscriptionApiSpec;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionApiSpec;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionResponse;
import com.alibaba.cloud.ai.dashscope.audio.transcription.DashScopeTranscriptionResponse.DashScopeAudioTranscription;
import com.alibaba.cloud.ai.dashscope.metadata.audio.DashScopeAudioTranscriptionResponseMetadata;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yingzi、yuluo
 * @since 2026/2/6
 * 章节内容：语音识别、语音翻译
 */
@RestController
@RequestMapping("/ai/video/transcription")
public class TranscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);

    private static final String API_KEY_ENV = "DASHSCOPE_API_KEY";

    // Test audio URL from DashScope official documentation
    private static final String TEST_AUDIO_URL = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250211/tixcef/cherry.wav";

    // Test audio URLs for Paraformer and Fun-ASR file recognition
    private static final String PARAFORMER_TEST_AUDIO_URL_1 = "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav";
    private static final String PARAFORMER_TEST_AUDIO_URL_2 = "https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_male2.wav";

    // Test audio URL for Qwen-ASR
    private static final String QWEN_ASR_TEST_AUDIO_URL = "https://dashscope.oss-cn-beijing.aliyuncs.com/audios/welcome.mp3";

    private final DashScopeAudioTranscriptionModel transcriptionModel;

    public TranscriptionController(DashScopeAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }


    @GetMapping("/qwen3-call")
    public String qwen3Call() {
        DashScopeAudioTranscriptionOptions.Audio audio = new DashScopeAudioTranscriptionOptions.Audio();
        audio.setVoice("Cherry");
        audio.setFormat("wav");

        DashScopeAudioTranscriptionOptions.TranslationOptions translationOptions =
                new DashScopeAudioTranscriptionOptions.TranslationOptions();
        translationOptions.setSourceLang("zh");
        translationOptions.setTargetLang("en");

        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.QWEN3_LIVETRANSLATE_FLASH.getValue())
                .modalities(List.of("text"))
                .audio(audio)
                .translationOptions(translationOptions)
                .build();

        // Arrange - 构造 Prompt
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio inputAudio =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio(TEST_AUDIO_URL, "wav");
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content content =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content("input_audio", inputAudio);
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage message =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage(List.of(content));
        AudioTranscriptionPrompt prompt = new DashScopeAudioTranscriptionPrompt(options, message);

        // Act
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse dashScopeResponse =
                (DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse) response;

        logger.info("LiveTranslate call test passed");
        logger.info("Response ID: {}", dashScopeResponse.getId());
        logger.info("Model: {}", dashScopeResponse.getModel());

        if (dashScopeResponse.getUsage() != null) {
            logger.info("Prompt Tokens: {}", dashScopeResponse.getUsage().promptTokens());
            logger.info("Completion Tokens: {}", dashScopeResponse.getUsage().completionTokens());
            logger.info("Total Tokens: {}", dashScopeResponse.getUsage().totalTokens());
        }

        if (response.getResult() != null) {
            logger.info("Transcription Result: {}", response.getResult().getOutput());
        }

        String finalContent = dashScopeResponse.getChoices().get(0).message().content();
        logger.info("content: {}", finalContent);
        return "LiveTranslate call result: " + finalContent;
    }

    /**
     * LiveTranslate 流式调用
     */
    @GetMapping("/qwen3-livetranslate/stream")
    public Flux<String> qwen3LiveTranslateStream() {
        DashScopeAudioTranscriptionOptions.Audio audio = new DashScopeAudioTranscriptionOptions.Audio();
        audio.setVoice("Cherry");
        audio.setFormat("wav");

        DashScopeAudioTranscriptionOptions.StreamOptions streamOptions =
                new DashScopeAudioTranscriptionOptions.StreamOptions();
        streamOptions.setIncludeUsage(true);

        DashScopeAudioTranscriptionOptions.TranslationOptions translationOptions =
                new DashScopeAudioTranscriptionOptions.TranslationOptions();
        translationOptions.setSourceLang("zh");
        translationOptions.setTargetLang("en");

        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.QWEN3_LIVETRANSLATE_FLASH.getValue())
                .modalities(List.of("text", "audio"))
                .audio(audio)
                .streamOptions(streamOptions)
                .translationOptions(translationOptions)
                .build();

        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio inputAudio =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio(TEST_AUDIO_URL, "wav");
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content content =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content("input_audio", inputAudio);
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage message =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage(List.of(content));
        AudioTranscriptionPrompt prompt = new DashScopeAudioTranscriptionPrompt(options, message);

        Flux<AudioTranscriptionResponse> result = transcriptionModel.stream(prompt);

        return result.map(response -> {
            DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse r =
                    (DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse) response;

            StringBuilder sb = new StringBuilder();
            sb.append("Request ID: ").append(r.getId()).append("\n");

            if (r.getChoices() != null && !r.getChoices().isEmpty()) {
                var choice = r.getChoices().get(0);
                if (choice.delta() != null && choice.delta().content() != null) {
                    sb.append("Delta content: ").append(choice.delta().content()).append("\n");
                }
                if (choice.message() != null && choice.message().content() != null) {
                    sb.append("Message content: ").append(choice.message().content()).append("\n");
                }
            }

            if (r.getUsage() != null) {
                sb.append("Usage - Prompt: ").append(r.getUsage().promptTokens())
                  .append(", Completion: ").append(r.getUsage().completionTokens())
                  .append(", Total: ").append(r.getUsage().totalTokens()).append("\n");
            }

            return sb.toString();
        })
        .doOnComplete(() -> logger.info("LiveTranslate stream test passed"));
    }

    /**
     * WebSocket 实时短语音翻译 (gummy-chat-v1)
     */
    @GetMapping("/websocket/short")
    public Flux<String> websocketShort() {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.GUMMY_CHAT_V1.getValue())
                .sampleRate(16000)
                .format("wav")
                .transcriptionEnabled(true)
                .translationEnabled(true)
                .translationTargetLanguages(List.of("en"))
                .build();

        ClassPathResource audioResource = new ClassPathResource("tts/stream-url-test.wav");
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);

        Flux<AudioTranscriptionResponse> result = transcriptionModel.stream(prompt);

        return result.map(response -> {
            DashScopeTranscriptionResponse r = (DashScopeTranscriptionResponse) response;
            StringBuilder sb = new StringBuilder();

            DashScopeAudioTranscription transcription = r.getResult();
            if (transcription != null) {
                sb.append("Transcription: ").append(transcription.getText()).append("\n");
            }

            List<DashScopeAudioTranscriptionResponseMetadata.Translation> translations = r.getMetadata().getTranslations();
            if (translations != null) {
                for (DashScopeAudioTranscriptionResponseMetadata.Translation translation : translations) {
                    sb.append("Translation: ").append(translation.text()).append("\n");
                }
            }

            logger.info("WebSocket short response: {}", sb);
            return sb.toString();
        })
        .doOnComplete(() -> logger.info("WebSocket short test passed"));
    }

    /**
     * WebSocket 实时长语音翻译 (gummy-realtime-v1)
     */
    @GetMapping("/websocket/long")
    public Flux<String> websocketLong() {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.GUMMY_REALTIME_V1.getValue())
                .sampleRate(16000)
                .format("wav")
                .transcriptionEnabled(true)
                .translationEnabled(true)
                .translationTargetLanguages(List.of("en"))
                .build();

        ClassPathResource audioResource = new ClassPathResource("tts/stream-url-test.wav");
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);

        Flux<AudioTranscriptionResponse> result = transcriptionModel.stream(prompt);

        return result.map(response -> {
            DashScopeTranscriptionResponse r = (DashScopeTranscriptionResponse) response;
            StringBuilder sb = new StringBuilder();

            DashScopeAudioTranscription transcription = r.getResult();
            if (transcription != null) {
                sb.append("Transcription: ").append(transcription.getText()).append("\n");
            }

            List<DashScopeAudioTranscriptionResponseMetadata.Translation> translations = r.getMetadata().getTranslations();
            if (translations != null) {
                for (DashScopeAudioTranscriptionResponseMetadata.Translation translation : translations) {
                    sb.append("Translation: ").append(translation.text()).append("\n");
                }
            }

            logger.info("WebSocket long response: {}", sb);
            return sb.toString();
        })
        .doOnComplete(() -> logger.info("WebSocket long test passed"));
    }

    /**
     * WebSocket paraformer-realtime-v2 高精度实时语音识别
     */
    @GetMapping("/websocket/paraformer")
    public Flux<String> websocketParaformer() {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.PARAFORMER_REALTIME_V2.getValue())
                .sampleRate(16000)
                .format("pcm")
                .disfluencyRemovalEnabled(false)
                .languageHints(List.of("zh"))
                .vocabularyId(null)
                .resources(null)
                .build();

        ClassPathResource audioResource = new ClassPathResource("tts/stream-url-test.wav");
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);

        Flux<AudioTranscriptionResponse> result = transcriptionModel.stream(prompt);

        return result.map(response -> {
            DashScopeTranscriptionResponse r = (DashScopeTranscriptionResponse) response;
            DashScopeAudioTranscriptionResponseMetadata.Sentence sentence = r.getMetadata().getSentence();
            String text = sentence != null ? sentence.text() : "";
            logger.info("WebSocket Paraformer response: {}", text);
            return "Transcription: " + text + "\n";
        })
        .doOnComplete(() -> logger.info("Paraformer WebSocket test passed"));
    }

    /**
     * WebSocket fun-asr-realtime 高精度实时语音识别
     */
    @GetMapping("/websocket/funasr")
    public Flux<String> websocketFunAsr() {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.FUN_ASR_REALTIME.getValue())
                .sampleRate(16000)
                .format("pcm")
                .vocabularyId(null)
                .build();

        ClassPathResource audioResource = new ClassPathResource("tts/stream-url-test.wav");
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);

        Flux<AudioTranscriptionResponse> result = transcriptionModel.stream(prompt);

        return result.map(response -> {
            DashScopeTranscriptionResponse r = (DashScopeTranscriptionResponse) response;
            DashScopeAudioTranscriptionResponseMetadata.Sentence sentence = r.getMetadata().getSentence();
            String text = sentence != null ? sentence.text() : "";
            logger.info("WebSocket FunASR response: {}", text);
            return "Transcription: " + text + "\n";
        })
        .doOnComplete(() -> logger.info("FunASR WebSocket test passed"));
    }

    /**
     * 录音文件识别：Paraformer (call)
     */
    @GetMapping("/asr/paraformer/call")
    public String asrParaformerCall() {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.PARAFORMER_V2.getValue())
                .channelId(List.of(0))
                .disfluencyRemovalEnabled(false)
                .timestampAlignmentEnabled(false)
                .languageHints(List.of("zh", "en"))
                .diarizationEnabled(false)
                .speakerCount(2)
                .build();

        List<String> fileUrls = List.of(PARAFORMER_TEST_AUDIO_URL_1, PARAFORMER_TEST_AUDIO_URL_2);
        AudioTranscriptionPrompt prompt = new DashScopeAudioTranscriptionPrompt(options, fileUrls);

        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse asrResponse =
                (DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse) response;

        StringBuilder sb = new StringBuilder();
        sb.append("Paraformer ASR call result:\n");

        for (DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse.TranscriptionResult result : asrResponse.getTranscriptionResults()) {
            sb.append("File URL: ").append(result.fileUrl()).append("\n");
            for (DashScopeAudioTranscription transcript : result.transcripts()) {
                logger.info("  - Channel: {}, Text: {}", transcript.getMetadata().channelId(), transcript.getText());
            }
        }

        logger.info("Paraformer ASR call test passed");
        return sb.toString();
    }

    /**
     * 录音文件识别：Fun-ASR (call)
     */
    @GetMapping("/asr/funasr/call")
    public String asrFunAsrCall() {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.FUN_ASR.getValue())
                .channelId(List.of(0))
                .diarizationEnabled(false)
                .speakerCount(2)
                .build();

        List<String> fileUrls = List.of(PARAFORMER_TEST_AUDIO_URL_1, PARAFORMER_TEST_AUDIO_URL_2);
        AudioTranscriptionPrompt prompt = new DashScopeAudioTranscriptionPrompt(options, fileUrls);

        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse asrResponse =
                (DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse) response;

        StringBuilder sb = new StringBuilder();
        sb.append("Fun-ASR call result:\n");

        for (DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse.TranscriptionResult result : asrResponse.getTranscriptionResults()) {
            sb.append("File URL: ").append(result.fileUrl()).append("\n");
            for (DashScopeAudioTranscription transcript : result.transcripts()) {
                logger.info("  - Channel: {}, Text: {}", transcript.getMetadata().channelId(), transcript.getText());
            }
        }

        logger.info("Fun-ASR call test passed");
        return sb.toString();
    }

    /**
     * 录音文件识别：Qwen-ASR (call)
     */
    @GetMapping("/asr/qwen/call")
    public String asrQwenCall() {
        DashScopeAudioTranscriptionOptions.AsrOptions asrOptions =
                new DashScopeAudioTranscriptionOptions.AsrOptions();
        asrOptions.setLanguage("zh");
        asrOptions.setEnableItn(false);

        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.QWEN3_ASR_FLASH.getValue())
                .asrOptions(asrOptions)
                .build();

        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio inputAudio =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio(QWEN_ASR_TEST_AUDIO_URL, null);
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content content =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content("input_audio", inputAudio);
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage message =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage(List.of(content));
        AudioTranscriptionPrompt prompt = new DashScopeAudioTranscriptionPrompt(options, message);

        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse dashScopeResponse =
                (DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse) response;

        String finalContent = dashScopeResponse.getChoices().get(0).message().content();

        logger.info("Qwen-ASR call test passed");
        logger.info("Response ID: {}", dashScopeResponse.getId());
        logger.info("Model: {}", dashScopeResponse.getModel());
        logger.info("Transcription content: {}", content);

        return "Qwen-ASR result: " + finalContent;
    }

    /**
     * 录音文件识别：Qwen-ASR (stream)
     */
    @GetMapping("/asr/qwen/stream")
    public Flux<String> asrQwenStream() {
        DashScopeAudioTranscriptionOptions.AsrOptions asrOptions =
                new DashScopeAudioTranscriptionOptions.AsrOptions();
        asrOptions.setLanguage("zh");
        asrOptions.setEnableItn(false);

        DashScopeAudioTranscriptionOptions.StreamOptions streamOptions =
                new DashScopeAudioTranscriptionOptions.StreamOptions();
        streamOptions.setIncludeUsage(true);

        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions.builder()
                .model(DashScopeModel.AudioModel.QWEN3_ASR_FLASH.getValue())
                .asrOptions(asrOptions)
                .streamOptions(streamOptions)
                .build();

        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio inputAudio =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.InputAudio(QWEN_ASR_TEST_AUDIO_URL, null);
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content content =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage.Content("input_audio", inputAudio);
        DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage message =
                new DashScopeAudioTranscriptionPrompt.TranscriptionUserMessage(List.of(content));
        AudioTranscriptionPrompt prompt = new DashScopeAudioTranscriptionPrompt(options, message);

        Flux<AudioTranscriptionResponse> result = transcriptionModel.stream(prompt);

        return result.map(response -> {
            DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse r =
                    (DashScopeTranscriptionApiSpec.DashScopeAudioTranscriptionResponse) response;

            StringBuilder sb = new StringBuilder();
            sb.append("Request ID: ").append(r.getId()).append("\n");

            if (r.getChoices() != null && !r.getChoices().isEmpty()) {
                var choice = r.getChoices().get(0);
                if (choice.delta() != null && choice.delta().content() != null) {
                    sb.append("Delta content: ").append(choice.delta().content()).append("\n");
                }
                if (choice.message() != null && choice.message().content() != null) {
                    sb.append("Message content: ").append(choice.message().content()).append("\n");
                }
            }

            if (r.getUsage() != null) {
                sb.append("Usage - Prompt: ").append(r.getUsage().promptTokens())
                  .append(", Completion: ").append(r.getUsage().completionTokens())
                  .append(", Total: ").append(r.getUsage().totalTokens()).append("\n");
            }

            return sb.toString();
        })
        .doOnComplete(() -> logger.info("Qwen-ASR stream test passed"));
    }
}
