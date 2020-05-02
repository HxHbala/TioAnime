package com.axiel7.tioanime.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

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

import org.acra.ACRA;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences preferences;
    private TinyDB tinyDB;
    private static String userEmail;
    private static boolean isUserLogged = true;

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
        userEmail = tinyDB.getString("userEmail");

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

            Preference bugReport = findPreference("bug_report");
            assert bugReport != null;
            bugReport.setOnPreferenceClickListener(preference -> {
                ACRA.getErrorReporter().handleException(null);
                return true;
            });

            Preference discord = findPreference("discord");
            assert discord != null;
            discord.setOnPreferenceClickListener(preference -> {
                String discordInvite = "https://discord.gg/QhAMKuV";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(discordInvite));
                startActivity(intent);
                return true;
            });

            Preference user = findPreference("username");
            assert user != null;
            user.setSummary(userEmail);

            Preference changePassword = findPreference("change_password");
            assert changePassword != null;
            changePassword.setOnPreferenceClickListener(preference -> {
                Toast.makeText(getActivity(), "Accede a TioAnime.com para cambiar tu contraseña", Toast.LENGTH_SHORT).show();
                return true;
            });

            Preference logout = findPreference("logout");
            assert logout != null;
            logout.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("isUserLogged", false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                TinyDB tinyDB = new TinyDB(getActivity());
                tinyDB.remove("userEmail");
                tinyDB.putBoolean("isUserLogged", false);
                requireActivity().startActivity(intent);
                requireActivity().finish();
                Runtime.getRuntime().exit(0);
                return true;
            });

            Preference rateUs = findPreference("rate_us");
            assert rateUs != null;
            rateUs.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.axiel7.tioanime"));
                startActivity(intent);
                return true;
            });
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