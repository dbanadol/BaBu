package com.example.babu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


public class deletePlaylistConfirmation extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_playlist_confirmation, null);

        builder.setView(view)
                .setTitle("Delete Playlist?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(MainActivity.CurrentPlaylist.getName().equals(MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).getName())){
                            if (AudioPlayer.mediaPlayer != null) {
                                AudioPlayer.pauseSong();
                                AudioPlayer.mediaPlayer.release();
                                AudioPlayer.mediaPlayer = null;
                                MainActivity.songName.setText("Select A Song");
                            }
                            MainActivity.CurrentPlaylist = MainActivity.Playlists.get(0);
                            FragmentPlaylists.playlistsListView.setItemChecked(0,true);
                            ArrayAdapter<String> songListAdapter = new ArrayAdapter<String>(
                                    getActivity(), android.R.layout.simple_list_item_1, MainActivity.CurrentPlaylist.songNames
                            );
                            FragmentSongs.songListView.setAdapter(songListAdapter);
                            FragmentSongs.songListView.invalidateViews();
                        }
                        MainActivity.Playlists.remove(playlistDeleteOrRename.playlistID);
                        playlistDeleteOrRename.playlistID = -1;
                        Toast.makeText(getActivity(), "Playlist Deleted Successfully" , Toast.LENGTH_LONG).show();
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