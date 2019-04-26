package com.hushproject.hush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> {


    private ArrayList<UserLocations> locations;

    //GeocoderService geocoderService = new GeocoderService();
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
        String address;
        String addressS;
        //set name of currentLoc to name textview.
        viewHolder.locTitle.setText(currentLoc.getLocationName()); // Do NOT Change/Edit the name!
        //addressS = geocoderService.getAddressFromCoordinates(currentLoc.getLocationLat(),currentLoc.getLocationLng(),viewHolder.context);
        viewHolder.locLat.setText("Address: " + currentLoc.getLocationAddress());
        //set coordinates in textviews
        //viewHolder.locLng.setText("Lng: " + Double.toString(currentLoc.getLocationLng()));
        viewHolder.locRad.setText("Radius: " + Double.toString(currentLoc.getLocationRad()) + " meters");
    }

//    private String getAddressFromCoordinates (double lat, double lng,ViewHolder viewHolder){
//        context = viewHolder.context;
//        Geocoder geo = new Geocoder(context, Locale.getDefault());
//        String address="";
//        try {
//            List<Address> addresses = geo.getFromLocation(lat,lng,1);
//            address = addresses.get(0).getAddressLine(0);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return address;
//    }

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

            //onClick listener for our edit button.
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get locTitle.
                    String cardKey = locTitle.getText().toString();
                    String cardAddress = locLat.getText().toString();
                    //open Edit activity.
                    Intent openEdit = new Intent(context, EditActivity.class);
                    //send information to edit activity.
                    openEdit.putExtra("cardKey", cardKey);
                    openEdit.putExtra("cardAddress", cardAddress);
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
                    //editor.clear(); //- Shared preferences required to be cleared when adding a new field to userLocation
                    editor.remove(cardKey);
                    editor.apply();
                }
            });
        }
    }

}
