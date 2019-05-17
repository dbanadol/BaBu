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

public class addRemoveSongsToPlaylist extends AppCompatDialogFragment {

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

        if(AudioPlayer.mediaPlayer!=null){
            AudioPlayer.pauseSong();
        }

        for (int x=0; x<MainActivity.AllSongs.numberOfSongs;x++){
            for (int y=0; y<MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).numberOfSongs;y++){
                if(MainActivity.AllSongs.songs.get(x).getName().equals(MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).songs.get(y).getName())){
                    songs_to_select.setItemChecked(x,true);
                    //MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).removeSong(MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).songs.get(y));
                }
            }
        }

        builder.setView(view)
                .setTitle("Add or Remove Songs")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        for (int x=0; x<MainActivity.AllSongs.numberOfSongs;x++){
                            for (int y=0; y<MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).numberOfSongs;y++){
                                if(MainActivity.AllSongs.songs.get(x).getName().equals(MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).songs.get(y).getName())){
                                    MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).removeSong(MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).songs.get(y));
                                }
                            }
                        }

                        if(songs_to_select.getCheckedItemCount() > 0){
                            for(int x=0; x<AllSongs.numberOfSongs; x++){
                                if(songs_to_select.isItemChecked(x)){
                                    MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).addSong(AllSongs.getSongs().get(x));
                                }
                            }
                        }
                        FragmentPlaylists.playlistsListView.invalidateViews();
                        if(MainActivity.CurrentPlaylist.getName().equals(MainActivity.Playlists.get(playlistDeleteOrRename.playlistID).getName())){
                            if(MainActivity.CurrentPlaylist.numberOfSongs==0){
                                FragmentSongs.noSongs.setVisibility(View.VISIBLE);
                            }
                            else    FragmentSongs.noSongs.setVisibility(View.INVISIBLE);

                            ArrayAdapter<String> songListAdapter = new ArrayAdapter<String>(
                                    getActivity(), android.R.layout.simple_list_item_1, MainActivity.CurrentPlaylist.songNames
                            );
                            FragmentSongs.songListView.setAdapter(songListAdapter);
                            FragmentSongs.songListView.invalidateViews();
                        }
                        if(AudioPlayer.mediaPlayer !=null){
                            AudioPlayer.mediaPlayer.release();
                            AudioPlayer.mediaPlayer=null;
                            MainActivity.songName.setText("Select A Song");
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
