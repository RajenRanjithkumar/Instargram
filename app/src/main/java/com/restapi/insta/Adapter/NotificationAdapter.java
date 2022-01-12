package com.restapi.insta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Fragments.OtherUserFragment;
import com.restapi.insta.Fragments.postDetailFragment;
import com.restapi.insta.Model.Notification;
import com.restapi.insta.Model.Post;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotifications;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification = mNotifications.get(position);

        getUser(holder.imageProfile, holder.userName, notification.getUserId());

        if (notification.getIsPost().equals("true")){

            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostId());
            holder.comment.setText(notification.getText());
        }else {
            holder.postImage.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (notification.getIsPost().equals("true")){

                    mContext.getSharedPreferences("userType", Context.MODE_PRIVATE)
                            .edit().putString("postPublisher", notification.getUserId()).apply();
                                                                //notification.getPostId()

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new postDetailFragment()).commit();
                }else {

                    mContext.getSharedPreferences("ProfileOther", Context.MODE_PRIVATE)
                            .edit().putString("publisherID", notification.getPostId()).apply();
                    //notification.getPostId()

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new OtherUserFragment()).commit();


                }

            }
        });



    }



    @Override
    public int getItemCount() {
        return mNotifications.size();
    }





    private void getUser(CircleImageView imageProfile, TextView userName, String userId) {

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user.getImageurl().equals("default"))
                {
                    Picasso.get().load(R.mipmap.ic_launcher_round).into(imageProfile);
                }else {
                    Picasso.get().load(user.getImageurl()).into(imageProfile);
                }
                userName.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getPostImage(ImageView postImage, String postId) {

        FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Post post = dataSnapshot.getValue(Post.class);
                Picasso.get().load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(postImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public ImageView postImage;
        public TextView userName;
        public TextView comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            postImage = itemView.findViewById(R.id.postImageNoti);
            userName = itemView.findViewById(R.id.usernameNoti);
            comment = itemView.findViewById(R.id.commentNoti);




        }
    }
}
