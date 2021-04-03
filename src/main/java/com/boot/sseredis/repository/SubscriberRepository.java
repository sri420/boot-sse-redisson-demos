package com.boot.sseredis.repository;


import java.util.Map;
import java.util.Optional;
import com.boot.sseredis.model.Subscriber;

public interface SubscriberRepository {

    void addOrReplaceSubscriber(String userId, Subscriber subscriber);

    void remove(String userId);

    Optional<Subscriber> get(String userId);
}
