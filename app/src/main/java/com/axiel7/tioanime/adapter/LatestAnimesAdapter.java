package com.axiel7.tioanime.adapter;

import android.annotation.SuppressLint;
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
import com.axiel7.tioanime.model.Category;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LatestAnimesAdapter extends RecyclerView.Adapter<LatestAnimesAdapter.AnimeViewHolder> {

    private List<Category> animes;
    private int rowLayout;
    private Context context;
    private ItemClickListener mClickListener;
    private OnBottomReachedListener onBottomReachedListener;
    private static final String IMAGE_URL_BASE_PATH="https://tioanime.com/uploads/portadas/";

    public LatestAnimesAdapter(List<Category> animes, int rowLayout, Context context) {
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
        }
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public LatestAnimesAdapter.AnimeViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new AnimeViewHolder(view);
    }
    @Override
    @SuppressLint("SimpleDateFormat")
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, final int position) {
        String image_url = IMAGE_URL_BASE_PATH + animes.get(position).getCategoryAnimeId() + ".jpg";
        if (holder.date.getVisibility() == View.VISIBLE) {
            String stringDate = animes.get(position).getSource().getCategoryStartDate();
            String day = "Sin fecha";
            try {
                if (stringDate!=null) {
                    Date date = new SimpleDateFormat("yyyy-M-dd").parse(stringDate);
                    assert date != null;
                    day = new SimpleDateFormat("EEEE").format(date);
                    day = day.substring(0,1).toUpperCase() + day.substring(1); //capitalize first letter
                }
            } catch (ParseException e) {
                Log.d("nomamesdate", e.toString());
            }
            holder.date.setText(day);
        }
        Log.d("nomames:", image_url);
        Glide.with(context)
                .load(image_url)
                .placeholder(R.drawable.ic_tioanime_white)
                .error(R.drawable.ic_tioanime_white)
                .into(holder.animeImage);
        holder.animeTitle.setText(animes.get(position).getSource().getCategoryMainTitle());

        if (position == animes.size() - 1) {
            onBottomReachedListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return animes.size();
    }

    public String getAnimeTitle(int position) {
        return animes.get(position).getSource().getCategoryMainTitle();
    }
    public String getAnimePosterUrl(int position) {
        return IMAGE_URL_BASE_PATH + animes.get(position).getCategoryAnimeId() + ".jpg";
    }
    public String getAnimeSynopsis(int position) {
        return animes.get(position).getSource().getCategorySynopsis();
    }
    public int getAnimeId(int position) {
        return animes.get(position).getCategoryAnimeId();
    }

    public int getAnimeType(int position) {
        return animes.get(position).getSource().getCategoryType();
    }
    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    //set bottom reached listener
    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
