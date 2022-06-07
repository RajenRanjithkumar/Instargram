package com.restapi.insta.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.ChatActivity;
import com.restapi.insta.Model.Chat;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersChatDisplayAdapter extends RecyclerView.Adapter<UsersChatDisplayAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private String Lastmsg;

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
        //holder.lastMessage.setText("Last msg");
        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        if (user.getOnline()){
            holder.onlineIcon.setVisibility(View.VISIBLE);

        }else {
            holder.onlineIcon.setVisibility(View.VISIBLE);
            holder.onlineIcon.setImageResource(android.R.color.darker_gray);

        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userId", user.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });

        //getLastMessage(user.getId(), holder.lastMessage);




    }

    private void getLastMessage(String chatUserId, TextView lastMessage) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    Chat chat = dataSnapshot.getValue(Chat.class);

                    assert firebaseUser != null;
                    //changed
                    //if (!firebaseUser.getUid().isEmpty() && !chat.getMessage().isEmpty()){
                    if (!firebaseUser.getUid().isEmpty() && !chat.getMessage().isEmpty()){

                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(chatUserId)
                        || chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(chatUserId)){


                            Lastmsg = chat.getMessage();


                        }


                        }

                }
                lastMessage.setText(Lastmsg);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfile;
        public TextView username;
        public TextView lastMessage;
        public CircleImageView onlineIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            onlineIcon = itemView.findViewById(R.id.onlineIcon);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.fullname);
        }
    }
}
