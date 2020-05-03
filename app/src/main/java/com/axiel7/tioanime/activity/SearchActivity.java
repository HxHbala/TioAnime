package com.axiel7.tioanime.activity;

import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.activity.ui.AnimeDetailsFragment;
import com.axiel7.tioanime.adapter.SearchAdapter;
import com.axiel7.tioanime.model.Category;
import com.axiel7.tioanime.model.LatestAnimesResponse;
import com.axiel7.tioanime.rest.AnimeApiService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

public class SearchActivity extends AppCompatActivity {

    private String searchTerm;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private AnimeDetailsFragment animeDetailsFragment;
    private int page;
    private int pageLimit;
    private Cache cache;
    private static final String TAG = "nomames:";
    private static final String BASE_URL = "https://tioanime.com/api/";
    private static Retrofit retrofit = null;
    private List<Category> animes;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin =
                    insets.getSystemWindowInsetTop();
            return insets.consumeSystemWindowInsets();
        });

        recyclerView = findViewById(R.id.recyclerview_search_results);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        animeDetailsFragment = new AnimeDetailsFragment();
        animeDetailsFragment.setEnterTransition(new Slide(Gravity.END));
        animeDetailsFragment.setExitTransition(new Slide(Gravity.START));
        page = 1;
        //connectAndGetApiData();
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
        Call<LatestAnimesResponse> call = animeApiService.getBrowser(searchTerm, page, API_KEY);
        call.enqueue(getFirstAnimesCallback);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        searchView.requestFocus();
        ImageView closeBtn = searchView.findViewById(R.id.search_close_btn);
        closeBtn.setEnabled(false);
        closeBtn.setVisibility(View.INVISIBLE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTerm = query;
                page = 1;
                connectAndGetApiData();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    closeBtn.setEnabled(false);
                    closeBtn.setVisibility(View.INVISIBLE);
                }
                else {
                    closeBtn.setEnabled(true);
                    closeBtn.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private Callback<LatestAnimesResponse> getFirstAnimesCallback = new Callback<LatestAnimesResponse>() {
        @Override
        public void onResponse(Call<LatestAnimesResponse> call, Response<LatestAnimesResponse> response) {
            Log.d(TAG, call.request().toString());
            if (response.isSuccessful()) {
                animes = response.body().getLatestAnimes();
                page = response.body().getPageInfo().getCurrentPage();
                pageLimit = response.body().getPageInfo().getTotalPages();
                searchAdapter = new SearchAdapter(animes, R.layout.list_item_anime_grid, getApplicationContext());
                searchAdapter.setClickListener((view, position) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("animeTypeInt", searchAdapter.getAnimeType(position));
                    bundle.putInt("animeId", searchAdapter.getAnimeId(position));
                    bundle.putString("animeTitle", searchAdapter.getAnimeTitle(position));
                    bundle.putString("animePosterUrl", searchAdapter.getAnimePosterUrl(position));
                    animeDetailsFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.details_fragment_container, animeDetailsFragment)
                            .addToBackStack(null)
                            .commit();
                });

                searchAdapter.setOnBottomReachedListener(position -> {
                    page = page + 1;
                    loadMoreItems();
                });
                recyclerView.setAdapter(searchAdapter);
                Log.d(TAG, "Number of animes received: " + animes.size());
            }
        }

        @Override
        public void onFailure(Call<LatestAnimesResponse> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(getApplicationContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
    };
    private void loadMoreItems() {
        if (page <= pageLimit) {
            Log.d("nomamespage", String.valueOf(page));
            if (retrofit!=null) {
                AnimeApiService animeApiService = retrofit.create(AnimeApiService.class);
                //switch was not working properly with strings
                Call<LatestAnimesResponse> getMoreCall = animeApiService.getBrowser(searchTerm, page, API_KEY);
                getMoreCall.enqueue(getMoreAnimesCallback);
            }
        }
    }
    private Callback<LatestAnimesResponse> getMoreAnimesCallback = new Callback<LatestAnimesResponse>() {
        @Override
        public void onResponse(Call<LatestAnimesResponse> call, Response<LatestAnimesResponse> response) {
            Log.e(TAG, call.request().toString());
            if (response.isSuccessful()) {
                List<Category> moreAnimes = response.body().getLatestAnimes();
                page = response.body().getPageInfo().getCurrentPage();
                animes.addAll(moreAnimes);
                searchAdapter.notifyDataSetChanged();
                Log.d(TAG, "Number of animes received: " + moreAnimes.size());
            }
        }
        @Override
        public void onFailure(Call<LatestAnimesResponse> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(getApplicationContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
    };
    private OkHttpClient okHttpClientCached() {
        int cacheSize = 20 * 1024 * 1024; // 20 MiB
        cache = new Cache(getCacheDir(), cacheSize);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();
    }
    private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
        okhttp3.Response originalResponse = chain.proceed(chain.request());
        if (isNetworkAvailable(getApplicationContext())) {
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
