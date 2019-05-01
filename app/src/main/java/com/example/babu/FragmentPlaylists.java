package com.example.babu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import static com.example.babu.MainActivity.Playlists;

public class FragmentPlaylists extends Fragment{

    public static ListView playlistsListView;
    Button createPlaylistButton;
    private static int lastSelectedPlaylist = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlists,container,false);
        playlistsListView = view.findViewById(R.id.playlistsListView);
        createPlaylistButton = view.findViewById(R.id.createPlaylist);
        createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaylistName();
            }
        });

        final PlaylistAdapter adapter = new PlaylistAdapter(getContext(), R.layout.playlist_adapter, Playlists);
        playlistsListView.setAdapter(adapter);
        playlistsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        /*
        ArrayAdapter<String> playlistListAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_single_choice, Playlists
        );
        playlistsListView.setAdapter(playlistListAdapter);  */
        if(lastSelectedPlaylist==-1)    MainActivity.getViewByPosition(0,playlistsListView).setBackgroundColor(Color.DKGRAY);

        playlistsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapterView, View view, int position, long l){

                MainActivity.CurrentPlaylist = MainActivity.Playlists.get(position);
                Toast.makeText(getActivity(), "Active Playlist : " + MainActivity.CurrentPlaylist.getName() , Toast.LENGTH_SHORT).show();

                if (AudioPlayer.mediaPlayer != null) {
                    AudioPlayer.pauseSong();
                    AudioPlayer.mediaPlayer.release();
                    AudioPlayer.mediaPlayer = null;
                    MainActivity.songName.setText("Select A Song");
                }

                if(MainActivity.CurrentPlaylist.numberOfSongs==0){
                    FragmentSongs.noSongs.setVisibility(View.VISIBLE);
                }
                else    FragmentSongs.noSongs.setVisibility(View.INVISIBLE);
                ArrayAdapter<String> songListAdapter = new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_list_item_1, MainActivity.CurrentPlaylist.songNames
                );
                FragmentSongs.songListView.setAdapter(songListAdapter);
                FragmentSongs.songListView.invalidateViews();

                MainActivity.getViewByPosition(position,playlistsListView).setBackgroundColor(Color.DKGRAY);
                if (lastSelectedPlaylist != -1 && lastSelectedPlaylist != position){
                    MainActivity.getViewByPosition(lastSelectedPlaylist,playlistsListView).setBackgroundColor(Color.TRANSPARENT);
                }
                lastSelectedPlaylist = position;
            }
        });

        playlistsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                playlistDeleteOrRename.playlistID = pos;
                playlistDeleteOrRename();
                return true;
            }
        });

        return view;
    }

    public void getPlaylistName(){
        getPlaylistName getPlaylistName_ = new getPlaylistName();
        getPlaylistName_.show(getFragmentManager(), "Playlist Name Dialog");
    }

    public void playlistDeleteOrRename(){
        playlistDeleteOrRename playlistDeleteOrRename_ = new playlistDeleteOrRename();
        playlistDeleteOrRename_.show(getFragmentManager(), "Playlist Delete Dialog");
    }
}