package com.restapi.insta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Adapter.UserSearchAdapter;
import com.restapi.insta.Model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchRv;
    private EditText search_bar;

    private List<User> mUsers;
    private UserSearchAdapter userSearchAdapter;
    public FirebaseUser firebaseUser;

    private TextView userEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userEmpty = findViewById(R.id.userEmpty);

        //open keyboard-auto
        search_bar = findViewById(R.id.searchEditText);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        searchRv = findViewById(R.id.searchRV);
        searchRv.setHasFixedSize(true);

        searchRv.setLayoutManager(new LinearLayoutManager(this));

        mUsers = new ArrayList<>();
        userSearchAdapter = new UserSearchAdapter(this, mUsers);
        searchRv.setAdapter(userSearchAdapter);

        //add users to mUsers
        readUsers();

        //Search users and hastags
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

                        assert user != null;
                        if(!user.getId().equals(firebaseUser.getUid())){
                            mUsers.add(user);
                        }



                    }

                    userSearchAdapter.notifyDataSetChanged();

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

        userSearchAdapter.filteredList(filterList);
        if(filterList.size()==0 ){

            userEmpty.setVisibility(View.VISIBLE);
        }else {
            userEmpty.setVisibility(View.INVISIBLE);
        }

    }


}