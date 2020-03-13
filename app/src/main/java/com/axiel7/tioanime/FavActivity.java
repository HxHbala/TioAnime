package com.axiel7.tioanime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FavActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
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
        TextView savedUrl = findViewById(R.id.textView2);
        sharedPreferences = getSharedPreferences("savedUrls" , Context.MODE_PRIVATE);
        String url1 = sharedPreferences.getString("favUrl",null);
        savedUrl.setText(url1);
    }
}
