package com.meow.chat.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import com.meow.chat.MessageActivity;
import com.meow.chat.R;
import com.meow.chat.TestActivity;
import com.meow.chat.model.MessageBubbleChat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatHeadService extends Service {
    public static String user_cur = "1";

    private WindowManager mWindowManager;


    //private View mChatHeadView;
    HashMap<String, Boolean> checkExist;

    HashMap<String, View> views;

    HashMap<String, Pair<Integer, Integer>> toado;

    ArrayList<View> arr = new ArrayList<>();

    HashMap<String, Integer> last_cnt;
    DatabaseReference bubble;



    void deleteAllBubble()
    {

    //   ArrayList<String> a = new ArrayList<>();
        for(HashMap.Entry<String, Boolean> entry : checkExist.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();

            if(value == true)
            {
                try
                {
                    mWindowManager.removeView(views.get(key));
                }
                catch (Exception ex)
                {

                }
            }
        }

        checkExist.clear();


        for(int i = 0; i < arr.size(); i++)
        {
            //if(mWindowManager.)
            try
            {
                mWindowManager.removeView(arr.get(i));
            }
            catch (Exception ex)
            {

            }

        }

        arr.clear();

        last_cnt.clear();

    }
    public ChatHeadService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();



        checkExist = new HashMap<>();

        toado = new HashMap<>();

        views = new HashMap<>();

        last_cnt = new HashMap<>();


        if(isMyServiceRunning(ChatHeadService.class))
        {
            Log.d("ameo", "khong co activity nao ne");
            deleteAllBubble();
            stopSelf();
        }
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        user_cur = FirebaseAuth.getInstance().getUid();


        if(user_cur == null) return;


        FirebaseDatabase.getInstance().getReference("bubble").child(user_cur).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //deleteAllBubble();

                arr.clear();

                for (DataSnapshot item: dataSnapshot.getChildren()) {


                    final String user_other = item.getKey();

                    MessageBubbleChat bubble  = item.getValue(MessageBubbleChat.class);



                    if(bubble.getCnt() != 0)
                    {


//                        HashMap<String, Object> temp = new HashMap<>();
//                        temp.put("cnt", 0);
//                        temp.put("mes", "xxx");
//                        item.getRef().updateChildren(temp);
                        final View x;

                        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.TYPE_PHONE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                PixelFormat.TRANSLUCENT);
                        params.gravity = Gravity.TOP | Gravity.LEFT;

                        params.x = 0;

                        params.y = 100;

                        final ViewHolder viewHolder;

                        if(checkExist.get(user_other) == null || checkExist.get(user_other).booleanValue() == false)
                        {
                            Log.d("thelast", "aaaaaaaaaaaaaaaaa");
                            x = LayoutInflater.from(ChatHeadService.this).inflate(R.layout.chat_head, null);

                            LinearLayout ll = x.findViewById(R.id.ll_bubble_message);

                            viewHolder = new ViewHolder();

                            viewHolder.avt = x.findViewById(R.id.chat_head_profile_iv);
                            viewHolder.close_btn = x.findViewById(R.id.close_btn);
                            viewHolder.cnt_mess = x.findViewById(R.id.cnt_mess);
                            viewHolder.text_mess = x.findViewById(R.id.text_test);
                            viewHolder.ll = x.findViewById(R.id.ll_bubble_message);


                            views.put(user_other, x);
                            mWindowManager.addView(x, params);
                            Pair<Integer, Integer> pair = new Pair<>(0, 100);
                            toado.put(user_other, pair);

                            x.setTag(viewHolder);

                            checkExist.put(user_other, true);

                            arr.add(x);
                        }
                        else
                        {
                            x = views.get(user_other);

                            Pair<Integer, Integer> pair = toado.get(user_other);

                            params.x = pair.first;

                            params.y = pair.second;

                            mWindowManager.updateViewLayout(x, params);

                            viewHolder = (ViewHolder) x.getTag();
                        }



                        if(last_cnt.get(user_other) == null || last_cnt.get(user_other).compareTo(bubble.getCnt()) != 0)
                        {

                            last_cnt.put(user_other, bubble.getCnt());

                            viewHolder.ll.setVisibility(View.INVISIBLE);

                            Animation fadeOut = new AlphaAnimation(1, 0);
                            fadeOut.setInterpolator(new AccelerateInterpolator());
                            fadeOut.setDuration(3000);

                            fadeOut.setAnimationListener(new Animation.AnimationListener()
                            {
                                public void onAnimationEnd(Animation animation)
                                {
                                    viewHolder.ll.setVisibility(View.GONE);
                                }
                                public void onAnimationRepeat(Animation animation) {}
                                public void onAnimationStart(Animation animation) {}
                            });

                            viewHolder.ll.startAnimation(fadeOut);

                        }


                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        v.vibrate(400);

//                        viewHolder.ll.animate()
//
//                                .alpha(0.0f)
//                                .setDuration(3000)
//
//                                .setListener(new AnimatorListenerAdapter() {
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        super.onAnimationEnd(animation);
//                                        viewHolder.ll.setVisibility(View.GONE);
//                                        Log.d("meomeo", "end");
//                                    }
//
//                                    @Override
//                                    public void onAnimationStart(Animator animation) {
//                                        super.onAnimationStart(animation);
//                                        viewHolder.ll.setVisibility(View.VISIBLE);
//                                        Log.d("meomeo", "start");
//                                    }
//                                });
//


//                        viewHolder.ll.setAnimation(AnimationUtils.loadAnimation(ChatHeadService.this, R.anim.fade_fake).setAnimationListener(new Animation.AnimationListener() {
//                            @Override
//                            public void onAnimationStart(Animation animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animation animation) {
//
//                            }
//                        }));




                        DatabaseReference avt_other = FirebaseDatabase.getInstance().getReference("user").child(user_other);
                        avt_other.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String url = dataSnapshot.child("imageURL").getValue(String.class);
                                if(url.equals("df"))
                                {
                                    viewHolder.avt.setImageResource(R.drawable.ic_account_circle_24dp);
                                }
                                else
                                {
                                    Picasso.with(ChatHeadService.this).load(url).placeholder(R.drawable.ic_account_circle_24dp).into(viewHolder.avt);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });




                        viewHolder.cnt_mess.setText(bubble.getCnt()+"");



                        viewHolder.close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //close the service and remove the chat head from the window
                                //deleteAllBubble();
                                mWindowManager.removeView(x);
                                checkExist.put(user_other, false);
                                  //stopSelf();
                            }
                        });





                        viewHolder.text_mess.setText(bubble.getMes());



                       // viewHolder.ll.setAnimation(AnimationUtils.loadAnimation(ChatHeadService.this, R.anim.fade_fake));




                        viewHolder.avt.setOnTouchListener(new View.OnTouchListener() {
                            private int lastAction;
                            private int initialX = 0;
                            private int initialY = 100;
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
                                            Intent intent = new Intent(ChatHeadService.this, MessageActivity.class);
                                            intent.putExtra("user_other", user_other);
//                                        tv.setAnimation(AnimationUtils.loadAnimation(BubbleChatService.this, R.anim.fade_fake));
//                                        tv.setVisibility(View.GONE);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                            //close the service and remove the chat heads
                                            deleteAllBubble();
                                            stopSelf();
                                        }
                                        lastAction = event.getAction();

                                        return true;
                                    case MotionEvent.ACTION_MOVE:
                                        //Calculate the X and Y coordinates of the view.
                                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                                        Pair<Integer, Integer> pair = new Pair<>(params.x, params.y);

                                        toado.put(user_other, pair);

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



    public class ViewHolder
    {
        public LinearLayout ll;
        public ImageView avt, close_btn;
        public TextView cnt_mess, text_mess;

    }



    boolean emptyActivity()
    {
        ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

        if(taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            return false;
        }

        return true;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isMyServiceRunning(ChatHeadService.class))
        {
            Log.d("ameo", "khong co activity nao ne");
            deleteAllBubble();
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
        deleteAllBubble();
    }
}