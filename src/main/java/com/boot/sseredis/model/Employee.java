package com.boot.sseredis.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.Serializable;
import lombok.Data;
import lombok.Setter;
import lombok.Getter;


@Data
public class Employee implements Serializable {

@Setter @Getter public String empno;
@Setter @Getter public String empname;
@Setter @Getter public float salary;

}
