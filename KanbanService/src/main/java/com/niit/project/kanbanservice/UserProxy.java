package com.niit.project.kanbanservice;


import com.niit.project.kanbanservice.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "eurekaAuthClient", url = "localhost:8081")
public interface UserProxy {
    @RequestMapping("/userAuth/register")
    public ResponseEntity<?> registerUser(@RequestBody User user);

}
