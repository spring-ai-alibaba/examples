/*
 * Copyright 2024-2026 the original author or authors.
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

package com.alibaba.cloud.ai.application.utils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.alibaba.cloud.ai.application.exception.SAAAppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public final class FilesUtils {

	private static final Logger logger = LoggerFactory.getLogger(FilesUtils.class);

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private static final Random RANDOM = new Random();

	private FilesUtils() {
	}

	private static String generateRandomFileName(String originalFilename) {

		String extension = "";
		int dotIndex = originalFilename.lastIndexOf(".");
		if (dotIndex > 0) {
			extension = originalFilename.substring(dotIndex);
		}

		return generateRandomString() + extension;
	}

	private static String generateRandomString() {

		StringBuilder result = new StringBuilder(8);

		for (int i = 0; i < 8; i++) {
			int index = RANDOM.nextInt(CHARACTERS.length());
			result.append(CHARACTERS.charAt(index));
		}

		return result.toString();
	}

	/**
	 * Init image and audio tmp folder
	 */
	public static void initTmpFolder(String path) {

		if (path == null || path.isEmpty()) {

			throw new SAAAppException("path is null or empty");
		}

		File tmpFolder = new File(path);
		if (!tmpFolder.exists()) {
			tmpFolder.mkdirs();
		}

		logger.info("Init tmp folder: {}", tmpFolder.getAbsolutePath());
	}

	public static void deleteDirectory(File directory) {

		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}

			directory.delete();
		}
	}

	/**
	 * save file to tmp folder
	 */
	public static String saveTempFile(MultipartFile file, String path) throws IOException {

		if (file == null || file.isEmpty()) {
			throw new SAAAppException("File is null or empty");
		}

		String randomFileName = generateRandomFileName(file.getOriginalFilename());
		String filePath = System.getProperty("user.dir") + path + randomFileName;

		file.transferTo(new File(filePath));

		return filePath;
	}

}
