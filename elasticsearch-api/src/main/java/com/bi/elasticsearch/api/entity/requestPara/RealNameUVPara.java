package com.bi.elasticsearch.api.entity.requestPara;

/**
 * @author CleverApe
 * @Classname RealNameUVPara
 * @Description 实名UV数据对象
 * @Date 2019-07-23 15:38
 * @Version V1.0
 */
public class RealNameUVPara {
    private int uid;
    private String uName;
    private String createTime;

    public int getUid() {
        return uid;
    }

    public RealNameUVPara setUid(int uid) {
        this.uid = uid;
        return this;
    }

    public String getuName() {
        return uName;
    }

    public RealNameUVPara setuName(String uName) {
        this.uName = uName;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public RealNameUVPara setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "RealNameUVPara{" +
                "uid=" + uid +
                ", uName='" + uName + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
