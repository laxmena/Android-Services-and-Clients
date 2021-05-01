package com.laxmena.musicclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicLibraryAdapter extends RecyclerView.Adapter<MusicLibraryAdapter.ViewHolder> {
    private String TAG = "MusicLibraryAdapter";
    private ArrayList<String> songTitles, artistNames;
    private ArrayList<Bitmap> thumbnails;
    private RVClickListener listener;

    public MusicLibraryAdapter(ArrayList<String> songTitles,
                               ArrayList<String> artistNames,
                               ArrayList<Bitmap> thumbnails,
                               RVClickListener listener) {
        this.songTitles = songTitles;
        this.artistNames = artistNames;
        this.thumbnails = thumbnails;
        this.listener = listener;

        System.out.println("MusicLibraryAdapter: " + songTitles.get(0));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listView = inflater.inflate(R.layout.music_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listView, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
        holder.setArtistName(this.artistNames.get(index));
        holder.setSongTitle(this.songTitles.get(index));
        holder.setThumbnail(this.thumbnails.get(index));
    }

    @Override
    public int getItemCount() {
        return songTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView thumbnail;
        public TextView songTitle;
        public TextView artistName;
        private RVClickListener listener;

        public ViewHolder(View view, RVClickListener listener) {
            super(view);
            thumbnail = view.findViewById(R.id.songCover);
            songTitle = view.findViewById(R.id.songTitle);
            artistName = view.findViewById(R.id.artistName);
            this.listener = listener;
            view.setOnClickListener(this);
        }

        public void setSongTitle(String songTitle) {
            this.songTitle.setText(songTitle);
        }

        public void setArtistName(String artistName) {
            this.artistName.setText(artistName);
        }

        public void setThumbnail(Bitmap thumbnail) {
            if(thumbnail != null) {
                this.thumbnail.setImageBitmap(thumbnail);
            } else {
                int defImg = getAdapterPosition()%2 == 0?
                                R.drawable.ham : R.drawable.heart;
                    this.thumbnail.setImageResource(defImg);
            }
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
