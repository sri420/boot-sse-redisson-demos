package com.boot.sseredis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.boot.sseredis.mapper.EventMapper;
import com.boot.sseredis.model.Subscriber;
import com.boot.sseredis.model.EventDto;
import com.boot.sseredis.repository.SubscriberRepository;

import java.io.IOException;

@Service
@Primary
@AllArgsConstructor
@Slf4j
public class SubscriberNotificationService implements NotificationService {

    private final SubscriberRepository subscriberRepository;
    private final EventMapper eventMapper;

    @Override
    public void sendNotification(String userId, EventDto event) {
        if (event == null) {
            log.info("No server event to send to device.");
           return;
        }
        doSendNotification(userId, event);
    }

    private void doSendNotification(String userId, EventDto event) {
        Subscriber subscriber =subscriberRepository.get(userId);
        if(null!=subscriber){
            try {
                log.info("Fetched subscriber: {}", subscriber);
                log.info("Fetched subscriber: {}", subscriber.toString());
                log.info("Received userId: {}", userId);
                log.info("Received event: {}", event);
                log.info("Sending event: {} for user: {}", event, userId);
                subscriber.send(eventMapper.toSseEventBuilder(event));
            } catch (IOException | IllegalStateException e) {
                log.error("Error while sending event: {} for subscriber: {} - exception: {}", event, userId, e);
                subscriberRepository.remove(userId);
            }
        }else{
                log.info("No subscriber for userId {}", userId);
        }
    }

}
