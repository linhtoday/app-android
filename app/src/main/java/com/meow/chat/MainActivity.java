package com.meow.chat;

import android.content.Intent;
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
import com.meow.chat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    String user_cur = Until.getInstance().getUid();

    DatabaseReference putStatus, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("main_activity","onCreate: ");
        Toolbar toolbar = findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        init();

        getInfo(); // get avt + name






    }

    private void getInfo() {

        Log.d("getInfo", user_cur);
        info = FirebaseDatabase.getInstance().getReference("user").child(user_cur);

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

                username.setText(user.getUsername());

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
        username = findViewById(R.id.username);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment(), "Chat");
        viewPagerAdapter.addFragment(new UsersFragment(), "User");
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

        viewPager = findViewById(R.id.view_pager);

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);

        tabLayout.setupWithViewPager(viewPager);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        //user_cur = mAuth.getCurrentUser().getUid();


        Log.d("user_cur =", user_cur);
        putStatus = FirebaseDatabase.getInstance().getReference("user").child(user_cur);




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

                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                finish();

                return true;
        }

        return false;
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
        status("online");
        Log.d("main_activity","onResume: ");
        //Log.d("user_cur", "onResume: " + user_cur);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        Log.d("main_activity","onPause: ");

        //Log.d("user_cur","onPause: " + user_cur);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("main_activity","onDestroy: ");
    }
}
