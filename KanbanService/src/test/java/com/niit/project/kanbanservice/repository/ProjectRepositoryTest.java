package com.niit.project.kanbanservice.repository;

import com.niit.project.kanbanservice.domain.Project;
import com.niit.project.kanbanservice.domain.Status;
import com.niit.project.kanbanservice.domain.Task;
import com.niit.project.kanbanservice.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    private Project project;
    private List<Status> statusList;
    private List<Task> taskList;

    @BeforeEach
    void setUp() {
        taskList = new ArrayList();
        statusList = new ArrayList();
        taskList.add(new Task(100000,"task1","2023-05-10T18:30:00.000Z","2023-05-18T18:30:00.000Z",1,"",new ArrayList<>()));
        statusList.add(new Status(10000,"To Do",taskList));
        project = new Project(1000,"Kanban","riyasadnani@gmail.com",statusList, Arrays.asList("faizanchaudhary284@gmail.com","riyasadnani@gmail.com","suhasmartin76@gmail.com","vareesqureshi@gmail.com"));
    }

    @AfterEach
    void tearDown() {
        project=null;
        statusList=null;
        taskList=null;
    }

    @Test
    @DisplayName("test for save project")
    public void saveProject() {
        projectRepository.save(project);
        Project project1 = projectRepository.findById(project.getProjectId()).get();
        assertEquals(project.getProjectTitle(),project1.getProjectTitle());
    }

    @Test
    @DisplayName("test for find project by owner email")
    void findByEmail() {
        List<Task> taskList2 = new ArrayList();
        List<Status> statusList2 = new ArrayList();
        taskList2.add(new Task(100001,"Add Food","2023-05-10T18:30:00.000Z","2023-05-18T18:30:00.000Z",1,"",new ArrayList<>()));
        statusList2.add(new Status(10001,"To Do",taskList));
        Project project2 = new Project(1001,"Food App","riyasadnani@gmail.com",statusList, Arrays.asList("faizanchaudhary284@gmail.com","riyasadnani@gmail.com","suhasmartin76@gmail.com"));
        projectRepository.save(project);
        projectRepository.save(project2);
        assertEquals(2,projectRepository.findByEmail("riyasadnani@gmail.com").size());

    }

    @Test
    @DisplayName("test for finding project by member")
    void findByEmailInMembers() {
        List<Task> taskList2 = new ArrayList();
        List<Status> statusList2 = new ArrayList();
        taskList2.add(new Task(100001,"Add Food","2023-05-10T18:30:00.000Z","2023-05-18T18:30:00.000Z",1,"",new ArrayList<>()));
        statusList2.add(new Status(10001,"To Do",taskList));
        Project project2 = new Project(1001,"Food App","riyasadnani@gmail.com",statusList, Arrays.asList("faizanchaudhary284@gmail.com","riyasadnani@gmail.com","suhasmartin76@gmail.com"));
        projectRepository.save(project);
        projectRepository.save(project2);
        assertEquals(1,projectRepository.findByEmailInMembers("vareesqureshi@gmail.com").size());
        assertNotEquals(2,projectRepository.findByEmailInMembers("vareesqureshi@gmail.com").size());
    }

    @Test
    void findByEmailAndProjectId() {
        projectRepository.save(project);
        Project project1 = projectRepository.findByEmailAndProjectId("riyasadnani@gmail.com",1000);
        assertEquals("Kanban",project1.getProjectTitle());
    }

    @Test
    void findByMemberAndProjectId() {
        projectRepository.save(project);
        Project project1 = projectRepository.findByMemberAndProjectId("vareesqureshi@gmail.com",1000);
        assertEquals("Kanban",project1.getProjectTitle());
    }
}