package com.axiel7.tioanime.activity.ui.home;

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

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ViewPager viewPager = root.findViewById(R.id.main_tab_viewPager);
        setupViewPager(viewPager);
        TabLayout tabs = root.findViewById(R.id.main_tab_layout);
        tabs.setupWithViewPager(viewPager);

        return root;
    }
    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new LatestEpisodesFragment(), "Nuevos Episodios");
        adapter.addFragment(new LatestAnimesFragment(), "Últimos Animes");
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
