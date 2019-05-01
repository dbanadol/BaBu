package com.example.babu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;


public class FragmentMode extends Fragment {

    Button confirmButton;
    public static RadioGroup radioGroup;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_mode,container,false);
        confirmButton = view.findViewById(R.id.confirmButton);
        radioGroup = view.findViewById(R.id.radioGroup);
        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(radioGroup.getCheckedRadioButtonId() == R.id.OnlyGPS){
                    if(MainActivity.Playlists.get(1).numberOfSongs > 0 && MainActivity.Playlists.get(2).numberOfSongs > 0 && MainActivity.Playlists.get(3).numberOfSongs > 0){
                        MainActivity.selectedMode = "OnlyGPS";
                        Toast.makeText(getActivity(), "GPS Mode Selected", Toast.LENGTH_SHORT).show();
                        MainActivity.isSensorModeActive = false;
                    }
                    else    Toast.makeText(getActivity(), "Please put some music into Slow, Medium and Fast Tempo Playlists", Toast.LENGTH_SHORT).show();
                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.OnlySmartWatch){
                    MainActivity.selectedMode = "OnlySmartWatch";
                    Toast.makeText(getActivity(), "Smart Watch Mode Selected", Toast.LENGTH_SHORT).show();
                    MainActivity.isGPSmodeActive = false;
                    MainActivity.isSensorModeActive = false;
                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.SensorMode){
                    MainActivity.selectedMode = "SensorMode";
                    Toast.makeText(getActivity(), "Sensor Mode Selected", Toast.LENGTH_SHORT).show();
                    MainActivity.isGPSmodeActive = false;
                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.FreeMode){
                    MainActivity.selectedMode = "FreeMode";
                    Toast.makeText(getActivity(), "Free Mode Selected", Toast.LENGTH_SHORT).show();
                    MainActivity.isGPSmodeActive = false;
                    MainActivity.isSensorModeActive = false;
                }
            }
        });

        return view;
    }

}