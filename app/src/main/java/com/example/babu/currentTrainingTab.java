package com.example.babu;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class currentTrainingTab extends Fragment {

    public static TextView dist, time, speed, warning, heartRate, topSpeed;
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

        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.p = 0;
                isTrainingStopped = true;
                MainActivity.isGPSmodeActive = false;
                MainActivity.isGPSalertShowedBefore = false;
                FragmentMode.radioGroup.check(R.id.FreeMode);
                Toast.makeText(getActivity(), "Training Session Ended", Toast.LENGTH_SHORT).show();
                warning.setText("Activate GPS Mode\nto start a training session.");
                endSession.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }
}

