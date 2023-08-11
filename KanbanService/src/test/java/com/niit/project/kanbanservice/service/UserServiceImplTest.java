package com.niit.project.kanbanservice.service;

import com.niit.project.kanbanservice.domain.User;
import com.niit.project.kanbanservice.exception.UserAlreadyExistsException;
import com.niit.project.kanbanservice.repository.ProjectRepository;
import com.niit.project.kanbanservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("faizanchaudhary284@gmail.com","Faizan","Faizan@123","9876543210","");
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    void registerUser() throws UserAlreadyExistsException {
//         when(userRepository.findById(user.getEmail())).thenReturn(Optional.ofNullable(null));
//         when(userRepository.save(user)).thenReturn(user);
//         assertEquals(user,userServiceImpl.registerUser(user));
//         verify(userRepository,times(1)).findById(any());
//         verify(userRepository,times(1)).save(any());
    }

    @Test
    void getUser() {
    }

    @Test
    void getUserEmailList() {
    }

    @Test
    void saveProject() {
    }

    @Test
    void getProjectByEmailAndId() {
    }

    @Test
    void getAllProjectsByEmail() {
    }

    @Test
    void updateProject() {
    }

    @Test
    void deleteProject() {
    }

    @Test
    void addStatus() {
    }

    @Test
    void addTask() {
    }
}