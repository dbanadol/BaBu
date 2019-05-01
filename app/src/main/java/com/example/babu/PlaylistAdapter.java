package com.example.babu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {

    private Context mContext;
    int mResource;
    List<Boolean> checkboxState;

    public PlaylistAdapter(Context context, int resource, ArrayList<Playlist> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.checkboxState = new ArrayList<Boolean>(Collections.nCopies(objects.size(), true));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String playlist_name = getItem(position).getName();
        int noSongs = getItem(position).getNumberOfSongs();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        CheckedTextView tvName = convertView.findViewById(R.id.playlistName);
        TextView tvNoSongs = convertView.findViewById(R.id.songNumber);

        tvName.setText(playlist_name);
        tvNoSongs.setText("(" + Integer.toString(noSongs) + " Songs)");

        return convertView;
    }
}