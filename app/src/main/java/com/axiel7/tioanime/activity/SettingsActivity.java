package com.axiel7.tioanime.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.axiel7.tioanime.BuildConfig;
import com.axiel7.tioanime.R;
import com.axiel7.tioanime.utils.TinyDB;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences preferences;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsFragment settingsFragment = new SettingsFragment();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, settingsFragment)
                .commit();

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        //change nav bar color
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //setup preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        tinyDB = new TinyDB(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("playWithVlc")) {
            boolean playWithVlc = sharedPreferences.getBoolean("playWithVlc", false);
            if (playWithVlc) {
                tinyDB.putBoolean("playWithVlc", true);
            }
            else {
                tinyDB.putBoolean("playWithVlc", false);
            }
        }
        else if (key.equals("searchUpdate")) {
            boolean searchUpdates = sharedPreferences.getBoolean("searchUpdates", true);
            if (searchUpdates) {
                tinyDB.putBoolean("searchUpdates", true);
            }
            else {
                tinyDB.putBoolean("searchUpdates", false);
            }
        }
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.main_preferences, rootKey);

            Preference about = findPreference("about");
            assert about != null;
            about.setTitle("Versión " + BuildConfig.VERSION_NAME);

            Preference discord = findPreference("discord");
            assert discord != null;
            discord.setOnPreferenceClickListener(preference -> {
                String discordInvite = "https://discord.gg/QhAMKuV";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(discordInvite));
                startActivity(intent);
                return true;
            });
        }
    }
    public static class DonatorsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.donators_preferences, rootKey);
        }
    }
    public static class TestersFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.testers_preferences, rootKey);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }
}