package com.restapi.insta.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.restapi.insta.Adapter.UserAdapter;
import com.restapi.insta.Model.User;
import com.restapi.insta.R;

import java.util.ArrayList;
import java.util.List;


public class SerachFragment extends Fragment {


    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView search_bar;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private TextView userEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_serach, container, false);

        recyclerView = view.findViewById(R.id.users_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

        search_bar = view.findViewById(R.id.search_bar);
        userEmpty = view.findViewById(R.id.userEmpty);

        //add users to mUsers
        readUsers();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchUser(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())){
                    mUsers.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void searchUser (String searchText){

        //Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                //.orderByChild("username").startAt(searchText).endAt(searchText + "\uf8ff");
        /*

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        ArrayList<User> filterList = new ArrayList<>();

        for(User item: mUsers){
            if (item.getUsername().toLowerCase().trim().contains(searchText.toLowerCase()))
            {
                filterList.add(item);
            }


        }

        userAdapter.filteredList(filterList);
        if(filterList.size()==0 ){

            userEmpty.setVisibility(View.VISIBLE);
        }else {
            userEmpty.setVisibility(View.INVISIBLE);
        }






    }


}