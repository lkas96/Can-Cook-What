package com.project.mini.lkas.ccw.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class User {

    @NotEmpty(message="Please enter a name.")
    @Size(min=2, max=50, message="Must be between 2-50 characters long.")
    private String name;

    @NotEmpty(message="Please enter an email.")
    @Email(message="Please enter a valid email address.")
    private String email;

    @NotEmpty(message="Please enter a password.")
    @Size(min=8, max=16, message="Password must be between 8 and 16 characters long.")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    @Override
    public String toString() {
        return name + "," + email + "," + password;
    }

}
