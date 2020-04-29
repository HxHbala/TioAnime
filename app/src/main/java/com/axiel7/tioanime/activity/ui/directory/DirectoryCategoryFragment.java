package com.axiel7.tioanime.activity.ui.directory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.activity.ui.AnimeDetailsFragment;
import com.axiel7.tioanime.adapter.LatestAnimesAdapter;
import com.axiel7.tioanime.model.Category;
import com.axiel7.tioanime.model.LatestAnimesResponse;
import com.axiel7.tioanime.rest.AnimeApiService;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

public class DirectoryCategoryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView noResultText;
    private AlertDialog genresDialog;
    private RecyclerView recyclerView;
    private LatestAnimesAdapter latestAnimesAdapter;
    private AnimeDetailsFragment animeDetailsFragment;
    private Cache cache;
    private static final String TAG = "nomames:";
    private static final String BASE_URL = "https://tioanime.com/api/";
    private String type;
    private int page = 1;
    private String recent = "recent";
    private Integer status = null;
    private int startYear = 1950;
    private int endYear = Calendar.getInstance().get(Calendar.YEAR);
    private String years;
    private List<CharSequence> genres = new ArrayList<>();
    private CharSequence[] genresItems;
    private CharSequence[] genresValues;
    private boolean[] checkedGenres;
    private int pageLimit;
    private static Retrofit retrofit = null;
    private Call<LatestAnimesResponse> call;
    private Call<LatestAnimesResponse> getMoreCall;
    private List<Category> animes;

    public void onCreate (Bundle savedInstanceState) {
        //setup details fragment
        animeDetailsFragment = new AnimeDetailsFragment();
        animeDetailsFragment.setEnterTransition(new Slide(Gravity.END));
        animeDetailsFragment.setExitTransition(new Slide(Gravity.START));

        assert getArguments() != null;
        type = getArguments().getString("fragmentType");
        years = startYear + "," + endYear;
        genresItems = getResources().getStringArray(R.array.genres);
        genresValues = getResources().getStringArray(R.array.genres_values);
        checkedGenres = new boolean[genresValues.length];
        //reset filters and get data
        resetFilters();
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_directory_animes, container, false);

        noResultText = root.findViewById(R.id.no_result_text);

        //setup filter fab and bottom sheet
        ExtendedFloatingActionButton filtersFab = root.findViewById(R.id.filters_fab);
        View dialogView = getLayoutInflater().inflate(R.layout.filters_bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
        dialog.setContentView(dialogView);
        BottomSheetBehavior bottomSheetBehavior = dialog.getBehavior();
        filtersFab.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setPeekHeight(1000);
            dialog.show();
        });

        //setup bottom sheet genres
        TextView genresText = dialog.findViewById(R.id.genres_text);
        assert genresText != null;
        genresText.setOnClickListener(v -> genresDialog.show());
        //setup bottom sheet recent and status options
        Spinner recentSelection = dialog.findViewById(R.id.recent_selection);
        assert recentSelection != null;
        recentSelection.setOnItemSelectedListener(this);
        Spinner statusSelection = dialog.findViewById(R.id.status_selection);
        assert statusSelection != null;
        statusSelection.setOnItemSelectedListener(this);
        //setup bottom sheet years input
        TextInputEditText startYearInput = dialog.findViewById(R.id.start_year_input);
        assert startYearInput != null;
        startYearInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    startYear = Integer.parseInt(s.toString());
                    years = startYear + "," + endYear;
                }
            }
        });
        TextInputEditText endYearInput = dialog.findViewById(R.id.end_year_input);
        assert endYearInput != null;
        endYearInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    endYear = Integer.parseInt(s.toString());
                    years = startYear + "," + endYear;
                }
            }
        });

        //setup bottom sheet buttons
        Button applyButton = dialog.findViewById(R.id.apply_button);
        assert applyButton != null;
        applyButton.setOnClickListener(v -> {
            connectAndGetApiData();
            dialog.dismiss();
        });
        Button resetButton = dialog.findViewById(R.id.reset_button);
        assert resetButton != null;
        resetButton.setOnClickListener(v -> {
            Objects.requireNonNull(startYearInput.getText()).clear();
            Objects.requireNonNull(endYearInput.getText()).clear();
            startYearInput.clearFocus();
            endYearInput.clearFocus();
            resetFilters();
            connectAndGetApiData();
        });

        //setup recyclerview
        recyclerView = root.findViewById(R.id.recyclerview_directory_animes);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    //scroll down
                    filtersFab.shrink();

                } else if (dy < 0) {
                    //scroll up
                    filtersFab.extend();
                }
            }
        });

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
        //switch was not working properly with strings, used else ifs
        if (type.equals("tv")) {
            call = animeApiService.getTV(page, recent, status, years, genres, API_KEY);
        }
        else if (type.equals("movie")) {
            call = animeApiService.getMovie(page, recent, status, years, genres, API_KEY);
        }
        else if (type.equals("ova")) {
            call = animeApiService.getOvas(page, recent, status, years, genres, API_KEY);
        }
        else if (type.equals("special")) {
            call = animeApiService.getSpecials(page, recent, status, years, genres, API_KEY);
        }
        assert call != null;
        call.enqueue(getFirstAnimesCallback);
    }
    private Callback<LatestAnimesResponse> getFirstAnimesCallback = new Callback<LatestAnimesResponse>() {
        @Override
        public void onResponse(Call<LatestAnimesResponse> call, Response<LatestAnimesResponse> response) {
            Log.e(TAG, call.request().toString());
            if (response.isSuccessful()) {
                animes = response.body().getLatestAnimes();
                page = response.body().getPageInfo().getCurrentPage();
                pageLimit = response.body().getPageInfo().getTotalPages();
                if (pageLimit == 0) {
                    noResultText.setVisibility(View.VISIBLE);
                }
                else {
                    noResultText.setVisibility(View.INVISIBLE);
                }
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
                latestAnimesAdapter.setOnBottomReachedListener(position -> {
                    page = page + 1;
                    loadMoreItems();
                });
                recyclerView.setAdapter(latestAnimesAdapter);
                Log.d(TAG, "Number of animes received: " + animes.size());
            }
        }

        @Override
        public void onFailure(Call<LatestAnimesResponse> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(getActivity(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
    };
    private Callback<LatestAnimesResponse> getMoreAnimesCallback = new Callback<LatestAnimesResponse>() {
        @Override
        public void onResponse(Call<LatestAnimesResponse> call, Response<LatestAnimesResponse> response) {
            Log.e(TAG, call.request().toString());
            if (response.isSuccessful()) {
                List<Category> moreAnimes = response.body().getLatestAnimes();
                page = response.body().getPageInfo().getCurrentPage();
                animes.addAll(moreAnimes);
                latestAnimesAdapter.notifyDataSetChanged();
                Log.d(TAG, "Number of animes received: " + animes.size());
            }
        }
        @Override
        public void onFailure(Call<LatestAnimesResponse> call, Throwable t) {
            Log.e(TAG, t.toString());
            Toast.makeText(getActivity(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }
    };
    private void loadMoreItems() {
        if (page <= pageLimit) {
            Log.d("nomamespage", String.valueOf(page));
            if (retrofit!=null) {
                AnimeApiService animeApiService = retrofit.create(AnimeApiService.class);
                //switch was not working properly with strings
                if (type.equals("tv")) {
                    getMoreCall = animeApiService.getTV(page, recent, status, years, genres, API_KEY);
                }
                else if (type.equals("movie")) {
                    getMoreCall = animeApiService.getMovie(page, recent, status, years, genres, API_KEY);
                }
                else if (type.equals("ova")) {
                    getMoreCall = animeApiService.getOvas(page, recent, status, years, genres, API_KEY);
                }
                else if (type.equals("special")) {
                    getMoreCall = animeApiService.getSpecials(page, recent, status, years, genres, API_KEY);
                }
                getMoreCall.enqueue(getMoreAnimesCallback);
            }
        }
    }
    private void resetFilters() {
        page = 1;
        recent = "recent";
        status = null;
        startYear = 1950;
        endYear = Calendar.getInstance().get(Calendar.YEAR);
        years = startYear + "," + endYear;
        genres.clear();
        checkedGenres = new boolean[genresValues.length];
        createGenresDialog();
    }
    private void createGenresDialog() {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme);
        builder.setTitle("Géneros")
                .setPositiveButton("OK", (dialog, which) -> genresDialog.dismiss());
        builder.setMultiChoiceItems(genresItems, checkedGenres, (dialog, which, isChecked) -> {
            if (isChecked) {
                genres.add(genresValues[which]);
            }
            else {
                genres.remove(genresValues[which]);
            }
        });
        genresDialog = builder.create();
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                                int pos, long id) {
        int parentId = parent.getId();
        if (parentId == R.id.recent_selection) {
            switch (parent.getSelectedItemPosition()) {
                case 0:
                    recent = "recent";
                    break;
                case 1:
                    recent = "-recent";
                    break;
            }
        }
        else if (parentId == R.id.status_selection) {
            switch (parent.getSelectedItemPosition()) {
                case 0:
                    status = null; //all
                case 1:
                    status = 2; //finished
                    break;
                case 2:
                    status = 1; //emission
                    break;
                case 3:
                    status = 3; //soon
                    break;
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
