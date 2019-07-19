# springboot-elasticsearch
通过jest 集成 spring boot elasticsearch ，实现实时计算、聚合查询功能

## jar 包引用

spring boot引入es后，需要单独添加以下jar,否则项目启动会报错，找不到log4j-core下面的部分内容
````
<dependency>
		<groupId>org.apache.logging.log4j</groupId>
		<artifactId>log4j-core</artifactId>
		<version>2.11.1</version>
</dependency>
````
