package com.restapi.insta.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Adapter.PhotoAdapter;
import com.restapi.insta.Model.Post;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class OtherUserFragment extends Fragment {

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

    private String dataFromAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_other_user, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imageProfile = view.findViewById(R.id.otherProfileImage);
        options = view.findViewById(R.id.optionsBtOther);
        posts = view.findViewById(R.id.otherNoOfPosts);
        followers = view.findViewById(R.id.followersCountOther);
        following = view.findViewById(R.id.followingCountOther);
        fullname = view.findViewById(R.id.actualNameOther);
        bio = view.findViewById(R.id.bioOther);
        username = view.findViewById(R.id.profile_usernameOther);
        editProfile = view.findViewById(R.id.editProfileBtOther);
        myPosts = view.findViewById(R.id.myPostsOther);
        postListEmpty = view.findViewById(R.id.postListEmptyOther);

        // Uploaded posts
        recyclerView = view.findViewById(R.id.profileRecyclerViewPostsOther);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPostList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPostList);
        recyclerView.setAdapter(photoAdapter);

        dataFromAdapter = getContext().getSharedPreferences("ProfileOther", Context.MODE_PRIVATE)
                .getString("publisherID", "none");

        profileId = dataFromAdapter;


        userInfo();

        getFollowersAndFollowingCount();

        getPostCount();
        getMyPosts();
        checkFollowingStatus();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String btText = editProfile.getText().toString();

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
        });






        return view;
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




}