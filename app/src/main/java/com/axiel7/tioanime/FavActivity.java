package com.axiel7.tioanime;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FavActivity extends AppCompatActivity implements AnimeAdapter.ItemClickListener, Serializable, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    private AnimeAdapter adapter;
    private TinyDB tinyDB;
    private NavigableMap<String, String> animeMap;
    private RecyclerView recyclerView;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private ArrayList<String> animeUrls;
    private ArrayList<String> animeTitles;
    private ImageView emptyChibi;
    private TextView emptyText;
    private String urlFav;
    private String nameFav;
    private PopupMenu popupMenu;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        //change navbar color
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        //setup empty list views
        emptyChibi = findViewById(R.id.emptyChibi);
        emptyText = findViewById(R.id.emptyText);

        //setup rapid fab
        rfaLayout = findViewById(R.id.rfab_layout);
        rfaBtn = findViewById(R.id.rfab_fab);
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getApplicationContext());
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        rapidFab(rfaContent);

        //setup RecyclerView
        recyclerView = findViewById(R.id.favList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        registerForContextMenu(recyclerView);

        //setup favorites database
        tinyDB = new TinyDB(this);
        animeUrls = tinyDB.getListString("animeUrls");
        animeTitles = tinyDB.getListString("animeTitles");
        createMap();
        checkListEmpty();
    }
    public void createMap() {
        boolean shouldSortZAList = tinyDB.getBoolean("ZA_sort?");
        if (shouldSortZAList) {
            animeMap = new TreeMap<>(Collections.reverseOrder());
        }
        else {
            animeMap = new TreeMap<>();
        }
        for (int i=0; i<animeUrls.size(); i++) {
            animeMap.put(animeUrls.get(i), animeTitles.get(i));
        }
        adapter = new AnimeAdapter(this, animeMap);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onItemClick(View view, int position) {
        String valueFav = adapter.getItem(position);
        tinyDB.putString("openFavUrl", valueFav);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onItemLongClick(View view, int position) {
        urlFav = adapter.getItem(position);
        nameFav = adapter.getItemName(position);
        popupMenu = new PopupMenu(recyclerView.getContext(), view);
        popupMenu.inflate(R.menu.context_menu);
        popupMenu.show();
        return true;
    }
    public void deleteItem(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.context_delete) {
            animeUrls.remove(urlFav);
            animeTitles.remove(nameFav);
            animeMap.remove(urlFav);
            tinyDB.putListString("animeUrls",animeUrls);
            tinyDB.putListString("animeTitles", animeTitles);
            adapter.notifyDataSetChanged();
            checkListEmpty();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fav_menu, menu);

        MenuItem sortAZ = menu.findItem(R.id.sort_AZ);
        MenuItem sortZA = menu.findItem(R.id.sort_ZA);
        boolean shouldSortZA = tinyDB.getBoolean("ZA_sort?"); //default false
        if (shouldSortZA) {
            sortZA.setChecked(true);
        }
        else {
            sortAZ.setChecked(true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.help) {
            mDialog(getString(R.string.help_message),
                    getString(R.string.help),
                    getString(R.string.ok),
                    "");
        }
        if (id == R.id.sort_AZ) {
            item.setChecked(true);
            tinyDB.putBoolean("ZA_sort?", false);
            createMap();
        }
        if (id == R.id.sort_ZA) {
            item.setChecked(true);
            tinyDB.putBoolean("ZA_sort?", true);
            createMap();
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            Toast.makeText(this, "Permiso concedido, vuelve a realizar la acción", Toast.LENGTH_LONG).show();
        }
    }
    public void writeObject(Object inputObject, Uri fileNameUri){
        try {
            objectOut = new ObjectOutputStream(getContentResolver().openOutputStream(fileNameUri));
            objectOut.writeObject(inputObject);
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
    public void readObject(Uri fileUri){
        try {
            objectIn = new ObjectInputStream(getContentResolver().openInputStream(fileUri));

            Map<String, String> animeMapFile = (Map<String, String>) objectIn.readObject();

            deleteList();
            animeMap.putAll(animeMapFile);
            for (String key : animeMap.keySet()) {
                animeUrls.add(key);
                animeTitles.add(animeMap.get(key));
            }
            tinyDB.putListString("animeUrls",animeUrls);
            tinyDB.putListString("animeTitles",animeTitles);
            adapter.notifyDataSetChanged();
            checkListEmpty();

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
    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "TioAnimefavs.txt");

        startActivityForResult(intent, 1);
    }
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, 2);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 1
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                writeObject(animeMap, uri);
            }
        }
        else if (requestCode == 2
                && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                readObject(uri);
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
                    checkListEmpty();
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
    public void checkListEmpty() {
        if (animeMap.size() == 0) {
            emptyChibi.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            emptyChibi.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }
    public void rapidFab(RapidFloatingActionContentLabelList rfaContent) {
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Eliminar todos")
                .setResId(R.drawable.ic_delete_sweep_black_24dp)
                .setIconNormalColor(0xff37d1a2)
                .setIconPressedColor(0xff009f73)
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Exportar lista")
                .setResId(R.drawable.ic_export_black_24dp)
                .setIconNormalColor(0xff37d1a2)
                .setIconPressedColor(0xff009f73)
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("Importar lista")
                .setResId(R.drawable.ic_import_black_24dp)
                .setIconNormalColor(0xff37d1a2)
                .setIconPressedColor(0xff009f73)
                .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(3)
                .setIconShadowColor(0x1c18142f)
                .setIconShadowDy(3)
        ;
        rfabHelper = new RapidFloatingActionHelper(
                getApplicationContext(),
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();
    }
    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        if (position == 0) {
            deleteDialog();
        }
        if (position == 1) {
            if (isStoragePermissionGranted()) {
                createFile();
            }
        }
        if (position == 2) {
            if (isStoragePermissionGranted()) {
                Toast.makeText(this, "Selecciona el fichero TioAnimefavs.txt", Toast.LENGTH_SHORT).show();
                openFile();
            }
        }
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        if (position == 0) {
            deleteDialog();
        }
        if (position == 1) {
            if (isStoragePermissionGranted()) {
                createFile();
            }
        }
        if (position == 2) {
            if (isStoragePermissionGranted()) {
                Toast.makeText(this, "Selecciona el fichero TioAnimefavs.txt", Toast.LENGTH_SHORT).show();
                openFile();
            }
        }
        rfabHelper.toggleContent();
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
