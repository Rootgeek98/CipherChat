package com.zephyr.cipherchat.model;

import java.io.Serializable;

public class Message implements Serializable {
    String id, message, sentAt;
    User user;

    public Message() {
    }

    public Message(String id, String message, String sentAt, User user) {
        this.id = id;
        this.message = message;
        this.sentAt = sentAt;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String createdAt) {
        this.sentAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
