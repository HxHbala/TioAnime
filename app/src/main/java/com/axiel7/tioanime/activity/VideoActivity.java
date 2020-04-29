package com.axiel7.tioanime.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.model.Episode;
import com.axiel7.tioanime.model.EpisodeResponse;
import com.axiel7.tioanime.rest.AnimeApiService;
import com.axiel7.tioanime.utils.TinyDB;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.axiel7.tioanime.activity.MainActivity.API_KEY;

public class VideoActivity extends AppCompatActivity {
    private JWPlayerView jwPlayerView;
    private WebView webView;
    private Episode episode;
    private String episodeUrl;
    private int episodeId;
    private TinyDB tinyDB;
    private static final String TAG = "nomames:";
    private static final String BASE_URL = "https://tioanime.com/api/episodes/";
    private static Retrofit retrofit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.video_layout);
        tinyDB = new TinyDB(this);
        episodeId = getIntent().getIntExtra("episodeId",0);
        jwPlayerView = findViewById(R.id.jwplayer);
        connectAndGetApiData();
    }
    private void connectAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        AnimeApiService animeApiService = retrofit.create(AnimeApiService.class);
        Call<EpisodeResponse> call = animeApiService.getEpisode(BASE_URL + episodeId, API_KEY);
        call.enqueue(new Callback<EpisodeResponse>() {
            @Override
            public void onResponse(Call<EpisodeResponse> call, Response<EpisodeResponse> response) {
                Log.e(TAG, call.request().toString());
                if (response.isSuccessful()) {
                    episode = response.body().getEpisodeData();
                    String[][] episodeOptions = episode.getVideos();
                    //choose the best option available
                    for (String[] episodeOption : episodeOptions) {
                        String option = episodeOption[0];
                        if (option.equals("Prime")) {
                            episodeUrl = episodeOption[1];
                            break;
                        } else if (option.equals("Amus")) {
                            episodeUrl = episodeOption[1];
                            break;
                        } else if (option.equals("Mega")) {
                            episodeUrl = episodeOption[1];
                        } else {
                            episodeUrl = episodeOptions[0][1];
                        }
                    }
                    setupWebView(episodeUrl);
                }
            }
            @Override
            public void onFailure(Call<EpisodeResponse> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                Toast.makeText(VideoActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(String videoUrl) {
        webView = findViewById(R.id.video_webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        boolean shouldPlayVlc = tinyDB.getBoolean("playWithVlc");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onLoadResource (WebView view, String url) {
                Uri uri = Uri.parse(url);
                boolean isPlayable = url.endsWith(".mp4");
                if (shouldPlayVlc && isPlayable) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setPackage("org.videolan.vlc");
                    intent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
                    intent.setDataAndTypeAndNormalize(uri,"video/*");
                    intent.putExtra("url", uri);
                    startActivity(intent);
                    finish();
                }
                else if (isPlayable) {
                    PlayerConfig playerConfig = new PlayerConfig.Builder()
                            .file(url)
                            .autostart(true)
                            .displayTitle(true)
                            .build();
                    jwPlayerView.setup(playerConfig);
                    jwPlayerView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    webView.destroy();
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
            {return false;}
        });
        webView.loadUrl(videoUrl);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        //getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        //decorView.setSystemUiVisibility(
        //        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        showSystemUI();
        tinyDB.putBoolean("fromVideoActivity", true);
        if (webView!=null) {
            webView.setVisibility(View.GONE);
            webView.clearHistory();
            webView.clearCache(false);
            webView.removeAllViews();
            webView.destroy();
        }
        if (jwPlayerView!=null) {
            jwPlayerView.stop();
            jwPlayerView.onDestroy();
        }
    }
}
