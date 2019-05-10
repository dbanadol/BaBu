package com.example.babu;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrainingSession {
    double topSpeed;
    double averageSpeed;
    double totalDistance;
    double caloriesBurnt;
    long timeinMinutes;
    String date;


    public TrainingSession(double topSpeed, double averageSpeed, double totalDistance, double caloriesBurnt, long timeinMinutes){
        this.averageSpeed = averageSpeed;
        this.caloriesBurnt = caloriesBurnt;
        this.timeinMinutes = timeinMinutes;
        this.totalDistance = totalDistance;
        this.topSpeed = topSpeed;
        this.date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

}
