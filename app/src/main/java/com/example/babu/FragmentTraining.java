package com.example.babu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentTraining extends Fragment {

    public static View view;
    SectionsPageAdapter trainingPageAdapter;
    private static TabLayout tabLayout_;
    private ViewPager viewPager;
    public static ArrayList<TrainingSession> TrainingList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_training, container, false);

        trainingPageAdapter = new SectionsPageAdapter(getFragmentManager());
        viewPager = view.findViewById(R.id.container__);
        setupPager(viewPager);

        tabLayout_ = view.findViewById(R.id.training_tabs);
        tabLayout_.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout_.getTabAt(1);
        tab.select();

        return view;
    }

    private void setupPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new pastTrainingTab(), "PAST SESSIONS");
        adapter.addFragment(new currentTrainingTab(), "CURRENT SESSION");
        viewPager.setAdapter(adapter);
    }
}