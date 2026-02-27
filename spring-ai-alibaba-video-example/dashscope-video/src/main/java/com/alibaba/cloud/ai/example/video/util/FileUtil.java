package com.alibaba.cloud.ai.example.video.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author yingzi
 * @since 2026/1/13
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static final int BUFFER_SIZE = 8192;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;

    /**
     * 从 URL 下载文件并保存到本地
     *
     * @param url      远程文件的 URL 地址
     * @param filePath 本地保存路径（包含文件名）
     * @return 下载成功返回 true，失败返回 false
     */
    public static boolean url2File(String url, String filePath) {
        if (url == null || url.trim().isEmpty()) {
            logger.error("URL 不能为空");
            return false;
        }

        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error("文件路径不能为空");
            return false;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // 创建目标文件的父目录
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                logger.info("创建目录: {}", parentDir);
            }

            // 建立连接
            URL fileUrl = new URL(url);
            connection = (HttpURLConnection) fileUrl.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // 检查响应码
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                logger.error("下载失败，HTTP 响应码: {}", responseCode);
                return false;
            }

            // 获取文件大小
            long contentLength = connection.getContentLengthLong();
            logger.info("开始下载文件，大小: {} bytes, URL: {}", contentLength, url);

            // 读取输入流并写入文件
            inputStream = new BufferedInputStream(connection.getInputStream());
            outputStream = new BufferedOutputStream(new FileOutputStream(filePath));

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            outputStream.flush();
            logger.info("文件下载成功，保存到: {}, 总大小: {} bytes", filePath, totalBytesRead);
            return true;

        } catch (IOException e) {
            logger.error("下载文件失败，URL: {}, 保存路径: {}, 错误: {}", url, filePath, e.getMessage(), e);
            return false;
        } finally {
            // 关闭资源
            closeQuietly(outputStream);
            closeQuietly(inputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 安静地关闭流，忽略异常
     *
     * @param closeable 可关闭的资源
     */
    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.warn("关闭流时发生异常: {}", e.getMessage());
            }
        }
    }

}
