package com.restapi.insta.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restapi.insta.Adapter.NotificationAdapter;
import com.restapi.insta.Model.Notification;
import com.restapi.insta.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationFragment extends Fragment {

    private RecyclerView recyclerViewNotification;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationsList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerViewNotification = view.findViewById(R.id.recyclerViewNotification);
        recyclerViewNotification.setHasFixedSize(true);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationsList);
        recyclerViewNotification.setAdapter(notificationAdapter);

        readNotifications();




        return view;
    }

    private void readNotifications() {

        FirebaseDatabase.getInstance().getReference()
                .child("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                            notificationsList.add(snapshot.getValue(Notification.class));

                        }
                        Collections.reverse(notificationsList);
                        notificationAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}