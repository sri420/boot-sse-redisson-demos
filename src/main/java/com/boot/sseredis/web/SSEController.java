package com.boot.sseredis.web;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import org.springframework.web.bind.annotation.PathVariable;


import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import com.boot.sseredis.model.SSEInfo;
import com.boot.sseredis.service.SSERedisService;

@RestController
@Slf4j
public class SSEController {

	@Autowired
	SSERedisService sseRedisService;
	
	//Method for Client Subscription
	@CrossOrigin
	@RequestMapping(value="subscribe/{userId}", consumes=MediaType.ALL_VALUE)
	public SSEInfo subscribe(@PathVariable("userId") String userId) {
		SSEInfo sseEmitter=new SSEInfo(Long.MAX_VALUE);

		try {
			
			sseEmitter.send(SseEmitter.event().name("INIT"));

			log.info("Before Call to  save...");	
			sseRedisService.save(userId,sseEmitter);
			log.info("After Call to  save...");	
			
			log.info("OBJ:" + sseEmitter);
			log.info("OBJ toString: "+ sseEmitter.toString());
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		sseEmitter.onCompletion( () -> sseRedisService.deleteById(userId));
		sseRedisService.save(userId,sseEmitter);
		return sseEmitter;
	}
	
	
	
	//Method to Dispatch events to connected Clients
	@PostMapping(value="dispatchEvent")
	public void dispatchEventsToAllClients(@RequestParam String event,@RequestParam String userId) {

			try {
				
				SSEInfo sseEmitter=(SSEInfo) sseRedisService.findById(userId);		
				log.info("fetched from redis:" + sseEmitter);
				log.info("Before Sending Event to clients...");
				sseEmitter.send(SseEmitter.event().name("event").data(event));
				log.info("After Sending message Event to clients...");
				
			} catch (IOException e) {
				e.printStackTrace();
				sseRedisService.deleteById(userId);
			}
	}
}
