package com.niit.NotificationService.repository;

import com.niit.NotificationService.domain.NotificationDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationDTO,String> {
    List<NotificationDTO> findByEmail(String email);
}
