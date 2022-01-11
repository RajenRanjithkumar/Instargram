package com.restapi.insta.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Adapter.PostAdapter;
import com.restapi.insta.Model.Post;
import com.restapi.insta.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class postDetailFragment extends Fragment {

    private Integer postPosition;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    String profileId;
    private FirebaseUser firebaseUser;
    private LinearLayoutManager linearLayoutManager;
    private String userType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        postPosition = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getInt("postPosition", 0);
        Toast.makeText(getContext(), ""+postPosition, Toast.LENGTH_SHORT).show();

        userType = getContext().getSharedPreferences("userType", Context.MODE_PRIVATE).getString("postPublisher", firebaseUser.getUid());

        recyclerView = view.findViewById(R.id.myPostsDetailedRv);

        //profileId = userType;

        if (!userType.equals(firebaseUser.getUid())){

            profileId = userType;
        }else {

            profileId = firebaseUser.getUid();
        }




        LinearSmoothScroller smoothScroller=new LinearSmoothScroller(getContext()){
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };


        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        //staggeredGridLayoutManager.scrollToPosition(postPosition);

        smoothScroller.setTargetPosition(postPosition);  // pos on which item you want to scroll recycler view
        Objects.requireNonNull(recyclerView.getLayoutManager()).startSmoothScroll(smoothScroller);



        getMyPosts();

        return view;


    }





    private void getMyPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){

                        postList.add(post);

                    }
                }

                Collections.reverse(postList);


                postAdapter.notifyDataSetChanged();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}