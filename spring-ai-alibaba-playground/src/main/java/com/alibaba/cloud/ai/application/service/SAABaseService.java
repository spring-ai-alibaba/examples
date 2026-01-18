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

package com.alibaba.cloud.ai.application.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.alibaba.cloud.ai.application.exception.SAAAppException;
import com.alibaba.cloud.ai.application.utils.ModelsUtils;

import org.springframework.stereotype.Service;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Service
public class SAABaseService {

	public Set<Map<String, String>> getDashScope() {

		Set<Map<String, String>> resultSet;

		try {
			resultSet = ModelsUtils.getDashScopeModels();
		}
		catch (IOException e) {
			throw new SAAAppException("Get DashScope Model failed, " + e.getMessage());
		}

		return resultSet;
	}

}
