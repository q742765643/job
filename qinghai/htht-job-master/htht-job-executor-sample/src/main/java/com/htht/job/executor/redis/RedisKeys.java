package com.htht.job.executor.redis;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisKeys {
 
  // begin
  public static final String _CACHE = "_cache";// 缓存key
  public static final Long _CACHE_SECOND = 60L;// 缓存时间
  public static final String _CACHE_T = "_cache_t";// 缓存key
  public static final Long _CACHE_T_SECOND = 36000L;// 缓存时间


  // end
 
  // 根据key设定具体的缓存时间
  private Map<String, Long> expiresMap = null;
 
  @PostConstruct
  public void init(){
    expiresMap = new HashMap<>();
    expiresMap.put(_CACHE, _CACHE_SECOND);
    expiresMap.put(_CACHE_T, _CACHE_T_SECOND);

  }
 
  public Map<String, Long> getExpiresMap(){
    return this.expiresMap;
  }
}