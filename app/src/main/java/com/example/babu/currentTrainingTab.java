package com.example.babu;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.babu.pastTrainingTab.spin;

public class currentTrainingTab extends Fragment {

    public static TextView dist, time, speed, warning, heartRate, topSpeed, maxheartRate, c1, c2, c3, c4, c5, c6;
    public static Button endSession;
    public static View view;

    public static boolean isTrainingStopped = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.current_training, container, false);

        dist = view.findViewById(R.id.distancetext);
        time = view.findViewById(R.id.timetext);
        speed =  view.findViewById(R.id.speedtext);
        topSpeed =  view.findViewById(R.id.topSpeed);
        heartRate = view.findViewById(R.id.heartRate);
        warning = view.findViewById(R.id.warning);
        endSession = view.findViewById(R.id.endSession);
        maxheartRate = view.findViewById(R.id.maxheartRate);
        c1 = view.findViewById(R.id.c1);
        c2 = view.findViewById(R.id.c2);
        c3 = view.findViewById(R.id.c3);
        c4 = view.findViewById(R.id.c4);
        c5 = view.findViewById(R.id.c5);
        c6 = view.findViewById(R.id.c6);

        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainingSession trainingSession = new TrainingSession(topSpeed.getText().toString(), dist.getText().toString(), time.getText().toString(), maxheartRate.getText().toString());
                FragmentTraining.TrainingList.add(trainingSession);
                if(MainActivity.isWatchModeActive)  AudioPlayer.pauseSong();
                MainActivity.p = 0;
                isTrainingStopped = true;
                MainActivity.isGPSmodeActive = false;
                MainActivity.isWatchModeActive = false;
                MainActivity.isGPSalertShowedBefore = false;
                FragmentMode.mode_list.setItemChecked(3,true);
                Toast.makeText(getActivity(), "Training Session Ended", Toast.LENGTH_SHORT).show();
                warning.setText("Activate GPS Mode\nto start a training session.");
                endSession.setVisibility(View.INVISIBLE);
                c1.setVisibility(View.INVISIBLE);
                c2.setVisibility(View.INVISIBLE);
                c3.setVisibility(View.INVISIBLE);
                c4.setVisibility(View.INVISIBLE);
                c5.setVisibility(View.INVISIBLE);
                c6.setVisibility(View.INVISIBLE);
                dist.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                speed.setVisibility(View.INVISIBLE);
                maxheartRate.setVisibility(View.INVISIBLE);
                heartRate.setVisibility(View.INVISIBLE);
                topSpeed.setVisibility(View.INVISIBLE);
                maxheartRate.setText("0");
                heartRate.setText("0");
                time.setText("0");
                MainActivity.startTimeforWatchMode=-1;
                MainActivity.MaxHeartRate=0;

                ArrayList<String> trainingDates = new ArrayList<>();

                for(int i = 0; i < FragmentTraining.TrainingList.size() ; i++){
                    trainingDates.add(FragmentTraining.TrainingList.get(i).date);
                }

                ArrayAdapter trainings = new ArrayAdapter(getActivity() ,android.R.layout.simple_spinner_item, trainingDates);
                trainings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setAdapter(trainings);
                pastTrainingTab.trainings = trainings;
            }
        });

        if(MainActivity.isGPSmodeActive){
            heartRate.setText("N/A");
            maxheartRate.setText("N/A");
            speed.setText("0");
            topSpeed.setText("0");
            dist.setText("0");
        }
        else if(MainActivity.isWatchModeActive){
            maxheartRate.setText("0");
            heartRate.setText("0");
            speed.setText("N/A");
            topSpeed.setText("N/A");
            dist.setText("N/A");
        }

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(MainActivity.isGPSmodeActive){
            heartRate.setText("N/A");
            speed.setText("0");
            topSpeed.setText("0");
            dist.setText("0");
            if(currentTrainingTab.endSession.getVisibility() == View.INVISIBLE){
                currentTrainingTab.warning.setText("Session Started");
                endSession.setVisibility(View.VISIBLE);
                c1.setVisibility(View.VISIBLE);
                c2.setVisibility(View.VISIBLE);
                c3.setVisibility(View.VISIBLE);
                c4.setVisibility(View.VISIBLE);
                c5.setVisibility(View.VISIBLE);
                c6.setVisibility(View.VISIBLE);
                dist.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                speed.setVisibility(View.VISIBLE);
                maxheartRate.setVisibility(View.VISIBLE);
                heartRate.setVisibility(View.VISIBLE);
                topSpeed.setVisibility(View.VISIBLE);
            }
        }
        else if(MainActivity.isWatchModeActive){
            heartRate.setText("0");
            speed.setText("N/A");
            topSpeed.setText("N/A");
            dist.setText("N/A");
            if(currentTrainingTab.endSession.getVisibility() == View.INVISIBLE){
                currentTrainingTab.warning.setText("Session Started");
                endSession.setVisibility(View.VISIBLE);
                c1.setVisibility(View.VISIBLE);
                c2.setVisibility(View.VISIBLE);
                c3.setVisibility(View.VISIBLE);
                c4.setVisibility(View.VISIBLE);
                c5.setVisibility(View.VISIBLE);
                c6.setVisibility(View.VISIBLE);
                dist.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                speed.setVisibility(View.VISIBLE);
                maxheartRate.setVisibility(View.VISIBLE);
                heartRate.setVisibility(View.VISIBLE);
                topSpeed.setVisibility(View.VISIBLE);
            }
        }
    }

}

