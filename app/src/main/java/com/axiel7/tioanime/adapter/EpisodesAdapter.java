package com.axiel7.tioanime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.model.Episode;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.AnimeViewHolder> {

    private List<Episode> animes;
    private int rowLayout;
    private Context context;
    private ItemClickListener mClickListener;
    private static final String IMAGE_URL_BASE_PATH="https://tioanime.com/uploads/thumbs/";
    private DateFormat sdf = SimpleDateFormat.getDateInstance();
    public EpisodesAdapter(List<Episode> animes, int rowLayout, Context context) {
        this.animes = animes;
        this.rowLayout = rowLayout;
        this.context = context;
    }
    //A view holder inner class where we get reference to the views in the layout using their ID
    public class AnimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout animeLayout;
        TextView animeTitle;
        TextView animeEpisode;
        TextView date;
        ImageView animeImage;
        AnimeViewHolder(View v) {
            super(v);
            animeLayout = v.findViewById(R.id.anime_layout);
            animeImage = v.findViewById(R.id.movie_image);
            animeImage.setClipToOutline(true);
            animeTitle = v.findViewById(R.id.title);
            animeEpisode = v.findViewById(R.id.episode_number);
            date = v.findViewById(R.id.date);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public EpisodesAdapter.AnimeViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new AnimeViewHolder(view);
    }
    @Override
    public void onBindViewHolder(AnimeViewHolder holder, final int position) {
        String image_url = IMAGE_URL_BASE_PATH + animes.get(position).getAnimeId() + ".jpg";
        String episode_text = "Episodio " + animes.get(position).getNumber();
        Date convertDate = new Date(animes.get(position).getCreatedAt()*1000L);
        String date = sdf.format(convertDate);
        Glide.with(context)
                .load(image_url)
                .placeholder(R.drawable.ic_tioanime_white)
                .error(R.drawable.ic_tioanime_white)
                .into(holder.animeImage);
        holder.date.setText(date);
        holder.animeEpisode.setText(episode_text);
    }

    @Override
    public int getItemCount() {
        return animes.size();
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