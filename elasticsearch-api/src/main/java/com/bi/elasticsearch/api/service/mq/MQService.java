package com.bi.elasticsearch.api.service.mq;

/**
 * @author CleverApe
 * @Classname MQService
 * @Description RocketMQ 接口
 * @Date 2019-07-26 16:25
 * @Version V1.0
 */
public interface MQService {

    /**
     * MQ消息异步发送接口
     * @param topic 必传参数
     * @param tag 必传参数
     * @param msgKey 可选参数 【建议传，方便查询】
     * @param body 消息体
     * @return
     */
    boolean sendMsg(String topic, String tag, String msgKey, String body);

}
