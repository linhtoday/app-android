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
import android.widget.Filter;
import android.widget.Filterable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.meow.chat.MessageActivity;
import com.meow.chat.R;
import com.meow.chat.model.Message;
import com.meow.chat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserSuggestAdapter extends RecyclerView.Adapter<UserSuggestAdapter.ViewHolder> implements Filterable{


    ArrayList<User> arr, arrFilter;

    Context context;

    public UserSuggestAdapter(Context context, ArrayList<User> arr) {
        this.arr = arr;
        this.arrFilter = arr;
        this.context = context;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.user_suggest_item, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.ll.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_anim));
        holder.avt.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_anim));
        User user = arrFilter.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return arrFilter.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView ll;
        ImageView avt;
        TextView username;

        View v;

        public ViewHolder(View itemView) {
            super(itemView);

            v = itemView;

            ll = itemView.findViewById(R.id.ll_user_suggest_item);
            avt = itemView.findViewById(R.id.user_suggest_avt);
            username = itemView.findViewById(R.id.user_suggest_username);




        }

        public void bind(final User item) {

            username.setText(item.getUsername());

            if (item.getImageURL().equals("df")) {
                avt.setImageResource(R.drawable.ic_account_circle_24dp);
            } else {
                Picasso.with(context).load(item.getImageURL()).placeholder(R.drawable.ic_account_circle_24dp).into(avt);
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("user_other", item.getUid());
                    context.startActivity(intent);
                }
            });


        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    arrFilter = arr;
                } else {
                    ArrayList<User> filteredList = new ArrayList<>();
                    for (User row : arr) {

                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    arrFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arrFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                arrFilter = (ArrayList<User>) filterResults.values;

                notifyDataSetChanged();
            }
        };
    }
}
