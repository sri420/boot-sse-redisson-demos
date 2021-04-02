package com.boot.sseredis.web;



import org.springframework.util.ObjectUtils;
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

import com.boot.sseredis.model.Subscriber;
import org.redisson.api.RMap;
import com.boot.sseredis.util.CacheUtil;

@RestController
@Slf4j
public class SSEController {

public static final String SUBSCRIBER_MAP="subscriberMap";

private void sendInitEvent(Subscriber subscriber) {
		log.info("Entering sendInitEvent..");
		try {
				log.info("Sending INIT Message...");
				subscriber.send(subscriber.event().name("INIT"));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

public void subscriberUser(){
	
	
}

//Method for Client Subscription
@CrossOrigin
@RequestMapping(value="sse/subscribe/{userId}", consumes=MediaType.ALL_VALUE)
public Subscriber subscribe(@PathVariable("userId") String userId) {
	
	log.info("Received PARAMS: userId: {}",userId);
	
	
	RMap<String, Subscriber> subscriberMap = CacheUtil.getCacheClient().getMap(SUBSCRIBER_MAP);		


	Subscriber subscriber;
	if(null!=subscriberMap && null!=subscriberMap.get(userId)){
		log.info("Key userId is present in subscriberMap...");
		subscriber=(Subscriber) subscriberMap.get(userId);
	    log.info("subscriber displayString: {}...",ObjectUtils.getDisplayString(subscriber));
	}else{
		log.info("Key userId NOT present in subscriberMap...");
	    subscriber=new Subscriber(Long.MAX_VALUE);
	    subscriber.setUserId(userId);
	    log.info("subscriber displayString: {}...",ObjectUtils.getDisplayString(subscriber));
	}

	log.info("Sending InitEvent to Client..");

	sendInitEvent(subscriber);

	log.info("Adding subscriber Object to subscriberMap..");
	log.info("BeforeStoring:subscriber: {}",subscriber);
	log.info("BeforeStoring:subscriber:toString: {}",subscriber.toString());

	
	subscriber.onCompletion( () -> subscriberMap.remove(userId));
	subscriber.onTimeout( () -> subscriberMap.remove(userId));
	subscriber.onError( (e) -> subscriberMap.remove(userId));
	
	
	
	
	subscriberMap.put(userId,subscriber);
	
	log.info("Returning from subscribe..");
	
	return subscriber;
}

//Method to Dispatch events to connected Clients
@PostMapping(value="sse/notify")
public void dispatchEventsToAllClients(@RequestParam String eventName,String eventValue) {

RMap<String, Subscriber> subscriberMap = CacheUtil.getCacheClient().getMap(SUBSCRIBER_MAP);		


for (Map.Entry<String, Subscriber> subscriberEntry : subscriberMap.entrySet()) 
  {
		        
    log.info("userId:{}, subsriber:{}",subscriberEntry.getKey(),subscriberEntry.getValue());
		        
    try {
	        	log.info("Fetching subscriber Object from map..");
	        	
	        	Subscriber subscriber=(Subscriber) subscriberEntry.getValue();
       		    
	        	log.info("Fetched subscriber Object from subscriberMap..");

	        	if(null==subscriber) {
			   		log.error("Fetched subscriber Object is NULL");
			    }else
			    {

			    	log.info("subscriber displayString: {}...",ObjectUtils.getDisplayString(subscriber));
			   		log.info("AfterFetching:subscriber: {}",subscriber);
					log.info("AfterFetching:subscriber:toString: {}",subscriber.toString());
				    log.info("Sending Message to Client,{}",subscriberEntry.getKey());
				    subscriber.send(SseEmitter.event().name(eventName).data(eventValue));
				    log.info("Sent message to Client, userId :{}",subscriberEntry.getKey());
			     }
			        
		}catch (IOException ioe) {
			ioe.printStackTrace();
			subscriberMap.remove(subscriberEntry.getKey());
		}catch (Exception e) {
			e.printStackTrace();
			subscriberMap.remove(subscriberEntry.getKey());
		}
  }
}



}