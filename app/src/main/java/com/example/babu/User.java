package com.example.babu;

import java.util.ArrayList;

public class User {

    String username;
    String password;
    ArrayList<TrainingSession> TrainingList;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<TrainingSession> getTrainingList() {
        return TrainingList;
    }

    public void setTrainingList(ArrayList<TrainingSession> trainingList) {
        TrainingList = trainingList;
    }
}