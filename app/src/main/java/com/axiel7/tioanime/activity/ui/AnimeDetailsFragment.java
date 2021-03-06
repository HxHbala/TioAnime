package com.axiel7.tioanime.activity.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.activity.VideoActivity;
import com.axiel7.tioanime.activity.ui.favorites.FavoritesFragment;
import com.axiel7.tioanime.adapter.EpisodesAdapter;
import com.axiel7.tioanime.model.Anime;
import com.axiel7.tioanime.model.AnimeResponse;
import com.axiel7.tioanime.model.Downloads;
import com.axiel7.tioanime.model.Episode;
import com.axiel7.tioanime.model.EpisodeResponse;
import com.axiel7.tioanime.model.FavAnime;
import com.axiel7.tioanime.model.Genre;
import com.axiel7.tioanime.model.JikanResponse;
import com.axiel7.tioanime.rest.AnimeApiService;
import com.axiel7.tioanime.utils.TinyDB;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.axiel7.tioanime.activity.MainActivity.API_KEY;
import static com.axiel7.tioanime.activity.MainActivity.isNetworkAvailable;

public class AnimeDetailsFragment extends Fragment {

    private Anime anime;
    private List<Episode> episodes;
    private int animeId, animeType, malId;
    private String animeTitle, image_url;
    private TinyDB tinyDB;
    private ArrayList<Integer> favAnimesIds = new ArrayList<>();
    private ArrayList<Integer> watchedEpisodesIds = new ArrayList<>();
    private ArrayList<Object> animesObject;
    private ArrayList<FavAnime> favAnimes = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChipGroup chipGroup;
    private Chip scoreChip, typeChip, seasonChip;
    private TextView synopsis;
    private EpisodesAdapter episodesAdapter;
    private Cache cache;
    private static final String TAG = "nomames:";
    private static String FINAL_URL;
    private static Retrofit retrofit = null;
    private static Retrofit retrofitMal = null;
    private final static String BASE_URL = "https://tioanime.com/api/";
    private final static String JIKAN_URL = "https://api.jikan.moe/v3/anime/";

    public void onCreate (Bundle savedInstanceState) {
        assert getArguments() != null;
        animeId = getArguments().getInt("animeId", 1);
        animeType = getArguments().getInt("animeTypeInt", 0);
        switch (animeType) {
            case 0:
                FINAL_URL = BASE_URL + "tv/";
                break;
            case 1:
                FINAL_URL = BASE_URL + "movies/";
                break;
            case 2:
                FINAL_URL = BASE_URL + "ovas/";
                break;
            case 3:
                FINAL_URL = BASE_URL + "specials/";
                break;
        }
        animeTitle = getArguments().getString("animeTitle");
        image_url = getArguments().getString("animePosterUrl");

        tinyDB = new TinyDB(requireActivity());
        animesObject = tinyDB.getListObject("favAnimes", FavAnime.class);
        for (Object object : animesObject) {
            favAnimes.add((FavAnime)object);
        }
        for (int i=0; i<favAnimes.size(); i++) {
            favAnimesIds.add(favAnimes.get(i).getAnimeId());
        }
        watchedEpisodesIds = tinyDB.getListInt("watchedEpisodesIds");

        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_anime_details, container, false);

        //setup titles
        TextView mainTitle = root.findViewById(R.id.main_title);
        mainTitle.setText(animeTitle);

        TextView episodesTitle = root.findViewById(R.id.episodes_text);
        episodesTitle.setOnClickListener(this::reverseOrder);

        chipGroup = root.findViewById(R.id.chip_group_genres);
        scoreChip = root.findViewById(R.id.score_chip);
        typeChip = root.findViewById(R.id.type_chip);
        seasonChip = root.findViewById(R.id.season_chip);
        synopsis = root.findViewById(R.id.synopsis);
        synopsis.setOnClickListener(v -> {
            if (synopsis.getMaxLines()==6) {
                synopsis.setMaxLines(Integer.MAX_VALUE);
            }
            else if (synopsis.getMaxLines()==Integer.MAX_VALUE){
                synopsis.setMaxLines(6);
            }
        });

        //load anime poster
        ImageView animePoster = root.findViewById(R.id.imageViewCollapsing);
        Glide.with(this)
                .load(image_url)
                .into(animePoster);

