package com.boot.sseredis.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.Serializable;

public class SSEInfo extends SseEmitter implements Serializable {

public SSEInfo(Long timeout) {
    super(timeout);
}

}
