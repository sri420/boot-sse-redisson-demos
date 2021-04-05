package com.boot.sseredis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.boot.sseredis.repository.SubscriberRepository;
import com.boot.sseredis.model.Subscriber;

@Service
@Slf4j
public class SubscriberService {

    private final long eventsTimeout;
    private final SubscriberRepository subscriberRepository;

    public SubscriberService(@Value("${events.connection.timeout}") long eventsTimeout,
                          SubscriberRepository repository) {
        this.eventsTimeout = eventsTimeout;
        this.subscriberRepository = repository;
    }

    public Subscriber createSubsriber(String userId) {
        
        log.info("Entering createSubscriber...");
        Subscriber subscriber = new Subscriber(eventsTimeout);
        subscriber.onCompletion(() -> subscriberRepository.remove(userId));
        subscriber.onTimeout(() -> subscriberRepository.remove(userId));
        subscriber.onError(e -> {
            log.error("Create Subscriber exception", e);
            subscriberRepository.remove(userId);
        });
        
        log.info("Calling addReplaceSubscrinet..");
        subscriberRepository.addOrReplaceSubscriber(userId, subscriber);
        return subscriber;
    }

}
