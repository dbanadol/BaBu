package com.example.babu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.babu.MainActivity.AllSongs;
import static com.example.babu.MainActivity.locate;

public class playlistDeleteOrRename extends AppCompatDialogFragment {

    RadioGroup radioGroup;
    public static int playlistID;
    RadioButton button;
    TextView text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.playlist_rename_or_delete, null);
        radioGroup = view.findViewById(R.id.radioGroup);

        if(playlistID == 0){
            button = view.findViewById(R.id.addRemoveSongs);
            button.setText("Synchronize All Songs in the Phone");
        }

        text = view.findViewById(R.id.wait);

        builder.setView(view)
                .setTitle(MainActivity.Playlists.get(playlistID).getName())
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(radioGroup.getCheckedRadioButtonId() == R.id.addRemoveSongs){
                            if(playlistID == 0){
                                dialogInterface.dismiss();
                                Toast.makeText(getActivity(), "Synchronizing Songs..." , Toast.LENGTH_LONG).show();
                                while(MainActivity.AllSongs.numberOfSongs > 0)   MainActivity.AllSongs.removeSong(MainActivity.AllSongs.getSongs().get(0));
                                MainActivity.readSongs(MainActivity.getSDCardPath());
                                //MainActivity.readSongs(Environment.getExternalStorageDirectory());
                                MainActivity.Playlists.set(0, AllSongs);

                                if(MainActivity.CurrentPlaylist.getName().equals(MainActivity.Playlists.get(0).getName())){
                                    MainActivity.CurrentPlaylist = MainActivity.AllSongs;
                                    ArrayAdapter<String> songListAdapter = new ArrayAdapter<String>(
                                            getActivity(), android.R.layout.simple_list_item_1, MainActivity.CurrentPlaylist.songNames
                                    );
                                    FragmentSongs.songListView.setAdapter(songListAdapter);
                                    FragmentSongs.songListView.invalidateViews();
                                }
                                Toast.makeText(getActivity(), "Synchronizing Successful" , Toast.LENGTH_SHORT).show();
                                button.setText("Add/Remove Songs");
                                radioGroup.setVisibility(View.VISIBLE);
                                text.setVisibility(View.INVISIBLE);
                            }
                            else    addRemoveSongs();
                        }
                        else if(radioGroup.getCheckedRadioButtonId() == R.id.deletePlaylist){
                            if(playlistID == 0 || playlistID == 1 || playlistID == 2 || playlistID == 3)    Toast.makeText(getActivity(), "This Playlist Cannot Be Deleted" , Toast.LENGTH_SHORT).show();
                            else    deletePlaylist();
                        }
                        else if(radioGroup.getCheckedRadioButtonId() == R.id.renamePlaylist){
                            if(playlistID == 0 || playlistID == 1 || playlistID == 2 || playlistID == 3)    Toast.makeText(getActivity(), "This Playlist Cannot Be Renamed" , Toast.LENGTH_SHORT).show();
                            else    renamePlaylist();
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

    public void deletePlaylist(){
        deletePlaylistConfirmation deletePlaylistConfirmation_ = new deletePlaylistConfirmation();
        deletePlaylistConfirmation_.show(getFragmentManager(), "Playlist Delete Dialog");
    }

    public void renamePlaylist(){
        renamePlaylist renamePlaylist_ = new renamePlaylist();
        renamePlaylist_.show(getFragmentManager(), "Playlist Delete Dialog");
    }
    public void addRemoveSongs(){
        addRemoveSongsToPlaylist addRemoveSongsToPlaylist_ = new addRemoveSongsToPlaylist();
        addRemoveSongsToPlaylist_.show(getFragmentManager(), "Playlist Add/Remove Song Dialog");
    }
}
