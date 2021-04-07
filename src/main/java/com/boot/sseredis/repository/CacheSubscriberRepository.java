package com.boot.sseredis.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import com.boot.sseredis.model.Subscriber;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.redisson.api.RMap;
import com.boot.sseredis.util.CacheUtil;
import org.springframework.context.annotation.Profile;


@Profile("cache")
@Repository
@RequiredArgsConstructor
@Slf4j
public class CacheSubscriberRepository implements SubscriberRepository {


    RMap<String, Subscriber> subscriberMap = CacheUtil.getCacheClient().getMap("subscriberMap");		

    @Override
    public void addOrReplaceSubscriber(String userId, Subscriber subscriber) {
        log.info("Adding Subscriber : {}", subscriber);
        subscriberMap.put(userId, subscriber);
    }

    @Override
    public void remove(String userId) {
        if (subscriberMap != null && subscriberMap.containsKey(userId)) {
            log.info("Removing subscriber for user: {}", userId);
            subscriberMap.remove(userId);
        } else {
            log.info("No subscriber to remove for user: {}", userId);
        }
    }

    @Override
    public Subscriber get(String userId) {
        return subscriberMap.get(userId);
    }
}
