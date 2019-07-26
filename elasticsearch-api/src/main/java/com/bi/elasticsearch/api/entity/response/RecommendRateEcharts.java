package com.bi.elasticsearch.api.entity.response;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author CleverApe
 * @Classname recommendRateEchartsResponse
 * @Description 推荐率Echarts图形值返回对象
 * @Date 2019-07-24 17:20
 * @Version V1.0
 */
@Component
public class RecommendRateEcharts {

    private StringBuffer xAxis;

    // 实名登陆日活List
    private List<BigDecimal> realNameUvList;

    // 可推荐用户数List
    private List<BigDecimal> recommendNumList;

    // 可推荐率List
    private List<BigDecimal> recommendRateList;

    public StringBuffer getxAxis() {
        return xAxis;
    }

    public RecommendRateEcharts setxAxis(StringBuffer xAxis) {
        this.xAxis = xAxis;
        return this;
    }

    public List<BigDecimal> getRealNameUvList() {
        return realNameUvList;
    }

    public RecommendRateEcharts setRealNameUvList(List<BigDecimal> realNameUvList) {
        this.realNameUvList = realNameUvList;
        return this;
    }

    public List<BigDecimal> getRecommendNumList() {
        return recommendNumList;
    }

    public RecommendRateEcharts setRecommendNumList(List<BigDecimal> recommendNumList) {
        this.recommendNumList = recommendNumList;
        return this;
    }

    public List<BigDecimal> getRecommendRateList() {
        return recommendRateList;
    }

    public RecommendRateEcharts setRecommendRateList(List<BigDecimal> recommendRateList) {
        this.recommendRateList = recommendRateList;
        return this;
    }

    @Override
    public String toString() {
        return "echarts{" +
                "xAxis=" + xAxis +
                ", realNameUvList=" + realNameUvList +
                ", recommendNumList=" + recommendNumList +
                ", recommendRateList=" + recommendRateList +
                '}';
    }
}
