package com.example.announcementspublicationapp.interfaces;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.announcementspublicationapp.BaseActivity;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.myadapters.SearchAnnouncementsAdapter;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import com.example.announcementspublicationapp.mymodels.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchAnnouncement extends BaseActivity {
    SearchAnnouncementsAdapter myAdapter;
    ArrayList<AnnouncementModel> dataList;
    ArrayList<AnnouncementModel> newList;
    SearchView searchView;
    RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_announcement_layout);

        searchView = findViewById(R.id.search);
        myRecyclerView = findViewById(R.id.rv);
        dataList = new ArrayList<>();
        newList = new ArrayList<>();

        //search view is working text in it has changed
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("newT", newText);
                newList = new ArrayList<>();
                if(checkInternetConnection(SearchAnnouncement.this)){
                    if(newText.length() == 0){
                        myAdapter = new SearchAnnouncementsAdapter(SearchAnnouncement.this, dataList);
                        myRecyclerView.setAdapter(myAdapter);
                        myAdapter.notifyDataSetChanged();
                    }
                    else{
                        // here we search for word in firebase we need the whole word to match it because firebase work like that
                        //we add index feature to title in real time database rules so that we will be able to search through it
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference dbRef = rootRef.child("announcements");
                        //this is search query
                        Query query = dbRef.orderByChild("ann_title").equalTo(newText);

                        //search request we get list of announcements that there title contain the word from search view
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    AnnouncementModel announcement = postSnapshot.getValue(AnnouncementModel.class);
                                    newList.add(announcement);
                                }
                                myAdapter = new SearchAnnouncementsAdapter(SearchAnnouncement.this, newList);

                                myRecyclerView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(SearchAnnouncement.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    Toast.makeText(SearchAnnouncement.this, "you can not search in offline mode", Toast.LENGTH_SHORT).show();
                }
                return true;

            }
        });

        if (checkInternetConnection(this)) {
            getOnlineData();
        } else {
            Toast.makeText(this, "You are in offline mode", Toast.LENGTH_SHORT).show();        }
    }



    private void getOnlineData() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbRef = rootRef.child("announcements");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataList = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//                        AnnouncementModel announcementModel = itemSnapshot.getValue(AnnouncementModel.class);
                        AnnouncementModel announcementModel = new AnnouncementModel(
                                (String) itemSnapshot.child("image_path").getValue(),
                                (String) itemSnapshot.child("ann_title").getValue(),
                                (long) itemSnapshot.child("ann_price").getValue(),
                                (String) itemSnapshot.child("ann_description").getValue(),
                                (String) itemSnapshot.child("ann_location").getValue(),
                                (double) itemSnapshot.child("ann_latitude").getValue(),
                                (double) itemSnapshot.child("ann_longitude").getValue(),
                                (String) itemSnapshot.child("firebaseId").getValue(),
                                new UserModel(
                                        (String) itemSnapshot.child("announcer_data").child("name").getValue(),
                                        (String) itemSnapshot.child("announcer_data").child("email").getValue(),
                                        (String) itemSnapshot.child("announcer_data").child("phone").getValue(),
                                        (String) itemSnapshot.child("announcer_data").child("location").getValue(),
                                        (long) itemSnapshot.child("announcer_data").child("age").getValue()
                                )
                        );
                        dataList.add(announcementModel);
                    }
                    newList = dataList;
                    myAdapter = new SearchAnnouncementsAdapter(SearchAnnouncement.this, dataList);
                    myRecyclerView.setAdapter(myAdapter);
                    Log.e("name", dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TAG", error.getMessage());
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }
}