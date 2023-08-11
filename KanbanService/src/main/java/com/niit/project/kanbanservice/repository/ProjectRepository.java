package com.niit.project.kanbanservice.repository;

import com.niit.project.kanbanservice.domain.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project,Integer> {
    List<Project> findByEmail(String email);
    @Query(value = "{'members':{$regex:?0,$options:'i'}}")
    List<Project> findByEmailInMembers(String email);
    Project findByEmailAndProjectId(String email,int projectId);
    @Query(value = "{'members':{$regex:?0,$options:'i'},'projectId':?1}")
    Project findByMemberAndProjectId(String assignee,int projectId);
}
