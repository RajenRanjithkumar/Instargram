package com.restapi.insta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.restapi.insta.Model.Post;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    private Context mContext;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    private List<Post> mPost;

    private FirebaseUser firebaseUser;


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





    }

    @Override
    public int getItemCount() {
        return mPost.size();
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