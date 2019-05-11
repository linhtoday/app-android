package com.meow.chat;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.chat.Fragment.ChatsFragment;
import com.meow.chat.Fragment.ProfileFragment;
import com.meow.chat.Fragment.UsersFragment;
import com.meow.chat.model.MessageBubbleChat;
import com.meow.chat.model.User;
import com.meow.chat.service.BubbleChatService;
import com.meow.chat.service.ChatHeadService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{


    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;


    TextView tv[] = new TextView[5];

    String user_cur = Until.getInstance().getUid();

    DatabaseReference putStatus, cnt_user_cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Log.d("main_activity","onCreate: ");

        init();

        swipeItemViewPager();




    }



    private void swipeItemViewPager() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changeColorText(i);
                Log.d("card_view", i+"");
                if(i==2)
                {
                    Intent intent = new Intent();
                    intent.setAction("My Broadcast");
                    LocalBroadcastManager.getInstance(TestActivity.this).sendBroadcast(intent);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void init() {


        tv[0] = findViewById(R.id.test_chat);
        tv[1] = findViewById(R.id.test_user);
        tv[2] = findViewById(R.id.test_profile);


        tv[0].setOnClickListener(this);
        tv[1].setOnClickListener(this);
        tv[2].setOnClickListener(this);

        changeColorText(0);

        viewPager = findViewById(R.id.view_pager_test);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment(), "Chat");
        viewPagerAdapter.addFragment(new UsersFragment(), "User");
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
        viewPager.setAdapter(viewPagerAdapter);


        viewPager.setCurrentItem(0);

        viewPager.setOffscreenPageLimit(3);

        putStatus = FirebaseDatabase.getInstance().getReference("user").child(user_cur);

        cnt_user_cur = FirebaseDatabase.getInstance().getReference("bubble").child(user_cur);

    }

    void resetListCountMessage()
    {
        cnt_user_cur.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("cnt", 0);
                    hashMap.put("mes", "xxx");

                    item.getRef().updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case  R.id.logout:

                //Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                //startActivity(intent);

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(TestActivity.this, StartActivity.class);
                startActivity(intent);
                finish();

                return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.test_chat:
                changeColorText(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.test_user:
                changeColorText(1);
                viewPager.setCurrentItem(1);
                break;
            case R.id.test_profile:
                viewPager.setCurrentItem(2);
                changeColorText(2);
                break;

        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status){

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);

        putStatus.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(this, ChatHeadService.class));
        status("online");
        Log.d("main_activity","onResume: ");
        resetListCountMessage();
        //Log.d("user_cur", "onResume: " + user_cur);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //startService(new Intent(this, BubbleChatService.class));
        stopService(new Intent(this, ChatHeadService.class));
        Log.d("main_activity","onPause: ");

        //Log.d("user_cur","onPause: " + user_cur);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("main_activity","onDestroy: ");
        status("offline");
        startService(new Intent(this, ChatHeadService.class));

    }

    void changeColorText(int idx)
    {

        for(int i = 0; i < 3; i++)
        {
            tv[i].setTextSize(16);
            tv[i].setTextColor(getResources().getColor(R.color.textTabBright));
        }

        tv[idx].setTextSize(22);

        //tv[idx].set
        tv[idx].setTypeface(Typeface.DEFAULT_BOLD);

        tv[idx].setTextColor(getResources().getColor(R.color.chinh));

    }

    @Override
    protected void onStop() {

        super.onStop();
        stopService(new Intent(this, ChatHeadService.class));
        resetListCountMessage();

    }




}
