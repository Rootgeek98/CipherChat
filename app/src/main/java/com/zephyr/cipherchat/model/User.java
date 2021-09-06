package com.zephyr.cipherchat.model;

import java.io.Serializable;

public class User implements Serializable {
    String phone_number, firstname, lastname, username;

    public User() {
    }

    public User(String phone_number, String firstname, String lastname, String username) {
        this.phone_number = phone_number;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
    }

    public User(String phone_number, String userName, Object o) {
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
