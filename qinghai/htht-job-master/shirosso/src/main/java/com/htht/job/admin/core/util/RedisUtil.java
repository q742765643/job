package com.htht.job.admin.core.util;

import com.htht.job.admin.core.shiro.common.UpmsConstant;
import com.htht.job.core.util.ObjectTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liunian on 6/7/17.
 */
public class RedisUtil {

    private static Logger _log = LoggerFactory.getLogger(RedisUtil.class);


    //ip
    private static String IP = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.redis.ip");

    private static String clusterName = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.redis.sentinel");

    //port
    private static int PORT = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).getInt("master.redis.port");
    //password
    private static String PASSWORD = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.redis.password");
    // max active connection
    private static int MAX_ACTIVE = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).getInt("master.redis.max_active");

    // max idle
    private static int MAX_IDLE = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).getInt("master.redis.max_idle");

    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).getInt("master.redis.max_wait");

    // 超时时间
    private static int TIMEOUT = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).getInt("master.redis.timeout");

    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = false;

    private static JedisPool jedisPool = null;

    private static JedisSentinelPool  jedisSentinelPool = null;

    //initial pool
    private static void initialPool() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            if(IP.indexOf(",")!=-1){
                String[] ips=IP.split(",");
                Set<String> sentinels = new HashSet<String>();
                for(String ip:ips){
                    sentinels.add(ip);
                }
                jedisSentinelPool = new JedisSentinelPool(clusterName,sentinels);


            }else{
                jedisPool = new JedisPool(config, IP, PORT, TIMEOUT);
            }

        } catch (Exception e) {
            _log.error("First create JedisPool error : " + e);
        }
    }

    //在多线程环境同步初始化
    private static synchronized void poolInit() {
        if (null == jedisPool&&jedisSentinelPool==null) {
            initialPool();
        }
    }

    //同步获取Jedis实例
    public synchronized static Jedis getJedis() {
        poolInit();
        Jedis jedis = null;
        try {
            if(IP.indexOf(",")!=-1){
                if(null != jedisSentinelPool) {
                    try {
                        jedis = jedisSentinelPool.getResource();
                    } catch (Exception e) {
                        jedis = jedisSentinelPool.getResource();
                    }
                }
            }else {
                if (null != jedisPool) {
                    jedis = jedisPool.getResource();
                    try {
                        jedis.auth(PASSWORD);
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {
            _log.error("Get jedis error : " + e);
        }
        return jedis;
    }

    //set string
    public synchronized static void set(String key, String value) {
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.close();
        } catch (Exception e) {
            _log.error("Set key error : " + e);
        }
    }

    //set byte[] seconds 键的到期时间
    public synchronized static void set(byte[] key, byte[] value, int seconds) {
        try {
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            _log.error("Set key error : " + e);
        }
    }

    //set string seconds 键的到期时间
    public synchronized static void set(String key, String value, int seconds) {
        try {
            Jedis jedis = getJedis();
            jedis.set(key, value);
            jedis.expire(key, seconds); //可以对一个已经带有生存时间的 key 执行 EXPIRE 命令，新指定的生存时间会取代旧的生存时间。
            jedis.close();
        } catch (Exception e) {
            _log.error("set key seconds error : " + e);
        }
    }

    //get string
    public synchronized static String get(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    //get byte[]
    public synchronized static byte[] get(byte[] key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        byte[] value = jedis.get(key);
        jedis.close();
        return value;
    }

    //remove string
    public synchronized static void remove(String key) {
        try {
            Jedis jedis = getJedis();
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            _log.error("Remove key error : " + e);
        }
    }

    //remove byte[]
    public synchronized static void remove(byte[] key) {
        try {
            Jedis jedis = getJedis();
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            _log.error("remove key byte error : " + e);
        }
    }

    //lpush 将一个或多个值插入列表key的头部
    public synchronized static void lpush(String key, String... strings) {
        try {
            Jedis jedis = getJedis();
            jedis.lpush(key, strings);
            jedis.close();
        } catch (Exception e) {
            _log.error("lpush error : " + e);
        }
    }

    //lrem
    public synchronized static void lrem(String key, long count, String value) {
        try {
            Jedis jedis = getJedis();
            jedis.lrem(key, count, value);
            jedis.close();
        } catch (Exception e) {
            _log.error("lrem error : " + e);
        }
    }

    //sadd
    public synchronized static void sadd(String key, String value, int seconds) {
        try {
            Jedis jedis = getJedis();
            jedis.sadd(key, value);
            jedis.expire(key, seconds);
            jedis.close();
        } catch (Exception e) {
            _log.error("sadd error : " + e);
        }
    }

    public synchronized static boolean tryGetDistributedLock(String lockKey) {
        Jedis jedis = getJedis();
        boolean success = false;
        long acquired = jedis.setnx(lockKey, lockKey);
        System.out.println("redis锁"+lockKey+"值"+acquired);
        jedis.expire(lockKey, 30);
        jedis.close();
        if (acquired == 1) {
            success = true;
        }

        return success;

    }
    public synchronized static void delete(String lockKey) {
        Jedis jedis = getJedis();
        Iterator<String> it = jedis.keys (lockKey+"*").iterator ();
        while (it.hasNext ()) {
            String key = it.next ();
            //清空缓存中记录的数据
            jedis.del(key);
        }
        jedis.close();

    }
    public static boolean exists(final String key) {
        Jedis jedis = getJedis();
        boolean success = false;
        success = jedis.exists(key);
        jedis.close();
        return success;
    }
    public  static List<String> get_output(String key) {
        Jedis jedis = getJedis();
        if (null == jedis) {
            return null;
        }
        byte[] in =jedis.get(key.getBytes());
        List<String> value =  ObjectTranscoder.deserialize(in);
        jedis.close();
        return value;
    }
    public static long setIncr(String key, int cacheSeconds) {
        long result = 0;
        Jedis jedis = getJedis();
        try {
            result =jedis.incr(key);
        } catch (Exception e) {
        } finally {
            jedis.close();
        }
        return result;
    }

    public static void main(String[] args){
        CountDownLatch countDownLatch=new CountDownLatch(10);
        for(int i=0;i<9;i++){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RedisUtil.tryGetDistributedLock("9f4f99794ff3448684b8aeb639b7dc0f");

                    } catch (Throwable e) {
                        // whatever
                    } finally {
                        // 很关键, 无论上面程序是否异常必须执行countDown,否则await无法释放
                        countDownLatch.countDown();
                    }
                }
            }).start();
        }
        RedisUtil.delete("9f4f99794ff3448684b8aeb639b7dc0f");
    }
}
