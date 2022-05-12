package com.example.announcementspublicationapp.myadapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.interfaces.DetailAnnouncement;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import com.example.announcementspublicationapp.mymodels.FavoriteModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.announcementspublicationapp.sqliteDatabase.DataBase;

public class ListAnnouncementsAdapter extends RecyclerView.Adapter<ListAnnouncementsAdapter.ViewHolder> {

    Context context;
    private List<AnnouncementModel> list;

    public ListAnnouncementsAdapter(Context context, ArrayList<AnnouncementModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListAnnouncementsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_announcement, parent, false);
        ListAnnouncementsAdapter.ViewHolder viewHolder = new ListAnnouncementsAdapter.ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListAnnouncementsAdapter.ViewHolder holder, int position) {

        AnnouncementModel item = list.get(position);
        holder.title.setText(item.getAnn_title());
        holder.description.setText(item.getAnn_description());
        if (checkInternetConnection(context)) {
            holder.announcer.setText(item.getAnnouncer_data().getName());
        } else {
            holder.announcer.setText("You");
        }
        if (item.getImage_path() != null) {
            if (checkInternetConnection(context)) {
                Glide.with(context)
                        .load(item.getImage_path())
                        .into(holder.image);
            } else {
                holder.image.setImageBitmap(BitmapFactory.decodeFile(item.getImage_path()));
            }

        }

        DatabaseReference databaseReference;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();
        databaseReference.child("favorites").child(DataBase.getDataBase(context).userDao().getUser().getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s1 : snapshot.getChildren()) {
                    if (item.equals(s1.getValue(AnnouncementModel.class))) {
                        holder.addItemToFavorite.setText("remove from favourites");
                        return;
                    }
                }
                holder.addItemToFavorite.setText("add to favourites");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //handle add to favorite clicks
        //when user click on this button we add this announcement to favorites
        //or if it is in favorites already we remove it
        holder.addItemToFavorite.setOnClickListener(v -> {
                  if(checkInternetConnection(context)){
                      DatabaseReference databaseReference1;
                      FirebaseDatabase db1 = FirebaseDatabase.getInstance();
                      if (holder.addItemToFavorite.getText().equals("add to favourites")) {
                          databaseReference1 = db1.getReference();
                          databaseReference1.child("favorites").child(DataBase.getDataBase(context).userDao().getUser().getId()).child(item.getFirebaseId()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                  Toast.makeText(context, "item added to favorites successfully", Toast.LENGTH_SHORT).show();
                                  holder.addItemToFavorite.setText("remove from favourites");
                                  @SuppressLint("StaticFieldLeak")
                                  class SaveUserTask extends AsyncTask<Void, Void, Void> {
                                      @Override
                                      protected Void doInBackground(Void... params) {
                                          FavoriteModel favoriteModel = new FavoriteModel(
                                                  item.getImage_path(),
                                                  item.getAnn_title(),
                                                  item.getAnn_price(),
                                                  item.getAnn_description(),
                                                  item.getAnn_location(),
                                                  item.getAnn_longitude(),
                                                  item.getAnn_latitude(),
                                                  item.getAnnouncer_name(),
                                                  item.getFirebaseId()
                                          );
                                          DataBase.getDataBase(context).favoriteDao().insertFavorite(favoriteModel);
                                          return null;
                                      }

                                      @Override
                                      protected void onPostExecute(Void aVoid) {
                                          super.onPostExecute(aVoid);
                                          Toast.makeText(context, "favorite saved in sqlite successfully", Toast.LENGTH_SHORT).show();
                                      }
                                  }
                                  new SaveUserTask().execute();
                              }
                          });
                      } else {
                          databaseReference1 = db1.getReference();
                          databaseReference1.child("favorites").child(DataBase.getDataBase(context).userDao().getUser().getId()).child(item.getFirebaseId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                  Toast.makeText(context, "item removed from online and offline storage successfully", Toast.LENGTH_SHORT).show();
                                  holder.addItemToFavorite.setText("add to favourites");
                                  DataBase.getDataBase(context).favoriteDao().deleteById(item.getFirebaseId());

                              }
                          });
                      }
                  }else{
                      Toast.makeText(context, "you are offline", Toast.LENGTH_SHORT).show();
                  }
                }
        );
        //when user click on item itself we show him announcement details
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, DetailAnnouncement.class);
            i.putExtra("announcement", item);
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description, announcer;
        public ImageView image;
        public Button addItemToFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.ann_title);
            this.description = itemView.findViewById(R.id.ann_description);
            this.announcer = itemView.findViewById(R.id.announcer);
            this.image = itemView.findViewById(R.id.img_container);
            this.addItemToFavorite = itemView.findViewById(R.id.add_to_favorite);
        }
    }

    public boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }
}

