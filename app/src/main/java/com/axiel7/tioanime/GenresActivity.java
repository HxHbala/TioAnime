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

import java.util.ArrayList;
import java.util.Collections;

import static com.axiel7.tioanime.MainActivity.tioAnimeUrl;

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

        //setup recyclerview
        tinyDB = new TinyDB(this);
        RecyclerView recyclerView = findViewById(R.id.genresList);
        recyclerView.setHasFixedSize(true);
        ArrayList<String> listGenre = new ArrayList<>();
        Collections.addAll(listGenre, getResources().getStringArray(R.array.genres));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenreAdapter(this, listGenre);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        String valueGenre = getResources().getStringArray(R.array.genres_values)[position];

        if (valueGenre.equals("hentai")) {
            tinyDB.putString("openFavGenreUrl", "https://tiohentai.com/directorio");
        }
        else {
            valueGenre = valueGenre.replaceAll("genre_", "");
            //webView.loadUrl(tioAnimeUrl + "/directorio?genero=" + value);
            tinyDB.putString("openFavGenreUrl", tioAnimeUrl + "/directorio?genero=" + valueGenre);
        }
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
