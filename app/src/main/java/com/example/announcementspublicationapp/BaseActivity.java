package com.example.announcementspublicationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.announcementspublicationapp.interfaces.AddAnnouncement;
import com.example.announcementspublicationapp.interfaces.ListAnnouncement;
import com.example.announcementspublicationapp.interfaces.SearchAnnouncement;
import com.example.announcementspublicationapp.interfaces.ViewFavorite;
import com.example.announcementspublicationapp.interfaces.ViewProfile;

public class BaseActivity extends AppCompatActivity {


    //we use this activity to be a parent for all other activities in this way we have menu work for all activities
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                startActivity(new Intent(this, ViewProfile.class));
                finish();
                break;
            case R.id.menu_home:
                startActivity(new Intent(this, ListAnnouncement.class));
                finish();
                break;
            case R.id.menu_search:
                startActivity(new Intent(this, SearchAnnouncement.class));
                break;
            case R.id.menu_add_new:
                startActivity(new Intent(this, AddAnnouncement.class));
                break;
            case R.id.menu_my_favorites:
                startActivity(new Intent(this, ViewFavorite.class));
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;

    }
}