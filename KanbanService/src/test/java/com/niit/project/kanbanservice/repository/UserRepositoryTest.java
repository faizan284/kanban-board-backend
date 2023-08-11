package com.niit.project.kanbanservice.repository;

import com.niit.project.kanbanservice.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    
    User user;
    @BeforeEach
    void setUp() {
        user = new User("faizanchaudhary284@gmail.com","Faizan","Faizan@123","9876543210","");
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    @DisplayName("test for saving user details into database")
    public void saveUserSuccess() {
        userRepository.save(user);
        User user1 = userRepository.findById(user.getEmail()).get();
        assertEquals(user.getEmail(),user1.getEmail());
    }

    @Test
    @DisplayName("test for saving user details into database")
    public void saveUserFailure() {
        userRepository.save(user);
        User user1 = userRepository.findById(user.getEmail()).get();
        assertNotEquals(null,user1);
    }

    @Test
    @DisplayName("test for find all emails of user")
    void findAllEmail() {
        userRepository.save(user);
        userRepository.save(new User("suhasmartin76@gmail.com","Suhas Martin","Suhas@123","9876543210",""));
        List<User> emailsList = userRepository.findAllEmail();
        assertEquals(10,emailsList.size());
    }
}