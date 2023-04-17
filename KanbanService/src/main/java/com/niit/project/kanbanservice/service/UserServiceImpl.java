package com.niit.project.kanbanservice.service;

import com.niit.project.kanbanservice.domain.Project;
import com.niit.project.kanbanservice.domain.Status;
import com.niit.project.kanbanservice.domain.Task;
import com.niit.project.kanbanservice.domain.User;
import com.niit.project.exception.*;
import com.niit.project.kanbanservice.exception.*;
import com.niit.project.kanbanservice.repository.ProjectRepository;
import com.niit.project.kanbanservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements ProjectService,UserService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    public UserServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }


    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findById(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        return userRepository.save(user);
    }

    @Override
    public User getUser(String email) throws UserNotFoundException {
        if (userRepository.findById(email).isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = userRepository.findById(email).get();
        user.setPassword(null);
        return user;
    }

    @Override
    public User updateUser(String email, User user) throws UserNotFoundException {
        if (userRepository.findById(email).isEmpty()) {
            throw new UserNotFoundException();
        }
        User existingUser = userRepository.findById(email).get();
        if (user.getName()!=null){
            existingUser.setName(user.getName());
        }
        if (user.getPhoneNumber()!=null){
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getPassword()!=null){
            existingUser.setPassword(user.getPassword());
        }
        return userRepository.save(existingUser);
    }

    @Override
    public Map<String,List<String>> getUserEmailList() {
         List<User> list = userRepository.findAllEmail();
         List<String> emails = new ArrayList<>();
         for (User user : list) {
             emails.add(user.getEmail());
         }
         Map map = new HashMap<>();
         map.put("emails",emails);
         return map;
    }


    // project related methods
    @Override
    public Project saveProject(String email,Project project) {
        project.setProjectId(sequenceGeneratorService.getSequenceNumber(Project.SEQUENCE_NAME));
        project.setEmail(email);
        project.setStatusList(Arrays.asList(new Status(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME),"To Do",null),
                new Status(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME),"In Progress",null),
                new Status(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME),"Complete",null)));
        return projectRepository.save(project);

    }

    @Override
    public List<Project> getAllProjectsByEmail(String email) {
        List<Project> projects = projectRepository.findByEmail(email);
        projects.addAll(projectRepository.findByEmailInMembers(email));
        return projects;
    }

    @Override
    public Project changeProjectName(String email, Project project) throws ProjectNotFoundException {
        Project existingProject = projectRepository.findByEmailAndProjectId(email, project.getProjectId());
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        if (project.getProjectTitle()!=null) {
            existingProject.setProjectTitle(project.getProjectTitle());
        }
        return projectRepository.save(existingProject);
    }

    @Override
    public boolean deleteProject(String email, int projectId) throws ProjectNotFoundException {
        if (projectRepository.findByEmailAndProjectId(email, projectId)==null) {
            throw new ProjectNotFoundException();
        }
        projectRepository.deleteById(projectId);
        return true;
    }



    @Override
    public Project addStatus(String email, int projectId, Status status) throws ProjectNotFoundException, StatusAlreadyExistsException {
        Project existingProject = projectRepository.findByEmailAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        if (existingProject.getStatusList().stream().filter(o -> o.getStatus().equalsIgnoreCase(status.getStatus())).findAny().isPresent()) {
            throw new StatusAlreadyExistsException();
        }
        status.setStatusId(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME));
        existingProject.getStatusList().add(status);
        return projectRepository.save(existingProject);

    }

    @Override
    public Project removeStatus(String email, int projectId, int statusId) throws ProjectNotFoundException, StatusNotFoundException, StatusNotEmptyException {
        Project existingProject = projectRepository.findByEmailAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        if (existingProject.getStatusList().stream().filter(o -> o.getStatusId()==statusId).findAny().isEmpty()) {
            throw new StatusNotFoundException();
        }
        if (!existingProject.getStatusList().stream().filter(o -> o.getStatusId()==statusId).findAny().get().getTasks().isEmpty()) {
            throw new StatusNotEmptyException();
        }
        existingProject.getStatusList().removeIf(o -> o.getStatusId()==statusId);
        return projectRepository.save(existingProject);
    }

    @Override
    public Project updateStatus(String email, int projectId,int statusId, Status status) throws ProjectNotFoundException, StatusNotFoundException {
        Project existingProject = projectRepository.findByEmailAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus =existingProject.getStatusList().stream().filter(o -> o.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }
        Status existingStatus = optionalStatus.get();
        if (status.getStatus()!=null && !status.getStatus().isEmpty()){
            existingStatus.setStatus(status.getStatus());
        }
        return projectRepository.save(existingProject);
    }

    @Override
    public List<Status> getAllStatus(String email, int projectId) throws ProjectNotFoundException, StatusNotFoundException {
        Project existingProject = projectRepository.findByEmailAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        return existingProject.getStatusList();
    }

    @Override
    public Project addTask(String email, int projectId, String status, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskAlreadyExistsException {
        return null;
    }

    @Override
    public boolean removeTask(String email, int projectId, String status, String taskName) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException {
        return false;
    }

    @Override
    public Project updateTask(String email, int projectId, String status, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException {
        return null;
    }

    @Override
    public Project changeTaskStatus(String email, int projectId, String oldStatus, String newStatus, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException {
        return null;
    }

    @Override
    public Project addMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberAlreadyExistsException {
        return null;
    }

    @Override
    public boolean removeMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberNotFoundException {
        return false;
    }

    @Override
    public List<String> getAllMembers(String email, int projectId) throws ProjectNotFoundException, MemberNotFoundException {
        return null;
    }

    @Override
    public Project addAssignee(String email, int projectId, String status, String task, String assignee) throws ProjectNotFoundException, AssigneeAlreadyExistsException {
        return null;
    }

    @Override
    public boolean removeAssignee(String email, int projectId, String status, String task, String assignee) throws ProjectNotFoundException, AssigneeNotFoundException {
        return false;
    }

    @Override
    public List<String> getAllAssignees(String email, int projectId, Status status, String task) throws ProjectNotFoundException, AssigneeNotFoundException {
        return null;
    }

}
