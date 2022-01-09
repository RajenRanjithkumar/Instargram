package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.restapi.insta.Fragments.HomeFragment;
import com.restapi.insta.Fragments.NotificationFragment;
import com.restapi.insta.Fragments.ProfileFragment;
import com.restapi.insta.Fragments.SerachFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SerachFragment();
                        break;

                    case R.id.nav_add:
                        selectorFragment = null;
                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_heart:
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if (selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();

                }
                return true;
            }
        });

        Bundle intent = getIntent().getExtras();

        if (intent!= null){

            // how we pass values from activities to fragments

            String profileId = intent.getString("publisherId");
            getSharedPreferences("Profile", MODE_PRIVATE).edit().putString("publisherId", profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        }else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Toast.makeText(getApplicationContext(), "back pressed", Toast.LENGTH_SHORT).show();

    }
}