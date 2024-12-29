package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.model.User;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

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

    public Boolean emailRegistered(String email) {
        //Check against redis db

        Boolean emailExist = mp.hashKeyExists(RedisKeys.ccwUsers, email);

        if (emailExist == true) {
            return true; // Email exists
        } else {
            return false; // Email not registeterd yet, allow proceed registering
        }
    }

    public String getUser(String email){

        String userData = mp.get(RedisKeys.ccwUsers, email);

        JsonReader jr = Json.createReader(new StringReader(userData));
        JsonObject jo = jr.readObject();

        String retrievedName = jo.getString("name");

        return retrievedName;
    }

}
