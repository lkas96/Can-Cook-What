package com.project.mini.lkas.ccw.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class LoginService {

    @Autowired
    private MapRepo mp;

    public Boolean loginValidation(String email, String password) {
        
        Boolean emailExist = mp.hashKeyExists(RedisKeys.ccwUsers, email);

        if (emailExist != true) {
            return false; // Email not found
        }

        String valueKey = mp.get(RedisKeys.ccwUsers, email);

        JsonReader jr = Json.createReader(new StringReader(valueKey));

        JsonObject jo = jr.readObject();

        if (password.equals(jo.getString("password"))){

            return true;
        } else {
            
            return false; // MMeans incorrect pw
        }
    }

    public Boolean emailExists(String email){
        Boolean emailExist = mp.hashKeyExists(RedisKeys.ccwUsers, email.toLowerCase());
        
        if (emailExist != true) {
            return false; // Email not found
        } else {
            return true;
        }
    }

    public Object getName(String email) {
        //add for homepge suer on greeting.

        String userDetails = mp.get(RedisKeys.ccwUsers, email);

        JsonReader jr = Json.createReader(new StringReader(userDetails));
        JsonObject jo = jr.readObject();

        return jo.getString("name");
    }

}
