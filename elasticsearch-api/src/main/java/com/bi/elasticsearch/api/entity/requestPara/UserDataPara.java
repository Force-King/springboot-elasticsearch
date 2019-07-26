package com.bi.elasticsearch.api.entity.requestPara;

/**
 * @author CleverApe
 * @Classname UserDataPara
 * @Description 用户
 * @Date 2019-07-23 14:40
 * @Version V1.0
 */
public class UserDataPara {

    private int uid;
    private String uName;
    private int pid;
    // 开始日期
    private String startTime;
    // 结束时间
    private String endTime;
    // 排序的字段
    private String orderField;


    public int getUid() {
        return uid;
    }

    public UserDataPara setUid(int uid) {
        this.uid = uid;
        return this;
    }

    public String getuName() {
        return uName;
    }

    public UserDataPara setuName(String uName) {
        this.uName = uName;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public UserDataPara setPid(int pid) {
        this.pid = pid;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public UserDataPara setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public UserDataPara setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getOrderField() {
        return orderField;
    }

    public UserDataPara setOrderField(String orderField) {
        this.orderField = orderField;
        return this;
    }

    @Override
    public String toString() {
        return "UserDataPara{" +
                "uid=" + uid +
                ", uName='" + uName + '\'' +
                ", pid=" + pid +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", orderField='" + orderField + '\'' +
                '}';
    }
}
