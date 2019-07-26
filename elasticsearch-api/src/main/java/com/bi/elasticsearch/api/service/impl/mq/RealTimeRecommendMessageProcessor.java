package com.bi.elasticsearch.api.service.impl.mq;

import com.alibaba.fastjson.JSONObject;
import com.bi.elasticsearch.api.entity.requestPara.RealNameUVPara;
import com.bi.elasticsearch.api.entity.requestPara.UserRecommendPara;
import com.bi.elasticsearch.api.service.UserAnalyzeService;
import com.bi.elasticsearch.api.service.mq.MessageProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author CleverApe
 * @Classname RealTimeRecommendMessageProcessor
 * @Description 实时推荐率分析-消息消费者
 * @Date 2019-07-19
 * @Version V1.0
 */
@Component
public class RealTimeRecommendMessageProcessor implements MessageProcessor {

    private Logger logger = LogManager.getLogger(RealTimeRecommendMessageProcessor.class);

    @Autowired
    private UserAnalyzeService userAnalyzeService;

    /**
     *
     * @param msg
     */
    @Override
    public void realNameUVDealMqMessage(MessageExt msg) {
        JSONObject body = this.parseBody(msg);

        if (checkBody(body)) {
            try {
                logger.debug("进入--实名用户首页UV数据MQ消费方法，消息body = {}",body.toJSONString());
                RealNameUVPara realNameUVPara = new RealNameUVPara().setUid(Integer.valueOf(body.getString("uid")))
                        .setuName(body.getString("uName")).setCreateTime(body.getString("createTime"));
                userAnalyzeService.addRealNameUVDataToES(realNameUVPara);
            } catch (Exception e) {
                logger.error("----实名用户首页UV数据MQ消费方法异常, 消息body = {}, Exception: ", body.toJSONString(), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void userRecommendDealMqMessage(MessageExt msg) {
        JSONObject body = this.parseBody(msg);

        if (checkBody(body)) {
            try {
                logger.debug("进入--可见产品用户推荐数据MQ消费方法，消息body = {}",body.toJSONString());
                UserRecommendPara para = new UserRecommendPara().setUid(Integer.valueOf(body.getString("uid")))
                        .setPids(body.getString("pIds")).setCreateTime(body.getString("createTime"));
                userAnalyzeService.addUserRecommendDataToES(para);
            } catch (Exception e) {
                logger.error("----可见产品用户推荐数据MQ消费方法异常, 消息body = {}, Exception: ", body.toJSONString(), e);
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 消息对象转Json对象
     * @param msg
     * @return
     */
    private JSONObject parseBody(MessageExt msg) {
        JSONObject rs = null;
        try {
            String msgStr = new String(msg.getBody(), "utf-8");
            rs = JSONObject.parseObject(msgStr);
        } catch (Exception e) {
            logger.error("parseBody error e", e);
        }
        return rs;
    }


    /**
     * 检查消息体数据完整性
     * @param body
     * @return
     */
    private boolean checkBody(JSONObject body) {
        boolean rs = true;
        try {
            if (body == null) {
                logger.warn("check MsgBody is empty.");
                return false;
            }
            if (body.containsKey("pageId") && StringUtils.isBlank(body.getString("pageId"))) {
                logger.warn("checkMsgBody pageId is empty, body: {}", body.toJSONString());
                return false;
            }
            if (body.getString("uid") == null || Integer.valueOf(body.getString("uid")) <= 0) {
                logger.warn("checkMsgBody uid is empty or uid is 0, body: {}", body.toJSONString());
                return false;
            }
            if (body.containsKey("uName") && StringUtils.isBlank(body.getString("uName"))) {
                logger.warn("checkMsgBody uName is empty, body: {}", body.toJSONString());
                return false;
            }
            if (body.containsKey("pIds") && StringUtils.isBlank(body.getString("pIds"))) {
                logger.warn("checkMsgBody pIds is empty, body: {}", body.toJSONString());
                return false;
            }
            if (body.containsKey("createTime") && StringUtils.isBlank(body.getString("createTime"))) {
                logger.warn("checkMsgBody createTime is empty, body: {}", body.toJSONString());
                return false;
            }

        } catch (Exception e) {
            logger.error("checkBody exception e" , e);
            return false;
        }
        return rs;
    }

}
