package com.meow.chat.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.chat.R;
import com.meow.chat.Until;
import com.meow.chat.adapter.UserSuggestAdapter;
import com.meow.chat.model.Message;
import com.meow.chat.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {


    RecyclerView rv;

    //SearchView searchView;

    EditText ed;

    UserSuggestAdapter adapter;

    String user_cur = Until.getInstance().getUid();

    ArrayList<User> arr = new ArrayList<>();

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("fragments_user", "onCreateView");
        return inflater.inflate(R.layout.fragment_users, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // searchView = view.findViewById(R.id.search_view_fragment_users);

        rv = view.findViewById(R.id.rv_fragment_users);


        ed = view.findViewById(R.id.search_users);




        adapter = new UserSuggestAdapter(getActivity(), arr);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        rv.setAdapter(adapter);



        DatabaseReference getListFriendSuggest = FirebaseDatabase.getInstance().getReference("user");

        getListFriendSuggest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arr.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    try {
                        User user = snapshot.getValue(User.class);
                        if(user.getUid().equals(user_cur) == false) arr.add(user);
                    }
                    catch (Exception ex)
                    {

                    }


                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        ed.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                adapter.getFilter().filter(s);
//                return false;
//            }
//        });






    }
}
