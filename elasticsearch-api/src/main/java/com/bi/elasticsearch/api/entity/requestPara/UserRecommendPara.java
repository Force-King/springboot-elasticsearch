package com.bi.elasticsearch.api.entity.requestPara;

/**
 * @author CleverApe
 * @Classname UserRecommendPara
 * @Description 用户推荐参数对象
 * @Date 2019-07-23 15:38
 * @Version V1.0
 */
public class UserRecommendPara {
    private int uid;
    private String pids;
    private String createTime;

    public int getUid() {
        return uid;
    }

    public UserRecommendPara setUid(int uid) {
        this.uid = uid;
        return this;
    }

    public String getPids() {
        return pids;
    }

    public UserRecommendPara setPids(String pids) {
        this.pids = pids;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public UserRecommendPara setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "UserRecommendPara{" +
                "uid=" + uid +
                ", pids='" + pids + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
