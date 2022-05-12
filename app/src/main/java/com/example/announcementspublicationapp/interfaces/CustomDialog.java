package com.example.announcementspublicationapp.interfaces;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.announcementspublicationapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public abstract class CustomDialog extends Dialog implements
        android.view.View.OnClickListener , OnMapReadyCallback {
    private GoogleMap mMap;

    public Activity c;
    public Context context;
    public Dialog d;
    public Button submit;
    public double longitude, latitude;
    public String location;
    public CustomDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        longitude = 0.0;
        latitude = 0.0;
        location = "";

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity)c).getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                onButtonClicked(longitude, latitude,location);
                break;
            default:
                break;
        }
        dismiss();
    }

    public abstract void onButtonClicked(double longitude, double latitude, String location);

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng saudi = new LatLng(24.774265, 	46.738586);
        mMap.addMarker(new MarkerOptions().position(saudi).title("Marker in Saudi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(saudi));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Creating a marker
                LatLng point = latLng;
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(point);
                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(point.latitude + " : " + point.longitude);
//                binding.longitude.setText(String.valueOf(point.longitude));
//                binding.latitude.setText(String.valueOf(point.latitude));
                String address = getAddress(c, point.latitude, point.longitude);
//                binding.location.setText(address);
                longitude = point.longitude;
                latitude = point.latitude;
                location = address;
                Log.e("address", address);
                // Clears the previously touched position
                mMap.clear();
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                mMap.addMarker(markerOptions);
            }
        });

    }
    //Function to get location name
    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            String add = "";
            add = add + obj.getCountryName();
            add = add + " : " + (obj.getAdminArea()==null?"":obj.getAdminArea());
            add = add + " : " + (obj.getSubAdminArea()==null?"":obj.getSubAdminArea());
            add = add + " : " + (obj.getLocality()==null?"":obj.getLocality());

            return add;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.c, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
