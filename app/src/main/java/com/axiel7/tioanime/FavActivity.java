package com.axiel7.tioanime;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class FavActivity extends AppCompatActivity implements AnimeAdapter.ItemClickListener, Serializable {
    private AnimeAdapter adapter;
    private TinyDB tinyDB;
    private Map<String, String> animeMap;
    private Map<String, String> animeMapFile;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private String filePath;
    private ArrayList<String> animeUrls;
    private ArrayList<String> animeTitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        //edge to edge support
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);

        //setup favorites database
        tinyDB = new TinyDB(this);
        animeUrls = tinyDB.getListString("animeUrls");
        animeTitles = tinyDB.getListString("animeTitles");
        Collections.sort(animeUrls);
        Collections.sort(animeTitles);
        if (animeMap==null) {
            animeMap = new LinkedHashMap<>();
        }
        for (int i=0; i<animeUrls.size(); i++) {
            animeMap.put(animeUrls.get(i), animeTitles.get(i));
        }

        //setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.favList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnimeAdapter(this, animeMap);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onItemClick(View view, int position) {
        String valueFav = adapter.getItem(position);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        tinyDB.putString("openFavUrl", valueFav);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_delete) {
            deleteDialog();
            return true;
        }
        if (id == R.id.export_fav) {
            if (isStoragePermissionGranted()) {
                writeObject(animeMap,"favoritos.txt");
            }
            return true;
        }
        if (id == R.id.import_fav) {
            if (isStoragePermissionGranted()) {
                Toast.makeText(this, "Importando lista…", Toast.LENGTH_SHORT).show();
                deleteList();
                readObject("favoritos.txt");
                for (String key : animeMap.keySet()) {
                    animeUrls.add(key);
                    animeTitles.add(animeMap.get(key));
                }
                tinyDB.putListString("animeUrls",animeUrls);
                tinyDB.putListString("animeTitles",animeTitles);
                adapter.notifyDataSetChanged();
            }
        }
        if (id == R.id.help) {
            mDialog(getString(R.string.help_message),
                    getString(R.string.help),
                    getString(R.string.ok),
                    "");
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            Toast.makeText(this, "Permiso concedido, vuelve a realizar la acción", Toast.LENGTH_LONG).show();
        }
    }
    public void writeObject(Object inputObject, String fileName){
        try {
            File output = new File(getApplicationContext().getExternalFilesDir(null), fileName);
            filePath = output.getAbsolutePath();
            fileOut = new FileOutputStream(filePath);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(inputObject);
            fileOut.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error:" + e, Toast.LENGTH_LONG).show();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                    mDialog("Se han exportado " + animeMap.size() + " favoritos.\n" +
                                    "Consulta el menú de ayuda para más info.",
                            getString(R.string.exported),
                            getString(R.string.ok),
                            "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void readObject(String fileName){
        try {
            File output = new File(getApplicationContext().getExternalFilesDir(null), fileName);
            filePath = output.getAbsolutePath();
            fileIn = new FileInputStream(filePath);
            objectIn = new ObjectInputStream(fileIn);

            animeMapFile = (Map<String, String>)objectIn.readObject();

            animeMap.putAll(animeMapFile);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error:" + e, Toast.LENGTH_LONG).show();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                    mDialog("Se han importado " + animeMap.size() + " favoritos.",
                            getString(R.string.imported),
                            getString(R.string.ok),
                            "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void deleteList() {
        tinyDB.remove("animeUrls");
        tinyDB.remove("animeTitles");
        animeUrls.clear();
        animeTitles.clear();
        animeMap.clear();
    }
    public void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Se borrarán todos tus favoritos")
                .setTitle("¿Estás seguro?")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    deleteList();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
    }
    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
