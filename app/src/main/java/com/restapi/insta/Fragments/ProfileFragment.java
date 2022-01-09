package com.restapi.insta.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.FragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Adapter.PhotoAdapter;
import com.restapi.insta.Adapter.PostAdapter;
import com.restapi.insta.EditProfileActivity;
import com.restapi.insta.MainActivity;
import com.restapi.insta.Model.Post;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView fullname;
    private TextView bio;
    private TextView username;
    private TextView postListEmpty;

    private ImageButton myPosts, savedPosts;
    private FirebaseUser firebaseUser;

    private Button editProfile;

    private String profileId;

    //for displaying our posts
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPostList;

    //for displaying saved posts
    private RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdapterSaves;
    private List<Post> mySavedPosts;
    private String data;
    private String fromHomeFrag, fromHomeFrag1 = "false";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        // check and get other users details
        data = getContext().getSharedPreferences("Profile", Context.MODE_PRIVATE)
                .getString("publisherId", "none");




        //Toast.makeText(getContext(), "current user ID:\n"+data, Toast.LENGTH_SHORT).show();

        if (data.equals("none"))
        {
            profileId = firebaseUser.getUid();
            data = "none";

        }
        else {

            profileId = data;
            data = "none";

        }




        imageProfile = view.findViewById(R.id.profileImage);
        options = view.findViewById(R.id.optionsBt);
        posts = view.findViewById(R.id.noOfPosts);
        followers = view.findViewById(R.id.followersCount);
        following = view.findViewById(R.id.followingCount);
        fullname = view.findViewById(R.id.actualName);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.profile_username);
        editProfile = view.findViewById(R.id.editProfileBt);
        myPosts = view.findViewById(R.id.myPosts);
        savedPosts = view.findViewById(R.id.mySavedPosts);
        postListEmpty = view.findViewById(R.id.postListEmpty);

        // Uploaded posts
        recyclerView = view.findViewById(R.id.profileRecyclerViewPosts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPostList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPostList);
        recyclerView.setAdapter(photoAdapter);


        //saved posts

        recyclerViewSaves = view.findViewById(R.id.profileRecyclerViewSaved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPosts = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(), mySavedPosts);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        postListEmpty.setVisibility(View.INVISIBLE);


        userInfo();

        getFollowersAndFollowingCount();

        getPostCount();
        getMyPosts();
        getSavedPosts();


        // set functions to edit profile button
        if(profileId.equals(firebaseUser.getUid())){

            editProfile.setText("Edit profile");
        }else {

            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), EditProfileActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity( intent);

                /*
                String btText = editProfile.getText().toString();

                if(btText.equals("Edit Profile")){

                    // Goto edit profile activity




                }

                else {

                    if (btText.equals("Follow")){

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("following")
                                .child(profileId).setValue(true);


                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);

                    } else {

                        // the user is already following

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("following")
                                .child(profileId).removeValue();


                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).removeValue();


                    }


                }

                */
            }
        });


        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
                myPosts.setImageResource(R.drawable.ic_my_posts);
                savedPosts.setImageResource(R.drawable.ic_save);

                if (myPostList.isEmpty()){

                    postListEmpty.setVisibility(View.VISIBLE);
                }else {
                    postListEmpty.setVisibility(View.INVISIBLE);
                }

            }
        });

        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
                myPosts.setImageResource(R.drawable.ic_post_uncheck);
                savedPosts.setImageResource(R.drawable.ic_post_saved);

                if (mySavedPosts.isEmpty()){

                    postListEmpty.setVisibility(View.VISIBLE);
                }else {
                    postListEmpty.setVisibility(View.INVISIBLE);
                }

            }
        });






        return view;
    }

    private void getSavedPosts() {

        List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    savedIds.add(snapshot.getKey());

                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                        mySavedPosts.clear();
                        for (DataSnapshot snapshot: dataSnapshot1.getChildren()){

                            Post post = snapshot.getValue(Post.class);

                            for (String id: savedIds){

                                if (post.getPostid().equals(id)){

                                    mySavedPosts.add(post);
                                }
                            }


                        }

                        postAdapterSaves.notifyDataSetChanged();




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }



    private void getMyPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myPostList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){

                        myPostList.add(post);

                    }
                }

                Collections.reverse(myPostList);


                photoAdapter.notifyDataSetChanged();

                if (myPostList.isEmpty()){

                    postListEmpty.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(profileId).exists()){

                    editProfile.setText("Following");
                }else {

                    editProfile.setText("Follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int counter = 0;
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){

                        counter+=1;
                    }

                }

                posts.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                followers.setText(dataSnapshot.getChildrenCount()+"");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                following.setText(dataSnapshot.getChildrenCount()+"");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if(user.getImageurl().equals("default")){

                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                }else {

                    Picasso.get().load(user.getImageurl()).into(imageProfile);

                }

                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}