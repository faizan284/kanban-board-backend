package com.niit.project.kanbanservice.repository;

import com.niit.project.kanbanservice.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    @Query(value = "{}",fields = "{'_id':1}")
    List<User> findAllEmail();

}
