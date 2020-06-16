package com.htht.job.executor.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
@Configuration
public class RedisConfig extends CachingConfigurerSupport{
	
	 /**
	   * 在使用@Cacheable时，如果不指定key，则使用找个默认的key生成器生成的key
	   *
	   * @return
	   */
	  /*@Override
	  public KeyGenerator  keyGenerator() {
	    return new KeyGenerator () {
	      *//**
	       * 对参数进行拼接后MD5
	       *//*
	      @Override
	      public Object generate(Object target, Method method, Object... params) {
	    	*//*  Object key=new BaseCacheKey(target,method,params);
	          return key.toString();  *//*
	        StringBuilder sb = new StringBuilder();
	        sb.append(target.getClass());
	        sb.append(".").append(method.getName());

	        StringBuilder paramsSb = new StringBuilder();
	        for (Object param : params) {
	          // 如果不指定，默认生成包含到键值中
	          if (param != null) {
	            paramsSb.append(param.toString());
	          }
	        }

	        if (paramsSb.length() > 0) {
	          sb.append("_").append(paramsSb);
	        }
	        return sb.toString();
	      }

	    };

	  }*/
	@Override
	@Bean
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				String[] value = new String[1];
				// sb.append(target.getClass().getName());
				// sb.append(":" + method.getName());
				Cacheable cacheable = method.getAnnotation(Cacheable.class);
				if (cacheable != null) {
					value = cacheable.value();
				}
				CachePut cachePut = method.getAnnotation(CachePut.class);
				if (cachePut != null) {
					value = cachePut.value();
				}
				CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
				if (cacheEvict != null) {
					value = cacheEvict.value();
				}
				sb.append(value[0]);
			/*	for (Object obj : params) {
					sb.append(":" + obj.toString());
				}*/
				return sb.toString();
			}
		};
	}
	@Bean
	public CacheManager cacheManager (RedisTemplate redisTemplate) {
		RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
		rcm.setDefaultExpiration(60);
		return rcm;
	}
	  /**
	   * 管理缓存
	   *
	   * @param redisTemplate
	   * @return
	   */
	/*  @Bean
	  public CacheManager cacheManager( @SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
	    RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
	    // 设置缓存默认过期时间（全局的）
	    rcm.setDefaultExpiration(1800);// 30分钟

	    // 根据key设定具体的缓存时间，key统一放在常量类RedisKeys中
	   // rcm.setExpires(redisKeys.getExpiresMap());

	    //List<String> cacheNames = new ArrayList<String>(redisKeys.getExpiresMap().keySet());
	    //rcm.setCacheNames(cacheNames);

	    return rcm;
	  }*/
	  
	/**序列化
	 * 
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		//使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		serializer.setObjectMapper(mapper);

		template.setValueSerializer(serializer);
		//使用StringRedisSerializer来序列化和反序列化redis的key值
		template.setKeySerializer(new StringRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}
	

}