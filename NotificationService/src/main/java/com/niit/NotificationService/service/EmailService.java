package com.niit.NotificationService.service;

import com.niit.NotificationService.domain.EmailDTO;

public interface EmailService {
    void sendEmail(EmailDTO emailDTO);
}
