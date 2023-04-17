package com.niit.project.kanbanservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Transient
    public static final String SEQUENCE_NAME = "task_sequence";
    @Id
    private int taskId;
    private String taskTitle;
    private Date startDate;
    private Date dueDate;
    private String priority;
    private String contents;
    private List<String> assignees;
//    private List<String> history;
}
