package com.example.babu;

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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import static com.example.babu.AudioPlayer.mediaPlayer;
import static com.example.babu.FragmentSongs.changeSeekbar;
import static com.example.babu.LoginScreen.user;

public class MainActivity extends AppCompatActivity
implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataClient.OnDataChangedListener {

    SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;

    public static Playlist activePlaylistBeforeGPSMode, AllSongs = new Playlist("All Songs");
    public static Playlist CurrentPlaylist = AllSongs;
    public static GoogleApiClient mApiClient;
    public static ImageButton playButton, pauseButton, nextButton, previousButton, repeatButton, shuffleButton;
    public static SeekBar seekbar;
    public static String selectedMode = "FreeMode";
    public static AudioPlayer audioPlayer;
    public static DetectedActivity lastActivity;
    public static TextView songName;
    public static ArrayList<Playlist> Playlists;
    public static SharedPreferences sharedPreferences;
    public static TabLayout tabLayout;
    public static boolean isGPSmodeActive = false, isSensorModeActive = false, isGPSworkedBefore = false, isWatchModeActive = false, isGPSalertShowedBefore = true, isShufflePressed = false, isRepeatPressed = false;

    LocationService myService;
    static boolean status;
    LocationManager locationManager;

    public static long startTime, endTime, startTimeforWatchMode = -1, endTimeforWatchMode;
    static ProgressDialog locate;
    static int p = 0;
    static String datapath = "/data_path";
    static String TAG = "MobileTAG", message = "0", operation = "";
    public static ArrayList<Integer> heartRates;

    public static int MaxHeartRate = 0;
    public Toast watchWarningToast;

    private DatabaseReference mDatabase, mPostReference;
    public String getOperationTrainingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostReference = FirebaseDatabase.getInstance().getReference().child("user-posts").child(LoginScreen.getUid()).child("ourPerfectPostID");
        if (!sharedPreferences.contains("Playlists")) {

            loadFromDatabase();
            Playlists = new ArrayList<>();
            readSongs(getSDCardPath());
            //readSongs(Environment.getExternalStorageDirectory());
            //AllSongs.sortSongsAlphabetically();
            Playlists.add(AllSongs);

            Playlist slowSongs = new Playlist("Slow Tempo Songs");
            Playlist mediumSongs = new Playlist("Medium Tempo Songs");
            Playlist fastSongs = new Playlist("Fast Tempo Songs");
            Playlists.add(slowSongs);
            Playlists.add(mediumSongs);
            Playlists.add(fastSongs);

            saveToSharedPreferences();
        }
        else{
            loadFromSharedPreferences();
        }

        CurrentPlaylist = AllSongs;

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        nextButton = findViewById(R.id.skipNext);
        previousButton = findViewById(R.id.skipPrevious);
        seekbar = findViewById(R.id.seekBar);
        songName = findViewById(R.id.songName);
        repeatButton = findViewById(R.id.repeat);
        shuffleButton = findViewById(R.id.shuffle);
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
        tab = tabLayout.getTabAt(3);
        tab.select();
        tab = tabLayout.getTabAt(0);
        tab.select();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();

        shuffleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isGPSmodeActive || isSensorModeActive || isWatchModeActive){
                    Toast.makeText(getApplicationContext(), "Shuffle button can only be depressed during Free Mode" , Toast.LENGTH_SHORT).show();
                }
                else{
                    if(isShufflePressed){
                        shuffleButton.getBackground().clearColorFilter();
                        isShufflePressed = false;
                    }
                    else{
                        shuffleButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC);
                        isShufflePressed = true;
                        if(isRepeatPressed){
                            repeatButton.getBackground().clearColorFilter();
                            isRepeatPressed = false;
                        }
                    }
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isGPSmodeActive || isSensorModeActive || isWatchModeActive){
                    Toast.makeText(getApplicationContext(), "Repeat button can only be pressed during Free Mode" , Toast.LENGTH_SHORT).show();
                }
                else{
                    if(isRepeatPressed){
                        repeatButton.getBackground().clearColorFilter();
                        isRepeatPressed = false;
                    }
                    else{
                        repeatButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC);
                        isRepeatPressed = true;
                        if(isShufflePressed){
                            shuffleButton.getBackground().clearColorFilter();
                            isShufflePressed = false;
                        }
                    }
                }
            }
        });

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

        startModeDecider(findViewById(R.layout.current_training));
        FragmentSongs.newAudioPlayer(this);
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        if(isSensorModeActive){
            mApiClient.disconnect();    //doesn't work for some reason
            stopService(new Intent(getBaseContext(), ActivityRecognizedService.class)); //doesn't work for some reason
            //Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
            //Log.d("ServiceStop","Service Stopped");
        }
        saveToSharedPreferences();
        super.onStop();
    }

    public void saveToSharedPreferences(){
        user.TrainingList = FragmentTraining.TrainingList;
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("User", json);
        prefsEditor.apply();
        prefsEditor.commit();

        json = gson.toJson(Playlists);
        prefsEditor.putString("Playlists", json);
        prefsEditor.apply();
        prefsEditor.commit();
        json = gson.toJson(AllSongs);
        prefsEditor.putString("AllSongs", json);
        prefsEditor.apply();
        prefsEditor.commit();
        /*
        json = gson.toJson(FragmentTraining.TrainingList);
        prefsEditor.putString("TrainingList", json);
        prefsEditor.apply();
        prefsEditor.commit();
        */
        saveToDatabase();
    }

    public void loadFromSharedPreferences(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("User", null);
        user = gson.fromJson(json, User.class);
        FragmentTraining.TrainingList = user.TrainingList;


        json = sharedPreferences.getString("Playlists", null);
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        Playlists = gson.fromJson(json, type);

        json = sharedPreferences.getString("AllSongs", null);
        AllSongs = gson.fromJson(json, Playlist.class);

        /*
        json = sharedPreferences.getString("TrainingList", null);
        type = new TypeToken<ArrayList<TrainingSession>>() {}.getType();
        FragmentTraining.TrainingList = gson.fromJson(json, type);
        */
    }

    public void saveToDatabase(){

        final String userId = LoginScreen.getUid();
        Gson gson = new Gson();
        String trainings = gson.toJson(FragmentTraining.TrainingList);
        if(!trainings.equalsIgnoreCase("[]") && !trainings.isEmpty() && trainings != null) writeNewPost(userId, user.getEmail(), trainings);

    }

    public void loadFromDatabase(){

        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                PostToDatabase post = dataSnapshot.getValue(PostToDatabase.class);
                if(post != null){
                    getOperationTrainingList = post.trainingList;
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<TrainingSession>>() {}.getType();
                    if(getOperationTrainingList != null)    FragmentTraining.TrainingList = gson.fromJson(getOperationTrainingList, type);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        //mPostReference.addValueEventListener(postListener);
        //mPostReference.removeEventListener(postListener);
        //Log.d("hhhhhhhhhh",getOperationTrainingList);
    }

    private void writeNewPost(String userId, String username, String trainingList) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        //String key = mDatabase.child("posts").push().getKey();
        String key = "ourPerfectPostID";
        PostToDatabase post = new PostToDatabase(userId, username, trainingList);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
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

        else if(isWatchModeActive){
            Wearable.getDataClient(this).addListener(this);
        }
    }

    protected void onPause() {
        super.onPause();
        if(isSensorModeActive){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        }
        else if(isWatchModeActive){
            Wearable.getDataClient(this).removeListener(this);
        }
    }

    private void sendData(String message) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(datapath);
        dataMap.getDataMap().putString("message", message);
        PutDataRequest request = dataMap.asPutDataRequest();
        request.setUrgent();

        Task<DataItem> dataItemTask = Wearable.getDataClient(this).putDataItem(request);
        dataItemTask
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d(TAG, "Sending message was successful: " + dataItem);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Sending message failed: " + e);
                    }
                });
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        //Log.d(TAG, "onDataChanged: " + dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (datapath.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    message = dataMapItem.getDataMap().getString("message");
                    Log.v(TAG, "Wear activity received message: " + message);
                    // Display message in UI
                    //logthis(message);
                } else {
                    Log.e(TAG, "Unrecognized path: " + path);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.v(TAG, "Data deleted : " + event.getDataItem().toString());
            } else {
                Log.e(TAG, "Unknown data event Type = " + event.getType());
            }
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
                    Song song = new Song(file.getName(), file.getAbsolutePath());
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

    public void WatchModeThreadStarter(View view) {
        WatchMode runnable = new WatchMode();
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
                if(currentTrainingTab.isTrainingStopped){
                    unbindService();
                    currentTrainingTab.isTrainingStopped = false;
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
                                if (isRepeatPressed)    repeatButton.callOnClick();
                                if (!isShufflePressed)  shuffleButton.callOnClick();
                                activePlaylistBeforeGPSMode = CurrentPlaylist;
                                startGPSmode();
                                isGPSmodeActive = true;
                                selectedMode = "FreeMode";
                                isGPSworkedBefore = true;
                                GPSModeThreadStarter(findViewById(R.layout.current_training));
                            }
                            else if(selectedMode.equals("OnlySmartWatch")){
                                if(heartRates != null)  heartRates.clear();
                                heartRates = new ArrayList<Integer>();
                                heartRates.clear();
                                operation.concat("start");
                                sendData(operation);
                                TabLayout.Tab tab = tabLayout.getTabAt(3);
                                tab.select();
                                if (isRepeatPressed)    repeatButton.callOnClick();
                                if (!isShufflePressed)  shuffleButton.callOnClick();
                                selectedMode = "FreeMode";
                                isWatchModeActive = true;
                                onResume();
                                WatchModeThreadStarter(findViewById(R.layout.current_training));

                                if(message.equalsIgnoreCase("0")){
                                    Toast.makeText(getApplicationContext(), "Connecting to Smart Watch...\nMake sure you have started BaBu Wear Application on your Smart Watch", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(selectedMode.equals("SensorMode")){
                                TabLayout.Tab tab = tabLayout.getTabAt(2);
                                tab.select();
                                if (isRepeatPressed)    repeatButton.callOnClick();
                                if (!isShufflePressed)  shuffleButton.callOnClick();
                                isSensorModeActive = true;
                                selectedMode = "FreeMode";
                                SensorModeThreadStarter(findViewById(R.layout.current_training));
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

    class WatchMode implements Runnable {
        String currentMode = "none";
        int localMinHeartRate = 99999;
        int localMaxHeartRate = -1;
        int lastChangeIndex = 0;
        @Override
        public void run() {
            while(isWatchModeActive){
                Handler threadHandler = new Handler(Looper.getMainLooper());
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //do what you do
                        if(!message.equalsIgnoreCase("0")){

                            if(message.equalsIgnoreCase("-1")){
                                currentTrainingTab.endSession.callOnClick();
                            }

                            if(watchWarningToast != null){
                                if(watchWarningToast.getView().isShown()) {
                                    watchWarningToast.cancel();
                                    startTimeforWatchMode = System.currentTimeMillis();
                                }
                            }
                            if(startTimeforWatchMode == -1)   startTimeforWatchMode = System.currentTimeMillis();
                            heartRates.add(Integer.parseInt(message));
                            currentTrainingTab.heartRate.setText(message);
                            Log.d("Heart rate :", message);

                            if(MaxHeartRate < heartRates.get(heartRates.size() - 1))    MaxHeartRate = heartRates.get(heartRates.size() - 1);
                            currentTrainingTab.maxheartRate.setText(String.valueOf(MainActivity.MaxHeartRate));

                            MainActivity.endTimeforWatchMode = System.currentTimeMillis();
                            long time = endTimeforWatchMode - startTimeforWatchMode;
                            time = TimeUnit.MILLISECONDS.toMinutes(time);
                            currentTrainingTab.time.setText(""+time);
                            currentTrainingTab.view.invalidate();

                            if(localMinHeartRate != 99999 && localMaxHeartRate != -1 && heartRates.size() > lastChangeIndex + 70) {

                                if (currentMode.equalsIgnoreCase("slow")) {
                                    if (heartRates.get(heartRates.size() - 1) > (localMinHeartRate + 14)) {
                                        AudioPlayer.playRandomFastSong();
                                        currentMode = "fast";
                                        Log.d("Now playing ", "FAST song");
                                        lastChangeIndex = heartRates.size() - 1;
                                    } else if (heartRates.get(heartRates.size() - 1) > (localMinHeartRate + 10)) {
                                        AudioPlayer.playRandomMediumTempoSong();
                                        currentMode = "medium";
                                        Log.d("Now playing ", "MEDIUM TEMPO song");
                                        lastChangeIndex = heartRates.size() - 1;
                                    }
                                } else if (currentMode.equalsIgnoreCase("medium")) {
                                    if (heartRates.get(heartRates.size() - 1) > (localMinHeartRate + 7)) {
                                        AudioPlayer.playRandomFastSong();
                                        currentMode = "fast";
                                        Log.d("Now playing ", "FAST song");
                                        lastChangeIndex = heartRates.size() - 1;
                                    } else if (heartRates.get(heartRates.size() - 1) < (localMaxHeartRate - 4)) {
                                        AudioPlayer.playRandomSlowSong();
                                        currentMode = "slow";
                                        Log.d("Now playing ", "SLOW song");
                                        lastChangeIndex = heartRates.size() - 1;
                                    }
                                } else if (currentMode.equalsIgnoreCase("fast")) {
                                    if (heartRates.get(heartRates.size() - 1) < (localMaxHeartRate - 7)) {
                                        AudioPlayer.playRandomSlowSong();
                                        currentMode = "slow";
                                        Log.d("Now playing ", "SLOW song");
                                        lastChangeIndex = heartRates.size() - 1;
                                    }else if (heartRates.get(heartRates.size() - 1) < (localMaxHeartRate - 4)) {
                                        AudioPlayer.playRandomMediumTempoSong();
                                        currentMode = "medium";
                                        Log.d("Now playing ", "MEDIUM TEMPO song");
                                        lastChangeIndex = heartRates.size() - 1;
                                    }
                                }
                            }
                                /*
                                if(heartRates.get(heartRates.size() - 1) > (localMinHeartRate + 9)){
                                    if(currentMode.equalsIgnoreCase("slow")){
                                        AudioPlayer.playRandomMediumTempoSong();
                                        currentMode = "medium";
                                        lastChangeIndex = heartRates.size() - 1;
                                    }
                                    else if(currentMode.equalsIgnoreCase("medium")){
                                        AudioPlayer.playRandomFastSong();
                                        currentMode = "fast";
                                        lastChangeIndex = heartRates.size() - 1;
                                    }
                                }
                                else if(heartRates.get(heartRates.size() - 1) < (localMaxHeartRate - 4)){
                                    if(currentMode.equalsIgnoreCase("fast")){
                                        AudioPlayer.playRandomMediumTempoSong();
                                        currentMode = "medium";
                                        lastChangeIndex = heartRates.size() - 1;
                                    }
                                    else if(currentMode.equalsIgnoreCase("medium")){
                                        AudioPlayer.playRandomSlowSong();
                                        currentMode = "slow";
                                        lastChangeIndex = heartRates.size() - 1;
                                    }
                                }
                            }*/

                            if(heartRates.size()<60){
                                /*if(currentMode.equalsIgnoreCase("none")){
                                    if(heartRates.get(heartRates.size() - 1) > 119){
                                        audioPlayer.playRandomFastSong();
                                        currentMode="fast";
                                    }
                                    else if(heartRates.get(heartRates.size() - 1) > 99){
                                        audioPlayer.playRandomMediumTempoSong();
                                        currentMode="medium";
                                    }
                                    else{
                                        audioPlayer.playRandomSlowSong();
                                        currentMode="slow";
                                    }
                                }*/
                                if(currentMode.equalsIgnoreCase("none")){
                                    audioPlayer.playRandomSlowSong();
                                    currentMode="slow";
                                    Log.d("Now playing ", "SLOW song");
                                }

                            }
                            else{
                                localMinHeartRate = 99999;
                                localMaxHeartRate = -1;
                                for (int i = heartRates.size() - 1; i > heartRates.size() - 61; i--){
                                    if(heartRates.get(i) < localMinHeartRate)   localMinHeartRate = heartRates.get(i);
                                    if(heartRates.get(i) > localMaxHeartRate)   localMaxHeartRate = heartRates.get(i);
                                }
                                Log.d("ldfkjgldkn",String.valueOf(localMinHeartRate));
                                Log.d("ldfkjgldkn",String.valueOf(localMaxHeartRate));
                            }
                        }
                        else{
                            if(watchWarningToast == null){
                                watchWarningToast = Toast.makeText(getApplicationContext(), "Make sure you have started BaBu Wear Application on your Smart Watch", Toast.LENGTH_LONG);
                                watchWarningToast.show();
                            }
                            else if(!watchWarningToast.getView().isShown()){
                                watchWarningToast.show();
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
            heartRates = null;
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
        Log.d("vvvvv: " + keyCode, event.toString());
        if(keyCode != 25 && keyCode != 24){ // if volume up and down buttons are not pressed from phone
            super.onKeyDown(keyCode, event);

            if(keyCode == 126){ //play button received
                AudioPlayer.continuePlayingSong();
            }
            else if(keyCode == 127){ //pause button received
                AudioPlayer.pauseSong();
            }
            else if(keyCode == 87){ //next button received
                AudioPlayer.playNextSong();
            }
            else if(keyCode == 88){ //previous button received
                AudioPlayer.playPreviousSong();
            }
            return true;
        }
        else return false;
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
        isGPSalertShowedBefore = true;
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
                        if(Double.parseDouble(currentTrainingTab.speed.getText().toString()) >= 0 && Double.parseDouble(currentTrainingTab.speed.getText().toString()) < 4 && !currentMode.equals("slow")){
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
                        else if(Double.parseDouble(currentTrainingTab.speed.getText().toString()) <= 8 && Double.parseDouble(currentTrainingTab.speed.getText().toString()) >= 6 && !currentMode.equals("medium")){
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
                        else if(Double.parseDouble(currentTrainingTab.speed.getText().toString()) > 9.5 && !currentMode.equals("fast")){
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