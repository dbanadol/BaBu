package com.example.babu;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.babu.AudioPlayer.mediaPlayer;
import static com.example.babu.FragmentSongs.changeSeekbar;

public class MainActivity extends AppCompatActivity
implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;

    public static Playlist activePlaylistBeforeGPSMode, AllSongs = new Playlist("All Songs");
    public static Playlist CurrentPlaylist = AllSongs;
    public static GoogleApiClient mApiClient;
    public static ImageButton playButton, pauseButton, nextButton, previousButton;
    public static SeekBar seekbar;
    public static String selectedMode = "FreeMode";
    public static AudioPlayer audioPlayer;
    public static DetectedActivity lastActivity;
    public static TextView songName;
    public static ArrayList<Playlist> Playlists;
    public static SharedPreferences sharedPreferences;
    public static TabLayout tabLayout;
    public static boolean isGPSmodeActive = false, isSensorModeActive = false, isGPSworkedBefore = false;

    LocationService myService;
    static boolean status;
    LocationManager locationManager;

    static long startTime, endTime;
    static ProgressDialog locate;
    static int p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains("RealPlaylists")) {
            Playlists = new ArrayList<Playlist>();
            //readSongs(getSDCardPath());
            readSongs(Environment.getExternalStorageDirectory());

            //AllSongs.sortSongsAlphabetically();
            Playlists.add(AllSongs);

            Playlist slowSongs = new Playlist("Slow Tempo Songs");
            Playlist mediumSongs = new Playlist("Medium Tempo Songs");
            Playlist fastSongs = new Playlist("Fast Tempo Songs");
            Playlists.add(slowSongs);
            Playlists.add(mediumSongs);
            Playlists.add(fastSongs);

            SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(Playlists);
            prefsEditor.putString("RealPlaylists", json);
            prefsEditor.apply();
            prefsEditor.commit();
            json = gson.toJson(AllSongs);
            prefsEditor.putString("AllSongs", json);
            prefsEditor.apply();
            prefsEditor.commit();
        }
        else{
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("RealPlaylists", null);
            Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
            Playlists = gson.fromJson(json, type);

            json = sharedPreferences.getString("AllSongs", null);
            AllSongs = gson.fromJson(json, Playlist.class);

        }

        CurrentPlaylist = AllSongs;

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        nextButton = findViewById(R.id.skipNext);
        previousButton = findViewById(R.id.skipPrevious);
        seekbar = findViewById(R.id.seekBar);
        songName = findViewById(R.id.songName);
        songName.setSelected(true);

        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.container);
        setupPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //To start the screen from MODE tab;
        TabLayout.Tab tab = tabLayout.getTabAt(3);
        tab.select();
        tab = tabLayout.getTabAt(2);
        tab.select();
        tab = tabLayout.getTabAt(1);
        tab.select();
        tab = tabLayout.getTabAt(0);
        tab.select();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();

        playButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                audioPlayer.continuePlayingSong();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                audioPlayer.pauseSong();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mediaPlayer == null)    Toast.makeText(getApplicationContext(), "Please Select a Song" , Toast.LENGTH_SHORT).show();
                else    audioPlayer.playNextSong();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mediaPlayer == null)    Toast.makeText(getApplicationContext(), "Please Select a Song" , Toast.LENGTH_SHORT).show();
                else if(AudioPlayer.currentSongIndex < 1)    Toast.makeText(getApplicationContext(), "Already Playing the First Song of the Playlist" , Toast.LENGTH_SHORT).show();
                else    audioPlayer.playPreviousSong();
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekbar,int i ,boolean b){
                if(b){
                    if(mediaPlayer != null){
                        mediaPlayer.seekTo(i);
                        changeSeekbar();
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekbar){            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar){            }
        });

        startModeDecider(findViewById(R.layout.fragment_training));
        FragmentSongs.newAudioPlayer(this);
    }

    @Override
    public void onStop() {
        if(isSensorModeActive){
            mApiClient.disconnect();    //doesn't work for some reason
            stopService(new Intent(getBaseContext(), ActivityRecognizedService.class)); //doesn't work for some reason
            //Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
            //Log.d("ServiceStop","Service Stopped");
        }
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Playlists);
        prefsEditor.putString("RealPlaylists", json);
        prefsEditor.apply();
        prefsEditor.commit();
        json = gson.toJson(AllSongs);
        prefsEditor.putString("AllSongs", json);
        prefsEditor.apply();
        prefsEditor.commit();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(isSensorModeActive){
            Intent intent = new Intent(this, ActivityRecognizedService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 94, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 999, pendingIntent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(isSensorModeActive){
            mApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(isSensorModeActive){
            Toast.makeText(getBaseContext(), getString(R.string.common_google_play_services_unsupported_text), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentMode(), "MODE");
        adapter.addFragment(new FragmentPlaylists(), "PLAYLISTS");
        adapter.addFragment(new FragmentSongs(), "SONGS");
        adapter.addFragment(new FragmentTraining(), "TRAINING");
        viewPager.setAdapter(adapter);
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isSensorModeActive){
                DetectedActivity newActivity = ActivityRecognizedService.mostProbableActivity;
                if(lastActivity == null){lastActivity = newActivity; }
                else{
                    if(lastActivity.getType() != newActivity.getType()){
                        lastActivity = newActivity;
                    }
                }
            }
        }};

    protected void onResume() {
        super.onResume();
        if(isSensorModeActive){
            LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("message"));
        }
    }

    protected void onPause() {
        super.onPause();
        if(isSensorModeActive){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        }
    }

    public static File getSDCardPath() {
        File fileList[] = new File("/storage").listFiles();
        for (File file : fileList) {
            if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead()) {
                return file;
            }
        }
        return Environment.getExternalStorageDirectory();
    }

    public static ArrayList<File> readSongs(File root){
        ArrayList<File> arrayList = new ArrayList<File>();
        File files[] = root.listFiles();

        for(File file : files){
            if(file.isDirectory()){
                arrayList.addAll(readSongs(file));
            }
            else{
                if(file.getName().endsWith(".mp3")){
                    arrayList.add(file);
                    Song song = new Song(file.getName(), "100", file.getAbsolutePath());
                    AllSongs.addSong(song);
                }
            }
        }
        return arrayList;
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void SensorModeThreadStarter(View view) {
        SensorMode runnable = new SensorMode();
        new Thread(runnable).start();
    }

    public void GPSModeThreadStarter(View view) {
        GPSMode runnable = new GPSMode();
        new Thread(runnable).start();
    }

    public void startModeDecider(View view) {
        ModeDecider runnable = new ModeDecider();
        new Thread(runnable).start();
    }

    class ModeDecider implements Runnable {
        @Override
        public void run() {
            while(true){
                if(FragmentTraining.isTrainingStopped){
                    unbindService();
                    FragmentTraining.isTrainingStopped = false;
                }
                if (!selectedMode.equals("FreeMode")) {
                    Handler threadHandler = new Handler(Looper.getMainLooper());
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //do what you do
                            if(selectedMode.equals("OnlyGPS")){
                                TabLayout.Tab tab = tabLayout.getTabAt(3);
                                tab.select();
                                activePlaylistBeforeGPSMode = CurrentPlaylist;
                                startGPSmode();
                                isGPSmodeActive = true;
                                selectedMode = "FreeMode";
                                isGPSworkedBefore = true;
                                GPSModeThreadStarter(findViewById(R.layout.fragment_training));
                            }
                            else if(selectedMode.equals("SensorMode")){
                                TabLayout.Tab tab = tabLayout.getTabAt(2);
                                tab.select();
                                isSensorModeActive = true;
                                selectedMode = "FreeMode";
                                SensorModeThreadStarter(findViewById(R.layout.fragment_training));
                            }
                        }
                    });
                }
                try {
                    Thread.sleep(333);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SensorMode implements Runnable {
        String currentMode = "none";
        @Override
        public void run() {
            while(isSensorModeActive){
                Handler threadHandler = new Handler(Looper.getMainLooper());
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //do what you do
                        if(ActivityRecognizedService.mostProbableActivity!=null){
                            if (ActivityRecognizedService.mostProbableActivity.toString().toLowerCase().contains("on_foot") && !currentMode.equals("on_foot")) {
                                //FragmentSongs.newAudioPlayer(FragmentSongs.activityGot);
                                if(mediaPlayer!=null)   mediaPlayer.release();
                                if(Playlists.get(1).numberOfSongs>0){
                                    currentMode = "on_foot";
                                    audioPlayer.playRandomMediumTempoSong();
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            seekbar.setMax(mediaPlayer.getDuration());
                                            changeSeekbar();
                                        }
                                    });
                                }
                                else    Toast.makeText(getApplicationContext(), "Put some music into 'Medium Tempo Songs' playlist", Toast.LENGTH_SHORT).show();
                            }
                            else if (ActivityRecognizedService.mostProbableActivity.toString().toLowerCase().contains("running") && !currentMode.equals("running")) {
                                //FragmentSongs.newAudioPlayer(FragmentSongs.activityGot);
                                if(mediaPlayer!=null)   mediaPlayer.release();
                                if(Playlists.get(3).numberOfSongs>0){
                                    currentMode = "running";
                                    audioPlayer.playRandomFastSong();
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            seekbar.setMax(mediaPlayer.getDuration());
                                            changeSeekbar();
                                        }
                                    });
                                }
                                else    Toast.makeText(getApplicationContext(), "Put some music into 'Fast Tempo Songs' playlist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        if(keyCode == 126){ //play button recieved
            AudioPlayer.continuePlayingSong();
        }
        else if(keyCode == 127){ //pause button recieved
            AudioPlayer.pauseSong();
        }

        else if(keyCode == 87){ //next button recieved
            AudioPlayer.playNextSong();
        }
        else if(keyCode == 88){ //previous button recieved
            AudioPlayer.playPreviousSong();
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////
    //////////////////////code for gps mode////////////////////////////
    ///////////////////////////////////////////////////////////////////

    public void startGPSmode(){
        checkGps();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
        if (status == false)
            //Here, the Location Service gets bound and the GPS Speedometer gets Active.
            bindService();

        if(!isGPSworkedBefore){
            locate = new ProgressDialog(MainActivity.this);
            locate.setIndeterminate(true);
            locate.setCancelable(false);
            locate.setMessage("Getting Location...");
            locate.show();
        }
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        //Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (status == true)
            unbindService();
    }

    @Override
    public void onBackPressed() {
        if (status == false)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }

    //This method leads user to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use GPS Mode")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                startGPSmode();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    class GPSMode implements Runnable {
        String currentMode = "none";
        boolean onChange= false;
        @Override
        public void run() {
            while(isGPSmodeActive){
                Handler threadHandler = new Handler(Looper.getMainLooper());
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //do what you do
                        if(Double.parseDouble(FragmentTraining.speed.getText().toString()) >= 0 && Double.parseDouble(FragmentTraining.speed.getText().toString()) < 4 && !currentMode.equals("slow")){
                            if (Playlists.get(1).numberOfSongs > 0){
                                if(onChange){
                                    AudioPlayer.playRandomSlowSong();
                                    currentMode = "slow";
                                    onChange = false;
                                }
                                else    onChange = true;
                            }
                            else    Toast.makeText(getApplicationContext(), "Put some music into 'Slow Tempo Songs' playlist", Toast.LENGTH_SHORT).show();
                        }
                        else if(Double.parseDouble(FragmentTraining.speed.getText().toString()) <= 8 && Double.parseDouble(FragmentTraining.speed.getText().toString()) >= 6 && !currentMode.equals("medium")){
                            if (Playlists.get(2).numberOfSongs > 0){
                                if(onChange){
                                    AudioPlayer.playRandomMediumTempoSong();
                                    currentMode = "medium";
                                    onChange = false;
                                }
                                else    onChange = true;
                            }
                            else    Toast.makeText(getApplicationContext(), "Put some music into 'Medium Tempo Songs' playlist", Toast.LENGTH_SHORT).show();

                        }
                        else if(Double.parseDouble(FragmentTraining.speed.getText().toString()) > 9.5 && !currentMode.equals("fast")){
                            if (Playlists.get(2).numberOfSongs > 0){
                                if(onChange){
                                    AudioPlayer.playRandomFastSong();
                                    currentMode = "fast";
                                    onChange = false;
                                }
                                else    onChange = true;
                            }
                            else    Toast.makeText(getApplicationContext(), "Put some music into 'Fast Tempo Songs' playlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}