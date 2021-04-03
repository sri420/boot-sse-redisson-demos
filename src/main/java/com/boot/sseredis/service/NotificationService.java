package com.boot.sseredis.service;

import com.boot.sseredis.model.EventDto;

public interface NotificationService {

    void sendNotification(String userId, EventDto event);
}
