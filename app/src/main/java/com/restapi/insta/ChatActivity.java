package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Model.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView name;
    private TextView userName;
    private TextView sendBt;
    private RelativeLayout sendImage;
    private EditText message;

    private String userId;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





        profileImage = findViewById(R.id.profileImage);
        name = findViewById(R.id.actualName);
        userName = findViewById(R.id.userName);
        sendBt = findViewById(R.id.sendBt);
        sendImage = findViewById(R.id.foot);
        message = findViewById(R.id.user_message);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
            //The key argument here must match that used in the other activity
        }


        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        userInfo();


    }

    private void userInfo() {

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                // fix this
                if(user.getImageurl().equals("default") || user.getImageurl().equals(null )){

                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }else {

                    Picasso.get().load(user.getImageurl()).into(profileImage);

                }

                userName.setText(user.getUsername());
                name.setText(user.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}