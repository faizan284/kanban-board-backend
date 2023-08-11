package com.niit.project.kanbanservice.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailDTO {
    private String email;
    private String name;
    private String subject;
    private String message;
}
