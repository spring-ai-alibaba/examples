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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.cloud.ai.application.entity.dashscope.DashScopeModel;
import com.alibaba.cloud.ai.application.entity.dashscope.DashScopeModels;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public final class ModelsUtils {

	private final static String MODELS_FILE_PATH = "models.yaml";

	private static final String MODEL = "model";

	private static final String DESC = "desc";

	private ModelsUtils() {
	}

	public static Set<Map<String, String>> getDashScopeModels() throws IOException {

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		InputStream resourceAsStream = ModelsUtils.class.getClassLoader().getResourceAsStream(MODELS_FILE_PATH);
		DashScopeModels models = mapper.readValue(resourceAsStream, DashScopeModels.class);

		Set<Map<String, String>> resultSet = new HashSet<>();
		for (DashScopeModel model : models.getDashScope()) {
			Map<String, String> modelMap = new HashMap<>();
			modelMap.put(MODEL, model.getName());
			modelMap.put(DESC, model.getDescription());
			resultSet.add(modelMap);
		}

		return resultSet;
	}

}
