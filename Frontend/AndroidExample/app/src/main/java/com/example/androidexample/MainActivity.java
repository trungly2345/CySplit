package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private HomeFragment homeFragment = new HomeFragment();
    private GroupFragment groupFragment = new GroupFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private LoginFragment loginFragment = new LoginFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        bottomNav = findViewById(R.id.bottom_navigation);

        loadFragment(homeFragment);

        bottomNav.setOnItemSelectedListener(item ->{
            int itemId = item.getItemId();

            if(itemId == R.id.nav_home){
                loadFragment(homeFragment);
            }
            else if (itemId == R.id.nav_groups){
                loadFragment(groupFragment);
            }
            else if (itemId == R.id.nav_profile){
                loadFragment(profileFragment);
            }
            else if (itemId == R.id.nav_login){
                loadFragment(loginFragment);
            }

            return true;
        });

        Bundle extras = getIntent().getExtras();
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
                return true;
        }
        return false;
    }
}