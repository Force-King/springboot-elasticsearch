package com.bi.elasticsearch.api.config;

import com.bi.elasticsearch.api.service.mq.MessageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * @author CleverApe
 * @Classname UserRecommendConsumerConfiguration
 * @Description 用户推荐数据消费者
 * @Date 2019-07-19 10:00
 * @Version V1.0
 */
@SpringBootConfiguration
public class UserRecommendConsumerConfiguration implements ApplicationListener<ContextRefreshedEvent>{

    private Logger logger = LogManager.getLogger(UserRecommendConsumerConfiguration.class);
  
    @Value("${rocketmq.consumer.namesrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.consumer.topic}")
    private String topic;

    @Value("${rocketmq.consumer.tag.user.recommend}")
    private String tag;
    
    @Value("${rocketmq.consumer.groupName.user.recommend}")
    private String groupName;

    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;
    
    @Autowired
    private MessageProcessor messageProcessor;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
          this.init();
        } catch (MQClientException e) {
          logger.error("RocketMQ UserRecommend Consummer init failed, Exception:" , e);
        } 
    }
  
    private void init() throws MQClientException{
//      logger.info("------------------- Starting " + topic + ":" + tag + " Consummer Begin -------------------");
      DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName); 
      consumer.setNamesrvAddr(namesrvAddr); 
      consumer.subscribe(topic, tag); // 开启内部类实现监听 
      consumer.setConsumeThreadMax(consumeThreadMax);
      consumer.setConsumeThreadMin(consumeThreadMin);
      consumer.setMessageListener(new MessageListenerConcurrently() {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,ConsumeConcurrentlyContext context) {
          for(MessageExt msg: msgs){
            try {
               String msgStr = new String(msg.getBody(), "utf-8");
               logger.debug("UserRecommend Consummer deal mq {}" , msgStr);
               messageProcessor.userRecommendDealMqMessage(msg);
            } catch (Exception e) {
                logger.error("UserRecommend Consummer deal mq failed, Exception:" , e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
          }
          return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        
      });
      consumer.start(); 
      logger.info("------------------- RocketMQ UserRecommend Consummer Start Succeed 😊 -------------------");

    }
    
}
