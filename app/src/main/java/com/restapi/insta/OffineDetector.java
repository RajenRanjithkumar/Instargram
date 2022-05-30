package com.restapi.insta;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class OffineDetector {


     public void updateStatus(Boolean status, FirebaseUser firebaseUser){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("online", status);

        reference.updateChildren(map);


    }

}
