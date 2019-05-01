package com.example.babu;

public class Song {
    String name;
    String bpm;
    String path;

    public Song(String name, String bpm, String path) {
        this.name = name;
        this.bpm = bpm;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getBpm() {
        return bpm;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
