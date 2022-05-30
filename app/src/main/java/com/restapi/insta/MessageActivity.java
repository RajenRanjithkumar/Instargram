package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Adapter.UsersChatDisplayAdapter;
import com.restapi.insta.Model.ChatList;
import com.restapi.insta.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {


    CardView searchBar;
    private RecyclerView usersRV;
    private UsersChatDisplayAdapter usersChatDisplayAdapter;
    private List<ChatList> mUserChatList;
    private List<User> mUsers;
    private FirebaseUser firebaseUser;

    private OffineDetector offineDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.messageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        offineDetector = new OffineDetector();

        usersRV = findViewById(R.id.chatListRV);
        usersRV.setHasFixedSize(true);
        usersRV.setLayoutManager(new LinearLayoutManager(this));

        mUserChatList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("ChatList").child(firebaseUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserChatList.clear();
                for (DataSnapshot userIds: snapshot.getChildren()){

                    ChatList chatList = userIds.getValue(ChatList.class);
                    mUserChatList.add(chatList);

                }

                retrieveChatList();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        searchBar = findViewById(R.id.searchCardView);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);

            }
        });


    }

    private void retrieveChatList() {

        mUsers = new ArrayList<>();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            mUsers.clear();

            for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                User user = dataSnapshot.getValue(User.class);

               for(ChatList chatList: mUserChatList){

                   if (user.getId().equals(chatList.getId())){

                    mUsers.add(user);

                   }


               }

            }

            usersChatDisplayAdapter = new UsersChatDisplayAdapter(getApplicationContext(), mUsers);
            usersRV.setAdapter(usersChatDisplayAdapter);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void finish() {
        super.finish();
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_out_right);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent, options.toBundle());
    }


    @Override
    protected void onResume() {
        super.onResume();

        offineDetector.updateStatus(true, firebaseUser);
    }

    @Override
    protected void onPause() {
        super.onPause();
        offineDetector.updateStatus(false, firebaseUser);

    }





}