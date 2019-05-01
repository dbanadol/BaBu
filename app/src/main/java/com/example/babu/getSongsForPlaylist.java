package com.example.babu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static com.example.babu.MainActivity.AllSongs;


public class getSongsForPlaylist extends AppCompatDialogFragment {

    private ListView songs_to_select;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.select_songs, null);
        songs_to_select = view.findViewById(R.id.songs_to_select);
        songs_to_select.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        ArrayAdapter<String> songstoSelectAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_multiple_choice, AllSongs.songNames
        );

        songs_to_select.setAdapter(songstoSelectAdapter);

        builder.setView(view)
                .setTitle("Select Songs")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Playlist playlist = new Playlist(getPlaylistName.playlist_Name);
                        if(songs_to_select.getCheckedItemCount() > 0){
                            for(int x=0; x<AllSongs.numberOfSongs; x++){
                                if(songs_to_select.isItemChecked(x)){
                                    playlist.addSong(AllSongs.getSongs().get(x));
                                }
                            }
                        }
                        MainActivity.Playlists.add(playlist);
                        FragmentPlaylists.playlistsListView.invalidateViews();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return builder.create();
    }
}

