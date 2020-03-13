package com.axiel7.tioanime;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavActivity extends AppCompatActivity implements AnimeAdapter.ItemClickListener {
    private AnimeAdapter adapter;
    private ArrayList<String> animeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        //edge to edge support
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        //setup favorites database
        TinyDB tinyDB = new TinyDB(this);
        if (animeList==null) {
            animeList = tinyDB.getListString("animeList");
        }
        for (String s : animeList) {
            if (!animeList.contains(s)) {
                animeList.add(tinyDB.getString("anime"+s));
            }
            break;
        }

        // set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.favList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnimeAdapter(this, animeList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onItemClick(View view, int position) {
        String valueFav = adapter.getItem(position);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("openFavUrl", valueFav);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
    }
    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
