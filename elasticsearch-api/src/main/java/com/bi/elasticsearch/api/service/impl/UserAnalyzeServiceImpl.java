package com.bi.elasticsearch.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bi.elasticsearch.api.enums.ESIndexTermType;
import com.bi.elasticsearch.api.service.es.ESIndexContext;
import com.bi.elasticsearch.api.service.es.ESIndexTerm;
import com.bi.elasticsearch.api.util.DateUtil;
import com.bi.elasticsearch.api.entity.HourPeriod;
import com.bi.elasticsearch.api.entity.requestPara.RealNameUVPara;
import com.bi.elasticsearch.api.entity.requestPara.UserDataPara;
import com.bi.elasticsearch.api.entity.requestPara.UserRecommendPara;
import com.bi.elasticsearch.api.entity.response.RecommendRateDetails;
import com.bi.elasticsearch.api.entity.response.RecommendRateEcharts;
import com.bi.elasticsearch.api.service.RedisService;
import com.bi.elasticsearch.api.service.UserAnalyzeService;
import com.bi.elasticsearch.api.service.es.ESIndexOperater;
import com.bi.elasticsearch.api.util.EnhancedOption;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.series.Line;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author CleverApe
 * @Classname UserAnalyzeServiceImpl
 * @Description 用户行为分析接口实现类
 * @Date 2019-07-23 14:27
 * @Version V1.0
 */
@Service
public class UserAnalyzeServiceImpl implements UserAnalyzeService {

    private Logger logger = LogManager.getLogger(UserAnalyzeServiceImpl.class);

    @Autowired
    private ESIndexOperater esIndexOperater;

    @Autowired
    private RedisService redisService;

    @Value("${index.name.real.name.uv}")
    private String realNameUV_IndexName;
    @Value("${index.type.real.name.uv}")
    private String realNameUV_Type;

    @Value("${index.name.user.recommend}")
    private String recommend_IndexName;
    @Value("${index.type.user.recommend}")
    private String recommend_Type;

    @Value("${spring.cache.redis.time-to-live}")
    private int expireTimes;

    private static String REDIS_KEY_REAL_NAME_UV = "show:analyze:real:name:uv:";
    private static String REDIS_KEY_USER_RECOMMEND_NUM = "show:analyze:user:recommend:num:";

    @Resource
    private RecommendRateEcharts recommendRateEcharts;

    @Override
    public JSONObject getRecommendRateData(Integer pid, Date date) {

        UserDataPara para = new UserDataPara().setPid(pid);

        // x轴数据
        StringBuffer xAxis = new StringBuffer();
        List<BigDecimal> realNameUvList = new ArrayList<>();
        List<BigDecimal> recommendNumList = new ArrayList<>();
        List<BigDecimal> recommendRateList = new ArrayList<>();
        List<RecommendRateDetails> detailsList = new ArrayList<>();

        //获取需要查询的时间段list
        ArrayList<HourPeriod> list = DateUtil.getToNowHourPeriodList(date);

        for(HourPeriod hourPer : list) {
            para.setStartTime(hourPer.getStartTime()).setEndTime(hourPer.getEndTime());

            /*********************** 查询该时间段的实名登陆日活 ********************************/
            String realNameKey = REDIS_KEY_REAL_NAME_UV + hourPer.getPeriodTime();
            String cacheTotalUvStr = redisService.get(realNameKey);
            BigDecimal totalUv;
            if(StringUtils.isBlank(cacheTotalUvStr)) {
                JSONObject realNameObj = this.getRealNameLoginNumForHour(para);
                totalUv = esIndexOperater.getAggregationValueByField(realNameObj,"uid");
                //存入缓存
                redisService.setex(realNameKey, totalUv.toString(),expireTimes);
            } else {
                totalUv = new BigDecimal(cacheTotalUvStr);
            }

            /*********************** 查询可推荐数 ********************************/
            String recommendKey = REDIS_KEY_USER_RECOMMEND_NUM + pid +":"+ hourPer.getPeriodTime();
            String recommendNumStr = redisService.get(recommendKey);
            BigDecimal totalRecommend;
            if(StringUtils.isBlank(recommendNumStr)) {
                JSONObject recommendObj = this.getUserRecommendNumForHour(para);
                totalRecommend = esIndexOperater.getAggregationValueByField(recommendObj,"uid");
                //存入缓存
                redisService.setex(recommendKey, totalRecommend.toString(),expireTimes);
            } else {
                totalRecommend = new BigDecimal(recommendNumStr);
            }

            /*********************** 计算可推荐率 ********************************/
            BigDecimal recommendRate = BigDecimal.ZERO;
            if(totalUv.compareTo(BigDecimal.ZERO) >0 && totalRecommend.compareTo(BigDecimal.ZERO)>0) {
                recommendRate = totalRecommend.divide(totalUv,2,BigDecimal.ROUND_HALF_UP);
            }
            xAxis.append(hourPer.getPeriodTime()).append(",");
            realNameUvList.add(totalUv);
            recommendNumList.add(totalRecommend);
            recommendRateList.add(recommendRate);

            /***********************  封装明细 ********************************/
            RecommendRateDetails details = new RecommendRateDetails();
            details.setPeriodTime(hourPer.getPeriodTime()).setRealNameUv(totalUv).setRecommendNum(totalRecommend)
                    .setRecommendRate(recommendRate);
            detailsList.add(details);
        }
        //封装 Echarts 数据
        recommendRateEcharts.setxAxis(xAxis).setRealNameUvList(realNameUvList)
                .setRecommendNumList(recommendNumList).setRecommendRateList(recommendRateList);
        JSONObject echartsJson = this.recommendRateEchartsJsonData(recommendRateEcharts);

        //汇总结果
        JSONObject allObj = new JSONObject();
        allObj.put("echarts",echartsJson);
        allObj.put("details",detailsList);
        return allObj;
    }

