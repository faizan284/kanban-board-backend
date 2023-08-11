package com.niit.project.kanbanservice.service;

import com.niit.project.kanbanservice.domain.Project;
import com.niit.project.kanbanservice.domain.Status;
import com.niit.project.kanbanservice.domain.Task;

import com.niit.project.kanbanservice.exception.*;

import java.util.List;

public interface ProjectService {
    // create project
    Project saveProject(String email,Project project) throws ProjectAlreadyExistsException;
    Project getProjectByEmailAndId(String email,int projectId) throws ProjectNotFoundException;
    // get all projects by email
    List<Project> getAllProjectsByEmail(String email) throws ProjectNotFoundException;
    Project changeProjectName(String email,Project project) throws ProjectNotFoundException;
    Project updateProject(String email,Project project) throws ProjectNotFoundException;
    boolean deleteProject(String email,int projectId) throws ProjectNotFoundException;


    // status methods
    Project addStatus(String email, int projectId, Status status) throws ProjectNotFoundException, StatusAlreadyExistsException;
    Project removeStatus(String email, int projectId, int statusId) throws ProjectNotFoundException, StatusNotFoundException, StatusNotEmptyException;
    Project updateStatus(String email, int projectId,int statusId, Status status) throws ProjectNotFoundException, StatusNotFoundException, StatusAlreadyExistsException;
    List<Status> getAllStatus(String email,int projectId) throws ProjectNotFoundException;

    // task methods
    Project addTask(String email, int projectId, int statusId, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskAlreadyExistsException, InvalidDataException;
    Project removeTask(String email, int projectId, int statusId, int taskId) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException;
    Project updateTask(String email, int projectId, int statusId, Task task) throws ProjectNotFoundException,StatusNotFoundException,TaskNotFoundException;
    Project changeTaskStatus(String email, int projectId, int oldStatus, int newStatus, Task task) throws ProjectNotFoundException,StatusNotFoundException,TaskNotFoundException;

    // members
    Project addMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberAlreadyExistsException;
    Project removeMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberNotFoundException;
    List<String> getAllMembers(String email, int projectId) throws ProjectNotFoundException, MemberNotFoundException;

    // assignees
    Project addAssignee(String email, int projectId,int statusId,int taskId, String assignee) throws ProjectNotFoundException, AssigneeAlreadyExistsException, StatusNotFoundException, TaskNotFoundException;
    Project removeAssignee(String email, int projectId, int statusId, int taskId, String assignee) throws ProjectNotFoundException, AssigneeNotFoundException, StatusNotFoundException, TaskNotFoundException;
    List<String> getAllAssignees(String email, int projectId,int statusId,int taskId) throws ProjectNotFoundException, AssigneeNotFoundException, StatusNotFoundException, TaskNotFoundException;

}
