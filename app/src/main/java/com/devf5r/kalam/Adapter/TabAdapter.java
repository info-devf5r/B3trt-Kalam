package com.devf5r.kalam.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.devf5r.kalam.Fragment.CategoryFragment;
import com.devf5r.kalam.Fragment.PhotoCategoryFragment;

public class TabAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CategoryFragment homeFragment = new CategoryFragment();
                return homeFragment;
            case 1:
                PhotoCategoryFragment photoFragment = new PhotoCategoryFragment();
                return photoFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
