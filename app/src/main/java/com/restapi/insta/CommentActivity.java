package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText addComment;
    private CircleImageView imageProfile;
    private Button postButton;

    private String postId;
    private String authorId;

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar( toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        addComment = findViewById(R.id.add_comment);
        imageProfile = findViewById(R.id.image_profile);
        postButton = findViewById(R.id.post_commentBt);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(addComment.getText().toString())){

                    Toast.makeText(CommentActivity.this, "No comment added", Toast.LENGTH_SHORT).show();
                }else {
                    
                    putComment();
                    
                }

            }
        });


    }

    private void putComment() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", addComment.getText().toString());
        map.put("publisher", firebaseUser.getUid());

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                .push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                
                if (task.isSuccessful()){

                    Toast.makeText(CommentActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CommentActivity.this, "Error:\n"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                
            }
        });




    }

    private void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                //check if user image Uri is null
                if(user.getImageurl().equals("default")){

                    imageProfile.setImageResource(R.mipmap.ic_launcher);

                }else {
                    Picasso.get().load(user.getImageurl()).into(imageProfile);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}