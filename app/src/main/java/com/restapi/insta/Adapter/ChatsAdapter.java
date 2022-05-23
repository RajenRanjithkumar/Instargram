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
import com.restapi.insta.Model.Chat;
import com.restapi.insta.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChatList;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public ChatsAdapter(Context mContext, List<Chat> mChatList) {
        this.mContext = mContext;
        this.mChatList = mChatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {


        // message_item_left  position == 0
        // message_item_Right  position == 1
        if(position == 0){

            View rView = LayoutInflater.from(mContext).inflate(R.layout.message_right_item, parent, false);


            return new ChatsAdapter.ViewHolder(rView);


        }else {

            View lView;
            lView = LayoutInflater.from(mContext).inflate(R.layout.message_left_item, parent, false);
            return new ChatsAdapter.ViewHolder(lView);

        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Chat chat = mChatList.get(position);

        holder.message.setText(chat.getMessage());



    }

    @Override
    public int getItemCount() {


        return mChatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Chat chat = mChatList.get(position);

        if(chat.getSender().equals(firebaseUser.getUid())){
            return 0;

        }else {
            return 1;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{



        public TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.show_text_message);



        }
    }


}