        //add or remove favorite
        FloatingActionButton fab = root.findViewById(R.id.fab);

        if (favAnimesIds.contains(animeId)) {
            fab.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_favorite_white_24dp));
        }
        fab.setOnClickListener(v -> {
            if (favAnimesIds.contains(animeId)) {
                int favoriteIndex = favAnimesIds.indexOf(animeId);
                animesObject.remove(favoriteIndex);
                favAnimesIds.remove(favoriteIndex);
                favAnimes.remove(favoriteIndex);
                tinyDB.putListObject("favAnimes", animesObject);
                fab.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                Fragment favoriteFragment = getParentFragmentManager().findFragmentById(R.id.nav_host_fragment);
                if (favoriteFragment instanceof FavoritesFragment) {
                    ((FavoritesFragment)favoriteFragment).getFavoritesDatabase(true);
                }
                //Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
            }
            else {
                FavAnime favAnime = new FavAnime();
                favAnime.setAnimeId(animeId);
                favAnime.setAnimeTitle(animeTitle);
                favAnime.setAnimeType(animeType);
                favAnime.setAnimePosterUrl(image_url);
                favAnime.setOrderIndex(animesObject.size() + 1);
                animesObject.add(favAnime);
                favAnimesIds.add(animeId);
                favAnimes.add(favAnime);
                tinyDB.putListObject("favAnimes", animesObject);
                fab.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_favorite_white_24dp));
                //Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();
            }
        });

        //setup episodes list
        recyclerView = root.findViewById(R.id.recycler_episodes);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        if (isAdded()) {
            connectAndGetApiData();
        }

        return root;
    }
    private void connectAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(FINAL_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClientCached())
                    .build();
        }
        AnimeApiService animeApiService = retrofit.create(AnimeApiService.class);
        Call<AnimeResponse> call = animeApiService.getAnime(FINAL_URL + animeId, API_KEY);
        call.enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                Log.d(TAG, call.request().toString());
                if (response.isSuccessful()) {
                    anime = response.body().getAnimeData();
                    episodes = anime.getEpisodes();
                    episodesAdapter = new EpisodesAdapter(episodes, watchedEpisodesIds, R.layout.list_item_episode, requireActivity());

                    episodesAdapter.setClickListener((view, position) -> {
                        int episodeId = episodes.get(position).getEpisodeId();
                        updateWatchList(false, episodeId, view);
                        Intent intent = new Intent(requireActivity(), VideoActivity.class);
                        intent.putExtra("episodeId", episodeId);
                        startActivity(intent);
                    });
                    episodesAdapter.setLongClickListener(((view, position) -> {
                        int episodeId = episodes.get(position).getEpisodeId();
                        openOptionsDialog(view, episodeId);
                        return true;
                    }));
                    recyclerView.setAdapter(episodesAdapter);
                    List<Genre> animeGenres = anime.getGenres();
                    for (int i = 0; i<animeGenres.size(); i++) {
                        Chip chip = new Chip(chipGroup.getContext());
                        chip.setText(Jsoup.parse(animeGenres.get(i).getGenreTitle()).text());
                        chipGroup.addView(chip);
                    }
                    switch (anime.getType()) {
                        case 0:
                            typeChip.setText(" TV");
                            break;
                        case 1:
                            typeChip.setText("Película");
                            break;
                        case 2:
                            typeChip.setText("OVA");
                            break;
                        case 3:
                            typeChip.setText("Especial");
                            break;
                    }
                    String year = anime.getStartDate();
                    String season;
                    switch (anime.getSeason()) {
                        case 4:
                            season = "Invierno " + year;
                            seasonChip.setText(season);
                            seasonChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorWinter)));
                            break;
                        case 1:
                            season = "Primavera " + year;
                            seasonChip.setText(season);
                            seasonChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorSpring)));
                            break;
                        case 3:
                            season = "Verano " + year;
                            seasonChip.setText(season);
                            seasonChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorSummer)));
                            break;
                        case 2:
                            season = "Otoño " + year;
                            seasonChip.setText(season);
                            seasonChip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAutumn)));
                            break;
                    }
                    synopsis.setText(Jsoup.parse(anime.getSynopsis()).text());
                    malId = anime.getMalId();
                    setScore(JIKAN_URL + malId);
                }
            }
            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                Toast.makeText(requireActivity(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setScore(String url) {
        if (retrofitMal == null) {
            retrofitMal = new Retrofit.Builder()
                    .baseUrl(JIKAN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClientCached())
                    .build();
        }
        AnimeApiService animeApiService = retrofitMal.create(AnimeApiService.class);
        Call<JikanResponse> jikanResponseCall = animeApiService.getMalData(url);
        jikanResponseCall.enqueue(new Callback<JikanResponse>() {
            @Override
            public void onResponse(Call<JikanResponse> call, Response<JikanResponse> response) {
                Log.d(TAG, call.request().toString());
                if (response.isSuccessful()) {
                    float score = response.body().getScoreMal();
                    scoreChip.setText(String.valueOf(score));
                }
            }

            @Override
            public void onFailure(Call<JikanResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
    private void getDownloads(int episodeId) {
        if (retrofit!=null) {
            AnimeApiService animeApiService = retrofit.create(AnimeApiService.class);
            Call<EpisodeResponse> call = animeApiService.getEpisode(BASE_URL + "episodes/" + episodeId, API_KEY);
            call.enqueue(getEpisodeDownloadsCallback);
        }
    }
    private Callback<EpisodeResponse> getEpisodeDownloadsCallback = new Callback<EpisodeResponse>() {
        @Override
        public void onResponse(Call<EpisodeResponse> call, Response<EpisodeResponse> response) {
            Log.d(TAG, call.request().toString());
            if (response.isSuccessful()) {
                Episode episodeData = response.body().getEpisodeData();
                List<Downloads> downloadsList = episodeData.getDownloads();
                ArrayList<String> serverNames = new ArrayList<>();
                ArrayList<String> downloadLinks = new ArrayList<>();
                for (int i=0; i<downloadsList.size(); i++) {
                    serverNames.add(downloadsList.get(i).getServerName());
                    downloadLinks.add(downloadsList.get(i).getDownloadUrl());
                }
                openDownloadOptions(serverNames, downloadLinks);
            }
        }
        @Override
        public void onFailure(Call<EpisodeResponse> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(requireActivity(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
    };
    private void openOptionsDialog(View view, int episodeId) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme);
        CharSequence[] options = {"Des/marcar como visto", "Descargar"};
        builder.setItems(options, ((dialog, which) -> {
            switch (which) {
                case 0:
                    if (view.getAlpha()==1) {
                        updateWatchList(false, episodeId, view);
                    }
                    else {updateWatchList(true, episodeId, view);}
                    break;
                case 1:
                    getDownloads(episodeId);
                    break;
            }
        }));
        AlertDialog optionsDialog = builder.create();
        optionsDialog.show();
    }
    private void openDownloadOptions(ArrayList<String> serverNames, ArrayList<String> downloadLinks) {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme);
        CharSequence[] names = serverNames.toArray(new CharSequence[0]);
        builder.setItems(names, (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(downloadLinks.get(which)));
            startActivity(intent);
        });
        AlertDialog downloadsDialog = builder.create();
        downloadsDialog.show();
    }
    private void updateWatchList(boolean remove, int episodeId, View view) {
        if (remove) {
            view.setAlpha(1);
            watchedEpisodesIds.remove(Integer.valueOf(episodeId));
            tinyDB.putListInt("watchedEpisodesIds", watchedEpisodesIds);
        }
        else {
            view.setAlpha((float) 0.5);
            watchedEpisodesIds.add(episodeId);
            tinyDB.putListInt("watchedEpisodesIds", watchedEpisodesIds);
        }
    }
    private OkHttpClient okHttpClientCached() {
        //File httpCacheDirectory = new File(getApplicationContext().getCacheDir(), "responses");
        int cacheSize = 50 * 1024 * 1024; // 50 MiB
        cache = new Cache(requireActivity().getCacheDir(), cacheSize);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();
    }
    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
        okhttp3.Response originalResponse = chain.proceed(chain.request());
        if (getActivity() != null && isNetworkAvailable(getActivity())) {
            int maxAge = 1800; // read from cache for 30 minutes
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    };
    private void reverseOrder(View view) {
        if (episodes!=null) {
            Collections.reverse(episodes);
            episodesAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cache != null) {
            try {
                cache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("nomames",e.toString());
            }
        }
    }
}
