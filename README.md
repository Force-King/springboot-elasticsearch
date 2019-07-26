# 亿级流量实时计算系统

主要功能是通过ElasticSearch实现实时计算、实时分析海量数据，聚合处理等。

## 项目搭建

### Spring Boot + ElasticSearch集群 + RocketMQ集群 + Codis集群

- RocketMQ：实现高并发、海量流量情况下的削峰，通过控制消费速度，批量入库ES。

- ElasticSearch：海量数据存储、聚合查询、统计分析，分布式快速索引(倒排索引)。

- Codis：实时计算结果增加缓存，重复利用，加快计算、查询速度。

- Rest-Api：提供api接口，供客户端实时查询。


## 启动项目

编译： 
````
mvn clean install -DskipTests -U
````
启动：
````
nohup java -jar elasticsearch-api.jar --spring.profiles.active=publish > nohup.log & 
````

## BI-analyze

### BI实时计算分析系统

#### 1. 实时推荐率

主要功能是通过 ElasticSearch 实现实时计算、实时分析用户行为数据，聚合处理等，

返回处理后的数据，展示在页面上，提供实时展示策略效果的监控
