package com.example.androidexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * BillItemsActivity
 * -----------------
 * Hosts the Bill Items system using:
 * - TabLayout
 * - ViewPager2
 *
 * Uses BillPagerAdapter (which you already have).
 */
public class BillItemsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_items);

        int groupId = getIntent().getIntExtra("groupId", -1);
        if (groupId == -1) {
            finish();
            return;
        }

        BillItemsStore.getInstance().setGroupId(groupId);
        BillItemsStore.getInstance().refreshAll();


        // Match IDs from your XML
        tabLayout = findViewById(R.id.bill_tabs);
        viewPager = findViewById(R.id.bill_viewpager);

        // Attach your existing pager adapter
        viewPager.setAdapter(new BillPagerAdapter(this));

        // Connect tabs → pager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("All Items"); break;
                case 1: tab.setText("Unpaid");    break;
                case 2: tab.setText("Paid");      break;
            }
        }).attach();
    }
}
