package com.htht.job.executor.redis;

import com.htht.job.core.util.MyOwnRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service("redisService")
@SuppressWarnings(value = "unchecked")
public class RedisService {

    volatile int dealAmount = 0;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            throw new MyOwnRuntimeException(key,e);
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            throw new MyOwnRuntimeException(key,e);
        }
        return result;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (null!=keys&&!keys.isEmpty())
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }


    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希 删除
     *
     * @param key
     * @param hashKey
     * @param
     */
    public void hmDel(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.delete(key, hashKey);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 哈希获取所有keys
     *
     * @param key
     * @return
     */
    public Set<Object> hmKeys(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.keys(key);
    }

    /**
     * 哈希获取所有values
     *
     * @param key
     * @return
     */
    public List<Object> hmValues(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.values(key);
    }

    /**
     * 列表添加
     *
     * @param k
     * @param v
     */
    public void lPush(String k, String v) {
        ListOperations<String, String> list = redisTemplate.opsForList();
        list.leftPush(k, v);
    }

    public String rpop(String k) {
        ListOperations<String, String> list = redisTemplate.opsForList();
        return list.rightPop(k);
    }

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 列表大小
     *
     * @param k
     */
    public Long getListSize(String k) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.size(k);
    }

    /**
     * 列表移除
     *
     * @param k
     * @param v
     */
    public void removeList(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.remove(k, 0, v);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    public void zrem(String key, Object obj) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.remove(key, obj);
    }

    public Object zRevRangeByScore(String key) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        Set<Object> obj = zset.reverseRange(key, 0, 0);
        Object o = null;
        for (Object t : obj) {
            o = t;
        }
        if (null != o) {
            return o;
        }
        return null;
    }

    /**
     * 模糊删除
     */
    public void fuzzyRemove(String fuzzyKey) {
        Set<String> keys = redisTemplate.keys(fuzzyKey + "*");
        if (null!=keys&&!keys.isEmpty()) {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        }
    }

    /**
     * 模糊查询
     */
    public Set<String> fuzzyQuery(String fuzzyKey) {
        return redisTemplate.keys(fuzzyKey + "*");

    }

    public synchronized void dealSl(String key, int value) {
        if (exists(key)) {
            int sl = (int) this.get(key);
            this.dealAmount = sl + value;
        } else {
            this.dealAmount = value;
        }
        this.set(key, dealAmount);
    }
}
