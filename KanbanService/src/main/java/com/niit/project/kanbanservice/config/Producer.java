package com.niit.project.kanbanservice.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DirectExchange exchange;

    public void sendMessageToRabbitMq(EmailDTO emailDTO) {
        rabbitTemplate.convertAndSend(exchange.getName(), "kanban_routingkey", emailDTO);
    }

    public void sendNotification(NotificationDTO notificationDTO) {
        rabbitTemplate.convertAndSend(exchange.getName(), "kanban_notification_routingkey", notificationDTO);
    }
}
