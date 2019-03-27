package com.hushproject.hush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    //create an ArrayList of UserLocations to display on our RecyclerList.
    private ArrayList<UserLocations> locations;

    //default constructor
    public LocationViewAdapter(ArrayList<UserLocations> i) {
        locations = i;
    }

    //required methods for our LocationViewAdapter.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate our RecycleView with cards containing the data from our list of saved locations.
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //create a UserLocations object to represent the object we're currently looking at.
        final UserLocations currentLoc = locations.get(i);

        //set name of our currentLoc to our name textview.
        viewHolder.locTitle.setText(currentLoc.getLocationName());
        //set address from our currentLoc to our address textview.
        viewHolder.locAddress.setText(currentLoc.getLocationAddress());
    }

    @Override
    public int getItemCount() {
        //just returns the size of our locations ArrayList.
        return locations.size();
    }
    //end of required methods for LocationViewAdapter.

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;

        private SharedPreferences locPrefs;
        private SharedPreferences.Editor editor;

        //Button declarations
        private Button editButton;
        private Button deleteButton;

        //create TextViews for cards.
        private TextView locTitle;
        private TextView locAddress;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            context = itemView.getContext();

            locPrefs = context.getSharedPreferences("LocPrefs", Context.MODE_PRIVATE);
            editor = locPrefs.edit();

            //bind our textviews to the appropriate elements.
            locTitle = itemView.findViewById(R.id.locTitle);
            locAddress = itemView.findViewById(R.id.locAddress);

            editButton = itemView.findViewById(R.id.editBtn);
            deleteButton = itemView.findViewById(R.id.delBtn);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openEdit = new Intent(context, EditActivity.class);
                    context.startActivity(openEdit);
                    Log.d("Edit", "was clicked ");
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cardKey = locTitle.getText().toString();
                    locations.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    editor.remove(cardKey);
                    editor.apply();
                    Log.d("Delete", "was clicked " + locTitle.getText());
                }
            });
        }
    }

}
