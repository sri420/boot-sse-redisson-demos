package com.boot.sseredis.web;

import java.io.IOException;
import java.util.Map;

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

private void sendInitEvent(SSEInfo sseInfo) {
		log.info("Entering sendInitEvent..");
		try {
				log.info("Sending INIT Message...");
				sseInfo.send(SseEmitter.event().name("INIT"));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}


//Method for Client Subscription
@CrossOrigin
@RequestMapping(value="sse/subscribe/{userId}", consumes=MediaType.ALL_VALUE)
public SSEInfo subscribe(@PathVariable("userId") String userId) {
	SSEInfo sseEmitter=new SSEInfo(Long.MAX_VALUE);

	RMap<String, SSEInfo> map = CacheUtil.getCacheClient().getMap("sseEmitterMap");		

	SSEInfo sseInfo=new SSEInfo(Long.MAX_VALUE);
	log.info("Sending InitEvent to Client..");
	sendInitEvent(sseInfo);
	log.info("Adding sseInfo Object to map..");
	map.put(userId, sseInfo);
	sseInfo.onCompletion( () -> map.remove(userId));
	sseInfo.onTimeout( () -> map.remove(userId));
	sseInfo.onError( (e) -> map.remove(userId));
	log.info("Returning from subscribe..");
	return sseEmitter;
}

//Method to Dispatch events to connected Clients
@PostMapping(value="sse/dispatchEvent")
public void dispatchEventsToAllClients(@RequestParam String eventName,String eventValue) {
RMap<String, SSEInfo> map = CacheUtil.getCacheClient().getMap("sseEmitterMap");		
for (Map.Entry<String, SSEInfo> entry : map.entrySet()) {
		        
log.info("userId:{}, sseInfo:{}",entry.getKey(),entry.getValue());
		        
    try {
	        	log.info("Fetching sseInfo Object from map..");
	        	SSEInfo sseInfo=entry.getValue();
	        	log.info("Fetched sseInfo Object from map..");
	        	if(null==sseInfo) {
			   		log.info("Fetched sseInfo Object is NULL");
			    }
			    log.info("Sending Message to Client,{}",entry.getKey());
			    sseInfo.send(SseEmitter.event().name(eventName).data(eventValue));
			    log.info("Sent message to Client, userId :{}",entry.getKey());
			        	
				}catch (IOException e) {
					e.printStackTrace();
					map.remove(entry.getKey());
				}
		    }
}



}