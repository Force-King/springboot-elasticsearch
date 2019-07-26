package com.bi.elasticsearch.api.entity;

/**
 * @author CleverApe
 * @Classname HourPeriod
 * @Description 时间段对象
 * @Date 2019-07-24 16:29
 * @Version V1.0
 */
public class HourPeriod {

    private int index;

    private String startTime;

    private String endTime;

    private String periodTime;

    public int getIndex() {
        return index;
    }

    public HourPeriod setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public HourPeriod setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public HourPeriod setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getPeriodTime() {
        return periodTime;
    }

    public HourPeriod setPeriodTime(String periodTime) {
        this.periodTime = periodTime;
        return this;
    }
}
