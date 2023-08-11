package com.niit.NotificationService.repository;

import com.niit.NotificationService.domain.EmailDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends MongoRepository<EmailDTO,String> {
}
