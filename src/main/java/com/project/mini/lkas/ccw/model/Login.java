package com.project.mini.lkas.ccw.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Login {
    @Email(message="Must be a valid email.")
    private String email;

    @NotBlank(message="Please enter password.")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Login(){
        
    }
}
