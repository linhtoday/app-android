package com.meow.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.chat.R;
import com.meow.chat.Until;
import com.meow.chat.model.Message;
import com.meow.chat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;

    String user_cur;

    String url_image_other = "df";
    private Context mContext;

    String uid_other;
    private ArrayList<Message> arr;

    public MessageAdapter(Context context, ArrayList<Message> messageList, String uid_other) {
        mContext = context;
        arr = messageList;

        //user_cur = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_cur = Until.getInstance().getUid();

        this.uid_other = uid_other;



        FirebaseDatabase.getInstance().getReference("user").child(uid_other).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                url_image_other = (String) dataSnapshot.child("imageURL").getValue();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return arr.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (user_cur.equals(arr.get(position).getFrom())){
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right, parent, false);
            return new RightHolder(view);
        } else {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left, parent, false);
            return new LeftHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Message message = arr.get(position);


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                //((RightHolder) holder).rl.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_fake));
                ((RightHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                //((LeftHolder) holder).rl.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_anim));
                ((LeftHolder) holder).bind(message);
        }
    }

    private class RightHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl;

        TextView text, seen;

        ImageView img;

        RightHolder(View itemView) {

            super(itemView);

            rl = itemView.findViewById(R.id.rl_item_right);
            text =  itemView.findViewById(R.id.show_message);
            seen =  itemView.findViewById(R.id.txt_seen);

            //img =  itemView.findViewById(R.id.img_right);
        }

        void bind(Message message) {

            text.setText(message.getMessage());


        }
    }

    private class LeftHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl;
        TextView text;
        ImageView profile_image;
        ImageView img, avt;
        LeftHolder(View itemView) {

            super(itemView);

            text =  itemView.findViewById(R.id.show_message);

            //img =  itemView.findViewById(R.id.img_left);
            profile_image = itemView.findViewById(R.id.profile_image);

            rl = itemView.findViewById(R.id.rl_item_left);
        }

        void bind(Message message) {


            text.setText(message.getMessage());

            if(url_image_other.equals("df"))
            {
                profile_image.setImageResource(R.drawable.ic_account_circle_24dp);
            }
            else
            {
                Picasso.with(mContext).load(url_image_other).placeholder(R.drawable.ic_account_circle_24dp).into(profile_image);
            }

        }
    }
}
