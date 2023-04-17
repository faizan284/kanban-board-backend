package com.niit.project.kanbanservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT,reason = "Task already exists")
public class TaskAlreadyExistsException extends Exception {
}
