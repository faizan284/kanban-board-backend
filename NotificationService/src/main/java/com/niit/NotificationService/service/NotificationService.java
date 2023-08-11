package com.niit.NotificationService.service;

import com.niit.NotificationService.domain.NotificationDTO;

import java.util.List;

public interface NotificationService {
    void saveNotification(NotificationDTO notificationDTO);
    List<NotificationDTO> getAllNotifications(String email);
}
