package com.niit.NotificationService.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class EmailDTO {
    @Transient
    public static final String SEQUENCE_NAME = "emailDTO_sequence";
    @MongoId
    private int id;
    private String email;
    private String name;
    private String subject;
    private String message;
}
