package com.example.babu;

import java.util.ArrayList;

public class User {

    String email;
    String password;
    ArrayList<TrainingSession> TrainingList;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.TrainingList = new ArrayList<>();
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

    public ArrayList<TrainingSession> getTrainingList() {
        return TrainingList;
    }

    public void setTrainingList(ArrayList<TrainingSession> trainingList) {
        TrainingList = trainingList;
    }
}