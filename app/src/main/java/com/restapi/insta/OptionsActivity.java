package com.restapi.insta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;


public class OptionsActivity extends AppCompatActivity {

    private TextView settings;
    private TextView logOut;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        settings = findViewById(R.id.settings);
        logOut = findViewById(R.id.logout);

        Toolbar toolbar = findViewById(R.id.toolBarSettings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), OpenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();



            }
        });




    }


    @Override
    public void finish() {
        super.finish();
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_out_right);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("publisherId","fromOptionsAct");
        startActivity(intent, options.toBundle());
    }
}