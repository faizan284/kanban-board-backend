package com.niit.project.kanbanservice.service;

import com.niit.project.kanbanservice.domain.Project;
import com.niit.project.kanbanservice.domain.Status;
import com.niit.project.kanbanservice.domain.Task;
import com.niit.project.exception.*;
import com.niit.project.kanbanservice.exception.*;

import java.util.List;

public interface ProjectService {
    // create project
    Project saveProject(String email,Project project);
    // get all projects by email
    List<Project> getAllProjectsByEmail(String email) throws ProjectNotFoundException;
    Project changeProjectName(String email,Project project) throws ProjectNotFoundException;
    boolean deleteProject(String email,int projectId) throws ProjectNotFoundException;


    // status methods
    Project addStatus(String email, int projectId, Status status) throws ProjectNotFoundException, StatusAlreadyExistsException;
    Project removeStatus(String email, int projectId, int statusId) throws ProjectNotFoundException, StatusNotFoundException, StatusNotEmptyException;
    Project updateStatus(String email, int projectId,int statusId, Status status) throws ProjectNotFoundException,StatusNotFoundException;
    List<Status> getAllStatus(String email,int projectId) throws ProjectNotFoundException,StatusNotFoundException;

    // task methods
    Project addTask(String email, int projectId, String status, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskAlreadyExistsException;
    boolean removeTask(String email, int projectId, String status, String taskName) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException;
    Project updateTask(String email, int projectId, String status, Task task) throws ProjectNotFoundException,StatusNotFoundException,TaskNotFoundException;
    Project changeTaskStatus(String email, int projectId, String oldStatus, String newStatus, Task task) throws ProjectNotFoundException,StatusNotFoundException,TaskNotFoundException;

    // members
    Project addMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberAlreadyExistsException;
    boolean removeMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberNotFoundException;
    List<String> getAllMembers(String email, int projectId) throws ProjectNotFoundException, MemberNotFoundException;

    // assignees
    Project addAssignee(String email, int projectId,String status,String task, String assignee) throws ProjectNotFoundException, AssigneeAlreadyExistsException;
    boolean removeAssignee(String email, int projectId, String status, String task, String assignee) throws ProjectNotFoundException, AssigneeNotFoundException;
    List<String> getAllAssignees(String email, int projectId,Status status,String task) throws ProjectNotFoundException, AssigneeNotFoundException;
}
