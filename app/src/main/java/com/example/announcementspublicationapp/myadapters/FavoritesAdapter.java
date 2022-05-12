package com.example.announcementspublicationapp.myadapters;

import android.content.Context;
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
import com.example.announcementspublicationapp.mymodels.AnnouncementModel;
import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    //we use adapter to handle fill items in recyclerview with information
    Context context;
    private List<AnnouncementModel> myList;

    public FavoritesAdapter(Context context, ArrayList<AnnouncementModel> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_favorite, parent, false);
        FavoritesAdapter.ViewHolder viewHolder = new FavoritesAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {

        AnnouncementModel item = myList.get(position);

        holder.title.setText(item.getAnn_title());
        holder.description.setText(item.getAnn_description());
        holder.announcer.setText(item.getAnnouncer_data().getName());
        //if there is no internet connection we use image path on device instead of uri and we dont need glide to display it
        //glide is a library that load image from server
        if (item.getImage_path() != null) {
            if (checkInternetConnection(context)) {
                Glide.with(context)
                        .load(item.getImage_path())
                        .into(holder.image);
            } else {
                holder.image.setImageBitmap(BitmapFactory.decodeFile(item.getImage_path()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description, announcer;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.ann_title);
            this.description = itemView.findViewById(R.id.ann_description);
            this.announcer = itemView.findViewById(R.id.announcer);
            this.image = itemView.findViewById(R.id.img_container);
        }
    }
    public boolean checkInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
    }
}

