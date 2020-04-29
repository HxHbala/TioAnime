package com.axiel7.tioanime.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.model.LatestEpisode;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LatestEpisodesAdapter extends RecyclerView.Adapter<LatestEpisodesAdapter.AnimeViewHolder> {

    private List<LatestEpisode> animes;
    private int rowLayout;
    private Context context;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;
    private static final String IMAGE_URL_BASE_PATH="https://tioanime.com/uploads/thumbs/";
    private DateFormat sdf = SimpleDateFormat.getDateInstance();
    public LatestEpisodesAdapter(List<LatestEpisode> animes, int rowLayout, Context context) {
        this.animes = animes;
        this.rowLayout = rowLayout;
        this.context = context;
    }
    //A view holder inner class where we get reference to the views in the layout using their ID
    public class AnimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        LinearLayout animeLayout;
        TextView animeTitle;
        TextView animeEpisode;
        TextView date;
        //TextView animeDescription;
        //TextView rating;
        ImageView animeImage;
        AnimeViewHolder(View v) {
            super(v);
            animeLayout = v.findViewById(R.id.anime_layout);
            animeImage = v.findViewById(R.id.movie_image);
            animeImage.setClipToOutline(true);
            animeTitle = v.findViewById(R.id.title);
            animeEpisode = v.findViewById(R.id.episode_number);
            date = v.findViewById(R.id.date);
            //animeDescription = v.findViewById(R.id.description);
            //rating = v.findViewById(R.id.rating);
            v.setOnClickListener(this);
            v.setLongClickable(true);
            v.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) {
                return mLongClickListener.onItemLongClick(view, getAdapterPosition());
            }
            return false;
        }
    }
    @NonNull
    @Override
    public LatestEpisodesAdapter.AnimeViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new AnimeViewHolder(view);
    }
    @Override
    public void onBindViewHolder(AnimeViewHolder holder, final int position) {
        String image_url = IMAGE_URL_BASE_PATH + animes.get(position).getLatestAnimeId() + ".jpg";
        String episode_text = "Episodio " + animes.get(position).getLatestEpisodeNumber();
        Date convertDate = new Date(animes.get(position).getLatestCreatedAt()*1000L);
        String date = sdf.format(convertDate);
        Log.d("nomames:", image_url);
        Glide.with(context)
                .load(image_url)
                .placeholder(R.drawable.ic_tioanime_white)
                .error(R.drawable.ic_tioanime_white)
                .into(holder.animeImage);
        holder.animeTitle.setText(animes.get(position).getLatestTitle());
        holder.date.setText(date);
        holder.animeEpisode.setText(episode_text);
    }

    @Override
    public int getItemCount() {
        return animes.size();
    }

    public int getAnimeType(int position) {
        return animes.get(position).getLatestType();
    }
    public int getAnimeId(int position) {
        return animes.get(position).getLatestAnimeId();
    }
    public String getAnimeTitle(int position) {
        return animes.get(position).getLatestTitle();
    }
    public String getAnimePosterUrl(int position) {
        return "https://tioanime.com/uploads/portadas/" + animes.get(position).getLatestAnimeId() + ".jpg";
    }


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface  ItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

}
