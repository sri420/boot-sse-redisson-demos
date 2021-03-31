package com.boot.sseredis;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;




@EnableCaching
@SpringBootApplication
public class BootSSERedisApplication  { 
    public static void main(String[] args) {
        SpringApplication.run(BootSSERedisApplication.class, args);
}
    
    
    
    

}
