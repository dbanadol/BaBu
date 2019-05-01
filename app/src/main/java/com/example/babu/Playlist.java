package com.example.babu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Playlist {

    public String name;
    public int numberOfSongs = 0;
    public ArrayList<Song> songs = new ArrayList<Song>();
    public ArrayList<String> songNames = new ArrayList<String>();;

    public Playlist(String name) {
        this.name = name;
    }

    public void addSong(Song song){

        songs.add(song);
        songNames.add(song.getName().replace(".mp3", ""));
        this.sortSongsAlphabetically();
        numberOfSongs++;
    }

    public void removeSong(Song song){
        if(songs != null) {
            for (int counter = 0; counter < songs.size(); counter++) {
                if(song.path==songs.get(counter).path) {
                    songs.remove(counter);
                    songNames.remove(counter);
                    this.numberOfSongs--;
                }
            }
        }
    }

    public void sortSongsAlphabetically(){
        if (songs.size() > 0) {
            Collections.sort(songs, new Comparator<Song>() {
                @Override
                public int compare(final Song object1, final Song object2) {
                    return object1.getName().compareToIgnoreCase(object2.getName());
                }
            });
        }

        if (songNames.size() > 0) {
            Collections.sort(songNames, new Comparator<String>() {
                @Override
                public int compare(final String object1, final String object2) {
                    return object1.compareToIgnoreCase(object2);
                }
            });
        }
    }

    public String getName() {
        return name;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }
}
