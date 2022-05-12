package com.example.announcementspublicationapp.interfaces;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.announcementspublicationapp.BaseActivity;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.mymodels.UserModel;
import com.example.announcementspublicationapp.sqliteDatabase.DataBase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewProfile extends BaseActivity {

    FirebaseAuth firebaseAuth;
    UserModel userModel;
    Button logoutBtn;
    TextView nameTV, emailTV, ageTV, phoneTV, locationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile_layout);
        logoutBtn = findViewById(R.id.logout_button);
        nameTV= findViewById(R.id.name);
        emailTV= findViewById(R.id.email);
        ageTV= findViewById(R.id.age);
        phoneTV= findViewById(R.id.phone);
        locationTV = findViewById(R.id.ann_location);
        //we check first for internet connection to decide where to get information online or offline
        if (checkInternetConnection(this)) {
            firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference uidRef = rootRef.child("users").child(userId);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            userModel = new UserModel(
                                    (String) dataSnapshot.child("name").getValue(),
                                    (String) dataSnapshot.child("email").getValue(),
                                    (String) dataSnapshot.child("phone").getValue(),
                                    (String) dataSnapshot.child("location").getValue(),
                                    (long) dataSnapshot.child("age").getValue()
                            );
                            nameTV.setText(userModel.getName());
                            emailTV.setText(userModel.getEmail());
                            ageTV.setText(String.valueOf(userModel.getAge()));
                            phoneTV.setText(userModel.getPhone());
                            locationTV.setText(userModel.getLocation());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("TAG", error.getMessage()); //Don't ignore potential errors!
                    }
                };
                uidRef.addListenerForSingleValueEvent(eventListener);
            }

        } else {
            //here we get user info from sqlite
            Toast.makeText(this, "Offline mode", Toast.LENGTH_SHORT).show();
            userModel = DataBase.getDataBase(this).userDao().getUser();
            nameTV.setText(userModel.getName());
            emailTV.setText(userModel.getEmail());
            ageTV.setText(String.valueOf(userModel.getAge()));
            phoneTV.setText(userModel.getPhone());
            locationTV.setText(userModel.getLocation());
        }

        //when user is logout we logout from firebase auth and delete all data in sqlite
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DataBase.getDataBase(ViewProfile.this).userDao().deleteAll();
                DataBase.getDataBase(ViewProfile.this).announcementDao().deleteAll();
                startActivity(new Intent(ViewProfile.this, Subscribe.class));
                finish();
            }
        });
    }

    public  boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }
}