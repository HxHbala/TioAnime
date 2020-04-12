package com.axiel7.tioanime;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements GenreAdapter.ItemClickListener {
    private DrawerLayout drawerLayout;
    private CoordinatorLayout rootLayout;
    private CoordinatorLayout snackbarLocation;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TinyDB tinyDB;
    private ArrayList<String> animeUrls;
    private ArrayList<String> animeTitles;
    public WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private SearchView searchView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton favFab;
    private FloatingActionButton commentsFab;
    private FrameLayout customViewContainer;
    public WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    public myWebChromeClient mWebChromeClient;
    public myWebViewClient mWebViewClient;
    public String currentUrl;
    public String externalUrl;
    private Pattern mPattern;
    private Pattern episodePattern;
    private AppUpdaterUtils appUpdater;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_view);

        //setup toolbar and drawer
        rootLayout = findViewById(R.id.root_view);
        snackbarLocation = findViewById(R.id.snackbar_location);
        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.main_layout);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = false;
                }

                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = true;
                }
            };
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();
        }

        //setup bottomBar
        bottomNavMenu();

        //setup webViews complements
        customViewContainer = findViewById(R.id.customViewContainer);
        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(Color.parseColor("#18142f"));

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> webView.loadUrl(currentUrl));

        favFab = findViewById(R.id.floatingActionButton);
        favFab.bringToFront();
        commentsFab = findViewById(R.id.commentsFab);
        commentsFab.bringToFront();

        mWebViewClient = new myWebViewClient();
        webView.setWebViewClient(mWebViewClient);

        mWebChromeClient = new myWebChromeClient();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        //allow cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(mWebChromeClient);

        //handle external links
        handleIntent(getIntent());

        //setup favorites database
        tinyDB = new TinyDB(this);
        animeUrls = tinyDB.getListString("animeUrls");
        animeTitles = tinyDB.getListString("animeTitles");

        //check if should load a favorite url
        String openFavUrl = tinyDB.getString("openFavUrl");
        if (openFavUrl.equals("")) {
            currentUrl = "https://tioanime.com";
        } else {
            currentUrl = openFavUrl;
        }
        //check if should load an external link
        if (externalUrl != null) {
            webView.loadUrl(externalUrl);
        }
        else {
            webView.loadUrl(currentUrl);
        }
        //setup recyclerView for genres
        RecyclerView recyclerView = findViewById(R.id.genres_list);
        recyclerView.setHasFixedSize(true);
        ArrayList<String> listGenre = new ArrayList<>();
        Collections.addAll(listGenre, getResources().getStringArray(R.array.genres));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GenreAdapter adapter = new GenreAdapter(this, listGenre);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);

        //setup app updater
        appUpdater = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("axiel7", "TioAnime")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Snackbar snackbar = Snackbar.make(snackbarLocation,"Actualización disponible",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Descargar", v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(update.getUrlToDownload().toString()));
                                    startActivity(intent);
                                });
                        snackbar.setBackgroundTint(getResources().getColor(R.color.defaultDark));
                        snackbar.setTextColor(getResources().getColor(R.color.colorText));
                        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                        if (isUpdateAvailable) {
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Toast.makeText(MainActivity.this, "AppUpdater: Algo salió mal :s", Toast.LENGTH_SHORT).show();
                    }
                });
        appUpdater.start();

        //other
        tinyDB.putString("openFavUrl", "");
        mPattern = Pattern.compile("(http|https)://(tioanime.com/anime/|tiohentai.com/hentai/).*");
        episodePattern = Pattern.compile("(http|https)://(tioanime.com/ver/|tiohentai.com/ver/).*");
        if (currentUrl != null) {
            checkUrl(currentUrl);
        }
    }
    public void myToast(View view) {
        String message = "Versión " + BuildConfig.VERSION_NAME + "\nDesarrollado por axiel7";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private void bottomNavMenu() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    webView.loadUrl("https://tioanime.com");
                    break;
                case R.id.navigation_dashboard:
                    webView.loadUrl("https://tioanime.com/directorio?estado=emision");
                    break;
                case R.id.navigation_notifications:
                    Intent openFav = new Intent(MainActivity.this, FavActivity.class);
                    MainActivity.this.startActivity(openFav);
                    break;
            }
            return true;
        });
    }
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            String recipeId = appLinkData.getPath();
            externalUrl = "https://tioanime.com" + recipeId;
        }
    }
    public void saveFav(View view) {
        String title = webView.getTitle();
        title = title.replaceAll(" - Tio(Anime|Hentai)","");
        if (!animeUrls.contains(currentUrl)) {
            animeUrls.add(currentUrl);
            animeTitles.add(title);
            Toast.makeText(this,getString(R.string.toast_saved), Toast.LENGTH_SHORT).show();
        }
        else if (animeUrls.contains(currentUrl)) {
            animeUrls.remove(currentUrl);
            animeTitles.remove(title);
            Toast.makeText(this,getString(R.string.toast_deleted), Toast.LENGTH_SHORT).show();
        }
        tinyDB.putListString("animeUrls",animeUrls);
        tinyDB.putListString("animeTitles", animeTitles);
        if (currentUrl != null) {
            checkUrl(currentUrl);
        }
    }
    public void viewComments(View view) {
        webView.loadUrl("javascript:document.getElementById('disqus_thread').scrollIntoView();");
    }
    @Override
    public void onItemClick(View view, int position) {
        String value = getResources().getStringArray(R.array.genres_values)[position];
        if (value.equals("hentai")) {
            webView.loadUrl("https://tiohentai.com/directorio");
        }
        else {
            value = value.replaceAll("genre_", "");
            webView.loadUrl("https://tioanime.com/directorio?genero=" + value);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //setup searchView
        MenuItem myActionMenuItem = menu.findItem( R.id.menu_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.title_search));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                webView.loadUrl("https://tioanime.com/directorio?q=" + query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            webView.reload();
            return true;
        }
        if (id == R.id.menu_settings) {
            Intent openSettings = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(openSettings);
        }
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webView.onResume();
        animeUrls = tinyDB.getListString("animeUrls");
        animeTitles = tinyDB.getListString("animeTitles");
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (inCustomView()) {
            hideCustomView();
        }
        appUpdater.stop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearHistory();
        webView.removeAllViews();
        webView.destroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (inCustomView()) {
                hideCustomView();
                return true;
            }

            if ((mCustomView == null) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    class myWebChromeClient extends WebChromeClient {
        private View mVideoProgressView;

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
        {
            WebView.HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            Context context = view.getContext();
            if (data != null) {
                if (data.startsWith("https://disqus.com/embed")) {
                    mDialog("Después de iniciar sesión volverás a la página principal y ya podrás comentar.",
                            "Inicia sesión en disqus por primera vez",
                            getString(R.string.ok),
                            "");
                    webView.loadUrl("https://disqus.com/profile/login/?next=https%3A%2F%2Fdisqus.com%2Fhome%2Finbox%2F&forum=https-tioanime-com");
                    return true;
                }
                else if (data.contains("disquscdn.com")) {
                    Toast.makeText(MainActivity.this, "No soportado", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
            context.startActivity(browserIntent);
            return false;
        }

        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            webView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            hideSystemUI();
        }

        @SuppressLint("InflateParams")
        @Override
        public View getVideoLoadingProgressView() {

            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
            }
            return mVideoProgressView;
        }

        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        public void onHideCustomView() {
            super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;

            webView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            mCustomView = null;
            customViewCallback.onCustomViewHidden();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            showSystemUI();
        }
    }
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

        rootLayout.setFitsSystemWindows(false);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        bottomNavigationView.setVisibility(View.GONE);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        rootLayout.setFitsSystemWindows(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        bottomNavigationView.setVisibility(View.VISIBLE);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }
    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
        }
        @Override
        public void onLoadResource (WebView view, String url) {
            Uri uri = Uri.parse(url);
            boolean shouldPlayVlc = tinyDB.getBoolean("playWithVlc");
            boolean isPlayable = url.endsWith(".mp4");
            if (shouldPlayVlc && isPlayable) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setPackage("org.videolan.vlc");
                intent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
                intent.setDataAndTypeAndNormalize(uri,"video/*");
                intent.putExtra("url", uri);
                startActivity(intent);
            }
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            swipeRefreshLayout.setRefreshing(true);
            currentUrl=webView.getUrl();
            //searchView.clearFocus();
            if (currentUrl != null) {
                checkUrl(currentUrl);
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            swipeRefreshLayout.setRefreshing(false);
            currentUrl=webView.getUrl();
            if (currentUrl != null) {
                checkUrl(currentUrl);
            }
        }
    }
    private void checkUrl(String currentUrl) {
        if (currentUrl.equals("https://disqus.com/home/inbox/")) {
            webView.loadUrl("https://tioanime.com");
        }
        Matcher matcher = mPattern.matcher(currentUrl);
        Matcher commentsMatcher = episodePattern.matcher(currentUrl);
        if (matcher.find()) {
            favFab.setVisibility(View.VISIBLE);
            if (animeUrls.contains(currentUrl)) {
                favFab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white_24dp));
            }
            else {
                favFab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_white_24dp));
            }
        }
        else {
            favFab.setVisibility(View.INVISIBLE);
        }
        if (commentsMatcher.find()) {
            commentsFab.setVisibility(View.VISIBLE);
        }
        else {
            commentsFab.setVisibility(View.GONE);
        }
    }
    public void mDialog(String message, String title, String positive, String negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(positive, (dialog, which) -> {
                })
                .setNegativeButton(negative, (dialog, which) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
