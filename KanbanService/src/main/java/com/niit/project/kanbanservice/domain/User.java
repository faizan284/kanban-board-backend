package com.niit.project.kanbanservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {
    @MongoId
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
}
