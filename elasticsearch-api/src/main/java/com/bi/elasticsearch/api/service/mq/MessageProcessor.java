package com.bi.elasticsearch.api.service.mq;

import org.apache.rocketmq.common.message.MessageExt;

public interface MessageProcessor {
  void realNameUVDealMqMessage(MessageExt msg);
  void userRecommendDealMqMessage(MessageExt msg);
}
