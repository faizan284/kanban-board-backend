package com.niit.project.kanbanservice.controller;

import com.niit.project.kanbanservice.domain.Project;
import com.niit.project.kanbanservice.domain.Status;
import com.niit.project.kanbanservice.domain.Task;
import com.niit.project.kanbanservice.domain.User;
import com.niit.project.kanbanservice.exception.*;
import com.niit.project.kanbanservice.service.ProjectService;
import com.niit.project.kanbanservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("kanbanService")
public class KanbanController {

    private final ProjectService projectService;
    private final UserService userService;
    @Autowired
    public KanbanController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @PostMapping("/Register")
    public ResponseEntity<?> saveUser(@RequestBody User user) {

        try {
            User user1 = userService.registerUser(user);
            return new ResponseEntity<>(user1, HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            return  new ResponseEntity<>(e, HttpStatus.CONFLICT);
        }

    }

    @GetMapping("/user/getUser")
    public ResponseEntity<?> getUser(HttpServletRequest httpServletRequest){
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            User user1 = userService.getUser(email);
            return new ResponseEntity<>(user1, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }

    }
    @PutMapping("/user/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User user) {

        try {
            User user1 = userService.updateUser(user.getEmail(),user);
            return new ResponseEntity<>(user1, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }

    }
    @GetMapping("/user/userEmails")
    public ResponseEntity<?> getUserEmails(HttpServletRequest httpServletRequest){
//        String email = httpServletRequest.getAttribute("email").toString();
        return new ResponseEntity<>(userService.getUserEmailList(), HttpStatus.OK);
    }
    @PostMapping("/project/save")
    public ResponseEntity<?> saveProject(@RequestBody Project project,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();
        try {
            Project project1 = projectService.saveProject(email, project);
            return new ResponseEntity<>(project1, HttpStatus.OK);
        }
        catch(ProjectAlreadyExistsException e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/project/getProject/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable int projectId, HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try{
            return new ResponseEntity<>(projectService.getProjectByEmailAndId(email,projectId),HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/project/getAll")
    public ResponseEntity<?> getAllProjects(HttpServletRequest httpServletRequest){
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.getAllProjectsByEmail(email), HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/project/update")
    public ResponseEntity<?> updateProject(@RequestBody Project project,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.updateProject(email,project), HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/project/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable int projectId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.deleteProject(email,projectId), HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/project/addStatus/{projectId}")
    public ResponseEntity<?> addStatus(@PathVariable int projectId,HttpServletRequest httpServletRequest,@RequestBody Status status) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.addStatus(email,projectId,status), HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (StatusAlreadyExistsException e) {
            return new ResponseEntity<>(e,HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/project/deleteStatus/{projectId}/{statusId}")
    public ResponseEntity<?> deleteStatus(@PathVariable int projectId,@PathVariable int statusId,HttpServletRequest httpServletRequest){
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.removeStatus(email,projectId,statusId), HttpStatus.OK);
        } catch (ProjectNotFoundException | StatusNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (StatusNotEmptyException e) {
            return new ResponseEntity<>(e,HttpStatus.CONFLICT);
        }
    }
    @PutMapping("/project/updateStatus/{projectId}/{statusId}")
    public ResponseEntity<?> updateStatus(@RequestBody Status status,@PathVariable int projectId,@PathVariable int statusId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.updateStatus(email,projectId,statusId,status), HttpStatus.OK);
        } catch (ProjectNotFoundException | StatusNotFoundException | StatusAlreadyExistsException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/project/allStatus/{projectId}")
    public ResponseEntity<?> getAllStatus(@PathVariable int projectId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.getAllStatus(email,projectId), HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/project/addTask/{projectId}/{statusId}")
    public ResponseEntity<?> addTask(@RequestBody Task task, @PathVariable int projectId, @PathVariable int statusId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.addTask(email,projectId,statusId,task), HttpStatus.OK);
        } catch (InvalidDataException | ProjectNotFoundException | StatusNotFoundException | TaskAlreadyExistsException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/project/deleteTask/{projectId}/{statusId}/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable int projectId, @PathVariable int statusId, @PathVariable int taskId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.removeTask(email,projectId,statusId,taskId), HttpStatus.OK);
        } catch (ProjectNotFoundException | StatusNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/project/updateTask/{projectId}/{statusId}")
    public ResponseEntity<?> updateTask(@RequestBody Task task, @PathVariable int projectId, @PathVariable int statusId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.updateTask(email,projectId,statusId,task), HttpStatus.OK);
        } catch (ProjectNotFoundException | StatusNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/project/changeStatus/{projectId}/{oldStatus}/{newStatus}")
    public ResponseEntity<?> changeStatus(@RequestBody Task task, @PathVariable int projectId, @PathVariable int oldStatus, @PathVariable int newStatus,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.changeTaskStatus(email,projectId,oldStatus,newStatus,task), HttpStatus.OK);
        } catch (ProjectNotFoundException | StatusNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/project/addMember/{projectId}")
    public ResponseEntity<?> addMember(@RequestBody User user, @PathVariable int projectId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.addMember(email,projectId,user.getEmail()), HttpStatus.OK);
        } catch (ProjectNotFoundException | MemberAlreadyExistsException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/project/deleteMember/{projectId}/{member}")
    public ResponseEntity<?> deleteMember(@PathVariable int projectId,@PathVariable String member,HttpServletRequest httpServletRequest){
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.removeMember(email,projectId,member), HttpStatus.OK);
        } catch (ProjectNotFoundException | MemberNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/project/members/{projectId}")
    public ResponseEntity<?> getAllMembers(@PathVariable int projectId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.getAllMembers(email,projectId), HttpStatus.OK);
        } catch (ProjectNotFoundException | MemberNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/project/addAssignee/{projectId}/{statusId}/{taskId}")
    public ResponseEntity<?> addAssignee(@RequestBody User user, @PathVariable int projectId,@PathVariable int statusId, @PathVariable int taskId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.addAssignee(email,projectId,statusId,taskId,user.getEmail()), HttpStatus.OK);
        } catch (ProjectNotFoundException | AssigneeAlreadyExistsException | StatusNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/project/deleteAssignee/{projectId}/{statusId}/{taskId}/{assignee}")
    public ResponseEntity<?> deleteAssignee(@PathVariable int projectId,@PathVariable int statusId, @PathVariable int taskId,@PathVariable String assignee,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.removeAssignee(email,projectId,statusId,taskId,assignee), HttpStatus.OK);
        } catch (ProjectNotFoundException | AssigneeNotFoundException | StatusNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/project/assignees/{projectId}/{statusId}/{taskId}")
    public ResponseEntity<?> getAllAssignees(@PathVariable int projectId, @PathVariable int statusId, @PathVariable int taskId,HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getAttribute("email").toString();

        try {
            return new ResponseEntity<>(projectService.getAllAssignees(email,projectId,statusId,taskId), HttpStatus.OK);
        } catch (ProjectNotFoundException | AssigneeNotFoundException | StatusNotFoundException | TaskNotFoundException e) {
            return new ResponseEntity<>(e,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
