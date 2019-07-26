package com.bi.elasticsearch.api.entity.response;

import java.math.BigDecimal;

/**
 * @author CleverApe
 * @Classname RecommendRateDetails
 * @Description 推荐率详情
 * @Date 2019-07-25 11:09
 * @Version V1.0
 */
public class RecommendRateDetails {

    //时间段
    private String periodTime;
    //实名登陆日活
    private BigDecimal realNameUv;
    // 可推荐用户数
    private BigDecimal recommendNum;
    // 可推荐率
    private BigDecimal recommendRate;

    public String getPeriodTime() {
        return periodTime;
    }

    public RecommendRateDetails setPeriodTime(String periodTime) {
        this.periodTime = periodTime;
        return this;
    }

    public BigDecimal getRealNameUv() {
        return realNameUv;
    }

    public RecommendRateDetails setRealNameUv(BigDecimal realNameUv) {
        this.realNameUv = realNameUv;
        return this;
    }

    public BigDecimal getRecommendNum() {
        return recommendNum;
    }

    public RecommendRateDetails setRecommendNum(BigDecimal recommendNum) {
        this.recommendNum = recommendNum;
        return this;
    }

    public BigDecimal getRecommendRate() {
        return recommendRate;
    }

    public RecommendRateDetails setRecommendRate(BigDecimal recommendRate) {
        this.recommendRate = recommendRate;
        return this;
    }

    @Override
    public String toString() {
        return "details{" +
                "periodTime='" + periodTime + '\'' +
                ", realNameUv=" + realNameUv +
                ", recommendNum=" + recommendNum +
                ", recommendRate=" + recommendRate +
                '}';
    }
}
