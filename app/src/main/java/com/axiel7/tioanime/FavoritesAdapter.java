package com.axiel7.tioanime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Map<String, String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    FavoritesAdapter(Context context, Map<String, String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.favorites_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int i = 0;
        for (Map.Entry<String, String> entry : mData.entrySet()) {
            if(position == i){
                String value = entry.getValue(); //animeTitle
                holder.myTextView.setText(value);
                if (entry.getKey().contains("hentai")) {
                    holder.myImageView.setImageResource(R.drawable.tiohentai_fav);
                }
                else {
                    holder.myImageView.setImageResource(R.drawable.tioanime_fav);
                }
                break;
            }
            i++;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myTextView;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.favRecyclerText);
            myImageView = itemView.findViewById(R.id.favRecyclerImg);
            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this::onLongClick);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {
                return mClickListener.onItemLongClick(view, getAdapterPosition());
            }
            return false;
        }
    }

    // convenience method for getting data at click position
    String getItem(int position) {
        int i = 0;
        for (Map.Entry<String, String> entry : mData.entrySet()) {
            if(position == i){
                return entry.getKey(); //animeUrl
            }
            i++;
        }
        return "https://tioanime.com";
    }
    String getItemName(int position) {
        int i = 0;
        for (Map.Entry<String, String> entry : mData.entrySet()) {
            if(position == i){
                return entry.getValue(); //animeTitle
            }
            i++;
        }
        return "anime";
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }
}

