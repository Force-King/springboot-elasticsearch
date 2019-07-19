# 亿级流量实时计算系统

主要功能是通过ElasticSearch实现实时计算、实时分析海量数据，聚合处理等。

## 项目搭建

### Spring Boot + ElasticSearch + RocketMQ

通过 RocketMQ实现高并发、海量流量情况下的削峰，通过控制消费速度，批量入库ES,

提供 rest-api 接口，供客户端实时查询。




### 启动项目

编译： mvn clean install -DskipTests -U

启动：nohup java -jar elasticsearch-api.jar --spring.profiles.active=publish > nohup.log & 

