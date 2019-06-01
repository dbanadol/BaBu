package com.example.babu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;


public class FragmentMode extends Fragment {

    public static ListView mode_list;
    public static ArrayAdapter<String> ModeListAdapter;
    public static View view;
    public static final String[] Modes = {"GPS Mode", "Smart Watch Mode", "Sensor Mode (No Internet Required)", "Free Mode (Manual Song Selection)"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mode,container,false);
        mode_list = view.findViewById(R.id.mode_list);
        mode_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ModeListAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_single_choice, Modes
        );

        mode_list.setAdapter(ModeListAdapter);
        if(!MainActivity.isWatchModeActive && !MainActivity.isGPSmodeActive && !MainActivity.isSensorModeActive)    mode_list.setItemChecked(3,true);

        mode_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (mode_list.isItemChecked(0)) {
                    if (MainActivity.Playlists.get(1).numberOfSongs > 0 && MainActivity.Playlists.get(2).numberOfSongs > 0 && MainActivity.Playlists.get(3).numberOfSongs > 0) {

                        if (currentTrainingTab.heartRate != null) {
                            currentTrainingTab.heartRate.setText("N/A");
                            currentTrainingTab.maxheartRate.setText("N/A");
                            currentTrainingTab.speed.setText("0");
                            currentTrainingTab.topSpeed.setText("0");
                            currentTrainingTab.dist.setText("0");
                        }
                        MainActivity.selectedMode = "OnlyGPS";
                        Toast.makeText(getActivity(), "GPS Mode Selected", Toast.LENGTH_SHORT).show();
                        MainActivity.isSensorModeActive = false;
                        MainActivity.isWatchModeActive = false;
                    }
                    else{
                        Toast.makeText(getActivity(), "Please put some music into Slow, Medium and Fast Tempo Playlists", Toast.LENGTH_SHORT).show();
                        mode_list.setItemChecked(3, true);
                    }
                }
                else if (mode_list.isItemChecked(1)) {

                    if (MainActivity.Playlists.get(1).numberOfSongs > 0 && MainActivity.Playlists.get(2).numberOfSongs > 0 && MainActivity.Playlists.get(3).numberOfSongs > 0) {
                        if (currentTrainingTab.heartRate != null) {
                            currentTrainingTab.heartRate.setText("0");
                            currentTrainingTab.speed.setText("N/A");
                            currentTrainingTab.topSpeed.setText("N/A");
                            currentTrainingTab.dist.setText("N/A");
                        }
                        MainActivity.selectedMode = "OnlySmartWatch";
                        Toast.makeText(getActivity(), "Smart Watch Mode Selected", Toast.LENGTH_SHORT).show();
                        MainActivity.isGPSmodeActive = false;
                        MainActivity.isSensorModeActive = false;
                    } else
                        Toast.makeText(getActivity(), "Please put some music into Slow, Medium and Fast Tempo Playlists", Toast.LENGTH_SHORT).show();
                        mode_list.setItemChecked(3, true);
                    }
                else if (mode_list.isItemChecked(2)) {
                    if (MainActivity.Playlists.get(1).numberOfSongs > 0 && MainActivity.Playlists.get(3).numberOfSongs > 0) {
                        MainActivity.selectedMode = "SensorMode";
                        Toast.makeText(getActivity(), "Sensor Mode Selected", Toast.LENGTH_SHORT).show();
                        MainActivity.isGPSmodeActive = false;
                        MainActivity.isWatchModeActive = false;
                    } else {
                        Toast.makeText(getActivity(), "Please put some music into Slow and Fast Tempo Playlists", Toast.LENGTH_SHORT).show();
                        mode_list.setItemChecked(3, true);
                    }
                }
                else if (mode_list.isItemChecked(3)) {
                    MainActivity.selectedMode = "FreeMode";
                    Toast.makeText(getActivity(), "Free Mode Selected", Toast.LENGTH_SHORT).show();
                    MainActivity.isGPSmodeActive = false;
                    MainActivity.isSensorModeActive = false;
                    MainActivity.isWatchModeActive = false;
                }
            }

        });

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) { //when this fragment is selected by user
        super.onViewStateRestored(savedInstanceState);

        if(MainActivity.isWatchModeActive) mode_list.setItemChecked(1,true);
        if(!MainActivity.isWatchModeActive && !MainActivity.isGPSmodeActive && !MainActivity.isSensorModeActive) mode_list.setItemChecked(3,true);
    }
}