package com.meow.chat.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.Test2Activity;
import com.meow.chat.R;
import com.meow.chat.TestActivity;

import java.util.HashMap;

public class BubbleChatService extends Service {

    public static String user_cur = "1";
    public static int cnt = 0;
    private WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    //private View mChatHeadView;
    HashMap<String, View> hashMap;
    DatabaseReference bubble;
    String user_other = "";
    //View f;
    boolean c = false;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();




        hashMap = new HashMap<>();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        cnt++;

        user_cur = FirebaseAuth.getInstance().getUid();



        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        Log.d("wtfA", "onCreate" + " " + user_cur);

        FirebaseDatabase.getInstance().getReference("bubble").child(user_cur).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item: dataSnapshot.getChildren()) {

                    user_other = item.getKey();
                    int cnt = item.getValue(Integer.class);
                    if(cnt != 0)
                    {

                        Log.d("updateX", cnt+"");
                        Log.d("caigivai", "deovaoa");
                        //if(user_other.contains(user_cur)) continue;
//                        if(hashMap.containsKey(user_other) && hashMap.get(user_other) != null) continue;

                        if(hashMap.containsKey(user_other) == false) hashMap.put(user_other, LayoutInflater.from(BubbleChatService.this).inflate(R.layout.chat_head, null));
                        final View x = hashMap.get(user_other);

                        mWindowManager.addView(x, params);

                        TextView text_cnt_message = x.findViewById(R.id.cnt_mess);

                        text_cnt_message.setText(cnt+"");


                        ImageView closeButton = (ImageView) x.findViewById(R.id.close_btn);
                        closeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //close the service and remove the chat head from the window
                                //  stopSelf();
                            }
                        });

                        final LinearLayout ll = x.findViewById(R.id.ll_bubble_message);

                        final TextView tv = x.findViewById(R.id.text_test);

                        ll.animate()
                                .alpha(0.0f)
                                .setDuration(4000)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        ll.setVisibility(View.GONE);
                                    }
                                });

                        final ImageView chatHeadImage = (ImageView) x.findViewById(R.id.chat_head_profile_iv);

                        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
                            private int lastAction;
                            private int initialX;
                            private int initialY;
                            private float initialTouchX;
                            private float initialTouchY;

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:

                                        //remember the initial position.
                                        initialX = params.x;
                                        initialY = params.y;

                                        //get the touch location
                                        initialTouchX = event.getRawX();
                                        initialTouchY = event.getRawY();

                                        lastAction = event.getAction();
                                        return true;
                                    case MotionEvent.ACTION_UP:
                                        //As we implemented on touch listener with ACTION_MOVE,
                                        //we have to check if the previous action was ACTION_DOWN
                                        //to identify if the user clicked the view or not.
                                        if (lastAction == MotionEvent.ACTION_DOWN) {
                                            //Open the chat conversation click.
                                            Intent intent = new Intent(BubbleChatService.this, Test2Activity.class);
//                                        tv.setAnimation(AnimationUtils.loadAnimation(BubbleChatService.this, R.anim.fade_fake));
//                                        tv.setVisibility(View.GONE);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                            //close the service and remove the chat heads
                                            stopSelf();
                                        }
                                        lastAction = event.getAction();
                                        return true;
                                    case MotionEvent.ACTION_MOVE:
                                        //Calculate the X and Y coordinates of the view.
                                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                                        //Update the layout with new X & Y coordinate
                                        mWindowManager.updateViewLayout(x, params);
                                        lastAction = event.getAction();
                                        return true;
                                }
                                return false;
                            }
                        });

                    }

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("wtfA", "onDestroy");
        //if (f != null) mWindowManager.removeView(f);
    }
}
