package com.niit.project.kanbanservice.controller;

import com.niit.project.kanbanservice.domain.Project;
import com.niit.project.kanbanservice.domain.Status;
import com.niit.project.kanbanservice.domain.User;
import com.niit.project.exception.*;
import com.niit.project.kanbanservice.exception.*;
import com.niit.project.kanbanservice.service.ProjectService;
import com.niit.project.kanbanservice.service.UserService;
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

    @PostMapping("/saveuser")
    public ResponseEntity<?> saveUser(@RequestBody User user) throws UserAlreadyExistsException {
        User user1 = userService.registerUser(user);
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestBody User user) throws UserNotFoundException {
        User user1 = userService.getUser(user.getEmail());
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }
    @PutMapping("/updateuser")
    public ResponseEntity<?> updateUser(@RequestBody User user) throws UserNotFoundException {
        User user1 = userService.updateUser(user.getEmail(),user);
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }
    @GetMapping("/useremails")
    public ResponseEntity<?> getUserEmails(){
        return new ResponseEntity<>(userService.getUserEmailList(), HttpStatus.OK);
    }
    @PostMapping("/project")
    public ResponseEntity<?> saveProject(@RequestBody Project project) {
        Project project1 = projectService.saveProject(project.getEmail(),project);
        return new ResponseEntity<>(project1, HttpStatus.OK);
    }

    @GetMapping("/projects/{email}")
    public ResponseEntity<?> getAllProjects(@PathVariable String email) throws ProjectNotFoundException {
        return new ResponseEntity<>(projectService.getAllProjectsByEmail(email), HttpStatus.OK);
    }

    @PutMapping("/project/update/{email}")
    public ResponseEntity<?> updateProject(@RequestBody Project project,@PathVariable String email) throws ProjectNotFoundException {
        return new ResponseEntity<>(projectService.changeProjectName(email,project), HttpStatus.OK);
    }
    @DeleteMapping("/project/delete/{projectId}/{email}")
    public ResponseEntity<?> deleteProject(@PathVariable int projectId,@PathVariable String email) throws ProjectNotFoundException {
        return new ResponseEntity<>(projectService.deleteProject(email,projectId), HttpStatus.OK);
    }
    @PostMapping("/project/addstatus/{projectId}/{email}")
    public ResponseEntity<?> addStatus(@PathVariable int projectId,@PathVariable String email,@RequestBody Status status) throws ProjectNotFoundException, StatusAlreadyExistsException {
        return new ResponseEntity<>(projectService.addStatus(email,projectId,status), HttpStatus.OK);
    }

    @DeleteMapping("/project/deletestatus/{projectId}/{email}/{statusId}")
    public ResponseEntity<?> deleteStatus(@PathVariable int projectId,@PathVariable String email,@PathVariable int statusId) throws ProjectNotFoundException, StatusNotFoundException, StatusNotEmptyException {
        return new ResponseEntity<>(projectService.removeStatus(email,projectId,statusId), HttpStatus.OK);
    }
    @PutMapping("/project/updatestatus/{projectId}/{email}/{statusId}")
    public ResponseEntity<?> updateStatus(@RequestBody Status status,@PathVariable int projectId,@PathVariable String email,@PathVariable int statusId) throws ProjectNotFoundException, StatusNotFoundException {
        return new ResponseEntity<>(projectService.updateStatus(email,projectId,statusId,status), HttpStatus.OK);
    }
    @GetMapping("/project/allstatus/{email}/{projectId}")
    public ResponseEntity<?> getAllStatus(@PathVariable String email,@PathVariable int projectId) throws ProjectNotFoundException, StatusNotFoundException {
        return new ResponseEntity<>(projectService.getAllStatus(email,projectId), HttpStatus.OK);
    }
}
