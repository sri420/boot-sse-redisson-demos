package com.boot.sseredis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


@Slf4j
@Service
public class SSERedisService {
    
	@Autowired
	RedisTemplate redisTemplate;

	 public void save(String userId, Object object) {
        redisTemplate.opsForValue()
            .set(userId,object);
    }

    public Object findById(String userId) {
        return  redisTemplate.opsForValue()
            .get(userId);
    }
	
    public Boolean deleteById(String userId) {
        return  redisTemplate.delete(userId);
    }



}