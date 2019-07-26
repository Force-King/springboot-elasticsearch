package com.bi.elasticsearch.api.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * AppId 实体类
 */
public class AppId {

    private Integer id;
    private Integer innerId;
    private String outerId;
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getInnerId() {
        return innerId;
    }

    public void setInnerId(Integer innerId) {
        this.innerId = innerId;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    @Override
    public String toString() {
        return "AppId = " + JSONObject.toJSONString(this);
    }
}
