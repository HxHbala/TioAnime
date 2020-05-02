package com.axiel7.tioanime.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.activity.ui.directory.DirectoryFragment;
import com.axiel7.tioanime.activity.ui.emission.EmissionFragment;
import com.axiel7.tioanime.activity.ui.favorites.FavoritesFragment;
import com.axiel7.tioanime.activity.ui.home.HomeFragment;
import com.axiel7.tioanime.utils.TinyDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "no tengo permiso para mostrarla v:";
    private BottomSheetDialog dialog;
    private TinyDB tinyDB;
    private Fragment homeFragment = new HomeFragment();
    private Fragment directoryFragment = new DirectoryFragment();
    private Fragment emissionFragment = new EmissionFragment();
    private Fragment favoritesFragment = new FavoritesFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSystemUI();
        tinyDB = new TinyDB(this);
        boolean isUserLogged = tinyDB.getBoolean("isUserLogged");
        if (!isUserLogged) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //setup toolbar elements
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_white_24dp));
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin =
                    insets.getSystemWindowInsetTop();
            return insets.consumeSystemWindowInsets();
        });
        //setup bottom sheet dialog
        View dialogView = getLayoutInflater().inflate(R.layout.main_bottom_sheet, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);
        toolbar.setNavigationOnClickListener(v -> dialog.show());

        setupTransitions();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        setupBottomBar(navView);
        if ("openFavorites".equals(getIntent().getAction())) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, favoritesFragment)
                    .commit();
            navView.setSelectedItemId(R.id.navigation_favorites);
        }
    }
    private void setupSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    private void setupBottomBar(BottomNavigationView navigationView) {
        navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = homeFragment;
                    break;
                case R.id.navigation_directory:
                    selectedFragment = directoryFragment;
                    break;
                case R.id.navigation_emission:
                    selectedFragment = emissionFragment;
                    break;
                case R.id.navigation_favorites:
                    selectedFragment = favoritesFragment;
                    break;
            }
            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();
            return true;
        });
    }
    private void setupTransitions() {
        Transition fade = new Fade();

        homeFragment.setEnterTransition(fade);
        homeFragment.setExitTransition(fade);

        directoryFragment.setEnterTransition(fade);
        directoryFragment.setExitTransition(fade);

        emissionFragment.setEnterTransition(fade);
        emissionFragment.setExitTransition(fade);

        favoritesFragment.setEnterTransition(fade);
        favoritesFragment.setExitTransition(fade);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Buscar");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(v -> {
            searchView.setIconified(true);
            searchView.clearFocus();
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        return true;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        dialog.dismiss();
    }
    public void openWallpapers(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.reddit.com/r/Animewallpaper/?f=flair_name%3A%22Mobile%22"));
        startActivity(intent);
        dialog.dismiss();
    }
    public void openMusic(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://themes.moe/list/popular/100"));
        startActivity(intent);
        dialog.dismiss();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (tinyDB.getBoolean("fromVideoActivity")) {
            setupSystemUI();
            tinyDB.putBoolean("fromVideoActivity", false);
        }
    }
}
