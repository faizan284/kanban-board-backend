package com.niit.NotificationService.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfiguration {
    private final String exchangeName="kanban_exchange";
    private final String queue="kanban_queue";
    private final String notificationQueue="kanban_notification_queue";

    @Bean
    public Queue queue()
    {
        return new Queue(queue);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue);
    }

    @Bean
    public DirectExchange directExchange()
    {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding bindingUser(Queue queue, DirectExchange exchange)
    {
        return BindingBuilder.bind(queue).to(exchange).with("kanban_routingkey");
    }

    @Bean
    public Binding bindingNotificationQueue(Queue notificationQueue, DirectExchange exchange)
    {
        return BindingBuilder.bind(notificationQueue).to(exchange).with("kanban_notification_routingkey");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter()
    {
        return new  Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory)
    {
        RabbitTemplate rabbitTemp=new RabbitTemplate(connectionFactory);
        rabbitTemp.setMessageConverter(messageConverter());
        return rabbitTemp;
    }


}

