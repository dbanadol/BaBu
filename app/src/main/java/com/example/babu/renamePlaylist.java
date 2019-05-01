package com.example.babu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class renamePlaylist extends AppCompatDialogFragment {
    private EditText playlistName;
    private boolean updatePermission;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.get_playlist_name, null);
        playlistName = view.findViewById(R.id.playlistName);

        builder.setView(view)
                .setTitle("Enter New Playlist Name")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updatePermission = true;
                        for (int x=0; x<MainActivity.Playlists.size();x++){
                            if(MainActivity.Playlists.get(x).getName().equals(playlistName.getText().toString()))  updatePermission = false;
                        }
                        if(updatePermission){
                            MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).setName(playlistName.getText().toString());
                            Toast.makeText(getActivity(), "Playlist Renamed Successfully" , Toast.LENGTH_SHORT).show();
                            FragmentPlaylists.playlistsListView.invalidateViews();
                        }
                        else{
                            Toast.makeText(getActivity(), "Playlist With This Name Already Exists" , Toast.LENGTH_SHORT).show();
                        }
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