package com.axiel7.tioanime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.model.AuthResponse;
import com.axiel7.tioanime.rest.AnimeApiService;
import com.axiel7.tioanime.utils.TinyDB;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private TinyDB tinyDB;
    private String email = null;
    private String password = null;
    private static final String BASE_URL = "https://tioanime.com/auth/";
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        tinyDB = new TinyDB(this);
        if (getIntent().getExtras()!=null) {
            tinyDB.putBoolean("isUserLogged", getIntent().getBooleanExtra("isUserLogged", true));
        }
        TextInputEditText emailInput = findViewById(R.id.username_input);
        TextInputEditText passwordInput = findViewById(R.id.password_input);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString();
            }
        });
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
            }
        });
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> connectAndGetApiData());
        Button resetPasswordButton = findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(v -> Toast.makeText(this, "Accede a TioAnime.com para cambiar tu contraseña", Toast.LENGTH_LONG).show());
    }
    private void connectAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        RequestBody requestUser = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody requestPassword = RequestBody.create(MediaType.parse("multipart/form-data"), password);
        AnimeApiService animeApiService = retrofit.create(AnimeApiService.class);
        Call<AuthResponse> call = animeApiService.postAuth(requestUser, requestPassword);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                Log.d("auth", response.message());
                if (response.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    tinyDB.putBoolean("isUserLogged", true);
                    tinyDB.putString("userEmail", email);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Email o contraseña no válidos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("auth", t.toString());
                Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
