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

package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MergeNode: 合并parallel_output1和parallel_output2为一个字符串，输出到outputKey。
 */
public class MergeNode implements NodeAction {

	private static final Logger logger = LoggerFactory.getLogger(MergeNode.class);

	private final List<String> inputKeys;

	private final String outputKey;

	public MergeNode(List<String> inputKeys, String outputKey) {
		this.inputKeys = inputKeys;
		this.outputKey = outputKey;
	}

	@Override
	public Map<String, Object> apply(OverAllState state) {
		logger.info("MergeNode is running, inputKeys:{} outputKey:{}, state: {}", JSON.toJSONString(inputKeys),
				outputKey, JSON.toJSONString(state));
		StringBuilder merged = new StringBuilder();
		for (String inputKey : inputKeys) {
			String s = state.value(inputKey).map(Object::toString).orElse("");
			logger.info("Merging inputKey: {}, value: {}", inputKey, s);
			merged.append(s).append("\n");
		}

		Map<String, Object> result = new HashMap<>();
		result.put(outputKey, merged);
		result.put("logs", "[MergeNode] Node execution completed");
		return result;
	}

}
