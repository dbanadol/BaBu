package com.example.babu;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class pastTrainingTab extends Fragment implements AdapterView.OnItemSelectedListener {

    public static View view;
    public static Spinner spin;
    public static ArrayAdapter trainings;
    TextView date, time, distance, top_speed, avg_speed, max_hr, avg_hr, p1, p2, p3, p4, p5, p6, p7, warning2;

    //public static boolean isTrainingStopped = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.past_training, container, false);

        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time_);
        distance = view.findViewById(R.id.distance_);
        top_speed = view.findViewById(R.id.top_speed_);
        avg_speed = view.findViewById(R.id.average_speed_);
        max_hr = view.findViewById(R.id.max_hr_);
        avg_hr = view.findViewById(R.id.avg_hr_);
        warning2 = view.findViewById(R.id.warning2);
        p1 = view.findViewById(R.id.past1);
        p2 = view.findViewById(R.id.past2);
        p3 = view.findViewById(R.id.past3);
        p4 = view.findViewById(R.id.past4);
        p5 = view.findViewById(R.id.past5);
        p6 = view.findViewById(R.id.past6);
        p7 = view.findViewById(R.id.past7);
        p1.setVisibility(View.INVISIBLE);
        p2.setVisibility(View.INVISIBLE);
        p3.setVisibility(View.INVISIBLE);
        p4.setVisibility(View.INVISIBLE);
        p5.setVisibility(View.INVISIBLE);
        p6.setVisibility(View.INVISIBLE);
        p7.setVisibility(View.INVISIBLE);
        avg_hr.setVisibility(View.INVISIBLE);
        max_hr.setVisibility(View.INVISIBLE);
        avg_speed.setVisibility(View.INVISIBLE);
        top_speed.setVisibility(View.INVISIBLE);
        distance.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        date.setVisibility(View.INVISIBLE);

        spin = view.findViewById(R.id.spinner);

        ArrayList<String> trainingDates = new ArrayList<>();

        for(int i = 0; i < FragmentTraining.TrainingList.size() ; i++){
            trainingDates.add(FragmentTraining.TrainingList.get(i).date);
        }

        trainings = new ArrayAdapter(getActivity() ,android.R.layout.simple_spinner_item, trainingDates);
        trainings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(trainings);
        spin.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(FragmentTraining.TrainingList.get(position) != null){

            TrainingSession trainingSession = FragmentTraining.TrainingList.get(position);
            warning2.setVisibility(View.INVISIBLE);
            p1.setVisibility(View.VISIBLE);
            p2.setVisibility(View.VISIBLE);
            p3.setVisibility(View.VISIBLE);
            p4.setVisibility(View.VISIBLE);
            p5.setVisibility(View.VISIBLE);
            p6.setVisibility(View.VISIBLE);
            p7.setVisibility(View.VISIBLE);
            date.setText(trainingSession.date);
            date.setVisibility(View.VISIBLE);
            time.setText(trainingSession.timeinMinutes);
            time.setVisibility(View.VISIBLE);
            distance.setText(trainingSession.totalDistance);
            distance.setVisibility(View.VISIBLE);
            top_speed.setText(trainingSession.topSpeed);
            top_speed.setVisibility(View.VISIBLE);
            avg_speed.setText(trainingSession.averageSpeed);
            avg_speed.setVisibility(View.VISIBLE);
            max_hr.setText(trainingSession.maxHeartRate);
            max_hr.setVisibility(View.VISIBLE);
            avg_hr.setText(trainingSession.averageHeartRate);
            avg_hr.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

