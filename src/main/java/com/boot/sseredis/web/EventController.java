package com.boot.sseredis.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.boot.sseredis.model.EventDto;
import com.boot.sseredis.model.Subscriber;
import com.boot.sseredis.service.SubscriberService;
import com.boot.sseredis.service.NotificationService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {


    private final SubscriberService subscriberService;
    private final NotificationService notificationService;

    @GetMapping(value="events/{userId}")
    public Subscriber subscribeToEvents(@PathVariable("userId") String userId) {
        log.info("Subscribing user with id {}", userId);
        return subscriberService.createSubsriber(userId);
    }

    @PostMapping(value="events/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishEvent(@PathVariable("userId") String userId, @RequestBody EventDto event) {
        log.info("Publishing event {} for user with id {}", event, userId);
        notificationService.sendNotification(userId, event);
    }
}
