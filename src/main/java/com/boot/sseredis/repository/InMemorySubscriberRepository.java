package com.boot.sseredis.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import com.boot.sseredis.model.Subscriber;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemorySubscriberRepository implements SubscriberRepository {

    private Map<String, Subscriber> subscriberMap = new ConcurrentHashMap<>();

    @Override
    public void addOrReplaceSubscriber(String userId, Subscriber subscriber) {
        subscriberMap.put(userId, subscriber);
    }

    @Override
    public void remove(String userId) {
        if (subscriberMap != null && subscriberMap.containsKey(userId)) {
            log.debug("Removing subscriber for user: {}", userId);
            subscriberMap.remove(userId);
        } else {
            log.debug("No subscriber to remove for user: {}", userId);
        }
    }

    @Override
    public Subscriber get(String userId) {
        return subscriberMap.get(userId);
    }
}
