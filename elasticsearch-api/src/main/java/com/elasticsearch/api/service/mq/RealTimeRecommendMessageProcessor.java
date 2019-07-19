package com.elasticsearch.api.service.mq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.common.message.MessageExt;
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


    /**
     * 消费消息
     * @param msg
     */
    @Override
    public void dealMqMessage(MessageExt msg) {
        JSONObject body = this.parseBody(msg);

        if (checkBody(body)) {
            try {
                logger.info("");



            } catch (Exception e) {

                logger.error("----消费异常, msg = {}, Exception ", body.toJSONString(), e);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * 消息对象转json
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
     * 验证消息字段*
     * @param body
     * @return
     */
    private boolean checkBody(JSONObject body) {
        boolean rs = true;
        try {
          if (body == null) {
    //          logger.warn("check MsgBody is empty.");
              return false;
          }
          if (body.getString("pageId") == null || Integer.valueOf(body.getString("pageId")) != 1) {
    //          logger.warn("checkMsgBody pageId is empty or pageId is not 1, body: {}", body.toJSONString());
              return false;
          }
          if (body.getString("uid") == null || Integer.valueOf(body.getString("uid")) != 0) {
    //          logger.warn("checkMsgBody uid is empty or uid is not 0, body: {}", body.toJSONString());
              return false;
          }
          if (!body.containsKey("appId")) {
    //          logger.warn("checkMsgBody appId is empty, body: {}", body.toJSONString());
              return false;
          }

        } catch (Exception e) {
          logger.error("checkBody exception e" , e);
        }
        return rs;
    }


}
