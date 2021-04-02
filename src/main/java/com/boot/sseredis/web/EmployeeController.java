package com.boot.sseredis.web;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.annotation.PathVariable;

import com.boot.sseredis.model.Employee;

import org.redisson.api.RMap;

import com.boot.sseredis.util.CacheUtil;

@RestController
@Slf4j
public class EmployeeController {

@PostMapping(value="employee", consumes=MediaType.ALL_VALUE)
public Employee addEmployee(@RequestBody Employee emp) {
	RMap<String, Employee> empMap = CacheUtil.getCacheClient().getMap("employeeMap");		
	log.info("Before Adding To Redis: Employee emp:{}",emp);
	log.info("Before Adding To Redis: Employee emp:{}",emp.toString());
	empMap.put(emp.getEmpno(),emp);
	return emp;
}

@GetMapping(value="employee", consumes=MediaType.ALL_VALUE)
public RMap<String, Employee> getEmployees() {
	RMap<String, Employee> empMap = CacheUtil.getCacheClient().getMap("employeeMap");		
	return empMap;
}

@GetMapping(value="employee/{empno}", consumes=MediaType.ALL_VALUE)
public Employee getEmployeeById(@PathVariable String empno) {
	RMap<String, Employee> empMap = CacheUtil.getCacheClient().getMap("employeeMap");		
	Employee emp=empMap.get(empno);
	log.info("Employee Object, After Fetching from Redis:{}",emp);
	return emp;
}

}
