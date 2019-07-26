package com.bi.elasticsearch.api.service.impl;

import com.bi.elasticsearch.api.util.DateUtil;
import com.bi.elasticsearch.api.service.RedisService;
import io.codis.jodis.JedisResourcePool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * Created by CleverApe on 2019/7/24.
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private JedisResourcePool jedisPool;

    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("redis exception, get failed! key = " + key);
            return null;
        }
    }

    @Override
    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("redis exception, set failed! key = {}, value = {}.", key, value);
        }
    }

    @Override
    public void setex(String key, String value, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds,value);
        } catch (Exception e) {
            logger.error("redis exception, setex failed! key = {}, value = {}, seconds = {}.", key, value, seconds);
        }
    }

    @Override
    public void expire(String key, int sec) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, sec);
        } catch (Exception e) {
            logger.error("redis exception, expire failed! key = {}, sec = {}.", key, sec);
        }
    }

    @Override
    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            logger.error("redis exception, del failed! key = {}.", key);
        }
    }

    @Override
    public boolean lock(String key, int repeatTime) {
        boolean isReentry = false;
        try (Jedis jedis = jedisPool.getResource()) {
            int nowTime = DateUtil.secondTimestamp();
            String hTimeStr = jedis.getSet(key, String.valueOf(nowTime + repeatTime));
            if (StringUtils.isBlank(hTimeStr) || nowTime > Integer.parseInt(hTimeStr)) {
                isReentry = true;
            }
            //todo 每次点击是否延长禁止重入时间
            jedis.expire(key, repeatTime);
        } catch (Exception e) {
            logger.error("redis exception, lock key = {}.", key, e);
        }
        return isReentry;
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    private static final Long RELEASE_SUCCESS = 1L;

    @Override
    public boolean unLock(String key, String requestId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("redis exception, un lock key {}.", key, e);
        }
        return false;
    }

    /**
     *incr
     * @param key
     */
    @Override
    public void incr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.incr(key);
        } catch (Exception e) {
            logger.error("redis exception, lock key = {}.", key, e);
        }
    }

    @Override
    public boolean tryLock(String key, String value, int expireTime) {

        logger.info("tryLock key={}, value={}, expireTime={}", key, value, expireTime);
        try (Jedis jedis = jedisPool.getResource()) {
            Long result = jedis.setnx(key, value);
            if (result == 1) jedis.expire(key, expireTime);
            return result == 1;
        } catch (Exception e) {
            logger.error("tryLock异常: ", e);
            return true;//如果操作Redis失败，按true处理
        }
    }

    @Override
    public boolean tryUnLock(String key) {
        logger.info("tryUnLock key={}",key);
        try (Jedis jedis = jedisPool.getResource()) {
            Long result = jedis.del(key);
            return result == 1;
        } catch (Exception e) {
            logger.error("tryUnLock锁异常: ", e);
            return false;
        }
    }

}
