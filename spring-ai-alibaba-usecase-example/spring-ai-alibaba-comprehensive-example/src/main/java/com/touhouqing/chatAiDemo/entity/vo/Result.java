package com.touhouqing.chatAiDemo.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result {
    private Integer ok;
    private String msg;

    private Result(Integer ok, String msg) {
        this.ok = ok;
        this.msg = msg;
    }

    public static Result ok() {
        return new Result(1, "ok");
    }

    public static Result fail(String msg) {
        return new Result(0, msg);
    }
}