package com.meow.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.meow.chat.MessageActivity;
import com.meow.chat.R;
import com.meow.chat.Until;
import com.meow.chat.model.Message;
import com.meow.chat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder> {

    Context context;

    ArrayList<User> arr;

    String user_cur = Until.getInstance().getUid();

    public ListChatAdapter(Context context, ArrayList<User> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v;

        v = LayoutInflater.from(context).inflate(R.layout.user_chat_item, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.ll.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_anim));

        User item = arr.get(position);

        holder.bind(item);

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView ll;
        ImageView avt, on, off;
        TextView text, username;

        View v;
        DatabaseReference update_last_message;

        public ViewHolder(View itemView) {
            super(itemView);

            v = itemView;
            avt = itemView.findViewById(R.id.user_chat_item_avt);
            text = itemView.findViewById(R.id.user_chat_item_text);
            username = itemView.findViewById(R.id.user_chat_item_username);
            ll = itemView.findViewById(R.id.ll_user_item);
            on = itemView.findViewById(R.id.img_on);
            off = itemView.findViewById(R.id.img_off);
        }

        public void bind(final User item) {


            username.setText(item.getUsername());

            if (item.getImageURL().equals("df")) {
                avt.setImageResource(R.drawable.ic_account_circle_24dp);
            } else {
                Picasso.with(context).load(item.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(avt);
            }


            final String user_other = item.getUid();



            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("user_other", item.getUid());
                    context.startActivity(intent);
                }
            });


            FirebaseDatabase.getInstance().getReference("user").child(item.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String status = (String) dataSnapshot.child("status").getValue();
                    if(status.equals("online"))
                    {
                        on.setVisibility(View.VISIBLE);
                        off.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        off.setVisibility(View.VISIBLE);
                        on.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Chat").child(user_cur).child(user_other);

            Query query = db.orderByKey().limitToLast(1);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Log.d("wtf", dataSnapshot.getKey());

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Log.d("wtf", snapshot.getKey());

                        Message message = snapshot.getValue(Message.class);

                        if (message.getType().equals("text")) {
                            text.setText(message.getMessage());
                        } else {
                            text.setText("da gui mot anh");
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
