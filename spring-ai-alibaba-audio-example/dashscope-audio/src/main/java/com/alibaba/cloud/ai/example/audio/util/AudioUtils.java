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
package com.alibaba.cloud.ai.example.audio.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

/**
 * @author yingzi
 * @since 2026/1/31
 */

public class AudioUtils {

    private static final Logger logger = LoggerFactory.getLogger(AudioUtils.class);

    /**
     * 从 URL 下载音频文件并保存到本地
     *
     * @param audioUrl 音频文件 URL
     * @param outputPath 相对输出文件路径 (相对于项目根目录)
     * @throws IOException 下载或保存失败时抛出
     */
    public static void saveAudioFromUrl(String audioUrl, String outputPath) throws IOException {
        logger.info("Downloading audio from URL: {}", audioUrl);

        // 确保输出目录存在
        Path path = Paths.get(outputPath);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 使用 HttpClient 下载文件
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(audioUrl))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request,
                    HttpResponse.BodyHandlers.ofInputStream());

            // 保存文件
            try (InputStream in = response.body();
                 var out = Files.newOutputStream(path)) {
                in.transferTo(out);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Download interrupted", e);
        }

        logger.info("Audio saved to: {} (size: {} bytes)", path.toAbsolutePath(), Files.size(path));
    }

    /**
     * 将原始音频字节数组保存为文件
     * <p>适用于 WebSocket 流式传输返回的原始音频数据（如 CosyVoice、Sambert 模型）</p>
     *
     * @param audioData 原始音频字节数组（已编码的音频格式，如 MP3）
     * @param outputPath 相对输出文件路径 (相对于项目根目录)
     * @throws IOException 保存失败时抛出
     */
    public static void saveAudioFromBytes(byte[] audioData, String outputPath) throws IOException {
        logger.info("Saving audio from byte array ({} bytes)", audioData.length);

        // 确保输出目录存在
        Path path = Paths.get(outputPath);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 直接保存字节数组
        Files.write(path, audioData);

        logger.info("Audio saved to: {} (size: {} bytes)", path.toAbsolutePath(), audioData.length);
    }

    /**
     * 将多个音频字节数组拼接后保存为文件
     * <p>适用于 WebSocket 流式传输返回的多个音频数据块</p>
     *
     * @param audioChunks 音频数据块列表
     * @param outputPath 相对输出文件路径 (相对于项目根目录)
     * @throws IOException 保存失败时抛出
     */
    public static void saveAudioFromByteChunks(List<byte[]> audioChunks, String outputPath) throws IOException {
        // 计算总大小
        int totalSize = audioChunks.stream().mapToInt(chunk -> chunk.length).sum();
        logger.info("Concatenating {} audio chunks (total: {} bytes)", audioChunks.size(), totalSize);

        // 拼接所有音频块
        byte[] combinedAudio = new byte[totalSize];
        int offset = 0;
        for (byte[] chunk : audioChunks) {
            System.arraycopy(chunk, 0, combinedAudio, offset, chunk.length);
            offset += chunk.length;
        }

        // 保存合并后的音频
        saveAudioFromBytes(combinedAudio, outputPath);
    }

    /**
     * 将多个音频字节数组合并为单个字节数组
     * <p>适用于需要在内存中处理完整音频数据的场景</p>
     *
     * @param audioChunks 音频数据块列表
     * @return 合并后的音频字节数组
     */
    public static byte[] mergeAudioChunks(List<byte[]> audioChunks) {
        if (audioChunks == null || audioChunks.isEmpty()) {
            return new byte[0];
        }
        
        // 计算总大小
        int totalSize = audioChunks.stream().mapToInt(chunk -> chunk.length).sum();
        logger.debug("Merging {} audio chunks (total: {} bytes)", audioChunks.size(), totalSize);

        // 拼接所有音频块
        byte[] combinedAudio = new byte[totalSize];
        int offset = 0;
        for (byte[] chunk : audioChunks) {
            System.arraycopy(chunk, 0, combinedAudio, offset, chunk.length);
            offset += chunk.length;
        }

        logger.debug("Successfully merged audio chunks");
        return combinedAudio;
    }

    /**
     * 将 Base64 编码的音频数据保存为 WAV 文件
     *
     * @param base64Data Base64 编码的音频数据 (PCM 格式)
     * @param outputPath 相对输出文件路径 (相对于项目根目录)
     * @throws IOException 解码或保存失败时抛出
     */
    public static void saveAudioFromBase64(String base64Data, String outputPath) throws IOException {
        logger.info("Decoding Base64 audio data ({} chars)", base64Data.length());

        // 确保输出目录存在
        Path path = Paths.get(outputPath);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 解码 Base64 数据获取 PCM 音频数据
        byte[] pcmData = Base64.getDecoder().decode(base64Data);

        // 从文件路径推断音频参数（如果文件名包含提示）
        // 默认参数（根据 Qwen-TTS 文档）
        int sampleRate = 24000;  // 24kHz
        int channels = 1;         // mono
        int bitsPerSample = 16;   // 16-bit
        int byteRate = sampleRate * channels * bitsPerSample / 8;

        // 创建 WAV 文件
        byte[] wavData = createWavHeader(pcmData.length, sampleRate, channels, bitsPerSample);
        byte[] fileData = new byte[wavData.length + pcmData.length];
        System.arraycopy(wavData, 0, fileData, 0, wavData.length);
        System.arraycopy(pcmData, 0, fileData, wavData.length, pcmData.length);

        // 保存文件
        Files.write(path, fileData);

        logger.info("WAV audio saved to: {} (PCM: {} bytes, total: {} bytes, {} Hz, {} ch, {} bit)",
                path.toAbsolutePath(), pcmData.length, fileData.length, sampleRate, channels, bitsPerSample);
    }

    /**
     * 创建 WAV 文件头
     *
     * @param dataSize PCM 数据大小（字节）
     * @param sampleRate 采样率 (Hz)
     * @param channels 声道数 (1=mono, 2=stereo)
     * @param bitsPerSample 每样本位数 (8 或 16)
     * @return WAV 文件头的字节数组
     */
    private static byte[] createWavHeader(int dataSize, int sampleRate, int channels, int bitsPerSample) {
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        byte[] header = new byte[44]; // WAV 标准头大小是 44 字节

        // RIFF chunk header
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // file size - 8
        int fileSize = 36 + dataSize;
        header[4] = (byte) (fileSize & 0xFF);
        header[5] = (byte) ((fileSize >> 8) & 0xFF);
        header[6] = (byte) ((fileSize >> 16) & 0xFF);
        header[7] = (byte) ((fileSize >> 24) & 0xFF);

        // WAVE format
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        // fmt chunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        // fmt chunk size (16 for PCM)
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

        // Audio format (1 = PCM)
        header[20] = 1;
        header[21] = 0;

        // Number of channels
        header[22] = (byte) channels;
        header[23] = 0;

        // Sample rate
        header[24] = (byte) (sampleRate & 0xFF);
        header[25] = (byte) ((sampleRate >> 8) & 0xFF);
        header[26] = (byte) ((sampleRate >> 16) & 0xFF);
        header[27] = 0;

        // Byte rate
        header[28] = (byte) (byteRate & 0xFF);
        header[29] = (byte) ((byteRate >> 8) & 0xFF);
        header[30] = (byte) ((byteRate >> 16) & 0xFF);
        header[31] = 0;

        // Block align
        header[32] = (byte) blockAlign;
        header[33] = 0;

        // Bits per sample
        header[34] = (byte) bitsPerSample;
        header[35] = 0;

        // Data chunk
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        // Data size
        header[40] = (byte) (dataSize & 0xFF);
        header[41] = (byte) ((dataSize >> 8) & 0xFF);
        header[42] = (byte) ((dataSize >> 16) & 0xFF);
        header[43] = (byte) ((dataSize >> 24) & 0xFF);

        return header;
    }


}
