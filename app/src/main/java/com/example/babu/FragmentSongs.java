package com.example.babu;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import static com.example.babu.AudioPlayer.mediaPlayer;
import static com.example.babu.MainActivity.CurrentPlaylist;
import static com.example.babu.MainActivity.seekbar;

public class FragmentSongs extends Fragment{

    public static ListView songListView;
    public static View view;
    public static AudioPlayer audioPlayer;
    public static Runnable runnable;
    public static Handler handler;
    public static Activity activityGot;
    public static TextView noSongs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_songs,container, false);
        songListView = view.findViewById(R.id.songListView);
        noSongs = view.findViewById(R.id.noSongs);
        handler = new Handler();
        activityGot = getActivity();

        //readSongs(getSDCardPath());
        //addAll(readSongs(Environment.getExternalStorageDirectory()));

        //AllSongs.sortSongsAlphabetically();

        ArrayAdapter<String> songListAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, MainActivity.CurrentPlaylist.songNames
        );

        songListView.setAdapter(songListAdapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapterView, View view, int position, long l){
                audioPlayer.playSong(CurrentPlaylist.songs.get(position), Uri.parse(CurrentPlaylist.songs.get(position).getPath()));
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer){
                        seekbar.setMax(mediaPlayer.getDuration());
                        changeSeekbar();
                    }
                });

            }
        });

        return view;
    }

    public static void changeSeekbar(){
        if(mediaPlayer!=null)   {
            seekbar.setProgress(mediaPlayer.getCurrentPosition());

            if(mediaPlayer.isPlaying()){
                runnable = new Runnable(){
                    @Override
                    public void run(){
                        changeSeekbar();
                    }
                };
                handler.postDelayed(runnable, 299);
                if((mediaPlayer.getDuration() - 300) <= mediaPlayer.getCurrentPosition()){
                    AudioPlayer.playNextSong();
                }
            }
        }
    }

    public static void newAudioPlayer(Activity activity) {
        audioPlayer = new AudioPlayer(activity);
    }
}
