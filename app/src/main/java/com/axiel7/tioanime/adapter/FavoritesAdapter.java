package com.axiel7.tioanime.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.model.FavAnime;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.AnimeViewHolder> {

    private ArrayList<FavAnime> animes;
    private int rowLayout;
    private Context context;
    private ItemClickListener mClickListener;
    private static final String IMAGE_URL_BASE_PATH="https://tioanime.com/uploads/portadas/";

    public FavoritesAdapter(ArrayList<FavAnime> animes, int rowLayout, Context context) {
        this.animes = animes;
        this.rowLayout = rowLayout;
        this.context = context;
    }
    //A view holder inner class where we get reference to the views in the layout using their ID
    public class AnimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView animeTitle;
        ImageView animeImage;
        AnimeViewHolder(View v) {
            super(v);
            animeImage = v.findViewById(R.id.movie_image);
            animeImage.setClipToOutline(true);
            animeTitle = v.findViewById(R.id.title);

            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public FavoritesAdapter.AnimeViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new AnimeViewHolder(view);
    }
    @Override
    public void onBindViewHolder(AnimeViewHolder holder, final int position) {
        String image_url = IMAGE_URL_BASE_PATH + animes.get(position).getAnimeId() + ".jpg";
        Log.d("nomames:", image_url);
        Glide.with(context)
                .load(image_url)
                .placeholder(R.drawable.ic_tioanime_white)
                .error(R.drawable.ic_tioanime_white)
                .into(holder.animeImage);
        holder.animeTitle.setText(animes.get(position).getAnimeTitle());
    }

    @Override
    public int getItemCount() {
        return animes.size();
    }

    public String getAnimeTitle(int position) {
        return animes.get(position).getAnimeTitle();
    }
    public String getAnimePosterUrl(int position) {
        return IMAGE_URL_BASE_PATH + animes.get(position).getAnimeId() + ".jpg";
    }
    public int getAnimeId(int position) {
        return animes.get(position).getAnimeId();
    }
    public int getAnimeType(int position) {
        return animes.get(position).getAnimeType();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
