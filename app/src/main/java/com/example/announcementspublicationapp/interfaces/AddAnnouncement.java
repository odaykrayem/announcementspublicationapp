package com.example.announcementspublicationapp.interfaces;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.announcementspublicationapp.BaseActivity;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import com.example.announcementspublicationapp.sqliteDatabase.DataBase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.UUID;

public class AddAnnouncement  extends BaseActivity {

    String title, description, location;
    EditText titleET, descriptionET, priceET, locationET, longitudeET, latitudeET;
    ImageView chooseImg;
    Button add;

    CustomDialog customDialog;

    long price;
    float longitude, latitude;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 100;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_announcement_layout);
        titleET = findViewById(R.id.ann_title);
        descriptionET = findViewById(R.id.ann_description);
        priceET  = findViewById(R.id.ann_price);
        locationET =findViewById(R.id.ann_location);
        longitudeET = findViewById(R.id.ann_longitude);
        latitudeET = findViewById(R.id.ann_latitude);
        chooseImg = findViewById(R.id.choose_img);
        add = findViewById(R.id.add);


        customDialog = new CustomDialog(this) {
            @Override
            public void onButtonClicked(double longitude, double latitude, String location) {
                longitudeET.setText(String.valueOf(longitude));
                latitudeET.setText(String.valueOf(latitude));
                locationET.setText(location);
                customDialog.dismiss();
            }
        };

        locationET.setOnClickListener(v->{
            customDialog.show();
        });
        //we use this to choose image from device
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    imageUri = uri;
                    chooseImg.setImageBitmap(BitmapFactory.decodeFile(getFilePath(uri)));
                });
        chooseImg.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(AddAnnouncement.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                mGetContent.launch("image/*");
            }
        });
        //add button functionality
        add.setOnClickListener(v -> {
            if(checkInternetConnection(AddAnnouncement.this)){
                title = titleET.getText().toString();
                description = descriptionET.getText().toString();
                price = Long.parseLong(priceET.getText().toString());
                location = locationET.getText().toString();
                longitude = Float.parseFloat(longitudeET.getText().toString());
                latitude = Float.parseFloat(latitudeET.getText().toString());
                uploadImage(imageUri);
                saveToSQLite(imageUri);
            }else{
                Toast.makeText(AddAnnouncement.this, "You cant upload in offline mode", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //we use this function to get image path in device to display it in offline mode
    public String getFilePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //upload image to firebase storage and the upload the whole announcement to real time database
    private void uploadImage(Uri uri) {
        if (uri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference ref = storageRef .child("images/" + UUID.randomUUID().toString());
            ref.putFile(uri)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                Toast.makeText(AddAnnouncement.this,"Image Uploaded successfully",Toast.LENGTH_SHORT) .show();
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri1) {
                                        Uri downloadUrl = uri1;
                                        if (downloadUrl != null) {
                                            AnnouncementModel announcementModel = new AnnouncementModel(
                                                    downloadUrl.toString(),
                                                    title,
                                                    price,
                                                    description,
                                                    location,
                                                    longitude,
                                                    latitude,
                                                    UUID.randomUUID().toString(),
                                                    DataBase.getDataBase(AddAnnouncement.this).userDao().getUser()
                                            );
                                            //upload the whole announcement to real time database
                                            DatabaseReference databaseReference;
                                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                                            databaseReference = db.getReference();
                                            databaseReference.child("announcements").child(announcementModel.getFirebaseId()).setValue(announcementModel).addOnSuccessListener(unused -> {
                                                Toast.makeText(AddAnnouncement.this, "Data Uploaded to firebase successfully", Toast.LENGTH_SHORT).show();

                                            });
                                        }
                                    }
                                });
                            })
                    .addOnFailureListener(e -> Toast.makeText(AddAnnouncement.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show());        } else {
            Toast.makeText(this, "operation failed", Toast.LENGTH_SHORT).show();
        }
    }

    //save announcement to sqlite database
    private void saveToSQLite(Uri uri) {
        AnnouncementModel announcementModel = new AnnouncementModel(
                getFilePath(uri),
                title,
                price,
                description,
                location,
                longitude,
                latitude,
                DataBase.getDataBase(AddAnnouncement.this).userDao().getUser().getName()
        );
        @SuppressLint("StaticFieldLeak")
        class SaveUserTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                DataBase.getDataBase(getApplicationContext()).announcementDao().insertAnnouncement(announcementModel);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(AddAnnouncement.this, "data saved in sqlite database successfully", Toast.LENGTH_SHORT).show();
            }
        }
        new SaveUserTask().execute();
    }

    //when click on back in device
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    // function to check internet connection availability
    public boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }
}