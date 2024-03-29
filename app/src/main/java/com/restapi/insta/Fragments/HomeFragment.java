package com.restapi.insta.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Adapter.PostAdapter;
import com.restapi.insta.MessageActivity;
import com.restapi.insta.Model.Post;
import com.restapi.insta.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private ImageView messageIcon;
    private List<Post> postList;

    private List<String> followingList;

    private LinearLayoutManager linearLayoutManager;
    Parcelable state;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        messageIcon = view.findViewById(R.id.messageIcon);

        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());


        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);

        recyclerViewPosts.setAdapter(postAdapter);
        linearLayoutManager.onRestoreInstanceState(state);


        followingList = new ArrayList<>();

        checkFollowingUsers();



        messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                Intent intent = new Intent(getContext(), MessageActivity.class);

                startActivity(intent, options.toBundle());


                //Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();



            }
        });

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

        linearLayoutManager.onRestoreInstanceState(state);
    }

    @Override
    public void onPause() {
        super.onPause();

        state = linearLayoutManager.onSaveInstanceState();
    }

    private void checkFollowingUsers() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followingList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                   followingList.add(snapshot.getKey());

                }
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                //once we get the following list, get their posts

                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void readPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Post post = snapshot.getValue(Post.class);

                    for (String id : followingList){

                        if (post.getPublisher().equals(id)){

                            postList.add(post);

                        }

                    }

                }

                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}