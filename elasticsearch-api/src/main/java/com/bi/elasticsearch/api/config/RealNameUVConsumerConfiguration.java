package com.bi.elasticsearch.api.config;

import java.util.List;

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

/**
 * @author CleverApe
 * @Classname RealNameUVConsumerConfiguration
 * @Description 实名用户登陆首页UV数据消费者
 * @Date 2019-07-19 10:00
 * @Version V1.0
 */
@SpringBootConfiguration
public class RealNameUVConsumerConfiguration implements ApplicationListener<ContextRefreshedEvent>{

    private Logger logger = LogManager.getLogger(RealNameUVConsumerConfiguration.class);
  
    @Value("${rocketmq.namesrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.consumer.topic}")
    private String topic;

    @Value("${rocketmq.consumer.tag.real.name.uv}")
    private String tag;
    
    @Value("${rocketmq.consumer.groupName.real.name.uv}")
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
          logger.error("RocketMQ RealNameUV Consumer init failed, Exception:" , e);
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
               logger.debug("RealNameUV Consumer deal mq {}" , msgStr);
               messageProcessor.realNameUVDealMqMessage(msg);
            } catch (Exception e) {
                logger.error("RealNameUV Consumer deal mq failed, Exception:" , e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
          }
          return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        
      });
      consumer.start(); 
      logger.info("------------------- RocketMQ RealNameUV Consumer Start Succeed 😝 -------------------");

    }
    
}
