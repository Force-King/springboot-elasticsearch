package com.bi.elasticsearch.api.service;

/**
 * @author CleverApe
 * @Classname RedisService
 * @Description Redis操作接口
 * @Date 2019-07-24
 * @Version V1.0
 */
public interface RedisService {

    String get(String key);

    /**
     * 设置缓存值及过期时间 <b/> 优先使用该方法
     *
     * @param key
     * @param value
     * @param seconds
     */
    void setex(String key, String value, int seconds);

    void set(String key, String value);

    void expire(String key, int sec);

    void del(String key);

    boolean lock(String key, int expireTime);

    boolean unLock(String key, String requestId);

    void incr(String key);

    boolean tryLock(String key, String value, int expireTime);

    boolean tryUnLock(String key);
}
