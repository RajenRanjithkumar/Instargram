package com.restapi.insta.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.restapi.insta.ChatActivity;
import com.restapi.insta.DatabaseHelper;
import com.restapi.insta.Model.Chat;
import com.restapi.insta.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChatList;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();



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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                //custom dialog
                Dialog dialog = new Dialog(view.getRootView().getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                RelativeLayout okButton = dialog.findViewById(R.id.yesBt);
                RelativeLayout cancelButton = dialog.findViewById(R.id.noBt);


                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //deleteMessage(holder, holder.getAdapterPosition());
                        deleteMessageFromSql(holder, holder.getAdapterPosition());
                        dialog.dismiss();



                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();



                return true;
            }

        });




    }

    private void deleteMessageFromSql(ViewHolder holder, int adapterPosition){

        String messageId = mChatList.get(adapterPosition).getMessageId();
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        databaseHelper.deleteMessage(messageId);
        notifyDataSetChanged();


    }



    // use this for delete for everyone feature
    private void deleteMessage(ViewHolder holder, int adapterPosition) {

        databaseReference.child("Chats")
                .child(mChatList.get(adapterPosition).getMessageId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(holder.itemView.getContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                            }

                    }
                });
    }


    @Override
    public int getItemCount() {


        return mChatList.size();
    }

    // to change the layouts
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




