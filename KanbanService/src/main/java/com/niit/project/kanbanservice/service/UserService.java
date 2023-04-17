package com.niit.project.kanbanservice.service;


import com.niit.project.kanbanservice.domain.User;
import com.niit.project.kanbanservice.exception.UserAlreadyExistsException;
import com.niit.project.kanbanservice.exception.UserNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService {
    User registerUser(User user) throws UserAlreadyExistsException;
    User getUser(String email) throws UserNotFoundException;
    User updateUser(String email, User user) throws UserNotFoundException;
    Map<String,List<String>> getUserEmailList();
//    Boolean deleteUser(String email) throws UserNotFoundException;
}
