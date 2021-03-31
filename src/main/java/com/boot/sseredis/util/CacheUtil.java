package com.boot.sseredis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.redisson.api.RedissonClient;
import org.redisson.Redisson;
import org.redisson.config.Config;


@Slf4j
@Service
public class CacheUtil {
    
public static RedissonClient getCacheClient(){
   Config config = new Config();
   config.useSingleServer().setAddress("redis://127.0.0.1:6379");
   return Redisson.create(config);	
}


}