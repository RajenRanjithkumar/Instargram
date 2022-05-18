package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView name;
    private TextView userName;
    private TextView sendBt;
    private RelativeLayout sendImage;
    private EditText messageEditText;

    private String ReceiverUserId;


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
        messageEditText = findViewById(R.id.user_message);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ReceiverUserId = extras.getString("userId");
            //The key argument here must match that used in the other activity
        }

        //
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(ReceiverUserId);

        userInfo();

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this, "Yet to implement", Toast.LENGTH_SHORT).show();
            }
        });


        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String message = messageEditText.getText().toString();
                if(message.isEmpty()){

                    Toast.makeText(ChatActivity.this, "Enter a message...", Toast.LENGTH_SHORT).show();

                }else {

                    sendMessage(firebaseUser.getUid(), ReceiverUserId, message);
                    messageEditText.setText("");

                }




            }
        });

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
                ReceiverUserId = user.getId();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendMessage(String senderId, String receiverUserId, String message){

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Chats");

        // generate unique keys for chat
        String uniqueId = databaseReference.push().getKey();

        HashMap<String, Object> map = new HashMap<>();

        map.put("messageId", uniqueId);
        map.put("sender", senderId);
        map.put("receiver", receiverUserId);
        map.put("message", message);
        map.put("url", "yet to implement");
        map.put("isSeen", false);

        databaseReference
                .child(uniqueId)
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            DatabaseReference chatListSenderRef = FirebaseDatabase
                                    .getInstance()
                                    .getReference()
                                    .child("ChatList")
                                    .child(firebaseUser.getUid())
                                    .child(ReceiverUserId);

                            chatListSenderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(!snapshot.exists()){

                                    chatListSenderRef.child("id").setValue(ReceiverUserId);


                                    DatabaseReference chatListReceiverRef = FirebaseDatabase
                                            .getInstance()
                                            .getReference()
                                            .child("ChatList")
                                            .child(ReceiverUserId)
                                            .child(firebaseUser.getUid());

                                    chatListReceiverRef.child("id").setValue(firebaseUser.getUid());


                                }



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });




                        }

                    }
                });





    }


}