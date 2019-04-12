package com.hushproject.hush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> {

    private ArrayList<UserLocations> locations;

    //default constructor
    public LocationViewAdapter(ArrayList<UserLocations> i) {
        locations = i;
    }

    //required methods for LocationViewAdapter.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate RecyclerView with cards containing the data from our list of saved locations.
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //create a UserLocations object for current location.
        final UserLocations currentLoc = locations.get(i);

        //set name of currentLoc to name textview.
        viewHolder.locTitle.setText(currentLoc.getLocationName());
        //set coordinates in textviews
        viewHolder.locLat.setText("Lat: " + Double.toString(currentLoc.getLocationLat()));
        viewHolder.locLng.setText("Lng: " + Double.toString(currentLoc.getLocationLng()));
        viewHolder.locRad.setText("Rad: " + Double.toString(currentLoc.getLocationRad()));
    }

    @Override
    public int getItemCount() {
        //just returns the size of the locations ArrayList.
        return locations.size();
    }
    //end of required methods for LocationViewAdapter.

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Context to access SharedPreferences.
        private Context context;

        private SharedPreferences locPrefs;
        private SharedPreferences.Editor editor;

        private Button editButton;
        private Button deleteButton;
        private Button volTestButton;

        private TextView locTitle;
        private TextView locLat;
        private TextView locLng;
        private TextView locRad;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            //set context to itemView.
            context = itemView.getContext();

            final AudioManager audioManager =
                    (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

            locPrefs = context.getSharedPreferences("LocPrefs", Context.MODE_PRIVATE);
            editor = locPrefs.edit();

            locTitle = itemView.findViewById(R.id.locTitle);
            locLat = itemView.findViewById(R.id.locLat);
            locLng = itemView.findViewById(R.id.locLng);
            locRad = itemView.findViewById(R.id.locRad);
            editButton = itemView.findViewById(R.id.editBtn);
            deleteButton = itemView.findViewById(R.id.delBtn);
            volTestButton = itemView.findViewById(R.id.volTest);

            //onClick listener for our edit button.
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get locTitle.
                    String cardKey = locTitle.getText().toString();
                    //open Edit activity.
                    Intent openEdit = new Intent(context, EditActivity.class);
                    //send information to edit activity.
                    openEdit.putExtra("cardKey", cardKey);
                    context.startActivity(openEdit);
                }
            });

            //onClick listener for our delete button.
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get locTitle.
                    String cardKey = locTitle.getText().toString();
                    //remove card from recyclerview.
                    locations.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    //use cardKey to remove the sharedpreferences from prefs file.
                    editor.remove(cardKey);
                    editor.apply();
                }
            });

            //onClick listener for volTest button.
            volTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //retrieve volume settings saved under this key and set device volume to these.
                    UserLocations getVolumes = locations.get(getAdapterPosition());

                    //get volumes for this card.
                    int volRingTest = getVolumes.getLocRingVol();

                    try {
                        if(volRingTest == 0)
                        {
                            audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                                    AudioManager.ADJUST_MUTE, 0);
                        }
                        else {
                            audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
                                    AudioManager.ADJUST_UNMUTE, 0);
                            audioManager.setStreamVolume(AudioManager.STREAM_RING,
                                    volRingTest, 0);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
