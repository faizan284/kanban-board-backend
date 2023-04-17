package com.niit.project.kanbanservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status {
    @Transient
    public static final String SEQUENCE_NAME = "status_sequence";
    @Id
    private int statusId;
    private String status;
    private List<Task> tasks;
}
