package com.example.announcementspublicationapp.interfaces;

import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.announcementspublicationapp.BaseActivity;
import com.example.announcementspublicationapp.myadapters.ListAnnouncementsAdapter;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import com.example.announcementspublicationapp.mymodels.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.announcementspublicationapp.sqliteDatabase.DataBase;
import java.util.ArrayList;
import java.util.List;

public class ListAnnouncement extends BaseActivity {

    ListAnnouncementsAdapter myAdapter;
    ArrayList<AnnouncementModel> MyDataList;
    RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_announcement_layout);

        MyDataList = new ArrayList<>();
        myRecyclerView = findViewById(R.id.rv);

        if (checkInternetConnection(this)) {
            getOnlineData();
        } else {
            getOfflineData();
        }
    }

    //get all announcements from sqlite database
    private void getOfflineData() {
        Toast.makeText(this, "offline data will be uploaded in offline mode", Toast.LENGTH_SHORT).show();

        @SuppressLint("StaticFieldLeak")
        class getAnnouncementsTask extends AsyncTask<Void, Void, List<AnnouncementModel>> {
            @Override
            protected List<AnnouncementModel> doInBackground(Void... voids) {
                return DataBase.getDataBase(getApplicationContext()).announcementDao().getAllAnnouncements();
            }
            @Override
            protected void onPostExecute(List<AnnouncementModel> announcements) {
                super.onPostExecute(announcements);
                MyDataList.addAll(announcements);
                myAdapter = new ListAnnouncementsAdapter(ListAnnouncement.this, MyDataList);
                myRecyclerView.setAdapter(myAdapter);
            }
        }
        new getAnnouncementsTask().execute();
    }

    //get all announcements from firebase

    private void getOnlineData() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbRef = rootRef.child("announcements");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MyDataList = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//                        AnnouncementModel announcementModel = itemSnapshot.getValue(AnnouncementModel.class);
                        AnnouncementModel announcementModel=  new AnnouncementModel(
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
                        MyDataList.add(announcementModel);
                    }
                    myAdapter = new ListAnnouncementsAdapter(ListAnnouncement.this, MyDataList);
                    myRecyclerView.setAdapter(myAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TAG", error.getMessage());
            }
        });

    }

    public boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}