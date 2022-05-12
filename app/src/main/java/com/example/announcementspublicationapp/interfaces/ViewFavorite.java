package com.example.announcementspublicationapp.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.announcementspublicationapp.BaseActivity;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.myadapters.FavoritesAdapter;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.announcementspublicationapp.sqliteDatabase.DataBase;
import java.util.ArrayList;

public class ViewFavorite extends BaseActivity {
    FavoritesAdapter myAdapter;
    ArrayList<AnnouncementModel> dataList;
    RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_favorite_layout);

        dataList = new ArrayList<>();
        myRecyclerView = findViewById(R.id.rv);

        if(checkInternetConnection(this)){
            getFavorites();

        }else{
            Toast.makeText(this, "You can not load data in offline mode", Toast.LENGTH_SHORT).show();
        }

    }

    //this function to get favorites list from firebase
    private void getFavorites() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String userId = DataBase.getDataBase(this).userDao().getUser().getId();
        DatabaseReference dbRef = rootRef.child("favorites").child(userId);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataList = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        AnnouncementModel announcementModel = itemSnapshot.getValue(AnnouncementModel.class);
                        dataList.add(announcementModel);
                    }
                    myAdapter = new FavoritesAdapter(ViewFavorite.this, dataList);
                    myRecyclerView.setAdapter(myAdapter);
                    Log.e("name", dataSnapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("TAG", error.getMessage()); //Don't ignore potential errors!
            }
        });
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