package com.niit.NotificationService.service;

import com.niit.NotificationService.domain.NotificationDTO;
import com.niit.NotificationService.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @RabbitListener(queues = "kanban_notification_queue")
    @Override
    public void saveNotification(NotificationDTO notificationDTO) {
        notificationDTO.setId(sequenceGeneratorService.getSequenceNumber(NotificationDTO.SEQUENCE_NAME));
        notificationRepository.save(notificationDTO);
    }

    @Override
    public List<NotificationDTO> getAllNotifications(String email) {
        return notificationRepository.findByEmail(email);
    }
}
