package com.boot.sseredis.web;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PathVariable;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class SSEMapController {
	
	//public List<SseEmitter> emitters=new CopyOnWriteArrayList<>();
	public Map<String, SseEmitter> emitters=new HashMap<>();
			
	
	private void sendInitEvent(SseEmitter sseEmitter) {
		log.info("Entering sendInitEvent..");
		try {
				log.info("Sending INIT Message...");
				sseEmitter.send(SseEmitter.event().name("INIT"));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	//Method for Client Subscription
	@CrossOrigin
	@RequestMapping(value="map/subscribe/{userID}", consumes=MediaType.ALL_VALUE)
	public SseEmitter subscribe(@PathVariable("userID") String userID) {
		log.info("Entering Subscribe Method");
		log.info("Received userID:{}",userID);
		SseEmitter sseEmitter=new SseEmitter(Long.MAX_VALUE);
		log.info("Sending InitEvent to Client..");
		sendInitEvent(sseEmitter);
		log.info("Adding sseEmitter Object to map..");
		emitters.put(userID, sseEmitter);
		sseEmitter.onCompletion( () -> emitters.remove(sseEmitter));
		sseEmitter.onTimeout( () -> emitters.remove(sseEmitter));
		sseEmitter.onError( (e) -> emitters.remove(sseEmitter));
		log.info("Returning from subscribe..");
		return sseEmitter;
	}
	
	
	
	//Method to Dispatch events to Specific Client
	@PostMapping(value="map/dispatchEvent/{userID}")
	public void dispatchEventsToSpecificClient(@RequestParam String eventName,String eventValue,@PathVariable("userID") String userID) {
			log.info("Entering dispatchEvents for specific  userID");
			log.info("Received params, userID:{},eventName:{},message:{}",userID,eventName,eventValue);
			
			try {
				log.info("Fetching sseEmitter for user:{}",userID);
				SseEmitter sseEmitter=emitters.get(userID);
				if(null==sseEmitter) {
					log.info("sseEmitter is NULL");
				}
				log.info("Sending Message to Client,{}",userID);
				sseEmitter.send(SseEmitter.event().name(eventName).data(eventValue));
				log.info("Sent message to  specific  userID :{}",userID);
			} catch (IOException e) {
				e.printStackTrace();
				emitters.remove(userID);
			}
	}
	
	//Method to Dispatch events to All Connected Clients")
	@PostMapping(value="map/dispatchEvent")
		public void dispatchEventsToAllClients(@RequestParam String eventName,@RequestParam String eventValue) {
			log.info("Entering dispatchEventsToAllClients.");
			
			for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
		        
				log.info("userID:{}, sseEmitter:{}",entry.getKey(),entry.getValue());
		        
		        try {
			        	log.info("Fetching sseEmitter Object from map..");
			        	SseEmitter sseEmitter=entry.getValue();
			        	log.info("Fetched sseEmitter Object from map..");
			        	if(null==sseEmitter) {
			        		log.info("Fetched sseEmitter Object is NULL");
			        	}
			        	log.info("Sending Message to Client,{}",entry.getKey());
			        	sseEmitter.send(SseEmitter.event().name(eventName).data(eventValue));
			        	log.info("Sent message to Client, userID :{}",entry.getKey());
			        	
				}catch (IOException e) {
					e.printStackTrace();
					emitters.remove(entry.getKey());
				}
		    }
			
		}


}
