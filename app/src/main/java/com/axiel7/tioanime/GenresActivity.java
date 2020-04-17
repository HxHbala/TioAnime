package com.axiel7.tioanime;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.LinkedHashMap;
import java.util.Map;

public class GenresActivity extends AppCompatActivity implements GenreAdapter.ItemClickListener {
    private GenreAdapter adapter;
    private TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        //change nav bar color
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.genres_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        //setup chips
        Chip movieChip = findViewById(R.id.chip_movies);
        Chip ovaChip = findViewById(R.id.chip_ovas);
        Chip specialChip = findViewById(R.id.chip_special);
        Chip hentaiChip = findViewById(R.id.chip_hentai);

        movieChip.setOnClickListener(v -> openMainActivity("https://tioanime.com/directorio?type%5B%5D=1"));
        ovaChip.setOnClickListener(v -> openMainActivity("https://tioanime.com/directorio?type%5B%5D=2"));
        specialChip.setOnClickListener(v -> openMainActivity("https://tioanime.com/directorio?type%5B%5D=3"));
        hentaiChip.setOnClickListener(v -> openMainActivity("https://tiohentai.com/directorio"));

        //setup genres
        Map<String, String> genresMap = new LinkedHashMap<>();
        String[] genres = getResources().getStringArray(R.array.genres);
        String[] genresValues = getResources().getStringArray(R.array.genres_values);
        for (int i=0; i<genres.length; i++) {
            genresMap.put(genres[i], genresValues[i]);
        }

        //setup recyclerview
        tinyDB = new TinyDB(this);
        RecyclerView recyclerView = findViewById(R.id.genresList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenreAdapter(this, genresMap);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        String valueGenre = adapter.getItem(position);
        openMainActivity("https://tioanime.com/directorio?genero=" + valueGenre);
    }
    private void openMainActivity(String directory) {
        tinyDB.putString("openFavGenreUrl", directory);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
