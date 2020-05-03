package com.axiel7.tioanime.activity.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.activity.VideoActivity;
import com.axiel7.tioanime.activity.ui.AnimeDetailsFragment;
import com.axiel7.tioanime.adapter.LatestEpisodesAdapter;
import com.axiel7.tioanime.model.LatestEpisode;
import com.axiel7.tioanime.model.LatestEpisodesResponse;
import com.axiel7.tioanime.rest.AnimeApiService;

import java.io.IOException;
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

public class LatestEpisodesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LatestEpisodesAdapter latestEpisodesAdapter;
    private AnimeDetailsFragment animeDetailsFragment;
    private Cache cache;
    private static final String TAG = "nomames:";
    private static final String BASE_URL = "https://tioanime.com/api/";
    private static Retrofit retrofit = null;
    private List<LatestEpisode> animes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_latest_episodes, container, false);
        recyclerView = root.findViewById(R.id.recyclerview_latest_episodes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animeDetailsFragment = new AnimeDetailsFragment();
        animeDetailsFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        animeDetailsFragment.setExitTransition(new Slide(Gravity.TOP));
        connectAndGetApiData();

        return root;
    }
    private void connectAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClientCached())
                    .build();
        }
        AnimeApiService animeApiService = retrofit.create(AnimeApiService.class);
        Call<LatestEpisodesResponse> call = animeApiService.getLatestEpisodes(API_KEY);
        call.enqueue(new Callback<LatestEpisodesResponse>() {
            @Override
            public void onResponse(Call<LatestEpisodesResponse> call, Response<LatestEpisodesResponse> response) {
                Log.d(TAG, call.request().toString());
                if (response.isSuccessful()) {
                    animes = response.body().getData();
                    latestEpisodesAdapter = new LatestEpisodesAdapter(animes, R.layout.list_item_anime, getActivity());
                    latestEpisodesAdapter.setClickListener((view, position) -> {
                        Intent intent = new Intent(getActivity(), VideoActivity.class);
                        intent.putExtra("episodeId", animes.get(position).getLatestEpisodeId());
                        requireActivity().startActivity(intent);
                    });
                    latestEpisodesAdapter.setLongClickListener(((view, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("animeTypeInt", latestEpisodesAdapter.getAnimeType(position));
                        bundle.putInt("animeId", latestEpisodesAdapter.getAnimeId(position));
                        bundle.putString("animeTitle", latestEpisodesAdapter.getAnimeTitle(position));
                        bundle.putString("animePosterUrl", latestEpisodesAdapter.getAnimePosterUrl(position));
                        animeDetailsFragment.setArguments(bundle);
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.details_fragment_container, animeDetailsFragment)
                                .addToBackStack(null)
                                .commit();
                        return true;
                    }));
                    recyclerView.setAdapter(latestEpisodesAdapter);
                    Log.d(TAG, "Number of animes received: " + animes.size());
                }
            }
            @Override
            public void onFailure(Call<LatestEpisodesResponse> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                Toast.makeText(getActivity(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private OkHttpClient okHttpClientCached() {
        //File httpCacheDirectory = new File(requireActivity().getCacheDir(), "responses");
        int cacheSize = 20 * 1024 * 1024; // 20 MiB
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
