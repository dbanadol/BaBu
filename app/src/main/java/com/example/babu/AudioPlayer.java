package com.example.babu;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import java.util.Random;
import static com.example.babu.MainActivity.CurrentPlaylist;
import static com.example.babu.MainActivity.isRepeatPressed;
import static com.example.babu.MainActivity.isShufflePressed;
import static com.example.babu.MainActivity.pauseButton;
import static com.example.babu.MainActivity.playButton;
import static com.example.babu.MainActivity.seekbar;
import static com.example.babu.MainActivity.songName;

public class AudioPlayer {

    static MediaPlayer mediaPlayer;
    static Context context;
    public static int currentSongIndex;
    static Song currentSong;

    AudioPlayer(Context context){
        this.context = context;
    }

    public static void playSong(Song song, Uri uri) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();
        seekbar.setMax(mediaPlayer.getDuration());
        FragmentSongs.changeSeekbar();
        currentSong=song;
        currentSongIndex = CurrentSongIndexFinder();
        playButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        songName.setText(currentSong.getName().replace(".mp3", ""));
        //Log.d("xxy", song.getPath());
    }

    public static void pauseSong(){
        if (mediaPlayer != null) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void continuePlayingSong(){
        if (mediaPlayer != null) {
            if(isPaused()){
                mediaPlayer.start();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                FragmentSongs.changeSeekbar();
            }
        }
    }

    public static void playNextSong(){
        if(isRepeatPressed){
            if (mediaPlayer != null) {
                mediaPlayer.release();
                playSong(CurrentPlaylist.songs.get(currentSongIndex), Uri.parse(CurrentPlaylist.songs.get(currentSongIndex).getPath()));
                currentSong = CurrentPlaylist.songs.get(currentSongIndex);
                songName.setText(currentSong.getName().replace(".mp3", ""));
                seekbar.setMax(mediaPlayer.getDuration());
                FragmentSongs.changeSeekbar();
            }
        }
        else if(isShufflePressed){
            if (mediaPlayer != null) {
                mediaPlayer.release();
                Random r = new Random();
                currentSongIndex = r.nextInt(MainActivity.CurrentPlaylist.numberOfSongs - 0);
                playSong(CurrentPlaylist.songs.get(currentSongIndex), Uri.parse(CurrentPlaylist.songs.get(currentSongIndex).getPath()));
                currentSong = CurrentPlaylist.songs.get(currentSongIndex);
                songName.setText(currentSong.getName().replace(".mp3", ""));
                seekbar.setMax(mediaPlayer.getDuration());
                FragmentSongs.changeSeekbar();
            }
        }
        else{
            if (mediaPlayer != null && (currentSongIndex + 1) < CurrentPlaylist.songs.size()) {
                mediaPlayer.release();
                currentSongIndex++;
                playSong(CurrentPlaylist.songs.get(currentSongIndex), Uri.parse(CurrentPlaylist.songs.get(currentSongIndex).getPath()));
                currentSong = CurrentPlaylist.songs.get(currentSongIndex);
                songName.setText(currentSong.getName().replace(".mp3", ""));
                seekbar.setMax(mediaPlayer.getDuration());
                FragmentSongs.changeSeekbar();
            }
            else if(currentSongIndex + 1 == CurrentPlaylist.songs.size()){
                if((currentSongIndex + 1) >= CurrentPlaylist.songs.size()){
                    if(mediaPlayer!=null)   mediaPlayer.release();
                    currentSongIndex=0;
                    playSong(CurrentPlaylist.songs.get(currentSongIndex), Uri.parse(CurrentPlaylist.songs.get(currentSongIndex).getPath()));
                    currentSong = CurrentPlaylist.songs.get(currentSongIndex);
                    songName.setText(currentSong.getName().replace(".mp3", ""));
                    seekbar.setMax(mediaPlayer.getDuration());
                    FragmentSongs.changeSeekbar();
                }
            }
        }
    }

    public static void playPreviousSong(){

        if (mediaPlayer != null && currentSongIndex>=1) {
            mediaPlayer.release();
            currentSongIndex--;
            playSong(CurrentPlaylist.songs.get(currentSongIndex), Uri.parse(CurrentPlaylist.songs.get(currentSongIndex).getPath()));
            currentSong = CurrentPlaylist.songs.get(currentSongIndex);
            songName.setText(currentSong.getName().replace(".mp3", ""));
            seekbar.setMax(mediaPlayer.getDuration());
            FragmentSongs.changeSeekbar();
        }
    }

    public static Boolean isPaused() {
        if (mediaPlayer != null) {
            if(!mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() > 1) return true;
            else return false;
        }
        else return false;
    }

    static int CurrentSongIndexFinder(){
        for (int i = 0; i < CurrentPlaylist.songs.size(); i++) {
            if(CurrentPlaylist.songs.get(i) == currentSong){
                return i;
            }
        }
        return -1;
    }

    public static void playRandomSlowSong(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        MainActivity.CurrentPlaylist = MainActivity.Playlists.get(1);
        Random r = new Random();
        int randomNumber = r.nextInt(MainActivity.CurrentPlaylist.numberOfSongs - 0);
        playSong(MainActivity.CurrentPlaylist.getSongs().get(randomNumber), Uri.parse(MainActivity.CurrentPlaylist.getSongs().get(randomNumber).getPath()));
    }

    public static void playRandomMediumTempoSong(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        MainActivity.CurrentPlaylist = MainActivity.Playlists.get(2);
        Random r = new Random();
        int randomNumber = r.nextInt(MainActivity.CurrentPlaylist.numberOfSongs - 0);
        playSong(MainActivity.CurrentPlaylist.getSongs().get(randomNumber), Uri.parse(MainActivity.CurrentPlaylist.getSongs().get(randomNumber).getPath()));
    }

    public static void playRandomFastSong(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        MainActivity.CurrentPlaylist = MainActivity.Playlists.get(3);
        Random r = new Random();
        int randomNumber = r.nextInt(MainActivity.CurrentPlaylist.numberOfSongs - 0);
        playSong(MainActivity.CurrentPlaylist.getSongs().get(randomNumber), Uri.parse(MainActivity.CurrentPlaylist.getSongs().get(randomNumber).getPath()));
    }
}
