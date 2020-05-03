package com.axiel7.tioanime.activity.ui.home;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.activity.ui.AnimeDetailsFragment;
import com.axiel7.tioanime.adapter.LatestAnimesAdapter;
import com.axiel7.tioanime.model.Category;
import com.axiel7.tioanime.model.LatestAnimesResponse;
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

public class LatestAnimesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LatestAnimesAdapter latestAnimesAdapter;
    private AnimeDetailsFragment animeDetailsFragment;
    private Cache cache;
    private static final String TAG = "nomames:";
    private static final String BASE_URL = "https://tioanime.com/api/";
    private static Retrofit retrofit = null;
    private List<Category> animes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_latest_animes, container, false);
        recyclerView = root.findViewById(R.id.recyclerview_latest_animes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        animeDetailsFragment = new AnimeDetailsFragment();
        animeDetailsFragment.setEnterTransition(new Slide(Gravity.END));
        animeDetailsFragment.setExitTransition(new Slide(Gravity.START));
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
        Call<LatestAnimesResponse> call = animeApiService.getLatestAnimes("recent", API_KEY);
        call.enqueue(new Callback<LatestAnimesResponse>() {
            @Override
            public void onResponse(Call<LatestAnimesResponse> call, Response<LatestAnimesResponse> response) {
                Log.d(TAG, call.request().toString());
                if (response.isSuccessful()) {
                    animes = response.body().getLatestAnimes();
                    latestAnimesAdapter = new LatestAnimesAdapter(animes, R.layout.list_item_anime_grid, getActivity());
                    latestAnimesAdapter.setClickListener((view, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("animeTypeInt", latestAnimesAdapter.getAnimeType(position));
                        bundle.putInt("animeId", latestAnimesAdapter.getAnimeId(position));
                        bundle.putString("animeTitle", latestAnimesAdapter.getAnimeTitle(position));
                        bundle.putString("animePosterUrl", latestAnimesAdapter.getAnimePosterUrl(position));
                        animeDetailsFragment.setArguments(bundle);
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.details_fragment_container, animeDetailsFragment)
                                .addToBackStack(null)
                                .commit();
                    });
                    latestAnimesAdapter.setOnBottomReachedListener(position -> Log.d("nomames", "limite de pag"));
                    recyclerView.setAdapter(latestAnimesAdapter);
                    Log.d(TAG, "Number of animes received: " + animes.size());
                }
            }
            @Override
            public void onFailure(Call<LatestAnimesResponse> call, Throwable throwable) {
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
