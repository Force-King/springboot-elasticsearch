package com.bi.elasticsearch.api.util;

import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.json.GsonUtil;

public class EnhancedOption extends GsonOption {
    private String filepath;

    /**
     * 输出到控制台
     */
    public void print() {
        GsonUtil.print(this);
    }

    /**
     * 输出到控制台
     */
    public void printPretty() {
        GsonUtil.printPretty(this);
    }
}
