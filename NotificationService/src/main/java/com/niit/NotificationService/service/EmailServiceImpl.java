package com.niit.NotificationService.service;

import com.niit.NotificationService.domain.EmailDTO;
import com.niit.NotificationService.repository.EmailRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @RabbitListener(queues = "kanban_queue")
    @Override
    public void sendEmail(EmailDTO emailDTO) {
        try{
            System.out.println(emailDTO);
            SimpleMailMessage mailMessage=new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(emailDTO.getEmail());
            mailMessage.setText(emailDTO.getMessage());
            mailMessage.setSubject(emailDTO.getSubject());
            javaMailSender.send(mailMessage);
            System.out.println("send successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
        emailDTO.setId(sequenceGeneratorService.getSequenceNumber(EmailDTO.SEQUENCE_NAME));
        emailRepository.save(emailDTO);
    }
}
