package com.alibaba.cloud.ai.example.audio;

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeTTSApiSpec;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.alibaba.cloud.ai.example.audio.util.AudioUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.Speech;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yingzi, yuluo
 * @since 2026/2/6
 * 章节内容：语音合成
 */
@RestController
@RequestMapping("/ai/video/tts")
public class TTSController {

    private static final Logger logger = LoggerFactory.getLogger(TTSController.class);

    private final TextToSpeechModel textToSpeechModel;

    private static final String TEST_TEXT = "那我来给大家推荐一款T恤，这款呢真的是超级好看";

    private final String SAVE_PATH = System.getProperty("user.dir") + "/spring-ai-alibaba-audio-example/dashscope-audio/src/main/resources/";


    public TTSController(@Qualifier("dashScopeSpeechSynthesisModel") TextToSpeechModel textToSpeechModel) {
        this.textToSpeechModel = textToSpeechModel;
    }

    /**
     * 实时语音合成Sambert：WebSocket的stream流形态
     * 返回Flux流，让客户端实时接收音频数据
     */
    @GetMapping("/sambert/stream")
    public Flux<byte[]> sambertStream() {
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .model(DashScopeModel.AudioModel.SAMBERT_ZHICHU_V1.getValue())
                .textType("PlainText")
                .format("mp3")
                .sampleRate(16000)
                .volume(50)
                .rate(1f)
                .pitch(1f)
                .wordTimestampEnabled(true)
                .phonemeTimestampEnabled(true)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
        
        // 直接返回Flux流，Spring会自动处理流式传输
        return textToSpeechModel.stream(prompt)
                .map(response -> {
                    Speech speech = response.getResult();
                    return speech.getOutput();
                })
                .doOnNext(chunk -> logger.debug("Received audio chunk, size: {} bytes", chunk.length))
                .doOnComplete(() -> logger.info("Sambert stream completed"))
                .doOnError(error -> logger.error("Error in sambert stream: {}", error.getMessage()));
    }

    /**
     * 实时语音合成Sambert：收集完整音频后返回
     * 适用于需要完整音频文件的场景
     */
    @GetMapping("/sambert/complete")
    public Mono<byte[]> sambertComplete() {
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .model(DashScopeModel.AudioModel.SAMBERT_ZHICHU_V1.getValue())
                .textType("PlainText")
                .format("mp3")
                .sampleRate(16000)
                .volume(50)
                .rate(1f)
                .pitch(1f)
                .wordTimestampEnabled(true)
                .phonemeTimestampEnabled(true)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
        List<byte[]> speechChunks = new ArrayList<>();

        // 使用Flux.subscribe()方式处理流
        return textToSpeechModel.stream(prompt)
                .doOnNext(response -> {
                    Speech speech = response.getResult();
                    speechChunks.add(speech.getOutput());
                    logger.debug("Collected audio chunk, total chunks: {}", speechChunks.size());
                })
                .doOnComplete(() -> logger.info("Collection completed, total chunks: {}", speechChunks.size()))
                .doOnError(error -> logger.error("Error collecting audio: {}", error.getMessage()))
                .then(Mono.fromCallable(() -> {
                    // 合并所有音频片段
                    if (!speechChunks.isEmpty()) {
                        byte[] mergedAudio = AudioUtils.mergeAudioChunks(speechChunks);
                        logger.info("Successfully merged audio, total size: {} bytes", mergedAudio.length);

                        // 可选：保存到文件
                        String outputPath = SAVE_PATH + "/websocket/sambert-complete.mp3";
                        try {
                            AudioUtils.saveAudioFromByteChunks(speechChunks, outputPath);
                            logger.info("Audio saved to: {}", Paths.get(outputPath).toAbsolutePath());
                        } catch (IOException e) {
                            logger.error("Failed to save audio file: {}", e.getMessage());
                        }

                        return mergedAudio;
                    } else {
                        logger.warn("No audio chunks collected");
                        return new byte[0];
                    }
                }));
    }

    // ==================== Qwen-TTS Model Methods ====================

