package com.meow.chat.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.chat.R;
import com.meow.chat.Until;
import com.meow.chat.adapter.ListChatAdapter;
import com.meow.chat.model.User;
import com.meow.chat.service.ChatHeadService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    RecyclerView rv;

    ListChatAdapter adapter;

    String user_cur = Until.getInstance().getUid();
    ArrayList<User> arr = new ArrayList<>();
    ArrayList<String> listUserFriend= new ArrayList<>();

    HashMap<String, String> hashMap = new HashMap<>();
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("fragments_chat", "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("fragments_chat", "onCreateddd View");
       // stopService(new Intent(getActivity(), ChatHeadService.class));
        rv = view.findViewById(R.id.rv_fragment_chats);
        adapter = new ListChatAdapter(getActivity(), arr);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(adapter);


        DatabaseReference getListFriend = FirebaseDatabase.getInstance().getReference("Chat").child(user_cur);

        getListFriend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    //usersList.add(chatlist);
                    listUserFriend.add(snapshot.getKey());
                    hashMap.put(snapshot.getKey(), "1");
                    //Log.d("fragment_chat", " user_cur" + user_cur + "     " + snapshot.getKey());
                }


                getList();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //adapter = new ListChatAdapter(getActivity(), );

    }

    private void getList() {

        DatabaseReference root = FirebaseDatabase.getInstance().getReference("user");


        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arr.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(hashMap.containsKey(snapshot.getKey()))
                    {
                        User user = snapshot.getValue(User.class);
                        arr.add(user);
                    }
                }


                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
