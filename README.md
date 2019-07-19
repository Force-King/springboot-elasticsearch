# show-analyze

## 展示分析系统

主要功能是通过 ElasticSearch 实现实时计算、实时分析用户行为数据，聚合处理等，

返回处理后的数据，展示在页面上，提供实时展示策略效果的监控


### 启动项目

编译： mvn clean install -DskipTests -U

启动：nohup java -jar elasticsearch-api.jar --spring.profiles.active=publish > nohup.log & 

