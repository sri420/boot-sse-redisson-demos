package com.boot.sseredis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.boot.sseredis.mapper.EventMapper;
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
            log.debug("No server event to send to device.");
           return;
        }
        doSendNotification(userId, event);
    }

    private void doSendNotification(String userId, EventDto event) {
        subscriberRepository.get(userId).ifPresentOrElse(subscriber -> {
            try {
                log.debug("Sending event: {} for user: {}", event, userId);
                subscriber.send(eventMapper.toSseEventBuilder(event));
            } catch (IOException | IllegalStateException e) {
                log.debug("Error while sending event: {} for subscriber: {} - exception: {}", event, userId, e);
                subscriberRepository.remove(userId);
            }
        }, () -> log.debug("No subscriber for userId {}", userId));
    }

}
