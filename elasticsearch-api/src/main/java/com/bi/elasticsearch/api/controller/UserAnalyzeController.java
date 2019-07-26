package com.bi.elasticsearch.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.bi.elasticsearch.api.enums.RequestResultEnum;
import com.bi.elasticsearch.api.util.RestApiResult;
import com.bi.elasticsearch.api.service.UserAnalyzeService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author CleverApe
 * @Classname UserAnalyzeController
 * @Description 用户行为分析Controller
 * @Date 2019-07-19 10:00
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=utf-8")
@SpringBootConfiguration
public class UserAnalyzeController {

    private Logger logger = LogManager.getLogger(UserAnalyzeController.class);

    @Autowired
    private UserAnalyzeService userAnalyzeService;

    /**
     * 实时推荐率eCharts图形数据查询
     * @param pid 产品id
     * @param date 查询日期yyyy-MM-dd
     * @return
     */
    @RequestMapping(value = "/recommend_rate/query", method = RequestMethod.GET)
    public String eChartsData (@RequestParam(value = "pid", required = true) Integer pid,
                         @RequestParam(value = "date", required = false) Date date){
        long start = System.currentTimeMillis();
        //期不传。默认查询当天
        if(date == null) {
            date = new Date();
        }
        JSONObject obj;
        try {
            obj = userAnalyzeService.getRecommendRateEchartsData(pid,date);
        } catch (Exception e){
            logger.error("------ 调用实时推荐率查询API异常，参数:{\"pid\":\"{}\",\"date\":\"{}\"},Exception：",pid,date,e);
            return RestApiResult.buildEnum(RequestResultEnum.SERVER_EXP).toString();
        }
        long end = System.currentTimeMillis();
        logger.info("------ 实时推荐率查询API ————> 耗时：{}ms, 参数:{\"pid\":\"{}\",\"date\":\"{}\"}, 结果：{} ",end-start,pid,date,obj);
        return RestApiResult.success(obj).toString();
    }

}
