package com.boot.sseredis.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.Serializable;
import lombok.Data;


@Data
public class Subscriber extends SseEmitter implements Serializable {

public Subscriber(Long timeout) {
    super(timeout);
}

String userId;

public String getUserId(){
    return userId;
}

public void setUserId(String userId){
    this.userId=userId;
}
}
