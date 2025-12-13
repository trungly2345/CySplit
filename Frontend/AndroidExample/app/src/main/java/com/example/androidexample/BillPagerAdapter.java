package com.example.androidexample;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BillPagerAdapter extends FragmentStateAdapter {

    public BillPagerAdapter(@NonNull BillItemsActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new BillAllFragment();
            case 1: return new BillUnpaidFragment();
            case 2: return new BillPaidFragment();
        }
        return new BillAllFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
