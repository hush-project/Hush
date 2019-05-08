package com.hushproject.hush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> {


    private ArrayList<UserLocations> locations;

    //default constructor
    LocationViewAdapter(ArrayList<UserLocations> i) {
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
        viewHolder.locTitle.setText(currentLoc.getLocationName()); // Do NOT Change/Edit the name!
        viewHolder.locAddress.setText(currentLoc.getLocationAddress());
        //set coordinates in textviews
        viewHolder.locRad.setText(Double.toString(currentLoc.getLocationRad()) + " meter radius.");
    }

    @Override
    public int getItemCount() {
        //just returns the size of the locations ArrayList.
        return locations.size();
    }
    //end of required methods for LocationViewAdapter.

    class ViewHolder extends RecyclerView.ViewHolder {
        //Context to access SharedPreferences.
        private Context context;

        private SharedPreferences locPrefs;
        private SharedPreferences.Editor editor;

        private Button editButton;
        private Button deleteButton;

        private TextView locTitle;
        private TextView locAddress;
        private TextView locRad;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            //set context to itemView.
            context = itemView.getContext();

            locPrefs = context.getSharedPreferences("LocPrefs", Context.MODE_PRIVATE);
            editor = locPrefs.edit();

            locTitle = itemView.findViewById(R.id.locTitle);
            locAddress = itemView.findViewById(R.id.locAddress);
            locRad = itemView.findViewById(R.id.locRad);
            editButton = itemView.findViewById(R.id.editBtn);
            deleteButton = itemView.findViewById(R.id.delBtn);

            //onClick listener for our edit button.
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get locTitle.
                    String cardKey = locTitle.getText().toString();
                    String cardAddress = locAddress.getText().toString();
                    //open Edit activity.
                    Intent openEdit = new Intent(context, EditActivity.class);
                    //send information to edit activity.
                    openEdit.putExtra("cardKey", cardKey);
                    openEdit.putExtra("cardAddress", cardAddress);
                    openEdit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                    editor.commit();
                }
            });
        }
    }

}
