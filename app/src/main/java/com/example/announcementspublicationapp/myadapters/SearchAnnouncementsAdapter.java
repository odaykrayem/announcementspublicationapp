package com.example.announcementspublicationapp.myadapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.announcementspublicationapp.R;
import com.example.announcementspublicationapp.interfaces.DetailAnnouncement;
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;

import java.util.ArrayList;
import java.util.List;

public class SearchAnnouncementsAdapter extends RecyclerView.Adapter<SearchAnnouncementsAdapter.ViewHolder> {

        Context context;
        private List<AnnouncementModel> list;
        public SearchAnnouncementsAdapter(Context context, ArrayList<AnnouncementModel> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public SearchAnnouncementsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.item_announcement_search, parent, false);
            SearchAnnouncementsAdapter.ViewHolder viewHolder = new SearchAnnouncementsAdapter.ViewHolder(listItem);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchAnnouncementsAdapter.ViewHolder holder, int position) {

            AnnouncementModel item = list.get(position);

            holder.title.setText(item.getAnn_title());
            holder.description.setText(item.getAnn_description());
            holder.announcer.setText(item.getAnnouncer_data().getName());
            if(item.getImage_path() != null){
                if(checkInternetConnection(context)){
                    Glide.with(context)
                            .load(item.getImage_path())
                            .into(holder.image);
                }else{
                    holder.image.setImageBitmap(BitmapFactory.decodeFile(item.getImage_path()));
                }

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailAnnouncement.class);
                    i.putExtra("announcement", item);
                    context.startActivity(i);
                }
            });

        }

    @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title,description, announcer;
            public ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                this.title = itemView.findViewById(R.id.ann_title);
                this.description = itemView.findViewById(R.id.ann_description);
                this.announcer = itemView.findViewById(R.id.announcer);
                this.image = itemView.findViewById(R.id.img_container);
            }
        }
    public  boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }
    }

