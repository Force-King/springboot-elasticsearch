package com.bi.elasticsearch.api.enums;

import com.bi.elasticsearch.api.util.ResultInterface;

/**
 * @author CleverApe
 * @Classname RequestResultEnum
 * @Description 对外api返回结果码
 * @Date 2019-03-05 11:48
 * @Version V1.0
 */
public enum RequestResultEnum implements ResultInterface {

    SUCCESS(0,"处理成功"),
    FAILD(1,"处理失败"),
    SERVER_EXP(2,"服务异常，请稍后重试！"),
    SERVER_BUSY(3,"SERVER BUSY, 请稍后重试！"),
    REPEAT(4,"排重数据"),
    PARAMETER_IS_NULL(5,"参数为空"),
    PARAMETER_IS_ERROR(6,"参数格式不正确");


    private int code;
    private String msg;

    RequestResultEnum(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
    @Override
    public int getCode() {
        return code;
    }

    public RequestResultEnum setCode(int code) {
        this.code = code;
        return this;
    }
    @Override
    public String getMsg() {
        return msg;
    }

    public RequestResultEnum setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
