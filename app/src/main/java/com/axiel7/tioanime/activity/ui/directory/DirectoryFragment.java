package com.axiel7.tioanime.activity.ui.directory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.axiel7.tioanime.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DirectoryFragment extends Fragment {

    private Fragment tvFragment = new DirectoryCategoryFragment();
    private Fragment moviesFragment = new DirectoryCategoryFragment();
    private Fragment ovasFragment = new DirectoryCategoryFragment();
    private Fragment specialsFragment = new DirectoryCategoryFragment();
    private Bundle tvBundle = new Bundle();
    private Bundle movieBundle = new Bundle();
    private Bundle ovaBundle = new Bundle();
    private Bundle specialBundle = new Bundle();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_directory, container, false);
        ViewPager viewPager = root.findViewById(R.id.directory_tab_viewPager);
        viewPager.setOffscreenPageLimit(1);
        setupViewPager(viewPager);
        TabLayout tabs = root.findViewById(R.id.directory_tab_layout);
        tabs.setupWithViewPager(viewPager);

        return root;
    }
    private void setupViewPager(ViewPager viewPager) {

        DirectoryFragment.Adapter adapter = new DirectoryFragment.Adapter(getChildFragmentManager());

        tvBundle.putString("fragmentType", "tv");
        tvFragment.setArguments(tvBundle);
        adapter.addFragment(tvFragment, "TV");

        movieBundle.putString("fragmentType", "movie");
        moviesFragment.setArguments(movieBundle);
        adapter.addFragment(moviesFragment, "Pelicula");

        ovaBundle.putString("fragmentType", "ova");
        ovasFragment.setArguments(ovaBundle);
        adapter.addFragment(ovasFragment, "OVA");

        specialBundle.putString("fragmentType", "special");
        specialsFragment.setArguments(specialBundle);
        adapter.addFragment(specialsFragment, "Especial");
        viewPager.setAdapter(adapter);

    }
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
