package com.meow.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.chat.adapter.MessageAdapter;
import com.meow.chat.adapter.TestAdapter;
import com.meow.chat.model.Message;
import com.meow.chat.model.MessageBubbleChat;
import com.meow.chat.model.User;
import com.meow.chat.service.ChatHeadService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    boolean checkTyping = false;
    RecyclerView rv;
    EditText content_chat;
    String user_cur = "";
    String user_other = "";
    ImageButton btnSend;

    int count_message_user_other = 0;
    TextView tvUsernameOther;
    ImageView profile_image;
    LinearLayout linearLayout;
    ArrayList<Message> arr = new ArrayList<>();

    MessageAdapter adapter;

    ValueEventListener listenerTyping;

    DatabaseReference getListMessage, send, user_cur_typing, user_other_typing, info, cnt_user_cur, cnt_user_other;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        stopService(new Intent(this, ChatHeadService.class));

        init();


        getData();

        actionEnter();

        actionSend();

        actionTyping();

        getInfo();


        countMessageUserOther();

    }

    private void countMessageUserOther() {

        cnt_user_other.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() == false) return;



                count_message_user_other = dataSnapshot.child("cnt").getValue(Integer.class);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void actionTyping() {

        content_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(checkTyping == false)
                {
                    checkTyping = true;
                    typing(checkTyping);
                    Log.d("typing", "olala");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d("typingAfter", editable.toString());
                if(editable.toString().equals(""))
                {
                    checkTyping = false;
                    typing(checkTyping);
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.d("messageActivity", "onPause");

        checkTyping = false;

        typing(checkTyping);

        user_other_typing.removeEventListener(listenerTyping);




    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("messageActivity", "onResume");
        listenerTyping = user_other_typing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() == false) return;

                boolean check = dataSnapshot.child("typing").getValue(Boolean.class);

                if(check)
                {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    linearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void actionSend() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //long timecur = getTime();
                Message mes = new Message(content_chat.getText().toString(),"text", false,user_cur);
                MessageBubbleChat bubble = new MessageBubbleChat(count_message_user_other+1, content_chat.getText().toString());

                cnt_user_other.setValue(bubble);

                content_chat.setText("");

                send.child(user_cur).child(user_other).push().setValue(mes);

                send.child(user_other).child(user_cur).push().setValue(mes);

                checkTyping = false;

                typing(checkTyping);




//                HashMap<String, Object> hashMap = new HashMap<>();
//
//
//
//                hashMap.put(user_cur, true);
//
//                bubble.updateChildren(hashMap);

                //timestamp.setValue(timecur);
            }
        });
    }

    private void getData() {

        getListMessage.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("aaaaa", "vao day na");
                Message mes = dataSnapshot.getValue(Message.class);
                arr.add(mes);
                adapter.notifyDataSetChanged();
                //Log.d("daduocthem", arr.size() + " " + mes.toString());
                rv.smoothScrollToPosition(arr.size()-1);

                //MessageBubbleChat bubble = new MessageBubbleChat(count_message_user_other+1, content_chat.getText().toString());

                //cnt_user_other.setValue(new MessageBubbleChat(0,"xxx"));






            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profile_image = findViewById(R.id.profile_image);

        tvUsernameOther = findViewById(R.id.username);

        linearLayout = findViewById(R.id.typing);

        user_cur = Until.getInstance().getUid();

        rv = findViewById(R.id.recycler_view);

        user_other = getIntent().getStringExtra("user_other");

        adapter = new MessageAdapter(this,arr, user_other);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        content_chat = findViewById(R.id.text_send);


        btnSend = findViewById(R.id.btn_send);


        getListMessage = FirebaseDatabase.getInstance().getReference("Chat").child(user_cur).child(user_other);

        send = FirebaseDatabase.getInstance().getReference("Chat");

        user_other_typing = FirebaseDatabase.getInstance().getReference("typing").child(user_other).child(user_cur);

        //bubble = FirebaseDatabase.getInstance().getReference("bubble").child(user_other);

        user_cur_typing = FirebaseDatabase.getInstance().getReference("typing").child(user_cur).child(user_other);

        cnt_user_cur = FirebaseDatabase.getInstance().getReference("bubble").child(user_cur).child(user_other);

        cnt_user_other = FirebaseDatabase.getInstance().getReference("bubble").child(user_other).child(user_cur);



    }

    private void actionEnter() {
        content_chat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){

                    Message mes = new Message(content_chat.getText().toString(),"text", false,user_cur);

                    MessageBubbleChat bubble = new MessageBubbleChat(count_message_user_other+1, content_chat.getText().toString());

                    cnt_user_other.setValue(bubble);

                    content_chat.setText("");

                    send.child(user_cur).child(user_other).push().setValue(mes);

                    send.child(user_other).child(user_cur).push().setValue(mes);

                    checkTyping = false;

                    typing(checkTyping);

//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put(user_cur, true);
//
//                    bubble.updateChildren(hashMap);
                    //cnt_user_other.setValue(count_message_user_other+1);

                    return true;
                }

                return false;
            }
        });

    }

    void typing(boolean isTyping)
    {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("typing", isTyping);

        user_cur_typing.updateChildren(hashMap);
    }

    private void getInfo() {

        Log.d("getInfo", user_cur);
        info = FirebaseDatabase.getInstance().getReference("user").child(user_other);

        info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if(user.getImageURL().equals("df"))
                {
                    profile_image.setImageResource(R.drawable.ic_launcher_background);
                }
                else
                {
                    Picasso.with(getApplicationContext()).load(user.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(profile_image);
                }

                tvUsernameOther.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        cnt_user_cur.setValue(new MessageBubbleChat(0,"xxx")); // vì đang ở màn hình xem tin nhắn luôn nên coi như không có tin nhắn nào mới
    }
}
