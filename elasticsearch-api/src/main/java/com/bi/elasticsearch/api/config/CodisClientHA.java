package com.bi.elasticsearch.api.config;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author CleverApe
 * @Classname CodisClientHA
 * @Description Codis客户端高可用封装
 * @Date 2019-07-24 11:28
 * @Version V1.0
 */
@Configuration
public class CodisClientHA {

    private Logger logger = LogManager.getLogger(CodisClientHA.class);

    @Value("${codis.zkAddr}")
    private String zkAddr;

    @Value("${codis.zk.proxy.dir}")
    private String zkProxyDir;

    @Value("${codis.password}")
    private String password;

    @Value("${codis.timeout}")
    private int timeout;

    @Value("${codis.maxActive}")
    private int max_active;

    @Value("${codis.maxIdle}")
    private int max_idle;

    @Value("${codis.minIdle}")
    private int min_idle;

    @Value("${codis.maxWait}")
    private long max_wait;

    @Bean
    public JedisResourcePool getPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(max_idle);
        poolConfig.setMaxTotal(max_active);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setMaxWaitMillis(max_wait);
        poolConfig.setBlockWhenExhausted(false); //连接耗尽的时候，是否阻塞，false 会抛出异常，true 阻塞直到超时。默认为true。

        JedisResourcePool pool = RoundRobinJedisPool.create().poolConfig(poolConfig)
                .curatorClient(zkAddr, timeout).zkProxyDir(zkProxyDir).build();
        logger.info("------------------- Codis Connection Pool Init Succeed 😊 -------------------");
        return pool;
    }


}