    /**
     * 查询时间段内首页实名UV
     * @param userDataPara
     * @return
     */
    @Override
    public JSONObject getRealNameLoginNumForHour(UserDataPara userDataPara) {
        //从ES查询startTime 到 endTime 之间的数据
        ESIndexContext indexContext = new ESIndexContext();

        ESIndexTerm uidTerm = new ESIndexTerm();
        uidTerm.setType(ESIndexTermType.AGGREGATION_DISTINCT_TYPE);
        uidTerm.setField("uid");
        uidTerm.setFieldName("uid");
        indexContext.addContext(uidTerm);

        ESIndexTerm timeTerm = new ESIndexTerm();
        timeTerm.setType(ESIndexTermType.RANGE_TYPE);
        timeTerm.setField("createTime");
        timeTerm.setRangeBegin(userDataPara.getStartTime());
        timeTerm.setRangeEnd(userDataPara.getEndTime());
        indexContext.addContext(timeTerm);

        String result = esIndexOperater.getDocumentResult(realNameUV_IndexName,realNameUV_Type,indexContext).getJsonString();
        JSONObject resultObject = JSON.parseObject(result);
        return resultObject;
    }


    /**
     * 查询时间段内可见产品的用户数
     * @param userDataPara
     * @return
     */
    @Override
    public JSONObject getUserRecommendNumForHour(UserDataPara userDataPara) {
        //从ES查询startTime 到 endTime 之间的数据
        ESIndexContext indexContext = new ESIndexContext();

        ESIndexTerm uidTerm = new ESIndexTerm();
        uidTerm.setType(ESIndexTermType.AGGREGATION_DISTINCT_TYPE);
        uidTerm.setField("uid");
        uidTerm.setFieldName("uid");
        indexContext.addContext(uidTerm);

        ESIndexTerm pidTerm = new ESIndexTerm();
        pidTerm.setType(ESIndexTermType.TERM_TYPE);
        pidTerm.setField("pIds");
        pidTerm.setValue(userDataPara.getPid());
        indexContext.addContext(pidTerm);

        ESIndexTerm timeTerm = new ESIndexTerm();
        timeTerm.setType(ESIndexTermType.RANGE_TYPE);
        timeTerm.setField("createTime");
        timeTerm.setRangeBegin(userDataPara.getStartTime());
        timeTerm.setRangeEnd(userDataPara.getEndTime());
        indexContext.addContext(timeTerm);

        String result = esIndexOperater.getDocumentResult(recommend_IndexName,recommend_Type,indexContext).getJsonString();
        JSONObject resultObject = JSON.parseObject(result);
        return resultObject;
    }



    @Override
    public boolean addRealNameUVDataToES(RealNameUVPara realNameUVPara) {
        try {
            Map<String, Object> obj = new HashMap<>();
            obj.put("uid",realNameUVPara.getUid());
            obj.put("uName",realNameUVPara.getuName());
            obj.put("createTime",realNameUVPara.getCreateTime());
            return esIndexOperater.addDocument(realNameUV_IndexName,realNameUV_Type,obj);
        } catch (Exception e) {
            logger.error("实名用户首页UV数据存入ES异常，realNameUVPara={}, Exception:",realNameUVPara,e);
            return false;
        }
    }

    @Override
    public boolean addUserRecommendDataToES(UserRecommendPara userRecommendPara) {
        try {
            Map<String, Object> obj = new HashMap<>();
            obj.put("uid",userRecommendPara.getUid());
            obj.put("pIds",userRecommendPara.getPids());
            obj.put("createTime",userRecommendPara.getCreateTime());
            return esIndexOperater.addDocument(recommend_IndexName,recommend_Type,obj);
        } catch (Exception e) {
            logger.error("可见产品推荐用户数据存入ES异常，realNameUVPara={}, Exception:",userRecommendPara,e);
            return false;
        }
    }

    public JSONObject recommendRateEchartsJsonData(RecommendRateEcharts rateEchartsResponse) {

        EnhancedOption option = new EnhancedOption();
        option.title("实时推荐率趋势图");
//        option.color("#FF0000", "#CD853F", "#0000EE");
        option.tooltip().trigger(Trigger.axis);
        option.toolbox().show(true).feature(Tool.restore, Tool.saveAsImage);
        option.legend().data("实名登陆日活", "可推荐用户数", "可推荐率").padding(0, 2);
        option.legend().selected("实名登陆日活", true);
        option.legend().selected("可推荐用户数", true);
        option.legend().selected("可推荐率", true);
        option.calculable(true);
        option.xAxis(new CategoryAxis().boundaryGap(false).data(rateEchartsResponse.getxAxis().toString().split(",")));
        option.yAxis(new ValueAxis().name("人数"), new ValueAxis().name("转化百分比"));

        Line realNameLoginLine = new Line("实名登陆日活");
        realNameLoginLine.areaStyle();
        realNameLoginLine.data(rateEchartsResponse.getRealNameUvList().toArray());

        Line recommendNumLine = new Line("可推荐用户数");
        recommendNumLine.areaStyle();
        recommendNumLine.data(rateEchartsResponse.getRecommendNumList().toArray());

        Line recommendRateLine = new Line("可推荐率");
        recommendRateLine.yAxisIndex(1);
        recommendRateLine.smooth(true).itemStyle().normal().lineStyle();
        recommendRateLine.data(rateEchartsResponse.getRecommendRateList().toArray());

        option.series(realNameLoginLine, recommendNumLine,recommendRateLine);
        String jsonString = JSON.toJSONString(option);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        return jsonObject;
    }
}
