package com.niit.project.kanbanservice.domain;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Project {
    @Transient
    public static final String SEQUENCE_NAME = "project_sequence";
    @MongoId
    private int projectId;
    private String projectTitle;
    private String email;
    private List<Status> statusList;

    private List<String> members;


}
