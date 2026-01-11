package com.alibaba.cloud.ai.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record Result(@JsonProperty(required = true, value = "status")
                     @JsonPropertyDescription("状态，1=需要补充信息;2=信息完善") String status,
                     @JsonProperty(value = "reply")
                     @JsonPropertyDescription("回复用户的信息，让用户补充槽位信息") String reply,
                     @JsonProperty(required = true, value = "slots")
                     @JsonPropertyDescription("槽位信息") SlotParams slots) {
    public record SlotParams(@JsonProperty(required = true, value = "departureDate")
                             @JsonPropertyDescription("出发日期, 格式 yyyy-MM-dd") String departureDate,
                             @JsonProperty(required = true, value = "departureCity")
                             @JsonPropertyDescription("出发城市") String departureCity,
                             @JsonProperty(required = true, value = "arrivalCity")
                             @JsonPropertyDescription("到达城市") String arrivalCity) {

    }

    public static Result empty() {
        return new Result("1", null, new SlotParams("", "", ""));
    }
}