package com.restapi.insta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Fragments.OtherUserFragment;
import com.restapi.insta.Fragments.ProfileFragment;
import com.restapi.insta.MainActivity;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private boolean isMessage;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment, boolean isMessage) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
        this.isMessage = isMessage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent, false);

        return new UserAdapter.ViewHolder(view);


    }


    //define logic
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = mUsers.get(position);

        holder.btnFollow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());

        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        isFollowed(user.getId(), holder.btnFollow);


        if (user.getId().equals(firebaseUser.getUid())){

            holder.btnFollow.setVisibility(View.GONE);

        }


        // user following feature
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.btnFollow.getText().toString().toLowerCase().equals("follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child((firebaseUser.getUid()))
                            .child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());


                }else {

                    FirebaseDatabase.getInstance().getReference().child("Follow").child((firebaseUser.getUid()))
                            .child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();


                }


            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment){

                    mContext.getSharedPreferences("ProfileOther", Context.MODE_PRIVATE)
                            .edit()
                            .putString("publisherID", user.getId()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new OtherUserFragment())
                            .commit();


                }else {

                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherId", user.getId());
                    mContext.startActivity(intent);


                }

            }
        });




    }

    private void addNotification(String userId) {


        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("text", "Started following you");
        map.put("postId", "");
        map.put("isPost", "false");

        FirebaseDatabase.getInstance().getReference()
                .child("Notifications")
                .child(firebaseUser.getUid())
                .push() // stores in an unique value
                .setValue(map);



    }

    private void isFollowed(final String id, Button btnFollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(id).exists()){

                    btnFollow.setText("Following");

                }else {

                    btnFollow.setText("Follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void filteredList(ArrayList<User> filterList) {

        mUsers = filterList;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView username;
        public TextView fullname;
        public Button btnFollow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            btnFollow = itemView.findViewById(R.id.follow_bt);
        }
    }



}
