package com.restapi.insta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Model.Comment;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {

    private Context mContext;
    private List<Comment> mComments;
    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);

        return new CommentAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Comment comment = mComments.get(position);

        holder.userComment.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                holder.usernameComment.setText(user.getUsername());

                if(user.getImageurl().equals("default")){

                    holder.commentImageProfile.setImageResource(R.mipmap.ic_launcher);

                }else {
                    Picasso.get().load(user.getImageurl()).into(holder.commentImageProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public CircleImageView commentImageProfile;
        public TextView usernameComment;
        public TextView userComment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            commentImageProfile = itemView.findViewById(R.id.comment_image_profile);
            usernameComment = itemView.findViewById(R.id.comment_username);
            userComment = itemView.findViewById(R.id.comment);


        }
    }
}
