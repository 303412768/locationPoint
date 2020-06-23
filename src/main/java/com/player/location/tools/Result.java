package com.player.location.tools;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    public Result() {
    }

    public Result(Object object, int code, String msg) {
        this.object = object;
        this.code = code;
        this.msg = msg;
    }

    public Object object;
    public int code;
    public String msg;

    public static Result ok(Object object) {
        Result result = new Result(object, StaticInfo.ok, null);
        return result;
    }
    public static Result error(Object object,String msg) {
        Result result = new Result(object, StaticInfo.error, msg);
        return result;
    }

}

