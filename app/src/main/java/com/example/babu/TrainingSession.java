package com.example.babu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrainingSession {
    String topSpeed;
    String averageSpeed;
    String totalDistance;
    String timeinMinutes;
    String averageHeartRate;
    String maxHeartRate;
    String date;
    int sum = 0;

    public TrainingSession(String topSpeed, String totalDistance, String timeinMinutes, String maxHeartRate){

        this.topSpeed = topSpeed;
        this.totalDistance = totalDistance;
        this.timeinMinutes = timeinMinutes;
        this.maxHeartRate = maxHeartRate;

        if(MainActivity.heartRates != null){
            for(int i = 0; i < MainActivity.heartRates.size(); i++){
                sum += MainActivity.heartRates.get(i);
            }
            this.averageHeartRate = String.valueOf(sum / MainActivity.heartRates.size());
        }
        else{
            this.averageHeartRate = "N/A";
        }

        if(!totalDistance.equalsIgnoreCase("N/A") && !timeinMinutes.equalsIgnoreCase("N/A")){
            if(totalDistance.equalsIgnoreCase("0")){
                this.averageSpeed = "0";
            }
            else{
                if(Integer.parseInt(timeinMinutes) >= 1){
                    this.averageSpeed = String.valueOf(Double.parseDouble(totalDistance) / (Double.parseDouble(timeinMinutes) / 60));
                }
                else {
                    this.averageSpeed = "0";
                }
            }
        }
        else{
            this.averageSpeed = "N/A";
        }

        this.date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }
}
