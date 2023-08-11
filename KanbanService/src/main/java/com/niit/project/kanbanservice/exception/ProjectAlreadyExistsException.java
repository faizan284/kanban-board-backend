package com.niit.project.kanbanservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT,reason = "Project already exists")
public class ProjectAlreadyExistsException extends Throwable {
}
