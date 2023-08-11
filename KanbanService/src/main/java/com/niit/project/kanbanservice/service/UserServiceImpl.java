package com.niit.project.kanbanservice.service;

import com.niit.project.kanbanservice.UserProxy;
import com.niit.project.kanbanservice.config.EmailDTO;
import com.niit.project.kanbanservice.config.NotificationDTO;
import com.niit.project.kanbanservice.config.Producer;
import com.niit.project.kanbanservice.domain.Project;
import com.niit.project.kanbanservice.domain.Status;
import com.niit.project.kanbanservice.domain.Task;
import com.niit.project.kanbanservice.domain.User;

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
    private final UserProxy userProxy;
    @Autowired
    public UserServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, SequenceGeneratorService sequenceGeneratorService, UserProxy userProxy) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.userProxy = userProxy;
    }

    @Autowired
    Producer producer;

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findById(user.getEmail().toLowerCase()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        user.setEmail(user.getEmail().toLowerCase());
        User user2 = userRepository.save(user);
        if (!user2.getName().isEmpty()) {
            userProxy.registerUser(user2);
            producer.sendMessageToRabbitMq(new EmailDTO(user2.getEmail(),user2.getName(),"Kanban Board","Your registration done successfully"));
        }
        return user2;
    }

    @Override
    public User getUser(String email) throws UserNotFoundException {
        System.out.println(email);
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
        existingUser.setImageUrl(user.getImageUrl());
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
    public Project saveProject(String email,Project project) throws  ProjectAlreadyExistsException {
        List<Project> existingProject = projectRepository.findByEmail(email);
        if (existingProject.stream().anyMatch(p->p.getProjectTitle().equalsIgnoreCase(project.getProjectTitle()))){
            throw new ProjectAlreadyExistsException();
        }
        project.setProjectId(sequenceGeneratorService.getSequenceNumber(Project.SEQUENCE_NAME));
        project.setEmail(email);

        project.getMembers().add(email);
        project.setStatusList(Arrays.asList(new Status(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME),"To Do",new ArrayList<>()),
                new Status(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME),"In Progress",new ArrayList<>()),
                new Status(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME),"Complete",new ArrayList<>())));
        for(String e: project.getMembers() ) {
            producer.sendMessageToRabbitMq(new EmailDTO(e,userRepository.findById(e).get().getName(),"Kanban Board",project.getProjectTitle()+" Project has been assigned to you "));
            producer.sendNotification(new NotificationDTO(e,project.getProjectTitle()+" Project has been assigned to you"));
        }
        return projectRepository.save(project);

    }

    @Override
    public Project getProjectByEmailAndId(String email,int projectId) throws ProjectNotFoundException {
        if (projectRepository.findByMemberAndProjectId(email,projectId)==null) {
            throw new ProjectNotFoundException();
        }
        return projectRepository.findByMemberAndProjectId(email,projectId);
    }
    @Override
    public List<Project> getAllProjectsByEmail(String email) {
        return projectRepository.findByEmailInMembers(email);
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
    public Project updateProject(String email, Project project) throws ProjectNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, project.getProjectId());
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        for(String e: project.getMembers()) {
            if(!existingProject.getMembers().contains(e)) {
                producer.sendMessageToRabbitMq(new EmailDTO(e,userRepository.findById(e).get().getName(),"Kanban Board",project.getProjectTitle()+" Project has been assigned to you "));
                producer.sendNotification(new NotificationDTO(e,project.getProjectTitle()+" Project has been assigned to you"));
            }
        }
        for(String e: existingProject.getMembers()) {
            if(!project.getMembers().contains(e)){
                producer.sendMessageToRabbitMq(new EmailDTO(e,userRepository.findById(e).get().getName(),"Kanban Board","You have been removed from the project "+project.getProjectTitle()));
                producer.sendNotification(new NotificationDTO(e,"You have been removed from the project "+project.getProjectTitle()));
            }
        }
        return projectRepository.save(project);
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
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        if (existingProject.getStatusList().stream().anyMatch(o -> o.getStatus().equalsIgnoreCase(status.getStatus()))) {
            throw new StatusAlreadyExistsException();
        }
        status.setStatusId(sequenceGeneratorService.getSequenceNumber(Status.SEQUENCE_NAME));
        status.setTasks(new ArrayList<>());
        existingProject.getStatusList().add(status);
        return projectRepository.save(existingProject);

    }

    @Override
    public Project removeStatus(String email, int projectId, int statusId) throws ProjectNotFoundException, StatusNotFoundException, StatusNotEmptyException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
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
    public Project updateStatus(String email, int projectId,int statusId, Status status) throws ProjectNotFoundException, StatusNotFoundException, StatusAlreadyExistsException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus =existingProject.getStatusList().stream().filter(o -> o.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }
        List<Status> compareStatusList =existingProject.getStatusList().stream().filter(s -> s.getStatusId()!=statusId).toList();
        if (compareStatusList.stream().anyMatch(o -> o.getStatus().equalsIgnoreCase(status.getStatus()))) {
            throw new StatusAlreadyExistsException();
        }
        Status existingStatus = optionalStatus.get();
        if (status.getStatus()!=null && !status.getStatus().isEmpty()){
            existingStatus.setStatus(status.getStatus());
        }
        return projectRepository.save(existingProject);
    }

    @Override
    public List<Status> getAllStatus(String email, int projectId) throws ProjectNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        return existingProject.getStatusList();
    }

    //task methods
    @Override
    public Project addTask(String email, int projectId, int statusId, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskAlreadyExistsException, InvalidDataException {
        if (task.getTaskTitle()==null || task.getTaskTitle().isEmpty())
            throw new InvalidDataException();
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus =existingProject.getStatusList().stream().filter(o -> o.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }
        Status status = optionalStatus.get();

        if (status.getTasks().stream().anyMatch(o -> o.getTaskTitle().equalsIgnoreCase(task.getTaskTitle()))) {
            throw new TaskAlreadyExistsException();
        }

        task.setTaskId(sequenceGeneratorService.getSequenceNumber(Task.SEQUENCE_NAME));
        if (task.getAssignees() == null) {
            task.setAssignees(new ArrayList<>());
        }
        for(String assignee : task.getAssignees()) {
            producer.sendNotification(new NotificationDTO(assignee,email+" assign you a task "+task.getTaskTitle()));
        }
        status.getTasks().add(task);
        task.setHistory(new ArrayList<>());
        task.getHistory().add(email+" create this task.");
        if(!(task.getStartDate()==null || task.getStartDate().isEmpty())) {
            task.getHistory().add(email+" set "+task.getStartDate().substring(0,10)+" as start date.");
        }
        if(!(task.getDueDate()==null || task.getDueDate().isEmpty())) {
            task.getHistory().add(email+" set "+task.getDueDate().substring(0,10)+" as due date.");
        }
        if(!(task.getAssignees()==null || task.getAssignees().isEmpty())) {
            task.getHistory().add(email+" assign this task to "+task.getAssignees());
        }
        return projectRepository.save(existingProject);
    }

    @Override
    public Project removeTask(String email, int projectId, int statusId, int taskId) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus =existingProject.getStatusList().stream().filter(o -> o.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }

        if (!optionalStatus.get().getTasks().removeIf(t->t.getTaskId()==taskId)) {
            throw new TaskNotFoundException();
        }
        return projectRepository.save(existingProject);
    }

    @Override
    public Project updateTask(String email, int projectId, int statusId, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus =existingProject.getStatusList().stream().filter(o -> o.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }
        Status status = optionalStatus.get();
        Optional<Task> optionalTask = status.getTasks().stream().filter(t -> t.getTaskId()== task.getTaskId()).findAny();
        if(optionalTask.isEmpty()) {
            throw new TaskNotFoundException();
        }
        Task existingTask=optionalTask.get();
        if (!(task.getTaskTitle()==null || task.getTaskTitle().isEmpty())) {
            if(!existingTask.getTaskTitle().equals(task.getTaskTitle())) {
                task.getHistory().add(email+" change task title from "+existingTask.getTaskTitle()+" to "+task.getTaskTitle());

            }
            existingTask.setTaskTitle(task.getTaskTitle());
        }
        for(String e: task.getAssignees()) {
            if(!existingTask.getAssignees().contains(e)) {
                task.getHistory().add(email+" add "+e+" as assignee.");
                producer.sendNotification(new NotificationDTO(e,email+" assign you a task "+task.getTaskTitle()));
            }
        }
        for(String e: existingTask.getAssignees()) {
            if(!task.getAssignees().contains(e)){
                task.getHistory().add(email+" remove "+e+" from assignees.");
                producer.sendNotification(new NotificationDTO(e,email+" removed you from task "+task.getTaskTitle()));
            }
        }
        if(!existingTask.getStartDate().equals(task.getStartDate())) {
            if(existingTask.getStartDate().isEmpty()) {
                task.getHistory().add(email+" set "+task.getStartDate().substring(0,10)+" as start date.");
            } else {
                task.getHistory().add(email+" change start date from "+existingTask.getStartDate().substring(0,10)+" to "+task.getStartDate().substring(0,10));
            }
        }
        if(!existingTask.getDueDate().equals(task.getDueDate())) {
            if(existingTask.getDueDate().isEmpty()) {
                task.getHistory().add(email+" set "+task.getDueDate().substring(0,10)+" as due date.");
            } else {
                task.getHistory().add(email+" change due date from "+existingTask.getDueDate().substring(0,10)+" to "+task.getDueDate().substring(0,10));
            }
        }
        if (existingTask.getPriority()!=task.getPriority()) {
            String existP ="";
            if (existingTask.getPriority()==3)
            existP= "Low";
            else if(existingTask.getPriority()==1)
            existP= "High";
            else if (existingTask.getPriority()==2)
            existP= "Normal";
            else
            existP= "Clear";
            String p ="";
            if (task.getPriority()==3)
                p= "Low";
            else if(existingTask.getPriority()==1)
                p= "High";
            else if (existingTask.getPriority()==2)
                p= "Normal";
            else
                p= "Clear";
            task.getHistory().add(email+" change priority from "+existP+" to "+p);
        }


        existingTask.setContents(task.getContents());
        existingTask.setAssignees(task.getAssignees());
        existingTask.setPriority(task.getPriority());
        existingTask.setStartDate(task.getStartDate());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setHistory(task.getHistory());
        return projectRepository.save(existingProject);
    }

    @Override
    public Project changeTaskStatus(String email, int projectId, int oldStatusId, int newStatusId, Task task) throws ProjectNotFoundException, StatusNotFoundException, TaskNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus = existingProject.getStatusList().stream().filter(s -> s.getStatusId()==oldStatusId).findAny();
        if (optionalStatus.isEmpty()){
            throw new StatusNotFoundException();
        }
        Status oldStatus=optionalStatus.get();
        Status newStatus=null;
        if (oldStatus.getTasks().removeIf(t -> t.getTaskId()== task.getTaskId())) {
            newStatus=existingProject.getStatusList().stream().filter(s -> s.getStatusId()==newStatusId).findAny().get();
            task.getHistory().add(email+" change status from "+oldStatus.getStatus()+" to "+newStatus.getStatus());
            newStatus.getTasks().add(task);

        }
        return projectRepository.save(existingProject);
    }

    @Override
    public Project addMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberAlreadyExistsException {
        Project existingProject = projectRepository.findByEmailAndProjectId(email, projectId);
        if (existingProject==null) {
            throw new ProjectNotFoundException();
        }
        if (existingProject.getMembers().contains(member)) {
            throw new MemberAlreadyExistsException();
        }
        existingProject.getMembers().add(member);
        return projectRepository.save(existingProject);
    }

    @Override
    public Project removeMember(String email, int projectId, String member) throws ProjectNotFoundException, MemberNotFoundException {
        Project existingProject = projectRepository.findByEmailAndProjectId(email, projectId);
        if (existingProject == null) {
            throw new ProjectNotFoundException();
        }
        if (!existingProject.getMembers().contains(member)) {
            throw new MemberNotFoundException();
        }
        existingProject.getMembers().remove(member);
        return projectRepository.save(existingProject);
    }

    @Override
    public List<String> getAllMembers(String email, int projectId) throws ProjectNotFoundException, MemberNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject == null) {
            throw new ProjectNotFoundException();
        }
        return existingProject.getMembers();
    }

    @Override
    public Project addAssignee(String email, int projectId, int statusId, int taskId, String assignee) throws ProjectNotFoundException, AssigneeAlreadyExistsException, StatusNotFoundException, TaskNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject == null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus = existingProject.getStatusList().stream().filter(s -> s.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }
        Optional<Task> optionalTask = optionalStatus.get().getTasks().stream().filter(t -> t.getTaskId()==taskId).findAny();
        if (optionalTask.isEmpty()) {
            throw new TaskNotFoundException();
        }
        if (optionalTask.get().getAssignees().contains(assignee)) {
            throw new AssigneeAlreadyExistsException();
        }
        optionalTask.get().getAssignees().add(assignee);
        return projectRepository.save(existingProject);
    }

    @Override
    public Project removeAssignee(String email, int projectId, int statusId, int taskId, String assignee) throws ProjectNotFoundException, AssigneeNotFoundException, StatusNotFoundException, TaskNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject == null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus = existingProject.getStatusList().stream().filter(s -> s.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }
        Optional<Task> optionalTask = optionalStatus.get().getTasks().stream().filter(t -> t.getTaskId()==taskId).findAny();
        if (optionalTask.isEmpty()) {
            throw new TaskNotFoundException();
        }
        if (!optionalTask.get().getAssignees().contains(assignee)) {
            throw new AssigneeNotFoundException();
        }
        optionalTask.get().getAssignees().remove(assignee);
        return projectRepository.save(existingProject);
    }

    @Override
    public List<String> getAllAssignees(String email, int projectId, int statusId, int taskId) throws ProjectNotFoundException, AssigneeNotFoundException, StatusNotFoundException, TaskNotFoundException {
        Project existingProject = projectRepository.findByMemberAndProjectId(email, projectId);
        if (existingProject == null) {
            throw new ProjectNotFoundException();
        }
        Optional<Status> optionalStatus = existingProject.getStatusList().stream().filter(s -> s.getStatusId()==statusId).findAny();
        if (optionalStatus.isEmpty()) {
            throw new StatusNotFoundException();
        }
        Optional<Task> optionalTask = optionalStatus.get().getTasks().stream().filter(t -> t.getTaskId()==taskId).findAny();
        if (optionalTask.isEmpty()) {
            throw new TaskNotFoundException();
        }
        return optionalTask.get().getAssignees();
    }


}
