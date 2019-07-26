package com.bi.elasticsearch.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author CleverApe
 * @Classname Application
 * @Description 项目启动类
 * @Date 2019-07-19 11:06
 * @Version V1.0
 */
@SpringBootApplication
public class Application {

    private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) { 
        SpringApplication.run(Application.class, args);
        logger.info("---------------- 😊😄 BI-Analyze Application Start SUCCEED 😄😂 ---------------");
    }  
}
