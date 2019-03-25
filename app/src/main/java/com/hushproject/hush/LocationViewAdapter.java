package com.hushproject.hush;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        //create a UserLocations object to represent the object we're currently looking at.
        UserLocations currentLoc = locations.get(i);

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

        //create TextViews for cards.
        TextView locTitle;
        TextView locAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //bind our textviews to the appropriate elements.
            locTitle = itemView.findViewById(R.id.locTitle);
            locAddress = itemView.findViewById(R.id.locAddress);
        }
    }

}
