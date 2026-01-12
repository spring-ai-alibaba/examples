package com.alibaba.example.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerNode implements NodeAction {

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\s*(.+?)\\s*\\}");

	private final String answerTemplate;

	private final String outputKey;

	private AnswerNode(String answerTemplate, String outputKey) {
		this.answerTemplate = answerTemplate;
		this.outputKey = outputKey;
	}

	@Override
	public Map<String, Object> apply(OverAllState state) {
		// Replace {{key}} in the answerTemplate with the value of state.get(key).
		StringBuffer sb = new StringBuffer();
		Matcher matcher = PLACEHOLDER_PATTERN.matcher(answerTemplate);
		while (matcher.find()) {
			String key = matcher.group(1);
			Object val = state.value(key).orElse("");
			String replacement = val != null ? val.toString() : "";
			replacement = replacement.replace("\\", "\\\\").replace("$", "\\$");
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		String resolved = sb.toString();

		// Write the final result back to the state with the key name fixed to "answer"
		Map<String, Object> result = new HashMap<>();
		result.put(this.outputKey, resolved);
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String answerTemplate;

		private String outputKey = "answer";

		public Builder answer(String answerTemplate) {
			this.answerTemplate = answerTemplate;
			return this;
		}

		public Builder outputKey(String outputKey) {
			this.outputKey = outputKey;
			return this;
		}

		public AnswerNode build() {
			return new AnswerNode(answerTemplate, outputKey);
		}

	}

}