    /**
     * Qwen3-TTS-Flash 语音合成 - 同步调用
     * 返回音频 URL，可用于下载音频文件
     */
    @GetMapping("/qwen3-tts/call")
    public String qwen3TtsCall() {
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .model(DashScopeModel.AudioModel.QWEN3_TTS_FLASH.getValue())
                .voice("Cherry")
                .languageType("Chinese")
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
        TextToSpeechResponse response = textToSpeechModel.call(prompt);

        DashScopeTTSApiSpec.DashScopeAudioTTSResponse dashScopeResponse =
                (DashScopeTTSApiSpec.DashScopeAudioTTSResponse) response;

        logger.info("Qwen3-tts-flash call test passed");
        logger.info("Audio URL: {}", dashScopeResponse.getOutput().audio().url());

        // 保存url音频文件
        String audioUrl = dashScopeResponse.getOutput().audio().url();
        String outputPath = SAVE_PATH + "/tts/call-url-test.wav";
        try {
            AudioUtils.saveAudioFromUrl(audioUrl, outputPath);
            logger.info("Audio saved from URL to: {}", Paths.get(outputPath).toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to save audio from URL: {}", e.getMessage());
        }

        return "Audio URL: " + audioUrl + ", saved to: " + Paths.get(outputPath).toAbsolutePath();
    }

    /**
     * Qwen3-TTS-Flash 语音合成 - 流式调用
     * 返回 Base64 音频数据块和音频 URL
     */
    @GetMapping("/qwen3-tts/stream")
    public Flux<String> qwen3TtsStream() {
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .model(DashScopeModel.AudioModel.QWEN3_TTS_FLASH.getValue())
                .voice("Cherry")
                .languageType("Chinese")
                .build();

        // 用于收集 Base64 音频数据块
        List<String> base64Chunks = new ArrayList<>();
        AtomicReference<String> audioUrlAtomic = new AtomicReference<>("");

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(TEST_TEXT, options);
        Flux<TextToSpeechResponse> result = textToSpeechModel.stream(prompt);

        return result.map(response -> {
            DashScopeTTSApiSpec.DashScopeAudioTTSResponse r = (DashScopeTTSApiSpec.DashScopeAudioTTSResponse) response;

            DashScopeTTSApiSpec.DashScopeAudioTTSResponse.TTSAudio audio = r.getOutput().audio();
            StringBuilder sb = new StringBuilder();
            sb.append("Request ID: ").append(r.getRequestId()).append("\n");

            if (audio != null) {
                if (audio.data() != null && !audio.data().isEmpty()) {
                    base64Chunks.add(audio.data());
                    sb.append("Base64 data length: ").append(audio.data().length()).append(" chars\n");
                }
                if (audio.url() != null && !audio.url().isEmpty()) {
                    logger.info("Audio URL: {}", audio.url());
                    audioUrlAtomic.set(audio.url());
                    sb.append("Audio URL: ").append(audio.url()).append("\n");
                }
            }

            logger.info("Qwen3-tts-flash stream response: {}", sb);

            return sb.toString();
        })
        .doOnComplete(() -> {
            // 保存base64音频文件
            if (!base64Chunks.isEmpty()) {
                String combinedBase64 = String.join("", base64Chunks);
                String outputPath = SAVE_PATH + "/tts/stream-binary-test.wav";
                try {
                    AudioUtils.saveAudioFromBase64(combinedBase64, outputPath);
                    logger.info("Audio saved to: {}", Paths.get(outputPath).toAbsolutePath());
                    logger.info("Total Base64 chunks: {}", base64Chunks.size());
                } catch (IOException e) {
                    logger.error("Failed to save audio: {}", e.getMessage());
                }
            }

            // 保存url音频文件
            String audioUrl = audioUrlAtomic.get();
            if (audioUrl != null && !audioUrl.isEmpty()) {
                String outputPath = SAVE_PATH + "/tts/stream-url-test.wav";
                try {
                    AudioUtils.saveAudioFromUrl(audioUrl, outputPath);
                    logger.info("Audio saved from URL to: {}", Paths.get(outputPath).toAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to save audio from URL: {}", e.getMessage());
                }
            }
            logger.info("Qwen3-tts-flash stream test passed");
        });
    }


}
