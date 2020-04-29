package com.axiel7.tioanime.activity.ui.favorites;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axiel7.tioanime.R;
import com.axiel7.tioanime.activity.ui.AnimeDetailsFragment;
import com.axiel7.tioanime.adapter.FavoritesAdapter;
import com.axiel7.tioanime.model.FavAnime;
import com.axiel7.tioanime.utils.TinyDB;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class FavoritesFragment extends Fragment {

    private TinyDB tinyDB;
    private Bundle bundle = new Bundle();
    private BottomSheetDialog bottomSheetDialog;
    private AlertDialog sortDialog;
    private AlertDialog deleteDialog;
    private ArrayList<FavAnime> favAnimes;
    private FavoritesAdapter favoritesAdapter;
    private TextView noFavsText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        noFavsText = root.findViewById(R.id.no_favorites_text);

        //setup fab
        FloatingActionButton plusFab = root.findViewById(R.id.plus_fab);
        View dialogView = getLayoutInflater().inflate(R.layout.favorites_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(requireActivity());
        bottomSheetDialog.setContentView(dialogView);
        plusFab.setOnClickListener(v -> bottomSheetDialog.show());
        createSortDialog();
        //setup sheet options
        TextView sortOption = bottomSheetDialog.findViewById(R.id.sort_option);
        assert sortOption != null;
        sortOption.setOnClickListener(v -> showSortDialog());
        TextView deleteAllOption = bottomSheetDialog.findViewById(R.id.delete_all_option);
        assert deleteAllOption != null;
        deleteAllOption.setOnClickListener(v -> confirmationDialog());

        //setup recyclerview
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_favorites);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    //scroll down
                    plusFab.hide();

                } else if (dy < 0) {
                    //scroll up
                    plusFab.show();
                }
            }
        });

        //fav database
        tinyDB = new TinyDB(getActivity());

        favAnimes = new ArrayList<>();
        getFavoritesDatabase(false);

        AnimeDetailsFragment animeDetailsFragment = new AnimeDetailsFragment();
        animeDetailsFragment.setEnterTransition(new Slide(Gravity.END));
        animeDetailsFragment.setExitTransition(new Slide(Gravity.START));

        favoritesAdapter = new FavoritesAdapter(favAnimes, R.layout.list_item_anime_favorite, getActivity());
        favoritesAdapter.setClickListener((view, position) -> {
            bundle.putInt("animeTypeInt", favoritesAdapter.getAnimeType(position));
            bundle.putInt("animeId", favoritesAdapter.getAnimeId(position));
            bundle.putString("animeTitle", favoritesAdapter.getAnimeTitle(position));
            bundle.putString("animePosterUrl", favoritesAdapter.getAnimePosterUrl(position));
            animeDetailsFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment_container, animeDetailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(favoritesAdapter);

        return root;
    }
    public void getFavoritesDatabase(boolean shouldClear) {
        ArrayList<Object> animesObject = tinyDB.getListObject("favAnimes", FavAnime.class);
        if (shouldClear) {
            favAnimes.clear();
        }
        for (Object objects : animesObject) {
            favAnimes.add((FavAnime)objects);
        }
        if (shouldClear) {
            favoritesAdapter.notifyDataSetChanged();
        }
        checkEmpty();
    }
    private void createSortDialog() {
        CharSequence[] values = {"A-Z", "Z-A", "Último añadido", "Tipo"};
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Orden");
        builder.setSingleChoiceItems(values, 2, (dialog, item) -> {
            switch(item) {
                case 0:
                    Collections.sort(favAnimes, (o1, o2) -> o1.getAnimeTitle().compareTo(o2.getAnimeTitle()));
                    break;
                case 1:
                    Collections.sort(favAnimes, (o1, o2) -> o2.getAnimeTitle().compareTo(o1.getAnimeTitle()));
                    break;
                case 2:
                    Collections.sort(favAnimes, (o1, o2) -> Integer.compare(o1.getOrderIndex(), o2.getOrderIndex()));
                    break;
                case 3:
                    Collections.sort(favAnimes, (o1, o2) -> o1.getAnimeType().compareTo(o2.getAnimeType()));
            }
            favoritesAdapter.notifyDataSetChanged();
            sortDialog.dismiss();
        });
        sortDialog = builder.create();
    }
    private void showSortDialog() {
        sortDialog.show();
        bottomSheetDialog.dismiss();
    }
    private void checkEmpty() {
        if (favAnimes.isEmpty()) {
            noFavsText.setVisibility(View.VISIBLE);
        }
        else {
            noFavsText.setVisibility(View.INVISIBLE);
        }
    }
    private void confirmationDialog() {
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.AlertDialogTheme);
        builder.setTitle("¿Estás seguro?")
                .setMessage("Se borrarán todos tus favoritos")
                .setPositiveButton("Borrar", (dialog, which) -> deleteAll())
                .setNegativeButton("Cancelar", (dialog, which) -> deleteDialog.dismiss());
        deleteDialog = builder.create();
        bottomSheetDialog.dismiss();
        deleteDialog.show();
    }
    private void deleteAll() {
        favAnimes.clear();
        ArrayList<Object> animesObject = tinyDB.getListObject("favAnimes", FavAnime.class);
        animesObject.clear();
        tinyDB.putListObject("favAnimes",animesObject);
        favoritesAdapter.notifyDataSetChanged();
        checkEmpty();
    }
}
