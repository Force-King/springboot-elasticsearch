package com.elasticsearch.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.elasticsearch.api.enums.RequestResultEnum;
import com.elasticsearch.api.util.DateUtil;
import com.elasticsearch.api.util.RestApiResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@RequestMapping(value = "/user/analyze", produces = "application/json;charset=utf-8")
@SpringBootConfiguration
public class UserAnalyzeController {

    private Logger logger = LogManager.getLogger(UserAnalyzeController.class);


    /**
     * 实时推荐率查询
     * @param pid 产品id
     * @param date 查询日期yyyy-MM-dd
     * @return
     */
    @RequestMapping(value = "/recommendation_rate/query", method = RequestMethod.GET)
    public String query (@RequestParam(value = "pid", required = true) Integer pid,
                         @RequestParam(value = "date", required = false) String date){

        logger.info("调用实时推荐率查询api，入参:{\"pid\":\"{}\",\"date\":\"{}\"},Exception：{}",pid,date);
        //日期不传。默认查询当天
        if(date.isEmpty()) {
            date = DateUtil.getDateStr("yyyy-MM-dd",new Date());
        }
        JSONObject obj = new JSONObject();
        try {


        } catch (Exception e){
            logger.error("调用实时推荐率查询api异常，参数:{\"pid\":\"{}\",\"date\":\"{}\"},Exception：{}",pid,date,e);
            return RestApiResult.buildEnum(RequestResultEnum.SERVER_EXP).toString();
        }
        return RestApiResult.success(obj).toString();
    }

}
