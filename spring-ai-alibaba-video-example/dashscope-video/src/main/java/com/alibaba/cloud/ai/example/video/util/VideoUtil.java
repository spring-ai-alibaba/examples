package com.alibaba.cloud.ai.example.video.util;

/**
 * @author yingzi
 * @since 2026/1/13
 */
public class VideoUtil {

    /**
     * 从 URL 中提取文件扩展名
     *
     * @param videoUrl 文件 URL
     * @return 文件扩展名（包含点号），如果无法提取则返回 .mp4
     */
    public static String getVideoExtension(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return ".mp4";
        }

        // 移除 URL 参数
        int queryIndex = videoUrl.indexOf('?');
        if (queryIndex != -1) {
            videoUrl = videoUrl.substring(0, queryIndex);
        }

        // 提取扩展名
        int lastDotIndex = videoUrl.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < videoUrl.length() - 1) {
            return videoUrl.substring(lastDotIndex);
        }

        return ".mp4"; // 默认扩展名
    }

}
