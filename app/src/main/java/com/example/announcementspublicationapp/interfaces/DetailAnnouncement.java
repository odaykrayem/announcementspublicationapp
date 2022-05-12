package com.example.announcementspublicationapp.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;

public class DetailAnnouncement extends AppCompatActivity {

    TextView title, description, price, location, announcerName;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_announcement_layout);

        title = findViewById(R.id.ann_title);
        description = findViewById(R.id.ann_description);
        price= findViewById(R.id.ann_price);
        location= findViewById(R.id.ann_location);
        announcerName = findViewById(R.id.announcer_name);
        image = findViewById(R.id.image);

        //here we get the object sent by list announcements
        Intent i = getIntent();
        AnnouncementModel announcementModel = (AnnouncementModel) i.getSerializableExtra("announcement");
        //fill the interface with information
        title.setText(announcementModel.getAnn_title());
        description.setText(announcementModel.getAnn_description());
        price.setText(String.valueOf(announcementModel.getAnn_price()));
        location.setText(announcementModel.getAnn_location());
        announcerName.setText(announcementModel.getAnnouncer_data().getName());
        if(announcementModel.getImage_path() != null){
            if(checkInternetConnection(this)){
                Glide.with(this)
                        .load(announcementModel.getImage_path())
                        .into(image);
            }else{
                 image.setImageBitmap(BitmapFactory.decodeFile(announcementModel.getImage_path()));
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }
}