package com.example.announcementspublicationapp.interfaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.announcementspublicationapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.announcementspublicationapp.mymodels.UserModel;
import com.example.announcementspublicationapp.sqliteDatabase.DataBase;

public class Subscribe extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    EditText nameET, emailET, passwordET, phoneET, locationET, ageET;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_layout);

        nameET = findViewById(R.id.name);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        phoneET = findViewById(R.id.phone_number);
        locationET = findViewById(R.id.ann_location);
        ageET = findViewById(R.id.age);
        registerBtn = findViewById(R.id.register_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();

        if(DataBase.getDataBase(this).userDao().getUser() == null){
                if(firebaseAuth.getCurrentUser() != null){
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference uidRef = rootRef.child("users").child(uid);
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("data", dataSnapshot.getValue().toString());
                            if(dataSnapshot.exists()) {
                                goToHome();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.d("TAG",error.getMessage()); //Don't ignore potential errors!
                        }
                    };
                    uidRef.addListenerForSingleValueEvent(eventListener);
                }
        }else{
            goToHome();
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void goToHome() {
        Intent i = new Intent(Subscribe.this, ListAnnouncement.class);
        startActivity(i);
        finish();
    }

    //this function to create user in firebase
    private void createUser(){

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        String name = nameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String age = ageET.getText().toString().trim();
        String location = locationET.getText().toString().trim();
        String phone = phoneET.getText().toString().trim();
        //we first check that all fields are not empty
        if(TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(phone)
        ){
            Toast.makeText(Subscribe.this, "You can not leave any field empty", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            //here we check for password is not less than 8 characters
        }else if(password.length() < 8){
            Toast.makeText(Subscribe.this, "Password must be more than 6 characters", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else{
            //here we create user auth by registering his email to get new id for user
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        UserModel userModel = new UserModel(
                                name, email, password, phone, location, Integer.parseInt(age)
                        );
                        String id1 = firebaseAuth.getCurrentUser().getUid();
                        //here we adding into firebase all user info

                        databaseReference.child("users").child(id1).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                @SuppressLint("StaticFieldLeak")
                                class SaveUserTask extends AsyncTask<Void, Void, Void> {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        userModel.setId(id1);
                                        //here we save user info in sqlite
                                        DataBase.getDataBase(getApplicationContext()).userDao().insertUser(userModel);
                                        return null;
                                    }
                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        progressDialog.dismiss();
                                        Toast.makeText(Subscribe.this, "User saved Offline", Toast.LENGTH_SHORT).show();
                                        goToHome();
                                    }
                                }
                                new SaveUserTask().execute();
                            }
                        });

                    }
                }
            });

        }
    }
}