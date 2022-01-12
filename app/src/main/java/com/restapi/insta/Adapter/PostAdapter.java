package com.restapi.insta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.restapi.insta.CommentActivity;
import com.restapi.insta.FollowersActivity;
import com.restapi.insta.Fragments.OtherUserFragment;
import com.restapi.insta.Fragments.ProfileFragment;
import com.restapi.insta.Model.Post;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    private Context mContext;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }




    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.Viewholder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = mPost.get(position);

        Picasso.get().load(post.getImageUrl()).into(holder.post_image);
        holder.description.setText(post.getDescription());

        // get the user details
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                // check if the image uri is null
                if (user.getImageurl().equals("default")){

                    holder.imageProfle.setImageResource(R.mipmap.ic_launcher);

                }else{
                    Picasso.get().load(user.getImageurl()).into(holder.imageProfle);
                }

                holder.username.setText(user.getUsername());
                holder.author.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isLiked(post.getPostid(), holder.like);
        noOfLikes(post.getPostid(), holder.noOfLikes);
        noOfComments(post.getPostid(), holder.noOfComments);
        isPostSaved(post.getPostid(), holder.save);

        // like feature
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                    // add notification
                    addNotification(post.getPostid(), post.getPublisher());

                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }

            }
        });



        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);


            }
        });

        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);

            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(holder.save.getTag().equals("save")){

                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);

                }else {

                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }



            }
        });


        holder.imageProfle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intend(post);

            }
        });


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intend(post);

            }
        });

        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPublisher());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);

            }
        });


    }

    private void addNotification(String postId, String publisherId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", publisherId);
        map.put("text", "Liked your post");
        map.put("postId", postId);
        map.put("isPost", "true");

        FirebaseDatabase.getInstance().getReference()
                .child("Notifications")
                .child(firebaseUser.getUid())
                .push() // stores in an unique value
                .setValue(map);

    }


    @Override
    public int getItemCount() {
        return mPost.size();
    }

    private void isPostSaved(String postId, ImageView saveImageBt) {

        FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(postId).exists()){

                    saveImageBt.setImageResource(R.drawable.ic_post_saved);
                    saveImageBt.setTag("saved");


                }else {

                    saveImageBt.setImageResource(R.drawable.ic_save);
                    saveImageBt.setTag("save");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void intend(Post post){

        if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

            ((FragmentActivity)mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();



        }else {

            mContext.getSharedPreferences("ProfileOther",Context.MODE_PRIVATE)
                    .edit().putString("publisherID", post.getPublisher()).apply();

            ((FragmentActivity)mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OtherUserFragment())
                    .commit();

        }


    }





    private void isLiked(String postId, ImageView imageView){

        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_like_red);
                    imageView.setTag("liked");
                }else {

                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void noOfLikes(String postId, TextView text){

        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                text.setText(dataSnapshot.getChildrenCount()+ "likes");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void noOfComments (String postId, TextView text){

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(((int) dataSnapshot.getChildrenCount())!= 0 ){
                    text.setText("View all "+ dataSnapshot.getChildrenCount() + " comments");
                }else {

                    text.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public class Viewholder extends RecyclerView.ViewHolder {

        public ImageView imageProfle;
        public ImageView post_image;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;

        public TextView username;
        public TextView noOfLikes;
        public TextView author;
        public TextView noOfComments;
        SocialTextView description;




        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageProfle = itemView.findViewById(R.id.profileImage);
            post_image = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.likeBt);
            comment = itemView.findViewById(R.id.commentBt);
            save = itemView.findViewById(R.id.saveBt);
            more = itemView.findViewById(R.id.moreBt);

            username = itemView.findViewById(R.id.userName);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            noOfComments = itemView.findViewById(R.id.no_0f_comments);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.post_description);




        }
    }
}
