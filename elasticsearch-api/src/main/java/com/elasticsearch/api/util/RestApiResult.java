package com.elasticsearch.api.util;

import com.alibaba.fastjson.JSONObject;
import com.elasticsearch.api.enums.RequestResultEnum;

/**
 * @author CleverApe
 * @Classname RestApiResult
 * @Description api返回结果对象
 * @Date 2019-03-05 15:49
 * @Version V1.0
 */
public class RestApiResult {

    //响应码
    private Integer code;
    //响应消息
    private String msg;
    //结果数据
    private JSONObject data;

    public static RestApiResult success() {
        return new RestApiResult(RequestResultEnum.SUCCESS.getCode(), RequestResultEnum.SUCCESS.getMsg(),null);
    }

    public static RestApiResult success(JSONObject data) {
        return new RestApiResult(RequestResultEnum.SUCCESS.getCode(), RequestResultEnum.SUCCESS.getMsg(), data);
    }

    public static RestApiResult faild() {
        return new RestApiResult(RequestResultEnum.FAILD.getCode(), RequestResultEnum.FAILD.getMsg(),null);
    }

    public static RestApiResult build(Integer code, String msg) {
        return new RestApiResult(code, msg,null);
    }

    public static RestApiResult build(Integer code, String msg, JSONObject data) {
        return new RestApiResult(code, msg,data);
    }

    public static RestApiResult buildEnum(ResultInterface re) {
        return new RestApiResult(re.getCode(),re.getMsg(),null);
    }

    public static RestApiResult buildEnum(ResultInterface re, JSONObject data) {
        return new RestApiResult(re.getCode(),re.getMsg(), data);
    }

    public RestApiResult(Integer code, String msg, JSONObject data) {
        this.code = code;
        this.msg = msg;
        this.data=data;
    }

    public Integer getCode() {
        return code;
    }

    public RestApiResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RestApiResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public JSONObject getData() {
        return data;
    }

    public RestApiResult setData(JSONObject data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "{\"code\":" + code + ",\"msg\":\"" + msg + "\",\"data\":" + data + "}";
    }


}
