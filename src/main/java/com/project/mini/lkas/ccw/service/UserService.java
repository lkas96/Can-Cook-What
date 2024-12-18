package com.project.mini.lkas.ccw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.model.User;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class UserService {

    @Autowired
    private MapRepo mp;

    public void createUser(User user){
        
        JsonObject jo = Json.createObjectBuilder()
                        .add("name", user.getName())
                        .add("email", user.getEmail())
                        .add("password", user.getPassword())
                        .build();
        
        mp.create(RedisKeys.ccwUsers, user.getEmail().toString(), jo.toString());
    }

}
