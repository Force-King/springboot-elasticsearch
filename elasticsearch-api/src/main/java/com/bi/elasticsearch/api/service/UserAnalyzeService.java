package com.bi.elasticsearch.api.service;

import com.alibaba.fastjson.JSONObject;
import com.bi.elasticsearch.api.entity.requestPara.RealNameUVPara;
import com.bi.elasticsearch.api.entity.requestPara.UserDataPara;
import com.bi.elasticsearch.api.entity.requestPara.UserRecommendPara;

import java.util.Date;

/**
 * @author CleverApe
 * @Classname UserAnalyzeService
 * @Description 用户行为分析接口
 * @Date 2019-07-23 14:27
 * @Version V1.0
 */
public interface UserAnalyzeService {

    /**
     * 查询产品当天各个时间段的推荐率
     *
     * @param pid
     * @param date
     * @return
     */
    JSONObject getRecommendRateData(Integer pid, Date date);

    /**
     * 查询某一段时间内进入首页的实名用户数
     *
     * @param userDataPara
     * @return
     */
    JSONObject getRealNameLoginNumForHour(UserDataPara userDataPara);

    /**
     * 查询某一段时间内可见某产品的用户数
     *
     * @param userDataPara
     * @return
     */
    JSONObject getUserRecommendNumForHour(UserDataPara userDataPara);

    /**
     * 新增进入首页的实名用户数据到ES
     *
     * @param realNameUVPara
     * @return
     */
    boolean addRealNameUVDataToES(RealNameUVPara realNameUVPara);

    /**
     * 新增用户可见产品数据到ES
     *
     * @param userRecommendPara
     * @return
     */
    boolean addUserRecommendDataToES(UserRecommendPara userRecommendPara);
}
