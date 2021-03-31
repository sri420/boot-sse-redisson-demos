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

import com.boot.sseredis.model.SSEInfo;
import org.redisson.api.RMap;
import com.boot.sseredis.util.CacheUtil;

@RestController
@Slf4j
public class SSEController {

//Method for Client Subscription
@CrossOrigin
@RequestMapping(value="sse/subscribe/{userId}", consumes=MediaType.ALL_VALUE)
public SSEInfo subscribe(@PathVariable("userId") String userId) {
	SSEInfo sseEmitter=new SSEInfo(Long.MAX_VALUE);
	RMap<String, SSEInfo> map = CacheUtil.getCacheClient().getMap("sseEmitterMap");		

	try {
		sseEmitter.send(SseEmitter.event().name("INIT"));
		map.put(userId,sseEmitter);
		log.info("OBJ:" + sseEmitter);
		log.info("OBJ toString: "+ sseEmitter.toString());
	}catch(IOException e) {
		e.printStackTrace();
	}

	sseEmitter.onCompletion( () -> map.remove(userId));
	map.put(userId,sseEmitter);
	return sseEmitter;
}
	
	
	
//Method to Dispatch events to connected Clients
@PostMapping(value="sse/dispatchEvent")
public void dispatchEventsToAllClients(@RequestParam String event,@RequestParam String userId) {
RMap<String, SSEInfo> map = CacheUtil.getCacheClient().getMap("sseEmitterMap");		

	try {
		SSEInfo sseEmitter=(SSEInfo) map.get(userId);
		log.info("fetched from redis:" + sseEmitter);
		sseEmitter.send(SseEmitter.event().name("event").data(event));
		log.info("After Sending message Event to clients...");
	} catch (IOException e) {
		e.printStackTrace();
		map.remove(userId);
	}
}


}
