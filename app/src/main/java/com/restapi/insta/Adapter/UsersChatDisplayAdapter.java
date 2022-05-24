package com.restapi.insta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersChatDisplayAdapter extends RecyclerView.Adapter<UsersChatDisplayAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public UsersChatDisplayAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public UsersChatDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_message_item,parent,false);
        return new UsersChatDisplayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersChatDisplayAdapter.ViewHolder holder, int position) {

        User user = mUsers.get(position);

        holder.username.setText(user.getUsername());
        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);




    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfile;
        public TextView username;
        public TextView lastMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.fullname);
        }
    }
}
